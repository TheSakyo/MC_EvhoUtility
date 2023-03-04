package fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit;

import java.lang.reflect.Method;

import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class NMSCraftServer {

  private static Class<?> craftServerClass; // Variable récupérant la 'class' "CraftServer"
  private static Method craftServerGetServer; // Variable récupérant la méthode de récupération du serveur "getServer()" de la 'class'

                            /* ------------------------------------------------------------------ */


  /**
   * Charge la 'class' du NMS "CraftServer", récupérant diverses informations utiles
   *
   */
  protected static void load() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {

    craftServerClass = NMSUtils.getCraftBukkitClass("CraftServer"); // Initialise la 'class' en question
    craftServerGetServer = craftServerClass.getMethod("getServer"); // Initialise la méthode en question
  }


                            /* ------------------------------------------------------------------ */


  /**
   * Récupère la 'class' du NMS "CraftServer"
   *
   */
  public static Class<?> getCraftServerClass() { return craftServerClass; }

  /**
   * Récupère la méthode de récupération du serveur "getServer()" de la 'class'
   *
   */
  public static Method getCraftServerGetServer() { return craftServerGetServer; }

  /**
   * Récupère une instance du Serveur (serveur Minecraft) en fonction du Serveur demandé
   *
   */
  public static MinecraftServer getMinecraftServer() { return getMinecraftServer(Bukkit.getServer()); }

  /**
   * Récupère une instance du Serveur (serveur Minecraft) en fonction du Serveur demandé
   *
   * @param server Une instance du serveur a récupéré
   *
   */
  public static MinecraftServer getMinecraftServer(Server server) {

    try { return(MinecraftServer)getCraftServerGetServer().invoke(getCraftServerClass().cast(server), new Object[0]); }
    catch(Exception e) { throw new IllegalArgumentException("Erreur à NMSCraftServer"); }
  }
}
