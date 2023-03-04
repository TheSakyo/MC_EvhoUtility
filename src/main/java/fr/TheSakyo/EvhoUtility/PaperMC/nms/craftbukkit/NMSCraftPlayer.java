package fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import fr.TheSakyo.EvhoUtility.ServerVersion;
import fr.TheSakyo.EvhoUtility.PaperMC.PaperPlugin;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.entity.Player;

public class NMSCraftPlayer {

  private static Class<?> craftPlayerClass; // Variable récupérant la 'class' "CraftPlayer"
  private static Method craftPlayerGetHandle; // Variable récupérant la méthode "getHandle()" de la 'class'
  private static Field playerConnectionField; // Variable récupérant la variable de connexion du joueur "playerConnection" de la 'class'
  private static Method sendPacketMethod; // Variable récupérant la méthode d'envoi de paquets


                            /* ------------------------------------------------------------------ */


  /**
   * Charge la 'class' du NMS "CraftPlayer", récupérant diverses informations utiles
   *
   */
  protected static void load() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {

    craftPlayerClass = NMSUtils.getCraftBukkitClass("entity.CraftPlayer"); // Initialise la 'class' en question
    craftPlayerGetHandle = craftPlayerClass.getMethod("getHandle"); // Initialise la méthode en question
    playerConnectionField = craftPlayerGetHandle.getReturnType().getField("b"); // Initialise la variable en question

    // ⬇️ Initialise la bonne méthode d'envoi de paquets, en fonction de la version du Serveur ⬇️ //
    if(PaperPlugin.getServerVersion().isOlderThanOrEqual(ServerVersion.VERSION_1_17_1)) { sendPacketMethod = playerConnectionField.getType().getMethod("sendPacket", Packet.class); }
    else { sendPacketMethod = playerConnectionField.getType().getMethod("a", Packet.class); }
    // ⬆️ Initialise la bonne méthode d'envoi de paquets, en fonction de la version du Serveur ⬆️ //

  }

                            /* ------------------------------------------------------------------ */


  /**
   * Récupère la 'class' de connection du Joueur à partir de la 'class' du NMS en question
   *
   * @param player Le Joueur en question
   *
   */
  public static ServerGamePacketListenerImpl getPlayerConnection(Player player) {

    try { return(ServerGamePacketListenerImpl)getPlayerConnectionField().get(getEntityPlayer(player)); }
    catch(Exception e) { throw new IllegalArgumentException("Erreur à NMSCraftPlayer"); }
  }

  /**
   * Récupère la 'class' d'entité du Joueur à partir de la 'class' du NMS en question
   *
   * @param player Le Joueur en question
   *
   */
  public static ServerPlayer getEntityPlayer(Player player) {

    try { return (ServerPlayer)getCraftPlayerGetHandle().invoke(getCraftPlayerClass().cast(player), new Object[0]); }
    catch(Exception e) { throw new IllegalArgumentException("Erreur à NMSCraftPlayer"); }
  }

                            /* ------------------------------------------------------------------ */


  /**
   * Envoit un paquet pour un Joueur
   *
   * @param player Le Joueur en question qui recevra le paquet
   * @param packet Le {@link Packet Paquet} à envoyer
   *
   */
  public static void sendPacket(Player player, Packet packet) { sendPacket(getPlayerConnection(player), packet); }

  /**
   * Envoit un paquet pour un Joueur
   *
   * @param playerConnection La {@link ServerGamePacketListenerImpl Connection du joueur} en question qui recevra le paquet
   * @param packet Le {@link Packet Paquet} à envoyer
   *
   */
  public static void sendPacket(ServerGamePacketListenerImpl playerConnection, Packet packet) {

    try { sendPacketMethod.invoke(playerConnection, packet); }
    catch(Exception e) { e.printStackTrace(); }
  }


                            /* ------------------------------------------------------------------ */


  /**
   * Récupère la 'class' du NMS "CraftPlayer"
   *
   */
  public static Class<?> getCraftPlayerClass() { return craftPlayerClass; }

  /**
   * Récupère la méthode "getHandle()" de la 'class'
   *
   */
  public static Method getCraftPlayerGetHandle() { return craftPlayerGetHandle; }


  /**
   * Récupère la variable de connexion du joueur "playerConnection" de la 'class'
   *
   */
  public static Field getPlayerConnectionField() { return playerConnectionField; }

   /**
   * Récupère la méthode d'envoi de paquets
   *
   */
  public static Method getSendPacketMethod() { return sendPacketMethod; }
}
