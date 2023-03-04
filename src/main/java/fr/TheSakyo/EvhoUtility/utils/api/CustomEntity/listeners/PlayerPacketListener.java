package fr.TheSakyo.EvhoUtility.utils.api.CustomEntity.listeners;

import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerChannelPipelineManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import fr.TheSakyo.EvhoUtility.utils.api.CustomEntity.packets.PacketInterceptor;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Cet écouteur est responsable de l'injection de notre {@link PacketInterceptor} dans la {@link ChannelPipeline} du joueur.
 * dans le {@link ChannelPipeline} de traitement des paquets du joueur.
 */
public final class PlayerPacketListener implements Listener {

    private final JavaPlugin plugin;
    public PlayerPacketListener(JavaPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        PlayerChannelPipelineManager.registerPlayer(player, PlayerChannelPipelineManager.CUSTOM_ENTITY, new PacketInterceptor(plugin, player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        PlayerChannelPipelineManager.unregisterPlayer(player, PlayerChannelPipelineManager.CUSTOM_ENTITY);
    }

}
