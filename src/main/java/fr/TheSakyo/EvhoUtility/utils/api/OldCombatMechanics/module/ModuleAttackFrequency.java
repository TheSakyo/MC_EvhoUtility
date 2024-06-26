package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ModuleAttackFrequency extends Module {

    private static final int DEFAULT_DELAY = 20;
    private static int playerDelay, mobDelay;

    public ModuleAttackFrequency(UtilityMain plugin) {

        super(plugin, "attack-frequency");
        this.module().getInt("delay");
    }

    @Override
    public void reload() {

        playerDelay = this.module().getInt("playerDelay");
        mobDelay = this.module().getInt("mobDelay");

        Bukkit.getWorlds().forEach(world -> world.getLivingEntities().forEach(livingEntity -> {

            final int toApply = livingEntity instanceof HumanEntity ? playerDelay : mobDelay;
            livingEntity.setMaximumNoDamageTicks(isEnabled(world) ? toApply : DEFAULT_DELAY);
        }));
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {

        final Player player = e.getPlayer();
        final World world = player.getWorld();
        if(isEnabled(world)) setDelay(player, playerDelay);
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e) { setDelay(e.getPlayer(), DEFAULT_DELAY); }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {

        final Player player = e.getPlayer();
        final World world = player.getWorld();
        setDelay(player, isEnabled(world) ? playerDelay : DEFAULT_DELAY);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {

        final Player player = e.getPlayer();
        final World world = player.getWorld();
        setDelay(player, isEnabled(world) ? playerDelay : DEFAULT_DELAY);
    }

    private void setDelay(Player player, int delay) {

        player.setMaximumNoDamageTicks(delay);
        debug("Délai d'exécution définit sur " + delay, player);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {

        final LivingEntity livingEntity = e.getEntity();
        final World world = livingEntity.getWorld();
        if(isEnabled(world)) livingEntity.setMaximumNoDamageTicks(mobDelay);
    }

    @EventHandler
    public void onEntityTeleportEvent(EntityTeleportEvent e) {

        final Entity entity = e.getEntity();

        if(entity instanceof final LivingEntity livingEntity) {

            final World fromWorld = e.getFrom().getWorld();
            final World toWorld = e.getTo().getWorld();

            if(fromWorld.getUID() != toWorld.getUID()) livingEntity.setMaximumNoDamageTicks(isEnabled(toWorld) ? mobDelay : DEFAULT_DELAY);
        }
    }
}
