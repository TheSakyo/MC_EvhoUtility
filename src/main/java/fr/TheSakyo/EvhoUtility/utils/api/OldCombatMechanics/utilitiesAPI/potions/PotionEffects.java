package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.potions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class PotionEffects {

    private static boolean canUseGetPotionEffectsMethod;

    static {

        try {

            LivingEntity.class.getDeclaredMethod("getPotionEffect", PotionEffectType.class);
            canUseGetPotionEffectsMethod = true;

        } catch(NoSuchMethodException e) { canUseGetPotionEffectsMethod = false; }
    }

    /**
     * Renvoie le {@link PotionEffect} d'un {@link PotionEffectType} donné pour une {@link LivingEntity} donnée, si elle existe.
     *
     * @param entity L'Entité à Interroger
     * @param type   Le Type à Rechercher
     * @return {@link PotionEffect} si présent
     */
    public static Optional<PotionEffect> get(LivingEntity entity, PotionEffectType type) { return Optional.ofNullable(getOrNull(entity, type)); }


    /**
     * Renvoie le {@link PotionEffect} d'un {@link PotionEffectType} donné pour une {@link LivingEntity} donnée, si elle est présente.
     *
     * @param entity L'Entité à interroger
     * @param type le type à rechercher
     * @return {@link PotionEffect} ou {@code null} si non présent
     */
    public static PotionEffect getOrNull(LivingEntity entity, PotionEffectType type){

        if(canUseGetPotionEffectsMethod){ return entity.getPotionEffect(type); }
        return entity.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().equals(type)).findAny().orElse(null);
    }
}
