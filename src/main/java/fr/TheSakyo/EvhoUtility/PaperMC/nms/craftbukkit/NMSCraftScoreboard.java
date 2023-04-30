package fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit;

import java.lang.reflect.Method;

import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.entity.Player;

public class NMSCraftScoreboard {

  private static Class<?> craftScoreBoardClass; // Variable récupérant la 'class' "CraftScoreboard"
  private static Method getHandle; // Variable récupérant la méthode "getHandle()" de la 'class'

                              /* ------------------------------------------------------------------ */

  /**
   * Charge la 'class' du NMS "CraftScoreboard", récupérant diverses informations utiles
   *
   */
  public static void load() throws ClassNotFoundException, NoSuchMethodException {

    craftScoreBoardClass = NMSUtils.getCraftBukkitClass("scoreboard.CraftScoreboard");
    getHandle = craftScoreBoardClass.getMethod("getHandle");
  }

                              /* ------------------------------------------------------------------ */

  /**
   * Récupère le Scoreboard du Joueur en question
   *
   * @param player Le joueur cible
   *
   */
  public static Scoreboard getScoreboard(Player player) {

    try { return (Scoreboard)getHandle.invoke(craftScoreBoardClass.cast(player.getScoreboard()), new Object[0]); }
    catch (Exception e) {
      e.printStackTrace(System.err);
      return null;
    }
  }
                              /* ------------------------------------------------------------------ */


  /**
   * Récupère la 'class' du NMS "CraftScoreboard"
   *
   */
  public static Class<?> getCraftScoreBoardClass() { return craftScoreBoardClass; }

  /**
   * Récupère la méthode "getHandle()" de la 'class'
   *
   */
  public static Method getCraftScoreBoardGetHandle() { return getHandle; }
}
