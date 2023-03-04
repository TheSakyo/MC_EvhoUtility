package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Un module pour contrôler les chorus fruits.
 */
public class ModuleChorusFruit extends Module {

    public ModuleChorusFruit(UtilityMain plugin) { super(plugin, "chorus-fruit"); }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {

        if(e.getItem().getType() != Material.CHORUS_FRUIT) return;
        final Player player = e.getPlayer();

        if(!isEnabled(player.getWorld())) return;

        if(this.module().getBoolean("prevent-eating")) {

            e.setCancelled(true);
            return;
        }

        final int hungerValue = this.module().getInt("hunger-value");
        final double saturationValue = this.module().getDouble("saturation-value");
        final int previousFoodLevel = player.getFoodLevel();
        final float previousSaturation = player.getSaturation();

        // Lance-le au prochain tick pour réinitialiser les choses tout en n'annulant pas l'événement chorus fruit eat.
        // Cela garantit que l'événement de téléportation est déclenché et qu'il est pris en compte dans les statistiques.
        new BukkitRunnable() {

            @Override
            public void run() {

                final int newFoodLevel = Math.min(hungerValue + previousFoodLevel, 20);
                final float newSaturation = Math.min((float) (saturationValue + previousSaturation), newFoodLevel);

                player.setFoodLevel(newFoodLevel);
                player.setSaturation(newSaturation);

                debug("Le niveau de nourriture est passé de : " + previousFoodLevel + " à " + player.getFoodLevel(), player);
            }

        }.runTaskLater(plugin, 2);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {

        if(e.getCause() != PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) return;

        final Player player = e.getPlayer();
        if(!isEnabled(player.getWorld())) return;

        final double distance = getMaxTeleportationDistance();

        if(distance == 8) {

            debug("En utilisant l'implémentation de téléportation vanille !", player);
            return;
        }

        if(distance <= 0) {

            debug("La téléportation du chorus n'est pas autorisée", player);
            e.setCancelled(true);
            return;
        }

        // Pas sûr que cela puisse se produire, mais il est marqué comme @Nullable.
        Location toLocation = e.getTo();

        if(toLocation == null) {

            debug("La cible de téléportation est nulle", player);
            return;
        }

        final int maxheight = toLocation.getWorld().getMaxHeight();

        e.setTo(player.getLocation().add(ThreadLocalRandom.current().nextDouble(-distance, distance),
                clamp(ThreadLocalRandom.current().nextDouble(-distance, distance), 0, maxheight - 1), ThreadLocalRandom.current().nextDouble(-distance, distance)));
    }

    private double clamp(double x, double min, double max) { return Math.max(Math.min(x, max), min); }

    private double getMaxTeleportationDistance() { return this.module().getDouble("max-teleportation-distance"); }
}
