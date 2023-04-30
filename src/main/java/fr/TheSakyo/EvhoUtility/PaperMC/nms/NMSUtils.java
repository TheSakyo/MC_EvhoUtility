package fr.TheSakyo.EvhoUtility.PaperMC.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit.NMSCraftPlayer;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit.NMSCraftServer;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit.NMSCraftWorld;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit.NMSCraftScoreboard;
import org.bukkit.Bukkit;

/***********************************************************************/
/* CETTE 'CLASS' PERMET DE RÉCUPÉRER DIFFÉRENTES 'CLASS' NMS CUSTOMISÉ */
/***********************************************************************/

public class NMSUtils {

  private static String version; // Variable récupérant la version NMS en question

  /**
   * Chargement des différentes 'class' du NMS customisé
   *
   */
  public static void load() {

    version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]; // Ajoute la version du NMS en question

    // ⬇️ On essaie de charger les différentes 'class' du NMS customisé, sinon, on affiche une erreur disant que la version du NMS n'est pas prise en charge ⬇️ //
    try {

      loadNMS(NMSCraftPlayer.class);
      loadNMS(NMSCraftWorld.class);
      loadNMS(NMSCraftServer.class);
      loadNMS(NMSCraftScoreboard.class);

    } catch(Exception e) { throw new IllegalStateException("Cette version du NMS (" + version + ") n'est pas pris en charge."); }
    // ⬆️ On essaie de charger les différentes 'class' du NMS customisé, sinon, on affiche une erreur disant que la version du NMS n'est pas prise en charge ⬆️ //

  }

                                      /* ---------------------------------------- */

  /**
   * Chargement d'une 'class' précise 'class' du NMS customisé
   *
   * @param c La 'class' en question a chargé
   *
   */
  public static void loadNMS(Class<?> c) {

    try {

      Method method = c.getDeclaredMethod("load");
      method.setAccessible(true);
      method.invoke(null);

    } catch(Exception e) {

      e.printStackTrace(System.err);
      throw new IllegalStateException("Erreur de chargement du NMS " + c.getName());
    }
  }

  /**
   * Récupère la 'class' NMS de "CraftBukkit" ('org.bukkit.craftbukkit')
   *
   */
  public static Class<?> getCraftBukkitClass(String nmsClassString) throws ClassNotFoundException { return getNMSClass("org.bukkit.craftbukkit", nmsClassString); }

  /**
   * Récupère la 'class' NMS de "Minecraft" ('net.minecraft')
   *
   */
  public static Class<?> getMinecraftClass(String nmsClassString) throws ClassNotFoundException { return Class.forName("net.minecraft." + nmsClassString); }


  /**
   * Récupère une 'class' NMS demandée
   *
   * @param nmsClassString La 'class' en question a récupéré
   *
   */
  public static Class<?> getClass(String nmsClassString) throws ClassNotFoundException { return Class.forName(nmsClassString); }


  /**
   * Récupère une 'class' NMS demandée avec ayant un préfix précis
   *
   * @param prefix L'Emplacement préfix en question de la 'class' a récupéré
   * @param nmsClassString La 'class' en question a récupéré
   *
   */
  public static Class<?>  getNMSClass(String prefix, String nmsClassString) throws ClassNotFoundException {

    String name = prefix + "." + version + "." + nmsClassString;
    return Class.forName(name);
  }

                        /* ------------------------------------------------------- */

  /**
   * Récupère la version du NMS en question
   *
   */
  public static String getVersion() { return version; }

  /**
   * Récupère un variable précis dans une 'class' précise du 'NMS'
   *
   * @param instance La 'class' NMS dans laquelle vérifiée la variable.
   * @param name Le nom de la variable qu'il faut récupérer.
   *
   */
  public static Object getValue(Object instance, String name) { return getValue(instance, name, false); }

  /**
   * Récupère une variable précise dans une 'class' précise du 'NMS'
   *
   * @param instance La 'class' NMS dans laquelle vérifiée la variable
   * @param name Le nom de la variable qu'il faut récupérer
   * @param printError Si une erreur est survenue, veut-on l'afficher dans la console ?
   *
   */
  public static Object getValue(Object instance, String name, boolean printError) {

    Object result = null; // Variable permettant par la suite retourner le résultat en question

    // ⬇️ On essaie de récupérer une variable en question à partir d'une 'class' du NMS en question, si une erreur survient, on affiche l'erreur, si cela a été demandé ⬇️ //
    try {

      Field field = instance.getClass().getDeclaredField(name); // Récupère la variable en question sur la 'class' du NMS en question
      field.setAccessible(true); // On autorise l'accessibilité à cette variable
      result = field.get(instance); // On récupère la valeur de cette variable et la définit dans la variable de résultat initialisé plus haut
      field.setAccessible(false); // On enlève l'accessibilité à cette variable

    } catch(Exception e) { if(printError) e.printStackTrace(System.err); /* Une erreur s'affiche, si cela a été demandé */ }
    // ⬆️ On essaie de récupérer une variable en question à partir d'une 'class' du NMS en question, si une erreur survient, on affiche l'erreur, si cela a été demandé ⬆️ //

    return result; // Affiche le résultat en question
  }

  /**
   * Définit une valeur précise pour une valeur précise dans une 'class' précise du 'NMS'
   *
   * @param instance La 'class' NMS dans laquelle vérifiée la variable
   * @param name Le nom de la variable dans laquelle changé sa valeur
   * @param value La valeur à définir dans la variable a récupéré
   *
   */
  public static void setValue(Object instance, String name, Object value) { setValue(instance, name, value, false); }


  /**
   * Définit une valeur précise pour une valeur précise dans une 'class' précise du 'NMS'
   *
   * @param instance La 'class' NMS dans laquelle vérifiée la variable
   * @param name Le nom de la variable dans laquelle changé sa valeur
   * @param value La valeur à définir dans la variable a récupéré
   * @param printError Si une erreur est survenue, veut-on l'afficher dans la console ?
   *
   */
  public static void setValue(Object instance, String name, Object value, boolean printError) {

    // ⬇️ On essaie de modifier une variable en question à partir d'une 'class' du NMS en question, si une erreur survient, on affiche l'erreur, si cela a été demandé ⬇️ //
    try {

      Field field = instance.getClass().getDeclaredField(name); // Récupère la variable en question sur la 'class' du NMS en question
      field.setAccessible(true); // On autorise l'accessibilité à cette variable
      field.set(instance, value); // On modifie la variable en question

    } catch(Exception e) { if(printError) e.printStackTrace(System.err); /* Une erreur s'affiche, si cela a été demandé */ }
    // ⬇️ On essaie de modifier une variable en question à partir d'une 'class' du NMS en question, si une erreur survient, on affiche l'erreur, si cela a été demandé ⬇️ //

  }
}
/***********************************************************************/
/* CETTE 'CLASS' PERMET DE RÉCUPÉRER DIFFÉRENTES 'CLASS' NMS CUSTOMISÉ */
/***********************************************************************/
