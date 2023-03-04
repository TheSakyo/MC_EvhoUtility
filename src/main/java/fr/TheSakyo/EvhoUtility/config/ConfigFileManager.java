package fr.TheSakyo.EvhoUtility.config;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.*;
import java.nio.charset.Charset;

public class ConfigFileManager {

    //Récupère la class "Main" en tant que "static"
    private static UtilityMain mainInstance = UtilityMain.getInstance();


	/************************************************************/
	/* METHODE POUR CHARGER DES FICHIERS CONFIGS PERSONNALISER */
	/***********************************************************/


    /* Méthode Chargement/Création du fichier de configuration 'servername.yml' */
	private static void loadServerNameConfig() {

		/* Chargement/Création du fichier de configuration 'servername.yml' si le serveur fonctionne sous "BungeeCord" */
		if(mainInstance.hasBungee() == true) {

			if(Bukkit.getServer().getOnlinePlayers().size() == 0) {

				if(ConfigFile.getString(mainInstance.servernameconfig, "server_name") == null) {

				   ConfigFile.set(mainInstance.servernameconfig, "server_name", "Example");
				   ConfigFile.saveConfig(mainInstance.servernameconfig);

				} else { ConfigFile.reloadConfig(mainInstance.servernameconfig); }
			}
		}
        /* Chargement/Création du fichier de configuration 'servername.yml' si le serveur fonctionne sous "BungeeCord" */
	}
    /* Méthode Chargement/Création du fichier de configuration 'servername.yml' */

                             /* ------------------------- */

	// En-Tête du fichier de configuration "servername.yml" //
	public static String[] headerServerName = {
	   "| ===== EvhoUtility Outils ===== |",
	   " ",
	   "*** NOM DU SERVEUR ***",
	   " ",
	   "Il vaut mieux ne pas toucher à ce fichier.",
	   " ",
	   "par TheSakyo",
	   " "
	};
	// En-Tête du fichier de configuration "servername.yml" //

                /* ----------------------------------------------- */

    // ⬇️ *** CHARGEMENT DES FICHIERS DE CONFIGURATIONS D'EVHOUILITY *** ⬇️ //
	public static void LoadUtilityConfig() {

		String[] headerAdvancements = {
		   "| ===== EvhoUtility Outils ===== |",
		   " ",
		   "*** SAUVEGARDE DES ACHIEVEMENTS ***",
		   " ",
		   "Il vaut mieux ne pas toucher à ce fichier.",
		   " ",
		   "par TheSakyo",
		   " "
		};

		// En-Tête des différents fichiers de configuration //
		String[] headerchat = {
		   "| ===== EvhoUtility Configuration ===== |",
		   " ",
		   "*** FORMAT DE CHAT ***",
		   " ",
		   "Variables de format éxistants :",
		   "%prefix% et %player%",
		   " ",
		   "par TheSakyo",
		   " "

		};

		String[] headerHologram = {
		   "| ===== EvhoUtility Outils ===== |",
		   " ",
		   "*** SAUVEGARDE DES HOLOGRAMMES ***",
		   " ",
		   "Il vaut mieux ne pas toucher à ce fichier.",
		   " ",
		   "par TheSakyo",
		   " "
		};


        String[] headerNPC = {
		   "| ===== EvhoUtility Outils ===== |",
		   " ",
		   "*** SAUVEGARDE DES 'NPC' ***",
		   " ",
		   "Il vaut mieux ne pas toucher à ce fichier.",
		   " ",
		   "par TheSakyo",
		   " "
		};

		String[] headerTablist = {
		   "| ===== EvhoUtility Configuration ===== |",
		   " ",
		   "*** FORMAT DU TABLIST ***",
		   " ",
		   "~~ TabList Animé ~~",
		   " ",
		   "ColorFooterSection :",
		   "(Placez des Codes Couleurs Minecraft à ce niveau)",
		   " ",
		   "par TheSakyo",
		   " "
		};


		String[] headerDeathOrAchievementMessage = {
		   "| ===== EvhoUtility Outils ===== |",
		   " ",
		   "*** ACTIVATION/DESACTIVATION ***",
		   "*** MESSAGE DE MORT OU ACHIEVEMENT ***",
		   " ",
		   "Il vaut mieux ne pas toucher à ce fichier.",
		   " ",
		   "par TheSakyo",
		   " "
		};


        String[] headerZone = {
		   "| ===== EvhoUtility Outils ===== |",
		   " ",
		   "*** Liste des Zones du Serveur ***",
		   " ",
		   "Il vaut mieux ne pas toucher à ce fichier.",
		   " ",
		   "par TheSakyo",
		   " "
		};

        String[] headerplayerSkin = {
		   "| ===== EvhoUtility Outils ===== |",
		   " ",
		   "*** Liste des Skins équipé par les Joueurs ***",
		   " ",
		   "Il vaut mieux ne pas toucher à ce fichier.",
		   " ",
		   "par TheSakyo",
		   " "
		};


        // En-Tête des différents fichiers de configuration //

		/* Chargement/Création du fichier de configuration 'advancements.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/advancements/advancements.yml").exists()) {

		   mainInstance.advconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/advancements/advancements.yml", headerAdvancements);
		   ConfigFile.reloadConfig(mainInstance.advconfig);

		} else {

		  mainInstance.advconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/advancements/advancements.yml", headerAdvancements);
		  ConfigFile.saveConfig(mainInstance.advconfig);
		}
		/* Chargement/Création du fichier de configuration 'advancements.yml' */


		/* Chargement/Création du fichier de configuration 'chatformat.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/config/chatformat.yml").exists()) {

			mainInstance.chatconfig = getNewConfig(mainInstance.getDataFolder(), "/config/chatformat.yml", headerchat);
			ConfigFile.reloadConfig(mainInstance.advconfig);

		} else {

			mainInstance.chatconfig = getNewConfig(mainInstance.getDataFolder(), "/config/chatformat.yml", headerchat);
			ConfigFile.set(mainInstance.advconfig, "chat_format", "%prefix% %player% &8&l:");
			ConfigFile.saveConfig(mainInstance.advconfig);

		}
		/* Chargement/Création du fichier de configuration 'chatformat.yml' */


		/*Chargement/Création du fichier de configuration 'holograms.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/holograms.yml").exists()) {

		   mainInstance.holoconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/holograms.yml", headerHologram);
		   ConfigFile.reloadConfig(mainInstance.holoconfig);

		} else {

		  mainInstance.holoconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/holograms.yml", headerHologram);
		  ConfigFile.createSection(mainInstance.holoconfig, "Holograms");
		  ConfigFile.saveConfig(mainInstance.holoconfig);
		}
		/* Chargement/Création du fichier de configuration 'holograms.yml' */

        /*Chargement/Création du fichier de configuration 'npc.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/NPC.yml").exists()) {

		   mainInstance.NPCconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/NPC.yml", headerNPC);

                                    /* ---------------------------------- */

                boolean isEmpty = false;

                File NPC_BACKUP = new File(mainInstance.getDataFolder(), "/utils/NPC_BACKUP.yml");
                File NPC = getConfigFile(mainInstance.getDataFolder(), "/utils/NPC.yml");

                                        /* ----------------------- */

                try {

                    String currentLine;

                    StringBuilder whole = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new FileReader(NPC));

                    while((currentLine = reader.readLine()) != null) {

                        if(currentLine.startsWith("#") || currentLine.startsWith("NPC")) isEmpty = true;
                        else isEmpty = false; break;
                    }

                    if(isEmpty == false) FileUtils.copyFile(NPC, NPC_BACKUP);

                } catch(IOException ignored) {}

                                    /* ---------------------------------- */

		   ConfigFile.reloadConfig(mainInstance.NPCconfig);

		} else {

		  mainInstance.NPCconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/NPC.yml", headerNPC);
		  ConfigFile.createSection(mainInstance.NPCconfig, "NPC");
		  ConfigFile.saveConfig(mainInstance.NPCconfig);
		}
		/* Chargement/Création du fichier de configuration 'holograms.yml' *


		/* Chargement/Création du fichier de configuration 'tablist.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/config/tablist.yml").exists()) {

		   mainInstance.tabconfig = getNewConfig(mainInstance.getDataFolder(), "/config/tablist.yml", headerTablist);
		   ConfigFile.reloadConfig(mainInstance.tabconfig);

		} else {

		  mainInstance.tabconfig = getNewConfig(mainInstance.getDataFolder(), "/config/tablist.yml", headerTablist);
		  ConfigFile.set(mainInstance.tabconfig, "title1", "&aExemple");
		  ConfigFile.set(mainInstance.tabconfig, "title2", "&eExemple");
		  ConfigFile.set(mainInstance.tabconfig, "title3", "&cExemple");
		  ConfigFile.set(mainInstance.tabconfig, "website", "&3&oExemple.fr");
		  ConfigFile.set(mainInstance.tabconfig, "ip", "&3&oPlay.Exemple.fr");
		  ConfigFile.set(mainInstance.tabconfig, "ColorFooterSection", "&6");
		  ConfigFile.saveConfig(mainInstance.tabconfig);
		}
		/* Chargement/Création du fichier de configuration 'tablist.yml' */


		/* Chargement/Création du fichier de configuration 'DeathOrAchievement.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/DeathOrAchievement.yml").exists()) {

		   mainInstance.DeathOrAchievementconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/DeathOrAchievement.yml", headerDeathOrAchievementMessage);
		   ConfigFile.reloadConfig(mainInstance.DeathOrAchievementconfig);

		} else {

		  mainInstance.DeathOrAchievementconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/DeathOrAchievement.yml", headerDeathOrAchievementMessage);
		  ConfigFile.set(mainInstance.DeathOrAchievementconfig, "DeathMessage", "on");
		  ConfigFile.set(mainInstance.DeathOrAchievementconfig, "AchievementMessage", "on");
		  ConfigFile.saveConfig(mainInstance.DeathOrAchievementconfig);
		}
		/* Chargement/Création du fichier de configuration 'DeathOrAchievement.yml' */


		/* Chargement/Création du fichier de configuration 'zone.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/zone.yml").exists()) {

		   mainInstance.zoneconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/zone.yml", headerZone);
		   ConfigFile.reloadConfig(mainInstance.zoneconfig);

		} else {

		  mainInstance.zoneconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/zone.yml", headerZone);
		  ConfigFile.createSection(mainInstance.zoneconfig, "ZONE");
		  ConfigFile.saveConfig(mainInstance.zoneconfig);
		}
		/* Chargement/Création du fichier de configuration 'zone.yml' */


		/* Chargement/Création du fichier de configuration 'playerSkin.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/playerSkin.yml").exists()) {

		   mainInstance.playerSkinconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/playerSkin.yml", headerplayerSkin);
		   ConfigFile.reloadConfig(mainInstance.playerSkinconfig);

		} else {

		  mainInstance.playerSkinconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/playerSkin.yml", headerplayerSkin);
		  ConfigFile.createSection(mainInstance.playerSkinconfig, "SKIN");
		  ConfigFile.saveConfig(mainInstance.playerSkinconfig);
		}
		/* Chargement/Création du fichier de configuration 'zone.yml' */


		/* Appel de la méthode "loadServerNameConfig()" */

		loadServerNameConfig();

		/* Appel de la méthode "loadServerNameConfig()" */

	}
    // ⬆️ *** CHARGEMENT DES FICHIERS DE CONFIGURATIONS D'EVHOUILITY *** ⬆️ //

                /* ----------------------------------------------- */

    /* Méthode Chargement/Création du fichier de configuration 'world.yml' */
    public static void loadUtilityWorldConfig() {

        String[] headerworld = {
                "| ===== EvhoUtility Outils ===== |",
                " ",
                "*** LISTES DES MONDES DU SERVEUR ***",
                " ",
                "Il vaut mieux ne pas toucher à ce fichier.",
                " ",
                "par TheSakyo",
                " "
        };

        /* Chargement/Création du fichier de configuration 'world.yml' */
        if(getConfigFile(mainInstance.getDataFolder(), "/utils/world.yml").exists()) {

            //Code couleur utile pour des informations au niveau de la console//
            String GI = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString();
            String YI = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString();
            //Code couleur utile pour des informations au niveau de la console//

            mainInstance.console.sendMessage(mainInstance.prefix + GI + "Actualisation des mondes...");

            mainInstance.worldconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/world.yml", headerworld);
            ConfigFile.reloadConfig(mainInstance.worldconfig);

            for(String name: ConfigFile.getConfigurationSection(mainInstance.worldconfig, "serverworlds").getKeys(false)) {

                File file = new File(name);

                if(file.exists()) {

                    if(Bukkit.getServer().getWorld(name) == null) {

                        // Recharge le monde souhaité
                        new WorldHandler(mainInstance, name);

                    } else { mainInstance.console.sendMessage(mainInstance.prefix + YI + name + ChatColor.RESET + " : " + ChatColor.GREEN + "OK"); }
                }
            }

            mainInstance.console.sendMessage(mainInstance.prefix + ChatColor.DARK_GREEN + "Les mondes ont été recharger !");

        } else {

            mainInstance.worldconfig = getNewConfig(mainInstance.getDataFolder(), "/utils/world.yml", headerworld);
            ConfigFile.saveConfig(mainInstance.worldconfig);
        }
        /* Chargement/Création du fichier de configuration 'world.yml' */
    }
    /* Méthode Chargement/Création du fichier de configuration 'world.yml' */

                             /* ------------------------- */

    /* Petite Méthode pour vider le config 'world.yml' */
    public static void clearKeyUtilityWorldConfig() {

        for(String str : ConfigFile.getKeys(mainInstance.worldconfig)) {  ConfigFile.removeKey(mainInstance.worldconfig, str); }
        ConfigFile.saveConfig(mainInstance.worldconfig);
    }
    /* Petite Méthode pour vider le config 'world.yml' */

	/************************************************************/
	/* METHODE POUR CHARGER DES FICHIERS CONFIGS PERSONNALISER */
	/***********************************************************/


    /* ------------------------------------------------------------------------------------------------------------------------------------- */

 
    /*
    * Obtient une nouvelle configuration avec l'en-tête
    * @param filePath - Chemin d'accés au fichier
    * @return - 'New ConfigFile'
    */
    public static ConfigFile getNewConfig(File DataFolder, String filePath, String[] header) {
 
        File file = getConfigFile(DataFolder, filePath);
 
        if(!file.exists()) {

            prepareFile(DataFolder, filePath);
 
            if(header != null && header.length != 0) setHeader(file, header);
        }
 
        ConfigFile config = new ConfigFile(getConfigContent(DataFolder, filePath), file, getCommentsNum(file), UtilityMain.getInstance());
        return config;
 
    }
 
    /*
    * Obtient une nouvelle configuration
    * @param filePath - Chemin d'accés au fichier
    * @return - 'New ConfigFile'
    */
    public static ConfigFile getNewConfig(File DataFolder, String filePath) { return getNewConfig(DataFolder, filePath, null); }
 
    /*
    * Obtient le fichier de configuration à partir de la chaîne
    * @param file - Chemin d'accés au fichier
    * @return - 'New file object'
    */
    public static File getConfigFile(File DataFolder, String file) {
 
        if(file.isEmpty() || file == null) return null;

        File configFile;
 
        if(file.contains("/")) {
 
            if(file.startsWith("/")) configFile = new File(DataFolder + file.replace("/", File.separator));
            else configFile = new File(DataFolder + File.separator + file.replace("/", File.separator));

        } else { configFile = new File(DataFolder, file); }

        return configFile;
    }
    
    
    /*
    * Lit le fichier et place les commentaires SnakeYAML
    * @param filePath - Chemin d'accés au fichier
    * @return - Fichier en tant que flux d'entrée
    */
    public static Reader getConfigContent(File file) {
 
        if(!file.exists()) return null;
 
        try {

            int commentNum = 0;
 
            String addLine;
            String currentLine;
            String pluginName = getPluginName();
 
            StringBuilder whole = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
 
            while((currentLine = reader.readLine()) != null) {
 
                if(currentLine.startsWith("#")) {
                    addLine = currentLine.replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
                    whole.append(addLine + "\n");
                    commentNum++;
 
                } else whole.append(currentLine + "\n");
            }
            
            Reader configStream = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF8"));
 
            reader.close();
            return configStream;
 
        } catch(IOException e) { e.printStackTrace(); return null; }
 
    }
    
    /*
    * Obtient le contenu de la configuration à partir du fichier
    * @param filePath - Chemin d'accés au fichier
    * @return - fichier prêt
    */
    public static Reader getConfigContent(File DataFolder, String filePath) { return getConfigContent(getConfigFile(DataFolder, filePath)); }
 
    /*
    * Créé un nouveau fichier pour la configuration et y copier la ressource
    * @param file - Chemin d'accés au fichier
    * @param resource - Ressource à copier
    */
    public static void prepareFile(File DataFolder, String filePath, String resource) {
 
        File file = getConfigFile(DataFolder, filePath);
 
        if(file.exists()) { return; }
 
        try {

            file.getParentFile().mkdirs();
            file.createNewFile();
 
            if(resource != null && !resource.isEmpty()) { copyResource(UtilityMain.getInstance().getResource(resource), file); }
 
        } catch(IOException e) { e.printStackTrace(); }
 
    }
 
    /*
    * Créé un nouveau fichier pour la configuration sans ressource
    * @param file - Fichier à créer
    */
    public static void prepareFile(File DataFolder, String filePath) { prepareFile(DataFolder, filePath, null); }
 
    /*
    * Ajoute un bloc d'en-tête à la configuration
    * @param file - Fichier Config
    * @param header - Lignes d'en-téte
    */
    public static void setHeader(File file, String[] header) {
 
        if(!file.exists()) { return; }
 
        try {

            String currentLine;
            StringBuilder config = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
 
            while((currentLine = reader.readLine()) != null) { config.append(currentLine + "\n"); }
 
            reader.close();
            config.append("# +----------------------------------------------------+ #\n");
 
            for(String line : header) {
 
                if(line.length() > 50) { continue; }
 
                int lenght = (50 - line.length()) / 2;
                StringBuilder finalLine = new StringBuilder(line);
 
                for(int i = 0; i < lenght; i++) {

                    finalLine.append(" ");
                    finalLine.reverse();
                    finalLine.append(" ");
                    finalLine.reverse();
                }
 
                if(line.length() % 2 != 0) { finalLine.append(" "); }
 
                config.append("# < " + finalLine.toString() + " > #\n");
 
        }
 
        config.append("# +----------------------------------------------------+ #");
 
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(prepareConfigString(config.toString()));
        writer.flush();
        writer.close();
 
        } catch(IOException e) { e.printStackTrace(); }
 
    }
 
    /*
    * Obtient des commentaires du fichier
    * @param file - Fichier
    * @return - Nombre de commentaires
    */
    private static int getCommentsNum(File file) {
 
        if(!file.exists()) { return 0; }
 
        try {
            int comments = 0;
            String currentLine;
 
            BufferedReader reader = new BufferedReader(new FileReader(file));
 
            while((currentLine = reader.readLine()) != null) { if(currentLine.startsWith("#")) { comments++; } }
 
        reader.close();
        return comments;
 
        } catch(IOException e) { e.printStackTrace(); return 0; }
 
    }
 
    private static String prepareConfigString(String configString) {
 
        int lastLine = 0;
        int headerLine = 0;
 
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();
 
        for(String line : lines) {
 
            if(line.startsWith(getPluginName() + "_COMMENT_")) {

                String comment = "#" + line.trim().substring(line.indexOf(":") + 1);
 
                if(comment.startsWith("# +-")) {
 
                    /*
                     * Si la ligne d'en-tête = 0, alors c'est
                     * début de l'en-tête, s'il est égal
                     * à 1, c'est la fin de l'en-tête
                     */
 
                    if(headerLine == 0) {

                        config.append(comment + "\n");
 
                        lastLine = 0;
                        headerLine = 1;
 
                    } else if(headerLine == 1) {

                        config.append(comment + "\n\n");
 
                        lastLine = 0;
                        headerLine = 0;
                    }
 
                } else {
 
                    /*
                     * Dernière ligne = 0 - Commentaire
                     * Dernière ligne = 1 - Cheminement normal
                     */
 
                    String normalComment;
 
                    if(comment.startsWith("# ' ")) { normalComment = comment.substring(0, comment.length() - 1).replaceFirst("# ' ", "# "); }
                    else { normalComment = comment; }
 
                    if(lastLine == 0) { config.append(normalComment + "\n"); }
                    else if(lastLine == 1) { config.append("\n" + normalComment + "\n"); }

                    lastLine = 0;
                }
 
            } else { config.append(line + "\n"); lastLine = 1; }
 
        }
 
     return config.toString();
 
    }
 
 
    /*
    * Enregistre la configuration dans un fichier
    * @param configString - Chaîne de configuration
    * @param file - Fichier Config
    */
    public static void saveConfig(String configString, File file) {
        String configuration = prepareConfigString(configString);
 
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(configuration);
            writer.flush();
            writer.close();
 
        } catch(IOException e) { e.printStackTrace(); }
 
    }
 
    public static String getPluginName() { return UtilityMain.getInstance().getDescription().getName(); }
 
    /*
    * Copie la ressource du flux d'entrée dans le fichier
    * @param resource - Ressource de .jar
    * @param file - Fichier à écrire
    */
    private static void copyResource(InputStream resource, File file) {
 
        try {

            OutputStream out = new FileOutputStream(file);
 
            int lenght;
            byte[] buf = new byte[1024];
 
            while((lenght = resource.read(buf)) > 0) { out.write(buf, 0, lenght); }
 
            out.close();
            resource.close();
 
        } catch(Exception e) { e.printStackTrace(); }
 
    }
 
}