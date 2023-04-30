package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module.Module;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class EntityDamageByEntityListener extends Module {

    private static EntityDamageByEntityListener INSTANCE;
    private boolean enabled;
    private final Map<UUID, Double> lastDamages;

    public EntityDamageByEntityListener(UtilityMain plugin) {

        super(plugin, "entity-damage-listener");
        INSTANCE = this;
        lastDamages = new WeakHashMap<>();
    }

    public static EntityDamageByEntityListener getINSTANCE() { return INSTANCE; }

    @Override
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        Entity damager = event.getDamager();
        Entity entityDamager = event.getEntity();
        OldEntityDamageByEntityEvent e = new OldEntityDamageByEntityEvent(damager, entityDamager, event.getCause(), event.getDamage());

        // Appel de l'événement pour que les autres modules apportent leurs modifications.
        plugin.getServer().getPluginManager().callEvent(e);
        if(e.isCancelled()) return;

        // Maintenant, nous recalculons les dommages modifiés par les modules et les remettons à l'événement original.
        // Ordre des dommages : base → effets des potions → coup critique → sur dommage → enchantements → effets de l'armure.
        double newDamage = e.getBaseDamage();

        debug("Base: " + e.getBaseDamage(), damager);

        //Potion de faiblesse
        double weaknessModifier = e.getWeaknessModifier();

        if(e.isWeaknessModifierMultiplier()) newDamage *= weaknessModifier;
        else newDamage += weaknessModifier;

        debug("Faible: " + e.getWeaknessModifier(), damager);

        //Potion de force
        debug("Niveau de force: " + e.getStrengthLevel(), damager);

        double strengthModifier = e.getStrengthModifier() * e.getStrengthLevel();

        if(!e.isStrengthModifierMultiplier()) newDamage += strengthModifier;
        else if (e.isStrengthModifierAddend()) newDamage *= ++strengthModifier;
        else newDamage *= strengthModifier;

        debug("Force: " + strengthModifier, damager);

        // Coup critique : 1.9 est *1.5, 1.8 est *rand(0%,50%) + 1
        // Code Bukkit 1.8_r3 : i += this.random.nextInt(i / 2 + 2) ;
        if (e.was1_8Crit() && e.wasSprinting()) {

            newDamage *= e.getCriticalMultiplier();

            if(e.RoundCritDamage()) newDamage = (int)newDamage;
            newDamage += e.getCriticalAddend();

            debug("Critique * " + e.getCriticalMultiplier() + " + " + e.getCriticalAddend(), damager);
        }

        final double lastDamage = newDamage;

        // Surdommagements dus à l'immunité
        if (e.wasInvulnerabilityOverdamage() && entityDamager instanceof final LivingEntity livingDamagee) {

            // Nous devons soustraire les dommages précédents des dommages de la nouvelle arme pour cette attaque.
            newDamage -= livingDamagee.getLastDamage();
        }

        //Enchantements
        newDamage += e.getMobEnchantmentsDamage() + e.getSharpnessDamage();

        debug("Mob " + e.getMobEnchantmentsDamage() + " Sharpness: " + e.getSharpnessDamage(), damager);

        if(newDamage < 0) {

            debug("Les dommages ont été " + newDamage + " pour 0", damager);
            newDamage = 0;
        }

        debug("Nouveaux dommages: " + newDamage, damager);

        event.setDamage(newDamage);

        // Selon le NMS, les derniers dégâts devraient en fait être l'outil de base + la force + le crie, avant l'overdamage.
        lastDamages.put(entityDamager.getUniqueId(), lastDamage);
    }

    /**
     * Définit les derniers dommages de l'entité 1 tick après l'événement.
     * Pour une raison quelconque, cela est réglé automatiquement après l'événement sur les dommages d'origine.
     * (Peut-être un bug de Spigot ?) Espérons que d'autres plugins vibrent avec cela. Sinon, on peut stocker ceci juste pour OCM.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void afterEntityDamage(EntityDamageByEntityEvent e) {

        //TODO devrait probablement juste stocker cette information pour nous-mêmes, car cela fera que certaines attaques ne seront pas comptées.
        // pour le surdommage, car les dégâts bruts de certaines armes sont inférieurs à ce que nous avons défini, et l'événement ne se déclenche jamais.
        // mais qu'en est-il de l'inverse, quand une arme est trop forte ? alors, d'après les calculs que nous effectuons, elle devrait faire 0 dégât.
        final Entity damagee = e.getEntity();
        if(damagee instanceof LivingEntity) {

            final UUID damageeId = damagee.getUniqueId();

            if(lastDamages.containsKey(damageeId)) {

                final double damage = lastDamages.get(damageeId);

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        ((LivingEntity) damagee).setLastDamage(damage);

                        debug("Définit le dernier dommage à " + damage, damagee);
                        lastDamages.remove(damageeId);
                    }

                }.runTaskLater(plugin, 1);
            }
        }
    }
}
