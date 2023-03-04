package fr.TheSakyo.EvhoUtility.utils.entity.player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Cet class gère la {@link ChannelPipeline} du joueur pour enregister des packets ou évènement customisé.
 */
public final class PlayerChannelPipelineManager {

    private static final UtilityMain plugin = UtilityMain.getInstance(); // Récupère l'instance de la class 'Main'

    public static final String CUSTOM_ENTITY = "custom_entity";  // Canal Pipeline constante "custom_entity".
    public static final String NPC_MANAGER = "npc_manager";  // Canal Pipeline constante "npc_manager".

                /* ---------------------------------------------------------------------- */

    public static void registerPlayer(Player player, final String CHANNEL_NAME, ChannelHandler handler) {

        Channel channel = ((CraftPlayer)player).getHandle().connection.connection.channel;
        ChannelPipeline pipeline = channel.pipeline();

        //Le pipeline de paquets du joueur possède-t-il déjà 'channel_name' ? Si c'est le cas, retour.
        if(pipeline.get(CHANNEL_NAME) != null) { return; }

        //Le contexte du gestionnaire de paquets existe-t-il ? Si non, retour.
        if(pipeline.context("packet_handler") == null) { return; }

        // Vérifie, si le canal n'éxiste pas
        if(!pipeline.names().contains(CHANNEL_NAME)) {

            //Tout est clair, On ajoute le canal 'channel_name' en question avec le gestionnaire de canal 'handler' en question avant le contexte du packet_handler.
            pipeline.addBefore("packet_handler", CHANNEL_NAME, handler);
        }
    }

                                        /* ---------------------------------------------- */

    public static void unregisterPlayer(Player player, String CHANNEL_NAME) {

        Channel channel = ((CraftPlayer)player).getHandle().connection.connection.channel;
        ChannelPipeline pipeline = channel.pipeline();

        //Le pipeline de paquets du joueur ne possède pas 'channel_name' ? Si c'est le cas, retour.
        if(pipeline.get(CHANNEL_NAME) == null) { return; }

        //Le contexte du gestionnaire de paquets existe-t-il ? Si non, retour.
        if(pipeline.context("packet_handler") == null) { return; }

        //Tout est clair, On supprime le canal 'channel_name', si le canal éxiste
        if(pipeline.names().contains(CHANNEL_NAME)) { pipeline.remove(CHANNEL_NAME); }
    }

                /* ---------------------------------------------------------------------- */
}