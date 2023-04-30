package fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit;

import java.lang.reflect.Method;

import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.World;

public class NMSCraftWorld {

  private static Class<?> craftWorldClass; // Variable récupérant la 'class' "CraftWorld"
  private static Method craftWorldGetHandle; // Variable récupérant la méthode "getHandle()" de la 'class'


                            /* ------------------------------------------------------------------ */


  /**
   * Charge la 'class' du NMS "CraftWorld", récupérant diverses informations utiles
   *
   */
  protected static void load() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {

    craftWorldClass = NMSUtils.getCraftBukkitClass("CraftWorld"); // Initialise la 'class' en question
    craftWorldGetHandle = craftWorldClass.getMethod("getHandle"); // Initialise la méthode en question
  }


                            /* ------------------------------------------------------------------ */

  /**
   * Récupère la 'class' du NMS "CraftWorld"
   *
   */
  public static Class<?> getCraftWorldClass() { return craftWorldClass; }

  /**
   * Récupère la méthode "getHandle()" de la 'class'
   *
   */
  public static Method getCraftWorldGetHandle() { return craftWorldGetHandle; }


                            /* ------------------------------------------------------------------ */


  /**
   * Récupère une instance du Monde (monde Minecraft) en fonction du Monde demandé
   *
   * @param world Une instance du monde qu'il faut récupérer.
   *
   */
  public static ServerLevel getWorldServer(World world) {

    try { return(ServerLevel)getCraftWorldGetHandle().invoke(getCraftWorldClass().cast(world), new Object[0]); }
    catch(Exception e) { throw new IllegalArgumentException("Erreur à NMSCraftWorld"); }
  }
}
