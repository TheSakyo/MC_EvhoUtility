package fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms;

import fr.TheSakyo.EvhoUtility.PaperMC.PaperPlugin;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit.NMSCraftPlayer;
import fr.TheSakyo.EvhoUtility.ServerVersion;
import io.netty.channel.Channel;
import java.lang.reflect.Field;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.entity.Player;

public class NMSNetworkManager {

  private static Field networkManagerField; // Variable récupérant une variable utile depuis de la 'class' en question

  private static Field channel; // Variable récupérant le canal de connexion

                            /* ------------------------------------------------------------------ */


  /**
   * Charge la 'class' du NMS "networkManager", récupérant diverses informations utiles
   *
   */
  public static void load() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {

    ServerVersion serverVersion = PaperPlugin.getServerVersion(); // Récupère la version du Serveur

    // Variable récupérant la 'class' "networkManager"
    Class<?> networkManagerClass = NMSUtils.getMinecraftClass("network.NetworkManager"); // Initialise la 'class' en question

      /** // Si la version du Serveur est inférieure ou égale à la version "1.18.1", on récupère la bonne class "k" du NMS pour récupérer la 'gestion de channel'
    if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_18_1)) channel = networkManagerClass.getField("k");
    else channel = networkManagerClass.getField("m"); // Sinon, on récupère la bonne class "m" du NMS pour récupérer la 'gestion de channel' **/

    //On récupère la class "m" pour récupérer la 'gestion de channel'
    channel = networkManagerClass.getField("m");
    channel.setAccessible(true); // Définit cette class accessible

    // Si la version du Serveur est inférieure ou égale à la version "1.18.2", on récupère la bonne variable "a" du NMS pour récupérer la 'gestion de channel'
    /** if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_18_2)) networkManagerField = ServerGamePacketListenerImpl.class.getField("a");
    else networkManagerField = ServerGamePacketListenerImpl.class.getField("b"); // Sinon, on récupère la bonne variable "b" du NMS pour récupérer la 'gestion de channel' **/

    //On récupère la variable "b" pour récupérer la 'gestion de channel'
    networkManagerField = ServerGamePacketListenerImpl.class.getField("h");
  }

                            /* ------------------------------------------------------------------ */


  /**
   * Récupère la {@link Connection Connexion} NMS à partir de la {@link ServerGamePacketListenerImpl connexion de l'Entité du Joueur}.
   *
   * @param player Le Joueur en question
   *
   * @return La {@link Connection Connexion} NMS à partir de la connexion de l'Entité du Joueur
   */
  public static Connection getNetworkManager(Player player) {

    ServerGamePacketListenerImpl playerConnection = NMSCraftPlayer.getPlayerConnection(player); // On récupère la connexion de l'entité du Joueur

    // ⬇️ On essaie de retourner la connexion NMS en question à partir de la connexion de l'entité du Joueur, sinon une exception est envoyée ⬇️ //
    try { return (Connection)networkManagerField.get(playerConnection); }
    catch(Exception e) {

      e.printStackTrace(System.err);
      return null;
    }
    // ⬆️ On essaie de retourner la connexion NMS en question à partir de la connexion de l'entité du Joueur, sinon une exception est envoyée ⬆️ //
  }

  /**
   * On récupère le {@link Channel canal} de la connexion en question.
   *
   * @param networkManager la {@link Connection Connexion} en question
   *
   *
   * @return Le {@link Channel canal} de la connexion en question
   */
  public static Channel getChannel(Connection networkManager) {

    try { return (Channel)channel.get(networkManager); }
    catch(Exception exception) { return null; }
  }
}
