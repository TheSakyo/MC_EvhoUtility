package fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms;

import java.lang.reflect.Method;
import java.util.UUID;

import fr.TheSakyo.EvhoUtility.PaperMC.PaperPlugin;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import fr.TheSakyo.EvhoUtility.ServerVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;

public class NMSEntity {

  private static Method setLocation; // Variable récupérant la méthode qui définira la localisation NMS de l'Entité
  private static Method setYRot; // Variable récupérant la méthode qui définira la rotation NMS de la coordonnée 'Y' de l'Entité
  private static Method setXRot; // Variable récupérant la méthode qui définira la rotation NMS de la coordonnée 'X' de l'Entité
  private static Method getUUID;  // Variable récupérant la méthode qui récupérera l'UUID NMS de l'Entité
  private static Method getID; // Variable récupérant la méthode qui récupérera l'Identifiant NMS de l'Entité
  private static Method getCustomName; // Variable récupérant la méthode qui récupérera le Nom Customisé NMS de l'Entité
  private static Method setCustomName; // Variable récupérant la méthode qui définira le Nom Customisé NMS de l'Entité
  private static Method setCustomNameVisible; // Variable récupérant la méthode qui définira si le Nom Customisé NMS de l'Entité est visible ou non
  private static Method setNoGravity; // Variable récupérant la méthode qui définira la gravité de l'Entité ou non
  private static Method getDataWatcher;  // Variable récupérant la méthode qui récupérera les métadonnées de l'Entité
  private static Method setPose; // Variable récupérant la méthode qui définira la posture de l'Entité
  private static Method setGlowingTag; // Variable récupérant la méthode qui définira la surbrillance de l'Entité


                            /* ------------------------------------------------------------------ */

  /**
   * Charge la 'class' du NMS "entity", récupérant diverses informations utiles
   *
   */
  public static void load() throws ClassNotFoundException, NoSuchMethodException {

    ServerVersion serverVersion = PaperPlugin.getServerVersion(); // Récupère la version du Serveur

    // Variable récupérant la 'class' "entity"
    Class<?> entityClass = NMSUtils.getMinecraftClass("world.entity.Entity"); // Initialise la 'class' en question

      /** // ⬇️ Récupère les bonnes méthodes du NMS à initialiser en fonction de la version actuel du Serveur ⬇️ //
    if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_17_1)) {

      setLocation = entityClass.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
      setYRot = entityClass.getMethod("setYRot", float.class);
      setXRot = entityClass.getMethod("setXRot", float.class);
      getID = entityClass.getMethod("getId");
      getUUID = entityClass.getMethod("getUniqueID");
      getCustomName = entityClass.getMethod("getCustomName");
      setCustomName = entityClass.getMethod("setCustomName", Component.class);
      setCustomNameVisible = entityClass.getMethod("setCustomNameVisible", boolean.class);
      setNoGravity = entityClass.getMethod("setNoGravity", boolean.class);
      getDataWatcher = entityClass.getMethod("getDataWatcher");
      setPose = entityClass.getMethod("setPose", Pose.class);
      setGlowingTag = entityClass.getMethod("setGlowingTag", boolean.class);

    } else {

      setLocation = entityClass.getMethod("a", double.class, double.class, double.class, float.class, float.class);
      setYRot = entityClass.getMethod("o", float.class);
      setXRot = entityClass.getMethod("p", float.class);

      if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_19_2)) getID = entityClass.getMethod("ae");
      else getID = entityClass.getMethod("ah");

      getUUID = entityClass.getMethod("cs");
      getCustomName = entityClass.getMethod("Z");

      if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_18_2)) setCustomName = entityClass.getMethod("a", Component.class);
      else setCustomName = entityClass.getMethod("b", Component.class);

      setCustomNameVisible = entityClass.getMethod("n", boolean.class);
      setNoGravity = entityClass.getMethod("e", boolean.class);

      if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_19_2)) getDataWatcher = entityClass.getMethod("ai");
      else getDataWatcher = entityClass.getMethod("al");

      setPose = entityClass.getMethod("b", Pose.class);
      setGlowingTag = entityClass.getMethod("i", boolean.class);
    }
    // ⬆️ Récupère les bonnes méthodes du NMS à initialiser en fonction de la version actuel du Serveur ⬆️ // **/

   // ⬇️ Récupère les méthodes du NMS à initialiser ⬇️ //
    setLocation = entityClass.getMethod("a", double.class, double.class, double.class, float.class, float.class);
    setYRot = entityClass.getMethod("a_", float.class);
    setXRot = entityClass.getMethod("b_", float.class);

    getID = entityClass.getMethod("af");

    getUUID = entityClass.getMethod("ct");
    getCustomName = entityClass.getMethod("ab");

    setCustomName = entityClass.getMethod("b", Component.class);

    setCustomNameVisible = entityClass.getMethod("n", boolean.class);
    setNoGravity = entityClass.getMethod("e", boolean.class);

    getDataWatcher = entityClass.getMethod("aj");

    setPose = entityClass.getMethod("b", Pose.class);
    setGlowingTag = entityClass.getMethod("i", boolean.class);
    // ⬇️ Récupère les méthodes du NMS à initialiser ⬇️ //

  }


                            /* ------------------------------------------------------------------ */

  /**
   * Définit la localisation d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   * @param x La Coordonnée 'X' où téléporter l'{@link Entity Entité}
   * @param y La Coordonnée 'Y' où téléporter l'{@link Entity Entité}
   * @param z La Coordonnée 'Z' où téléporter l'{@link Entity Entité}
   * @param yaw La Rotation 'yaw' de l'{@link Entity Entité}
   * @param pitch La Rotation 'pitch' de l'{@link Entity Entité}
   */
  public static void setLocation(Entity entity, double x, double y, double z, float yaw, float pitch) {

    try { setLocation.invoke(entity, x, y, z, yaw, pitch); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

  /**
   * Définit la rotation de la coordonnée 'Y' d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   * @param y La Rotation de la coordonnée 'Y' de l'{@link Entity Entité}
   */
  public static void setYRot(Entity entity, float y) {

    try { setYRot.invoke(entity, y); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

  /**
   * Définit la rotation de la coordonnée 'X' d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   * @param x La Rotation de la coordonnée 'X' de l'{@link Entity Entité}
   */
  public static void setXRot(Entity entity, float x) {

    try { setXRot.invoke(entity, x); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

  /**
   * Définit la {@link Pose Posture} d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   * @param pose La {@link Pose Posture} en question
   */
  public static void setPose(Entity entity, Pose pose) {

    try { setPose.invoke(entity, pose); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

  /**
   * Définit le Nom Customisé d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   * @param name Le Nom Customisé en question
   */
  public static void setCustomName(Entity entity, String name) {

    try { setCustomName.invoke(entity, Component.nullToEmpty(name)); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

  /**
   * Définit si le Nom Customisé d'une {@link Entity Entité} spécifiée sera visible ou non.
   *
   * @param entity L'{@link Entity Entité} en question
   * @param b Le Nom Customisé sera-t-il visible ?
   */
  public static void setCustomNameVisible(Entity entity, boolean b) {

    try { setCustomNameVisible.invoke(entity, b); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

  /**
   * Définit si on doit désactiver la gravité d'une {@link Entity Entité} spécifiée ou non.
   *
   * @param entity L'{@link Entity Entité} en question
   * @param b Doit-on désactiver la gravité de l'{@link Entity Entité} ?
   */
  public static void setNoGravity(Entity entity, boolean b) {

    try { setNoGravity.invoke(entity, b); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

  /**
   * Définit si on doit faire surbriller une {@link Entity Entité} spécifiée ou non.
   *
   * @param entity L'{@link Entity Entité} en question
   * @param b Doit-on faire surbriller l'{@link Entity Entité} ?
   */
  public static void setGlowingTag(Entity entity, boolean b) {

    try { setGlowingTag.invoke(entity, b); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

                            /* ------------------------------------------------------------------ */


  /**
   * Récupère l'Identifiant d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   *
   * @return L'Identifiant de l'{@link Entity Entité} demandé.
   */
  public static Integer getEntityID(Entity entity) {

    try { return (Integer) getID.invoke(entity, new Object[0]); }
    catch(Exception e) {

      e.printStackTrace(System.err);
      return null;
    }
  }

  /**
   * Récupère l'UUID d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   *
   * @return L'UUID de l'{@link Entity Entité} demandé.
   */
  public static UUID getEntityUUID(Entity entity) {

    try { return (UUID)getUUID.invoke(entity, new Object[0]); }
    catch(Exception e) {

      e.printStackTrace(System.err);
      return null;
    }
  }

  /**
   * Récupère les métadonnées d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   *
   * @return Les métadonnées de l'{@link Entity Entité} demandé.
   */
  public static SynchedEntityData getSynchedEntityData(Entity entity) {

    try { return(SynchedEntityData)getDataWatcher.invoke(entity); }
    catch(Exception e) {

      e.printStackTrace(System.err);
      return null;
    }
  }

  /**
   * Récupère le Nom Customisé d'une {@link Entity Entité} spécifiée.
   *
   * @param entity L'{@link Entity Entité} en question
   *
   * @return Le Nom Customisé de l'{@link Entity Entité} demandé.
   */
  public static Component getCustomName(Entity entity) {

    try { return (Component)getCustomName.invoke(entity, new Object[0]); }
    catch(Exception e) {

      e.printStackTrace(System.err);
      return null;
    }
  }
}
