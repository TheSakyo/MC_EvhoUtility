package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.ConfigUtils;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.OldEntityDamageByEntityEvent;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.potions.GenericPotionDurations;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.potions.PotionDurations;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.potions.PotionEffects;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;


/**
 * Permet de configurer la durée des effets des potions.
 */
public class ModuleOldPotionEffects extends Module {

    private static final Set<PotionType> EXCLUDED_POTION_TYPES = EnumSet.of(

            // Ceci n'inclut que les potions de la version 1.9, les autres seront ajoutées plus tard pour des raisons de compatibilité.
            // Les potions instantanées n'ont pas de durée modifiable.
            PotionType.INSTANT_DAMAGE, PotionType.INSTANT_HEAL,
            // Potions de base sans effet
            PotionType.AWKWARD, PotionType.MUNDANE, PotionType.THICK, PotionType.UNCRAFTABLE, PotionType.WATER
    );

    private Map<PotionType, PotionDurations> durations;

    public ModuleOldPotionEffects(UtilityMain plugin) {

        super(plugin, "old-potion-effects");

        try {

            //La potion de 'Maître Tortue' a deux effets et Bukkit n'en renvoie qu'un seul avec '#getEffectType()'
            EXCLUDED_POTION_TYPES.add(PotionType.TURTLE_MASTER);

        } catch(NoSuchFieldError e) { debug("Saut en excluant une potion (probablement une ancienne version du serveur)"); }

        reload();
    }

    @Override
    public void reload() { durations = ConfigUtils.loadPotionDurationsList(this.module()); }

    /**
     * Change la durée en utilisant les valeurs définies dans la configuration pour boire des potions
     */
    @Deprecated
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDrinksPotion(PlayerItemConsumeEvent event) {

        final Player player = event.getPlayer();
        if(!isEnabled(player.getWorld())) return;

        final ItemStack potionItem = event.getItem();
        if(potionItem.getType() != Material.POTION) return;

        final PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
        if(potionMeta == null) return;

        final PotionData potionData = potionMeta.getBasePotionData();
        final PotionType potionType = potionData.getType();

        if(EXCLUDED_POTION_TYPES.contains(potionType)) return;

        event.setCancelled(true);

        final int amplifier = potionData.isUpgraded() ? 1 : 0;
        final int duration = getPotionDuration(potionData, false);

        final PotionEffectType effectType = requireNonNull(potionType.getEffectType());
        final PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);

        setNewPotionEffect(player, potionEffect);

        // Retirer l'objet de la main puisque nous avons annulé l'événement.
        if (player.getGameMode() == GameMode.CREATIVE) return;

        final PlayerInventory playerInventory = player.getInventory();

        final int amount = potionItem.getAmount();
        ItemStack toSet = new ItemStack(Material.GLASS_BOTTLE);

        final boolean isInMainHand = potionItem.equals(playerInventory.getItemInMainHand());

        // Il y avait plus d'une potion dans la pile.
        if(amount > 1) {

            playerInventory.addItem(toSet);
            toSet = potionItem;
            toSet.setAmount(amount - 1);
        }

        // Si la potion est dans la main principale
        if(isInMainHand) playerInventory.setItemInMainHand(toSet);
        else playerInventory.setItemInOffHand(toSet);
    }

    /**
     * Change la durée en utilisant les valeurs définies dans la configuration pour les potions splash.
     */
    @Deprecated
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPotionSplash(PotionSplashEvent event) {

        if (!isEnabled(event.getEntity().getWorld())) return;

        final ThrownPotion thrownPotion = event.getPotion();
        final PotionMeta potionMeta = (PotionMeta) thrownPotion.getItem().getItemMeta();

        for(PotionEffect potionEffect : thrownPotion.getEffects()) {

            final PotionData potionData = potionMeta.getBasePotionData();
            if(EXCLUDED_POTION_TYPES.contains(potionData.getType())) return;

            event.setCancelled(true);

            final int duration = getPotionDuration(potionData, true);
            final PotionEffect newEffect = new PotionEffect(potionEffect.getType(), duration, potionEffect.getAmplifier());

            event.getAffectedEntities().forEach(livingEntity -> setNewPotionEffect(livingEntity, newEffect));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageByEntity(OldEntityDamageByEntityEvent event) {

        final Entity damager = event.getDamager();
        if(!isEnabled(damager.getWorld())) return;

        final double weaknessModifier = event.getWeaknessModifier();

        if(weaknessModifier != 0) {

            event.setIsWeaknessModifierMultiplier(this.module().getBoolean("weakness.multiplier"));
            final double newWeaknessModifier = this.module().getDouble("weakness.modifier");

            event.setWeaknessModifier(newWeaknessModifier);
            debug("Ancien modificateur de faiblesse: " + weaknessModifier + " Nouveau: " + newWeaknessModifier, damager);
        }

        final double strengthModifier = event.getStrengthModifier();

        if(strengthModifier != 0) {

            final double newStrengthModifier = this.module().getDouble("strength.modifier");

            event.setIsStrengthModifierMultiplier(this.module().getBoolean("strength.multiplier"));
            event.setIsStrengthModifierAddend(this.module().getBoolean("strength.addend"));
            event.setStrengthModifier(newStrengthModifier);

            debug("Ancien modificateur de force: " + strengthModifier + " Nouveau: " + newStrengthModifier, damager);
        }
    }

    @Deprecated
    private void setNewPotionEffect(LivingEntity livingEntity, PotionEffect potionEffect) {

        if(!livingEntity.hasPotionEffect(potionEffect.getType())) {

            livingEntity.addPotionEffect(potionEffect);
            return;
        }

        final PotionEffect activeEffect = PotionEffects.getOrNull(livingEntity, potionEffect.getType());
        final int remainingDuration = activeEffect.getDuration();

        // Si le nouvel effet est de type II alors que l'ancien ne l'était pas, ou si le nouvel effet dure plus longtemps que
        // le temps restant, mais n'est pas un abaissement de niveau (par exemple II -> I), définissez-le
        final int newAmplifier = potionEffect.getAmplifier();
        final int activeAmplifier = activeEffect.getAmplifier();

        if(newAmplifier < activeAmplifier) return;

        if(newAmplifier > activeAmplifier || remainingDuration < potionEffect.getDuration()) livingEntity.addPotionEffect(potionEffect, true);
    }

    private int getPotionDuration(PotionData potionData, boolean splash) {
        
        final PotionType potionType = potionData.getType();
        debug("Potion type: " + potionType.name());

        final GenericPotionDurations potionDurations = splash ? durations.get(potionType).getSplash() : durations.get(potionType).getDrinkable();

        int duration;
        if (potionData.isExtended()) duration = potionDurations.getExtendedTime();
        else if (potionData.isUpgraded()) duration = potionDurations.getIITime();
        else duration = potionDurations.getBaseTime();

        return duration * 20; // Convert seconds to ticks
    }
}
