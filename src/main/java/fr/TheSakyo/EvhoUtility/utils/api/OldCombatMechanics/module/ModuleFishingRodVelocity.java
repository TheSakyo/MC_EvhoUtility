package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * Ce module ramène la gravité et la vitesse de la canne à pêche au comportement de la version 1.8.
 * La gravité de la canne à pêche en 1.14+ est de 0,03 alors qu'elle est de 0,04 en 1.8.
 * La vitesse de lancement dans les versions 1.9+ est également différente de la formule de la version 1.8.
 */
public class ModuleFishingRodVelocity extends Module {

    private Random random;
    private boolean hasDifferentGravity;
    private Method getHook;

    public ModuleFishingRodVelocity(UtilityMain plugin) {

        super(plugin, "fishing-rod-velocity");
        reload();
    }

    @Override
    public void reload() {

        random = new Random();

        // Les versions 1.14+ ont une gravité différente de celle des versions précédentes.
        hasDifferentGravity = Reflector.versionIsNewerOrEqualAs(1, 14, 0);

        // Reflection car en 1.12- cette méthode renvoie la classe Fish, qui a été renommée FishHook en 1.13+.
        getHook = Reflector.getMethod(PlayerFishEvent.class, "getHook");
    }

    @EventHandler
    public void onFishEvent(PlayerFishEvent event) {

        final FishHook fishHook = Reflector.invokeMethod(getHook, event);

        if(!isEnabled(fishHook.getWorld()) || event.getState() != PlayerFishEvent.State.FISHING) return;

        final Location location = event.getPlayer().getLocation();
        final double playerYaw = location.getYaw();
        final double playerPitch = location.getPitch();

        final float oldMaxVelocity = 0.4F;
        double velocityX = -Math.sin(playerYaw / 180.0F * (float)Math.PI) * Math.cos(playerPitch / 180.0F * (float) Math.PI) * oldMaxVelocity;
        double velocityZ = Math.cos(playerYaw / 180.0F * (float)Math.PI) * Math.cos(playerPitch / 180.0F * (float) Math.PI) * oldMaxVelocity;
        double velocityY = -Math.sin(playerPitch / 180.0F * (float)Math.PI) * oldMaxVelocity;

        final double oldVelocityMultiplier = 1.5;

        final double vectorLength = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
        velocityX /= vectorLength;
        velocityY /= vectorLength;
        velocityZ /= vectorLength;

        velocityX += random.nextGaussian() * 0.007499999832361937D;
        velocityY += random.nextGaussian() * 0.007499999832361937D;
        velocityZ += random.nextGaussian() * 0.007499999832361937D;

        velocityX *= oldVelocityMultiplier;
        velocityY *= oldVelocityMultiplier;
        velocityZ *= oldVelocityMultiplier;

        fishHook.setVelocity(new Vector(velocityX, velocityY, velocityZ));

        if(!hasDifferentGravity) return;

        // Ajuste la gravité à chaque tic, sauf s'il est dans l'eau.
        new BukkitRunnable() {

            @Override
            public void run() {

                if(!fishHook.isValid() || fishHook.isOnGround()) cancel();

                // Nous vérifions les deux conditions, car il arrive qu'il soit sous l'eau, mais sur l'herbe marine, ou qu'il ne soit pas sous l'eau, mais que le matériau soit de l'eau.
                if (!fishHook.isInWater() && fishHook.getWorld().getBlockAt(fishHook.getLocation()).getType() != Material.WATER) {

                    final Vector fVelocity = fishHook.getVelocity();
                    fVelocity.setY(fVelocity.getY() - 0.01);
                    fishHook.setVelocity(fVelocity);
                }
            }

        }.runTaskTimer(plugin, 1, 1);
    }
}
