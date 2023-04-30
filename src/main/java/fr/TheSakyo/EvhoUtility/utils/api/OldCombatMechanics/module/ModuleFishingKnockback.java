package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM_ConfigHandler;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.MemoizingFeatureBranch;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.util.Vector;

import java.util.EnumMap;

/**
 * Ramène l'ancien knockback de pêche.
 */
public class ModuleFishingKnockback extends Module {

    private final MemoizingFeatureBranch<PlayerFishEvent, Entity> hookEntityFeature;

    public ModuleFishingKnockback(UtilityMain plugin) {

        super(plugin, "old-fishing-knockback");

        //'noinspection Convert2MethodRef' car la référence à la méthode serait erronée à l'initialisation, et pas seulement lorsqu'elle est invoquée.
        hookEntityFeature = MemoizingFeatureBranch.onException(PlayerFishEvent::getHook, PlayerFishEvent::getHook,  Reflector.memoizeMethodAndInvoke(PlayerFishEvent.class, "getHook") /* Retour à la réflexion en 1.12 et perte de performance. */);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRodLand(ProjectileHitEvent e) {

        Entity hookEntity = e.getEntity();
        World world = hookEntity.getWorld();

        if(!isEnabled(world)) return;
        if(e.getEntityType() != EntityType.FISHING_HOOK) return;

        final boolean knockbackNonPlayerEntities = isSettingEnabled("knockbackNonPlayerEntities");
        Entity hitEntity;

        try { hitEntity = e.getHitEntity(); }
        catch(NoSuchMethodError e1) { //Pour les versions plus anciennes qui ne disposent pas de cette méthode

            hitEntity = world.getNearbyEntities(hookEntity.getLocation(), 0.25, 0.25, 0.25).stream()
                    .filter(entity -> !knockbackNonPlayerEntities && entity instanceof Player).findFirst().orElse(null);
        }

        if(hitEntity == null) return;
        if(!(hitEntity instanceof LivingEntity livingEntity)) return;
        if(!knockbackNonPlayerEntities && !(hitEntity instanceof Player)) return;

        // Ne bouge pas les Entitités 'NPC'
        if(hitEntity instanceof NPC) return;

        FishHook hook = (FishHook) hookEntity;
        Player rodder = (Player) hook.getShooter();

        if(!knockbackNonPlayerEntities) {
            Player player = (Player) hitEntity;

            debug("You were hit by a fishing rod!", player);

            if (player.equals(rodder)) return;

            if (player.getGameMode() == GameMode.CREATIVE) return;
        }

        //Vérifie si le temps de refroidissement s'est écoulé.
        if(livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2f) return;

        double damage = this.module().getDouble("damage");
        if(damage < 0) damage = 0.2;

        EntityDamageEvent event = makeEvent(rodder, hitEntity, damage);
        Bukkit.getPluginManager().callEvent(event);

        if(this.module().getBoolean("checkCancelled") && event.isCancelled()) {

            if(new OldCM_ConfigHandler(plugin).getConfig("/OldCombatCfg.yml").getBoolean("debug.enabled")) {

                debug("Vous ne pouvez pas faire ça ici !", rodder);
                HandlerList hl = event.getHandlers();

                // Ceci permet de vérifier quels sont les plugins qui écoutent l'événement.
                for(RegisteredListener rl : hl.getRegisteredListeners()) debug("Plugin Listening: " + rl.getPlugin().getName(), rodder);
            }
            return;
        }

        livingEntity.damage(damage);
        livingEntity.setVelocity(calculateKnockbackVelocity(livingEntity.getVelocity(), livingEntity.getLocation(), hook.getLocation()));
    }

    private Vector calculateKnockbackVelocity(Vector currentVelocity, Location player, Location hook) {

        double xDistance = hook.getX() - player.getX();
        double zDistance = hook.getZ() - player.getZ();

        // Assure que la distance n'est pas nulle et randomiser dans ce cas (je suppose ?)
        while(xDistance * xDistance + zDistance * zDistance < 0.0001) {

            xDistance = (Math.random() - Math.random()) * 0.01D;
            zDistance = (Math.random() - Math.random()) * 0.01D;
        }

        double distance = Math.sqrt(xDistance * xDistance + zDistance * zDistance);

        double y = currentVelocity.getY() / 2;
        double x = currentVelocity.getX() / 2;
        double z = currentVelocity.getZ() / 2;


        x -= xDistance / distance * 0.4; // Normalise la distance pour avoir un knockback similaire, quelle que soit la distance.

        y += 0.4; // Ralenti la chute ou lancer vers le haut

        z -= zDistance / distance * 0.4;  // Normalise la distance pour avoir un knockback similaire, quelle que soit la distance.

        // Évite de tirer trop haut
        if(y >= 0.4) y = 0.4;

        return new Vector(x, y, z);
    }

     /**
     * Ceci permet d'annuler le rapprochement de l'entité lorsque vous faites un moulinet.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onReelIn(PlayerFishEvent e) {

        if(e.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;

        final String cancelDraggingIn = this.module().getString("cancelDraggingIn");
        final boolean isPlayer = e.getCaught() instanceof HumanEntity;
        if((cancelDraggingIn.equals("players") && isPlayer) || cancelDraggingIn.equals("mobs") && !isPlayer ||  cancelDraggingIn.equals("all")) {

            hookEntityFeature.apply(e).remove(); // Enlive le 'bobber' et ne fait rien d'autre.
            e.setCancelled(true);
        }
    }

                                                 /* --------------------------- */
    @SuppressWarnings("deprecation")
    private EntityDamageEvent makeEvent(Player rodder, Entity entity, double damage) {

        if(this.module().getBoolean("useEntityDamageEvent")) return new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.PROJECTILE,
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, damage)),
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(damage))));

        else return new EntityDamageByEntityEvent(rodder, entity, EntityDamageEvent.DamageCause.PROJECTILE,
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, damage)),
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(damage))));
    }
}