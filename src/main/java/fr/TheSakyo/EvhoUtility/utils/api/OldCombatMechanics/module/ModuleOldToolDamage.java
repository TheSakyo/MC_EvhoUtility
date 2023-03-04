package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.DamageUtils;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.OldEntityDamageByEntityEvent;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.WeaponDamages;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.Locale;

/**
 * Restaure les anciens dommages causés par les outils.
 */
public class ModuleOldToolDamage extends Module {

    private static final String[] WEAPONS = {"sword", "axe", "pickaxe", "spade", "shovel", "hoe"};

    public ModuleOldToolDamage(UtilityMain plugin) { super(plugin, "old-tool-damage"); }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamaged(OldEntityDamageByEntityEvent event) {

        final Entity damager = event.getDamager();
        if(event.getCause() == EntityDamageEvent.DamageCause.THORNS) return;

        final Entity damagee = event.getDamagee();
        final World world = damager.getWorld();

        if(!isEnabled(world)) return;

        final Material weaponMaterial = event.getWeapon().getType();

        if(!isTool(weaponMaterial)) return;

        double weaponDamage = WeaponDamages.getDamage(weaponMaterial);
        if(weaponDamage <= 0) {

            debug("Type d'outil inconnu: " + weaponMaterial, damager);
            return;
        }

        final double oldBaseDamage = event.getBaseDamage();

        // Si le raw n'est pas ce que nous attendons pour la 1.9, nous devons l'ignorer, pour des raisons de compatibilité avec d'autres plugins //
        //if(oldBaseDamage == expectedDamage) event.setBaseDamage(weaponDamage)
        event.setBaseDamage(weaponDamage);

        debug("Ancien " + weaponMaterial + " dommages: " + oldBaseDamage + " Nouvel outil endommagé: " + weaponDamage +
                (event.wasInvulnerabilityOverdamage() ? " (sur les dommages)" : ""), damager);

        // Définir la netteté à la valeur de dommage 1.8
        final double newSharpnessDamage = DamageUtils.getOldSharpnessDamage(event.getSharpnessLevel());
        debug("Ancienne détérioration de la netteté: " + event.getSharpnessDamage() + " Nouveau: " + newSharpnessDamage, damager);
        event.setSharpnessDamage(newSharpnessDamage);
    }

    private boolean isTool(Material material) { return Arrays.stream(WEAPONS).anyMatch(type -> isOfType(material, type)); }
    private boolean isOfType(Material mat, String type) { return mat.toString().endsWith("_" + type.toUpperCase(Locale.ROOT)); }
}
