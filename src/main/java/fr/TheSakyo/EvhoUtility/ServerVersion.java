package fr.TheSakyo.EvhoUtility;

import fr.TheSakyo.EvhoUtility.PaperMC.PaperPlugin;

import java.util.Arrays;

/**************************************************************************************
/* ÉNUMÉRATION STOCKANT TOUTES LES VERSIONS SUPPORTÉES PAR LE SERVEUR POUR LE PLUGIN */
/*************************************************************************************/

public enum ServerVersion {
  
  VERSION_1_20(1, "1.20-R0.1-SNAPSHOT") {
    @Override
    public String toString() {
      return super.getBukkitVersion(); // Transforme la méthode 'toString()' en la méthode 'getBukkitVersion()'
    }
  },

  VERSION_1_20_1(2, "1.20.1-R0.1-SNAPSHOT") {
    @Override
    public String toString() {
      return super.getBukkitVersion(); // Transforme la méthode 'toString()' en la méthode 'getBukkitVersion()'
    }
  },

  VERSION_1_20_2(3, "1.20.2-R0.1-SNAPSHOT") {
    @Override
    public String toString() {
      return super.getBukkitVersion(); // Transforme la méthode 'toString()' en la méthode 'getBukkitVersion()'
    }
  },

  VERSION_1_20_3(4, "1.20.3-R0.1-SNAPSHOT") {
    @Override
    public String toString() {
      return super.getBukkitVersion(); // Transforme la méthode 'toString()' en la méthode 'getBukkitVersion()'
    }
  },

  VERSION_1_20_4(5, "1.20.4-R0.1-SNAPSHOT") {
    @Override
    public String toString() {
      return super.getBukkitVersion(); // Transforme la méthode 'toString()' en la méthode 'getBukkitVersion()'
    }
  };

                            /* -------------------------------------------------------------- */

  private final String bukkitVersion; // Variable récupérant la version Bukkit de la Version supportée actuelle
  private final int versionOrder; // Variable récupérant l'ordre de la Version supportée actuelle

  /**
   * Constructeur de la 'class' pour récupérer les versions supportées du Serveur pour le Plugin en question
   *
   */
  ServerVersion(Integer versionOrder, String bukkitVersion) {

    this.bukkitVersion = bukkitVersion;
    this.versionOrder = versionOrder;
  }

                            /* -------------------------------------------------------------- */

   /**
   * Vérifie si la version supportée actuelle est plus récente par rapport à une autre version supportée
   *
   * @param version La {@link ServerVersion version supportée} à comparer
   *
   */
  public boolean isNewerThan(ServerVersion version) { return(this.versionOrder > version.versionOrder); }

  /**
   * Vérifie si la version supportée actuelle est plus récente ou égale par rapport à une autre version supportée
   *
   * @param version La {@link ServerVersion version supportée} à comparer
   *
   */
  public boolean isNewerThanOrEqual(ServerVersion version) { return(this.versionOrder >= version.versionOrder); }

   /**
   * Vérifie si la version supportée actuelle est obsolète par rapport à une autre version supportée
   *
   * @param version La {@link ServerVersion version supportée} à comparer
   *
   */
  public boolean isOlderThan(ServerVersion version) { return(this.versionOrder < version.versionOrder); }

   /**
   * Vérifie si la version supportée actuelle est obsolète ou égale par rapport à une autre version supportée
   *
   * @param version La {@link ServerVersion version supportée} à comparer
   *
   */
  public boolean isOlderThanOrEqual(ServerVersion version) { return(this.versionOrder <= version.versionOrder); }


                            /* -------------------------------------------------------------- */

  /**
   * Récupère l'ordre de la version supportée actuelle
   *
   */
  public int getOrder() { return this.versionOrder; }

  /**
   * Récupère la version Bukkit de la version supportée actuelle
   *
   */
  public String getBukkitVersion() { return this.bukkitVersion; }

  /**
   * Récupère la version Minecraft de la version supportée actuelle
   *
   */
  public String getMinecraftVersion() { return this.bukkitVersion.split("-")[0]; }


  /**
   * Récupère une version supportée en précisant la version en chaîne de caractère
   *
   * @param version La version supportée à récupérer
   *
   */
  public static ServerVersion getVersion(String version) { return Arrays.stream(values()).filter(x -> x.getBukkitVersion().equals(version)).findAny().orElse(null); }

   /**
   * Récupère la version en question du Serveur
   *
   */
  public static ServerVersion getServerVersion() { return PaperPlugin.getServerVersion(); }
}
/**************************************************************************************
/* ÉNUMÉRATION STOCKANT TOUTES LES VERSIONS SUPPORTÉES PAR LE SERVEUR POUR LE PLUGIN */
/*************************************************************************************/