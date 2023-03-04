package fr.TheSakyo.EvhoUtility.PaperMC;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import fr.TheSakyo.EvhoUtility.ServerVersion;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import fr.TheSakyo.EvhoUtility.utils.api.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PaperPlugin extends JavaPlugin {


  public static Plugin plugin; // Variable pour récupérer le plugin actuel

  public static ServerVersion serverVersion; // Variable pour récupérer la version du Serveur

  private int bStatsResourceID; // Variable récupérant l'identifiant de la ressource bStats (pour le fonctionnement des NPCs)

  private Metrics metrics; // Variable récupérant la class 'Metrics'

  private List<ServerVersion> supportedVersions; // Variable afficheant les versions du Serveur supportées


  /*************************/
  /* CONSTRUCTEUR DE CLASS */
  /************************/

  public PaperPlugin(ServerVersion... serverVersion) {

    this.supportedVersions = new ArrayList<>(); // Récupèrera les versions supportées
    this.supportedVersions.addAll(List.of(serverVersion)); // Ajoute toutes les versions supportées sur le Serveur

    PaperPlugin.serverVersion = ServerVersion.getVersion(Bukkit.getServer().getBukkitVersion()); // Définit la version du Serveur

    getLogger().log(Level.INFO, "Votre serveur utilise la version Bukkit : " + Bukkit.getServer().getBukkitVersion()); // Affiche la version du Serveur en question dans la console
  }

  /*************************/
  /* CONSTRUCTEUR DE CLASS */
  /************************/



  /**************************************************/
  /* PARTIE AVEC ACTIVATION/DÉSACTIVATION DU PLUGIN */
  /*************************************************/

  /**
   * Activation du Plugin en question
   */
  public void onEnable() {

    plugin = (Plugin)this; // Récupère le plugin en question

    boolean unsafeByPass = false;
    String minecraftVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];

    // ⬇️ On vérifie si la version du Serveur n'est pas dans les versions supportées, alors on vérifie de force la version Minecraft du Serveur si elle est égale aux versions supportées ⬇️ //
    if(!this.supportedVersions.contains(PaperPlugin.serverVersion)) {

      StringBuilder vs = new StringBuilder();  // On construit une chaîne caractère qui nous sera utile plus tard, pour récupérer les versions supportées

      // Pour toutes les versions supportées par le Serveur, si une des versions, sont bien égales, à la version Minecraft du Serveur, on continue l'activation du plugin //
      for(ServerVersion serverVersion : this.supportedVersions) {

        vs.append(", ").append(serverVersion.getBukkitVersion()); // On ajoute la version à la chaîne de caractère
        if(serverVersion.getMinecraftVersion().equals(minecraftVersion)) unsafeByPass = true; // Si tous est bon, on définit, une variable sur vrai (unsafeByPass = plugin activé possibilités d'erreurs)
      }
      // ⬆️ Pour toutes les versions supportées par le Serveur, si une des versions, sont bien égales, à la version Minecraft du Serveur, on continue l'activation du plugin ⬆️ //

      // ⬇️ Si aucune des versions, ont été trouvées, on envoie un message d'erreur à la console et on annule l'activation du Plugin ⬇️ //
      if(!unsafeByPass) {

        vs = new StringBuilder(vs.toString().replaceFirst(", ", "")); // Supprime la première virgule de la chaîne de caractère construit plus haut
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Cette version du serveur (" + minecraftVersion + ") n'est pas supporté par ce plugin (" + getDescription().getName() + " v" + getDescription().getVersion() + ")");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "versions du serveur supportées : " + vs);
        Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this);
        return;
      }
      // ⬆️ Si aucune des versions, ont été trouvées, on envoie un message d'erreur à la console et on annule l'activation du Plugin ⬆️ //
    }
    // ⬆️ On vérifie si la version du Serveur n'est pas dans les versions supportées, alors on vérifie de force la version Minecraft du Serveur si elle est égale aux versions supportées ⬆️ //

                                                                    /* ----------------------------------------------------------------------- */

    // ⬇️ Si une des versions supportées a pu trouver une versioon Minecraft compatible avec le Serveur, on affiche un Message disant que le plugin s'active bien, mais peut avoir des erreurs ⬇️  //
    if(unsafeByPass) {

      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Cette compilation Bukkit (" + Bukkit.getBukkitVersion() + ") n'est pas supporté, mais la version du serveur Minecraft (" + minecraftVersion + ") est supporté dans une autre compilation.");
      Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Autorisation " + getDescription().getName() + " v" + getDescription().getVersion() + " Quoi qu'il en soit, des erreurs peuvent apparaître.");
    }
    // ⬆️ Si une des versions supportées a pu trouver une versioon Minecraft compatible avec le Serveur, on affiche un Message disant que le plugin s'active bien, mais peut avoir des erreurs ⬆️ //

                                                                        /* ----------------------------------------------------------------------- */

    // ⬇️ On essaie de charger les 'class' du NMS customisée et appele la méthode d'activation dépendant de UtilityMain, sinon, on affiche une erreur sur la console et on désactive le plugin ⬇️ //
    try {

      NMSUtils.load(); // Charge les 'class' NMS customisée
      enable(); // Autre Méthode d'activation du Plugin dépendant de UtilityMain

    } catch(Exception e) {

      Bukkit.getServer().getConsoleSender().sendMessage("Une erreur s'est produite lors de l'activation du plugin " + getDescription().getName() + " v" + getDescription().getVersion());
      e.printStackTrace(); // Affiche le détail de l'erreur dans la console
      Bukkit.getServer().getPluginManager().disablePlugin((Plugin)this); // Désactive le Plugin en question
    }
    // ⬆️ On essaie de charger les 'class' du NMS customisée et appele la méthode d'activation dépendant de UtilityMain, sinon, on affiche une erreur sur la console et on désactive le plugin ⬆️  //

  }

  /**
   * Désactivation du Plugin en question
   */
  public void onDisable() {

    // ⬇️ On essaie d'appeler la méthode de désactivation dépendant de UtilityMain, sinon, on fait rien ⬇️ //
    try { disable(); }
    catch(Exception ignored) {}
    // ⬆️ On essaie d'appeler la méthode de désactivation dépendant de UtilityMain, sinon, on fait rien ⬆️ //
  }

  /**************************************************/
  /* PARTIE AVEC ACTIVATION/DÉSACTIVATION DU PLUGIN */
  /*************************************************/

  /**
   * Permet de paramétrer les réglages {@link Metrics}
   *
   * @param bStatsResourceID Récupère l'Identifiant de la ressource bStats
   */
  public void setupMetrics(int bStatsResourceID) {

    this.bStatsResourceID = bStatsResourceID;
    this.metrics = new Metrics(this, bStatsResourceID);
  }

  /**
   * Récupère les réglages {@link Metrics}
   *
   */
  public Metrics getMetrics() { return this.metrics; }


  /**
   * Récupère la version du Serveur
   *
   */
  public static ServerVersion getServerVersion() { return serverVersion; }

  /**
   * Récupère les versions supportées du Serveur
   *
   */
  public List<ServerVersion> getSupportedVersions() { return this.supportedVersions; }


          /* ---------------------------------------------------------------------------------------------- */
          /* ---------------------------------------------------------------------------------------------- */

  /******************************************************************/
  /* Méthodes lors de l'activation ou de la désactivation du Plugin */
  /******************************************************************/

  public abstract void enable();
  public abstract void disable();

  /******************************************************************/
  /* Méthodes lors de l'activation ou de la désactivation du Plugin */
  /******************************************************************/
}
