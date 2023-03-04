package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class DamageUtils {

    // Ordre des dégâts : base + effets des potions + coup critique + enchantements + effets de l'armure.
    // getDamage() renvoie sans les effets d'armure, getFinalDamage() renvoie avec les effets d'armure.

    /**
     * Obtient le multiplicateur de dégâts 'Sharpness' pour 1.9
     *
     * @param level Le niveau de l'enchantement
     * @return multiplicateur de dommages 'Sharpness' (multiplicateur de dégâts)
     */
    public static double getNewSharpnessDamage(int level) { return level >= 1 ? 1 + (level - 1) * 0.5 : 0; }

     /**
     * Obtient le multiplicateur de dégâts 'Sharpness' pour 1.8
     *
     * @param level Le niveau de l'enchantement
     * @return  multiplicateur de dommages 'Sharpness'
     */
    public static double getOldSharpnessDamage(int level) {
        return level >= 1 ? level * 1.25 : 0;
    }

    /**
     * Vérifier les préconditions pour les coups critiques
     *
     * @param le Entité vivante sur laquelle effectuer les vérifications
     * @return Le fait d'être touché est critique
     */
    public static boolean isCriticalHit1_8(LivingEntity le) {

        /* Code tiré de Bukkit 1.8_R3 :
            boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.k_() && !this.V()
            && !this.hasEffect(MobEffectList.BLINDNESS) && this.vehicle == null && entity instanceof EntityLiving;
            Où k_() signifie être sur une échelle ou une vigne, et V() dans l'eau.

            Dans la version 1.9, le joueur ne doit pas non plus être en train de sprinter.
        */
        return le.getFallDistance() > 0.0F && !le.isOnGround() && // Bien que déprécié, auto se rabat sur la méthode Entity qui est côté serveur.
                !isLivingEntityClimbing(le) && !isInWater(le) && le.getActivePotionEffects().stream().noneMatch(pe -> pe.getType() == PotionEffectType.BLINDNESS) && !le.isInsideVehicle();
    }

    private static boolean isInWater(LivingEntity le) {

        if(Reflector.versionIsNewerOrEqualAs(1, 16, 0))  return le.isInWater();
        else return le.getLocation().getBlock().getType() == Material.WATER;
    }

    private static boolean isLivingEntityClimbing(LivingEntity le) {

        final Material material = le.getLocation().getBlock().getType();
        return material == Material.LADDER || material == Material.VINE;
    }
}
