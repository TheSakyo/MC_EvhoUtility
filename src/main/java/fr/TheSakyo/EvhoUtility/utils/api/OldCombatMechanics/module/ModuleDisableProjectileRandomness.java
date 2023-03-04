package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

/**
 * Empêche le bruit introduit lors du tir à l'arc pour que les flèches aillent droit.
 */
public class ModuleDisableProjectileRandomness extends Module {

    private static double EPSILON;

    public ModuleDisableProjectileRandomness(UtilityMain plugin) {

        super(plugin, "disable-projectile-randomness");
        reload();
    }

    @Override
    public void reload() { EPSILON = this.module().getDouble("epsilon"); }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

        Projectile projectile = e.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (shooter instanceof Player player) {

            if(!isEnabled(player.getWorld())) return;

            debug("Making projectile go straight", player);

            Vector playerDirection = player.getLocation().getDirection().normalize();
            Vector projectileDirection = projectile.getVelocity();

             // Conserver la vitesse d'origine
            double originalMagnitude = projectileDirection.length();
            projectileDirection.normalize();

            // Ce qui suit fonctionne, car l'utilisation de rotate modifie le vecteur, nous devons donc le doubler pour annuler la rotation.
            // Le vecteur est tourné autour de l'axe Y et mis en correspondance en vérifiant uniquement les valeurs X et Z.
            // Les angles sont spécifiés en radians, où 10° = 0,17 radians.
            if (!fuzzyVectorEquals(projectileDirection, playerDirection)) { // Si le projectile ne va pas tout droit

                if(fuzzyVectorEquals(projectileDirection, rotateAroundY(playerDirection, 0.17))) debug("10° Décalage", player);
                else if (fuzzyVectorEquals(projectileDirection, rotateAroundY(playerDirection, -0.35))) debug("-10° Offset", player);
            }

            playerDirection.multiply(originalMagnitude);
            projectile.setVelocity(playerDirection);
        }
    }

    private boolean fuzzyVectorEquals(Vector a, Vector b) { return Math.abs(a.getX() - b.getX()) < EPSILON && Math.abs(a.getZ() - b.getZ()) < EPSILON; }

    private Vector rotateAroundY(Vector vector, double angle) {

        if(Reflector.versionIsNewerOrEqualAs(1, 14, 0)) return vector.rotateAroundY(angle);
        else {

            double angleCos = Math.cos(angle);
            double angleSin = Math.sin(angle);

            double x = angleCos * vector.getX() + angleSin * vector.getZ();
            double z = -angleSin * vector.getX() + angleCos * vector.getZ();
            return vector.setX(x).setZ(z);
        }
    }
}