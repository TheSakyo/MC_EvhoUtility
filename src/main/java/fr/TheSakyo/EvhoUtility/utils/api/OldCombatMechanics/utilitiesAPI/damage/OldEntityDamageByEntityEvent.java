package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.potions.PotionEffects;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger.debug;

public class OldEntityDamageByEntityEvent extends Event implements Cancellable {

    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }

    private final Entity damager, damagee;
    private final DamageCause cause;
    private final double rawDamage;

    private ItemStack weapon;
    private int sharpnessLevel;
    private int strengthLevel;

    private double baseDamage = 0, mobEnchantmentsDamage = 0, sharpnessDamage = 0, criticalMultiplier = 1, criticalAddend = 0;
    private double strengthModifier = 0, weaknessModifier = 0;

    // En 1.9, le modificateur de force est un addendum, en 1.8, c'est un multiplicateur et un addendum (+130%).
    private boolean isStrengthModifierMultiplier = false;
    private boolean isStrengthModifierAddend = true;
    private boolean isWeaknessModifierMultiplier = false;

    private boolean was1_8Crit = false;
    private boolean wasSprinting = false;
    private boolean roundCritDamage = false;

    // Si ces dommages sont des dommages réduits d'une attaque à dommages plus élevés survenant pendant une période d'invulnérabilité noDamageTicks.
    private boolean wasInvulnerabilityOverdamage = false;

    // Ici, nous procédons à la rétro-ingénierie de tous les différents dommages causés en les supprimant un par un, à l'inverse de ce que fait le code NMS.
    // Ainsi, les modules peuvent écouter cet événement et effectuer leurs modifications, puis l'EntityDamageByEntityListener rétablit les nouvelles valeurs.
    public OldEntityDamageByEntityEvent(Entity damager, Entity damagee, DamageCause cause, double rawDamage) {

        this.damager = damager;
        this.damagee = damagee;
        this.cause = cause;
        this.rawDamage = rawDamage;

        if(!(damager instanceof final LivingEntity le)) {

            setCancelled(true);
            return;
        }

        final EntityEquipment equipment = le.getEquipment();
        weapon = equipment.getItemInMainHand();

        // Yay paper. Pourquoi dois-tu retourner null ici ?
        if (weapon == null) weapon = new ItemStack(Material.AIR);

        // Techniquement, l'arme peut être en main secondaire, par exemple un arc.
        // Cependant, nous ne nous intéressons ici qu'aux armes de mêlée, qui seront toujours dans la main principale.

        final EntityType damageeType = damagee.getType();

        debug(le, "Dégâts bruts : " + rawDamage);

        /*
        L'invulnérabilité causera moins de dégâts s'ils attaquent avec une arme plus puissante alors qu'ils sont vulnérables.
        Nous devons détecter cela et en tenir compte, au lieu de définir les dommages de base habituels des armes.
        */
        if (damagee instanceof LivingEntity livingDamagee) {

            if((float)livingDamagee.getNoDamageTicks() > (float) livingDamagee.getMaximumNoDamageTicks() / 2.0F) {

                // Le code du NMS vérifie également si le dommage actuel est supérieur au dommage précédent. Cependant, ici l'événement
                // a déjà la différence entre les deux comme dommage brut et l'événement ne se déclenche pas du tout
                // si cette condition préalable n'est pas remplie.
                wasInvulnerabilityOverdamage = true;
                debug(le, "Trop endommagé!");

            } else {

                debug(le, "Invulnérabilité : " + livingDamagee.getNoDamageTicks() + "/" +  livingDamagee.getMaximumNoDamageTicks() / 2.0F + " dernier : " + livingDamagee.getLastDamage());
            }
        }

        mobEnchantmentsDamage = MobDamage.applyEntityBasedDamage(damageeType, weapon, rawDamage) - rawDamage;

        sharpnessLevel = weapon.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        sharpnessDamage = DamageUtils.getNewSharpnessDamage(sharpnessLevel);

        debug(le, "Mob: " + mobEnchantmentsDamage + " Sharpness: " + sharpnessDamage);

        //Montant des dommages, y compris les effets des potions et les coups critiques.
        double tempDamage = rawDamage - mobEnchantmentsDamage - sharpnessDamage;

        debug(le, "Pas de dégâts d'enchère: " + tempDamage);

        //Vérifie si c'est un coup critique
        if(DamageUtils.isCriticalHit1_8(le)) {

            was1_8Crit = true;
            debug(le, "1.8 Critical hit detected");

            // En 1.9, un crit nécessite également que le joueur ne sprinte pas.
            if(le instanceof Player) {

                wasSprinting = ((Player) le).isSprinting();

                if(!wasSprinting) {

                    debug(le, "1.9 Critical hit detected");
                    criticalMultiplier = 1.5;
                    tempDamage /= 1.5;
                }
            }
        }

        //amplificateur 0 = force I amplificateur 1 = force II
        int amplifier = PotionEffects.get(le, PotionEffectType.INCREASE_DAMAGE).map(PotionEffect::getAmplifier).orElse(-1);

        strengthLevel = ++amplifier;
        strengthModifier = strengthLevel * 3;

        debug(le, "Modificateur de force: " + strengthModifier);

        if(le.hasPotionEffect(PotionEffectType.WEAKNESS)) weaknessModifier = -4;

        debug(le, "Modificateur de force: " + weaknessModifier);

        baseDamage = tempDamage + weaknessModifier - strengthModifier;
        debug(le, "Dommage de l'outil de base: " + baseDamage);
    }

    public Entity getDamager() { return damager; }

    public Entity getDamagee() { return damagee; }

    public DamageCause getCause() { return cause; }

    public double getRawDamage() { return rawDamage; }

    public ItemStack getWeapon() { return weapon; }

    public int getSharpnessLevel() { return sharpnessLevel; }

    public double getStrengthModifier() { return strengthModifier; }

    public void setStrengthModifier(double strengthModifier) { this.strengthModifier = strengthModifier; }

    public int getStrengthLevel() { return strengthLevel; }

    public double getWeaknessModifier() { return weaknessModifier; }

    public void setWeaknessModifier(double weaknessModifier) { this.weaknessModifier = weaknessModifier; }

    public boolean isStrengthModifierMultiplier() { return isStrengthModifierMultiplier; }

    public void setIsStrengthModifierMultiplier(boolean isStrengthModifierMultiplier) { this.isStrengthModifierMultiplier = isStrengthModifierMultiplier; }

    public void setIsStrengthModifierAddend(boolean isStrengthModifierAddend) { this.isStrengthModifierAddend = isStrengthModifierAddend; }

    public boolean isWeaknessModifierMultiplier() { return isWeaknessModifierMultiplier; }

    public void setIsWeaknessModifierMultiplier(boolean weaknessModifierMultiplier) { isWeaknessModifierMultiplier = weaknessModifierMultiplier; }

    public boolean isStrengthModifierAddend() { return isStrengthModifierAddend; }

    public double getBaseDamage() { return baseDamage; }

    public void setBaseDamage(double baseDamage) { this.baseDamage = baseDamage; }

    public double getMobEnchantmentsDamage() { return mobEnchantmentsDamage; }

    public void setMobEnchantmentsDamage(double mobEnchantmentsDamage) { this.mobEnchantmentsDamage = mobEnchantmentsDamage; }

    public double getSharpnessDamage() { return sharpnessDamage; }

    public void setSharpnessDamage(double sharpnessDamage) { this.sharpnessDamage = sharpnessDamage; }

    public double getCriticalMultiplier() { return criticalMultiplier; }

    public void setCriticalMultiplier(double criticalMultiplier) { this.criticalMultiplier = criticalMultiplier; }

    @Override
    public boolean isCancelled() { return cancelled; }

    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public double getCriticalAddend() { return criticalAddend; }

    public void setCriticalAddend(double criticalAddend) { this.criticalAddend = criticalAddend; }

    public boolean wasSprinting() { return wasSprinting; }

    public void setWasSprinting(boolean wasSprinting) { this.wasSprinting = wasSprinting; }

    public boolean was1_8Crit() { return was1_8Crit; }

    public void setWas1_8Crit(boolean was1_8Crit) { this.was1_8Crit = was1_8Crit; }

    public boolean RoundCritDamage() { return roundCritDamage; }

    public void setRoundCritDamage(boolean roundCritDamage) { this.roundCritDamage = roundCritDamage; }

    public boolean wasInvulnerabilityOverdamage() { return wasInvulnerabilityOverdamage; }

    public void setWasInvulnerabilityOverdamage(boolean wasInvulnerabilityOverdamage) { this.wasInvulnerabilityOverdamage = wasInvulnerabilityOverdamage; }
}
