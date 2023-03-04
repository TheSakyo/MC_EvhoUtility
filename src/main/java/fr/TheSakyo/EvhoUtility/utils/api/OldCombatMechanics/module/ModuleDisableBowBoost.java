package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Empêche les joueurs de se propulser en avant en se tirant dessus.
 */
public class ModuleDisableBowBoost extends Module {


    public ModuleDisableBowBoost(UtilityMain plugin) { super(plugin, "disable-bow-boost"); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(EntityDamageByEntityEvent e){

        if(!(e.getEntity() instanceof Player player)) return;
        if(!(e.getDamager() instanceof Arrow arrow)) return;

         if(!isEnabled(player.getWorld())) return;

        ProjectileSource shooter = arrow.getShooter();
        if(shooter instanceof Player shootingPlayer) {

            if(player.getUniqueId().equals(shootingPlayer.getUniqueId())) {

                e.setCancelled(true);
                debug("Nous avons annulé le boost de votre arc", player);
            }
        }
    }
}