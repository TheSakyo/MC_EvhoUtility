package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Locale;

/**
 * Ajoute du knockback aux œufs, aux boules de neige et aux perles Ender.
 */
public class ModuleProjectileKnockback extends Module {

    public ModuleProjectileKnockback(UtilityMain plugin) { super(plugin, "projectile-knockback"); }

    @Deprecated
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityHit(EntityDamageByEntityEvent e) {

        if(!isEnabled(e.getEntity().getWorld())) return;
        EntityType type = e.getDamager().getType();

        switch(type) {

            case SNOWBALL: case EGG: case ENDER_PEARL:

                if(e.getDamage() == 0.0) { // Pour ne pas surcharger les dégâts de la chute de l'enderpearl.

                    e.setDamage(this.module().getDouble("damage." + type.toString().toLowerCase(Locale.ROOT)));
                    if(e.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION)) e.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0); }
        }

    }
}