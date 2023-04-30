package fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms;

import java.lang.reflect.Method;
import java.util.List;

import fr.TheSakyo.EvhoUtility.PaperMC.PaperPlugin;
import fr.TheSakyo.EvhoUtility.ServerVersion;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

public class NMSEntityData {

  private static Method set; // Variable récupérant la méthode pour définir des métadonnées
  private static Method getMetadataList; // Variable récupérant la méthode de liste des métadonnées


                /* ------------------------------------------------------------------ */

  /**
   * Charge toutes les méthodes du NMS pour travailler avec les métadonnées des entités
   *
   */
  public static void load() throws ClassNotFoundException, NoSuchMethodException {

    ServerVersion serverVersion = PaperPlugin.getServerVersion(); // Récupère la version du Serveur

    // ⬇️ Initialise toutes les méthodes en fonction de la version actuelle du Serveur ⬇️ //
    if(serverVersion.isOlderThan(ServerVersion.VERSION_1_20)) set = SynchedEntityData.class.getMethod("set", EntityDataAccessor.class, Object.class);
    else {

      set = SynchedEntityData.class.getMethod("b", EntityDataAccessor.class, Object.class);
      getMetadataList = SynchedEntityData.class.getMethod("c");
    }
    // ⬆️ Initialise toutes les méthodes en fonction de la version actuelle du Serveur ⬆️ //

  }

                           /* ------------------------------------------------------------------ */

  /**
   * Ajoute des données de métadonnées pour une Entité en question
   *
   * @param synchedEntityData Métadonnées de l'Entité
   * @param entityDataAccessor Type de Métadonnée qui sera ajoutée
   * @param o Données de Métadonnées à ajouter
   *
   * @param <T> Type de Métadonnée qui sera ajoutée
   */
  public static <T> void set(SynchedEntityData synchedEntityData, EntityDataAccessor<T> entityDataAccessor, T o) {

    try { set.invoke(synchedEntityData, entityDataAccessor, o); }
    catch(Exception e) { e.printStackTrace(System.err); }
  }

  /**
   * Récupère la liste des données de métadonnées pour l'Entité en question
   *
   * @param synchedEntityData Métadonnées de l'Entité
   */
  public static List<?> getMetadataList(SynchedEntityData synchedEntityData) {

    try { return (List<?>)getMetadataList.invoke(synchedEntityData, new Object[0]); }
    catch(Exception e) {

      e.printStackTrace(System.err);
      return null;
    }
  }
}
