package fr.TheSakyo.EvhoUtility.config;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.minecraft.ChatFormatting;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ConfigFileManager {

    //Récupère la class "Main" en tant que "static"
    private static final UtilityMain mainInstance = UtilityMain.getInstance();


	/************************************************************/
	/* METHODE POUR CHARGER DES FICHIERS CONFIGS PERSONNALISER */
	/***********************************************************/


    /* Méthode Chargement/Création du fichier de configuration 'servername.yml' */
	private static void loadServerNameConfig() {

		/* Chargement/Création du fichier de configuration 'servername.yml' si le serveur fonctionne sous "BungeeCord" */
		if(mainInstance.hasBungee()) {

			if(Bukkit.getServer().getOnlinePlayers().isEmpty()) {

				if(ConfigFile.getString(mainInstance.serverNameConfig, "server_name") == null) {

				   ConfigFile.set(mainInstance.serverNameConfig, "server_name", "Example");
				   ConfigFile.saveConfig(mainInstance.serverNameConfig);

				} else ConfigFile.reloadConfig(mainInstance.serverNameConfig);
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
		   "*** ACTIVATION/DÉSACTIVATION ***",
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

		   mainInstance.advConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/advancements/advancements.yml", headerAdvancements);
		   ConfigFile.reloadConfig(mainInstance.advConfig);

		} else {

		  mainInstance.advConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/advancements/advancements.yml", headerAdvancements);
		  ConfigFile.saveConfig(mainInstance.advConfig);
		}
		/* Chargement/Création du fichier de configuration 'advancements.yml' */


		/* Chargement/Création du fichier de configuration 'chatformat.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/config/chatformat.yml").exists()) {

			mainInstance.chatConfig = getNewConfig(mainInstance.getDataFolder(), "/config/chatformat.yml", headerchat);
			ConfigFile.reloadConfig(mainInstance.advConfig);

		} else {

			mainInstance.chatConfig = getNewConfig(mainInstance.getDataFolder(), "/config/chatformat.yml", headerchat);
			ConfigFile.set(mainInstance.advConfig, "chat_format", "%prefix% %player% &8&l:");
			ConfigFile.saveConfig(mainInstance.advConfig);

		}
		/* Chargement/Création du fichier de configuration 'chatformat.yml' */


		/*Chargement/Création du fichier de configuration 'holograms.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/holograms.yml").exists()) {

		   mainInstance.holoConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/holograms.yml", headerHologram);
		   ConfigFile.reloadConfig(mainInstance.holoConfig);

		} else {

		  mainInstance.holoConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/holograms.yml", headerHologram);
		  ConfigFile.createSection(mainInstance.holoConfig, "Holograms");
		  ConfigFile.saveConfig(mainInstance.holoConfig);
		}
		/* Chargement/Création du fichier de configuration 'holograms.yml' */

        /*Chargement/Création du fichier de configuration 'npc.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/NPC.yml").exists()) {

		   mainInstance.NPCConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/NPC.yml", headerNPC);

                                    /* ---------------------------------- */

                boolean isEmpty = false;

                File NPC_BACKUP = new File(mainInstance.getDataFolder(), "/utils/NPC_BACKUP.yml");
                File NPC = getConfigFile(mainInstance.getDataFolder(), "/utils/NPC.yml");

                                        /* ----------------------- */

                try {

                    String currentLine;

                    StringBuilder whole = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new FileReader(NPC));

                    /**************************************/

                    while((currentLine = reader.readLine()) != null) {

                        isEmpty = currentLine.startsWith("#") || currentLine.startsWith("NPC");
                        break;
                    }

                    /**************************************/

                    if(!isEmpty) FileUtils.copyFile(NPC, NPC_BACKUP);

                } catch(IOException ignored) {}

                                    /* ---------------------------------- */

		   ConfigFile.reloadConfig(mainInstance.NPCConfig);

		} else {

		  mainInstance.NPCConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/NPC.yml", headerNPC);
		  ConfigFile.createSection(mainInstance.NPCConfig, "NPC");
		  ConfigFile.saveConfig(mainInstance.NPCConfig);
		}
		/* Chargement/Création du fichier de configuration 'holograms.yml' *


		/* Chargement/Création du fichier de configuration 'tablist.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/config/tablist.yml").exists()) {

		   mainInstance.tabConfig = getNewConfig(mainInstance.getDataFolder(), "/config/tablist.yml", headerTablist);
		   ConfigFile.reloadConfig(mainInstance.tabConfig);

		} else {

		  mainInstance.tabConfig = getNewConfig(mainInstance.getDataFolder(), "/config/tablist.yml", headerTablist);
		  ConfigFile.set(mainInstance.tabConfig, "title1", "&aExemple");
		  ConfigFile.set(mainInstance.tabConfig, "title2", "&eExemple");
		  ConfigFile.set(mainInstance.tabConfig, "title3", "&cExemple");
		  ConfigFile.set(mainInstance.tabConfig, "website", "&3&oExemple.fr");
		  ConfigFile.set(mainInstance.tabConfig, "ip", "&3&oPlay.Exemple.fr");
		  ConfigFile.set(mainInstance.tabConfig, "ColorFooterSection", "&6");
		  ConfigFile.saveConfig(mainInstance.tabConfig);
		}
		/* Chargement/Création du fichier de configuration 'tablist.yml' */


		/* Chargement/Création du fichier de configuration 'DeathOrAchievement.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/DeathOrAchievement.yml").exists()) {

		   mainInstance.DeathOrAchievementConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/DeathOrAchievement.yml", headerDeathOrAchievementMessage);
		   ConfigFile.reloadConfig(mainInstance.DeathOrAchievementConfig);

		} else {

		  mainInstance.DeathOrAchievementConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/DeathOrAchievement.yml", headerDeathOrAchievementMessage);
		  ConfigFile.set(mainInstance.DeathOrAchievementConfig, "DeathMessage", "on");
		  ConfigFile.set(mainInstance.DeathOrAchievementConfig, "AchievementMessage", "on");
		  ConfigFile.saveConfig(mainInstance.DeathOrAchievementConfig);
		}
		/* Chargement/Création du fichier de configuration 'DeathOrAchievement.yml' */


		/* Chargement/Création du fichier de configuration 'zone.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/zone.yml").exists()) {

		   mainInstance.zoneConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/zone.yml", headerZone);
		   ConfigFile.reloadConfig(mainInstance.zoneConfig);

		} else {

		  mainInstance.zoneConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/zone.yml", headerZone);
		  ConfigFile.createSection(mainInstance.zoneConfig, "ZONE");
		  ConfigFile.saveConfig(mainInstance.zoneConfig);
		}
		/* Chargement/Création du fichier de configuration 'zone.yml' */


		/* Chargement/Création du fichier de configuration 'playerSkin.yml' */
		if(getConfigFile(mainInstance.getDataFolder(), "/utils/playerSkin.yml").exists()) {

		   mainInstance.playerSkinConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/playerSkin.yml", headerplayerSkin);
		   ConfigFile.reloadConfig(mainInstance.playerSkinConfig);

		} else {

		  mainInstance.playerSkinConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/playerSkin.yml", headerplayerSkin);
		  ConfigFile.createSection(mainInstance.playerSkinConfig, "SKIN");
		  ConfigFile.saveConfig(mainInstance.playerSkinConfig);
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
            String GI = ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString();
            String YI = ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString();
            //Code couleur utile pour des informations au niveau de la console//

            /******************************/

            mainInstance.console.sendMessage(mainInstance.prefix + GI + "Actualisation des mondes...");
            mainInstance.worldConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/world.yml", headerworld);
            ConfigFile.reloadConfig(mainInstance.worldConfig);

            /******************************/

            ConfigurationSection configSection = ConfigFile.getConfigurationSection(mainInstance.worldConfig, "serverworlds");
            if(configSection != null) {

                Set<String> sectionString = configSection.getKeys(false);

                /******************************/

                for(String name : sectionString) {

                    File file = new File(name);

                    /************************/

                    if(file.exists()) {

                        // Recharge le monde souhaité
                        if(Bukkit.getServer().getWorld(name) == null)  new WorldHandler(mainInstance, name);
                        else mainInstance.console.sendMessage(mainInstance.prefix + YI + name + ChatFormatting.RESET + " : " + ChatFormatting.GREEN + "OK");
                    }
                }

                /******************************/

                mainInstance.console.sendMessage(mainInstance.prefix + ChatFormatting.DARK_GREEN + "Les mondes ont été recharger !");

            }
            
        } else {

            mainInstance.worldConfig = getNewConfig(mainInstance.getDataFolder(), "/utils/world.yml", headerworld);
            ConfigFile.saveConfig(mainInstance.worldConfig);
        }
        /* Chargement/Création du fichier de configuration 'world.yml' */
    }
    /* Méthode Chargement/Création du fichier de configuration 'world.yml' */

                             /* ------------------------- */

    /* Petite Méthode pour vider la config 'world.yml' */
    public static void clearKeyUtilityWorldConfig() {

        for(String str : ConfigFile.getKeys(mainInstance.worldConfig)) ConfigFile.removeKey(mainInstance.worldConfig, str);
        ConfigFile.saveConfig(mainInstance.worldConfig);
    }
    /* Petite Méthode pour vider la config 'world.yml' */

	/************************************************************/
	/* METHODE POUR CHARGER DES FICHIERS CONFIGS PERSONNALISER */
	/***********************************************************/


    /* ------------------------------------------------------------------------------------------------------------------------------------- */

 
    /*
    * Obtient une nouvelle configuration avec l'en-tête
    * @param filePath - Chemin d'accès au fichier
    * @return - 'New ConfigFile'
    */
    public static ConfigFile getNewConfig(File DataFolder, String filePath, String[] header) {
 
        File file = getConfigFile(DataFolder, filePath);

        /******************************/

        if(!file.exists()) {

            prepareFile(DataFolder, filePath);
            if(header != null && header.length != 0) setHeader(file, header);
        }

        /******************************/

        return new ConfigFile(getConfigContent(DataFolder, filePath), file, getCommentsNum(file), UtilityMain.getInstance());
    }
 
    /*
    * Obtient une nouvelle configuration
    * @param filePath - Chemin d'accès au fichier
    * @return - 'New ConfigFile'
    */
    public static ConfigFile getNewConfig(File DataFolder, String filePath) { return getNewConfig(DataFolder, filePath, null); }
 
    /*
    * Obtient le fichier de configuration à partir de la chaîne
    * @param file - Chemin d'accès au fichier
    * @return - 'New file object'
    */
    public static File getConfigFile(File DataFolder, String file) {
 
        if(file.isEmpty()) return null;
        File configFile;

        /******************************/

        if(file.contains("/")) {
 
            if(file.startsWith("/")) configFile = new File(DataFolder + file.replace("/", File.separator));
            else configFile = new File(DataFolder + File.separator + file.replace("/", File.separator));

        } else configFile = new File(DataFolder, file);

        /******************************/

        return configFile;
    }
    
    
    /*
    * Lit le fichier et place les commentaires SnakeYAML
    * @param filePath - Chemin d'accès au fichier
    * @return - Fichier en tant que flux d'entrée
    */
    public static Reader getConfigContent(File file) {
 
        if(!file.exists()) return null;

        /******************************/

        try {

            int commentNum = 0;

            /**********************/

            String addLine;
            String currentLine;
            String pluginName = getPluginName();

            /**********************/

            StringBuilder whole = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));

            /***************************/

            while((currentLine = reader.readLine()) != null) {
 
                if(currentLine.startsWith("#")) {
                    addLine = currentLine.replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
                    whole.append(addLine).append("\n");
                    commentNum++;
 
                } else whole.append(currentLine).append("\n");
            }

            /***************************/

            Reader configStream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

            /**********************/

            reader.close();
            return configStream;
 
        } catch(IOException e) {

            e.printStackTrace(System.err);
            return null;
        }
    }
    
    /*
    * Obtient le contenu de la configuration à partir du fichier
    * @param filePath - Chemin d'accès au fichier
    * @return - fichier prêt
    */
    public static Reader getConfigContent(File DataFolder, String filePath) { return getConfigContent(getConfigFile(DataFolder, filePath)); }
 
    /*
    * Créé un nouveau fichier pour la configuration et y copier la ressource
    * @param file - Chemin d'accès au fichier
    * @param resource - Ressource à copier
    */
    public static void prepareFile(File DataFolder, String filePath, String resource) {
 
        File file = getConfigFile(DataFolder, filePath);
        if(file.exists()) { return; }

        /*************************************************/

        try {

            file.getParentFile().mkdirs();
            file.createNewFile();
 
            if(resource != null && !resource.isEmpty()) copyResource(UtilityMain.getInstance().getResource(resource), file);
 
        } catch(IOException e) { e.printStackTrace(System.err); }
    }
 
    /*
    * Créé un nouveau fichier pour la configuration sans ressource
    * @param file - Fichier à créer
    */
    public static void prepareFile(File DataFolder, String filePath) { prepareFile(DataFolder, filePath, null); }
 
    /*
    * Ajoute un bloc d'en-tête à la configuration
    * @param file - Fichier Config
    * @param header - Lignes d'en-tête
    */
    public static void setHeader(File file, String[] header) {
 
        if(!file.exists()) { return; }

        /******************************/

        try {

            String currentLine;
            StringBuilder config = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));

            /***********************/

            while((currentLine = reader.readLine()) != null) { config.append(currentLine).append("\n"); }

            /***********************/

            reader.close();
            config.append("# +----------------------------------------------------+ #\n");

            /********************************************************/

            for(String line : header) {
 
                if(line.length() > 50) continue;
                StringBuilder finalLine = getStringBuilder(line);
                config.append("# < ").append(finalLine.toString()).append(" > #\n");
            }

            /********************************************************/

            config.append("# +----------------------------------------------------+ #");

            /***********************/

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(prepareConfigString(config.toString()));
            writer.flush();
            writer.close();
 
        } catch(IOException e) { e.printStackTrace(System.err); }
    }
    
    @NotNull
    private static StringBuilder getStringBuilder(String line) {

        int length = (50 - line.length()) / 2;
        StringBuilder finalLine = new StringBuilder(line);

        /***********************/

        for(int i = 0; i < length; i++) {

            finalLine.append(" ");
            finalLine.reverse();
            finalLine.append(" ");
            finalLine.reverse();
        }

        /***********************/

        if(line.length() % 2 != 0) { finalLine.append(" "); }
        return finalLine;
    }

    /*
    * Obtient des commentaires du fichier
    * @param file - Fichier
    * @return - Nombre de commentaires
    */
    private static int getCommentsNum(File file) {
 
        if(!file.exists()) { return 0; }

        /********************************************************/

        try {

            int comments = 0;
            String currentLine;

            /***********************/

            BufferedReader reader = new BufferedReader(new FileReader(file));

            /***********************/

            while((currentLine = reader.readLine()) != null) { if(currentLine.startsWith("#")) comments++; }

            /***********************/

            reader.close();
            return comments;
 
        } catch(IOException e) { e.printStackTrace(System.err); return 0; }
    }
 
    private static String prepareConfigString(String configString) {
 
        int lastLine = 0;
        int headerLine = 0;

        /********************************************************/

        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();

        /********************************************************/

        for(String line : lines) {
 
            if(line.startsWith(getPluginName() + "_COMMENT_")) {

                String comment = "#" + line.trim().substring(line.indexOf(":") + 1);

                /***********************************/

                if(comment.startsWith("# +-")) {
 
                    /*
                     * Si la ligne d'en-tête = 0, alors c'est
                     * début de l'en-tête, s'il est égal
                     * à 1, c'est la fin de l'en-tête
                     */
 
                    if(headerLine == 0) {

                        config.append(comment).append("\n");

                        /***********************/

                        lastLine = 0;
                        headerLine = 1;
 
                    } else {

                        config.append(comment).append("\n\n");

                        /***********************/

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

                    /***********************/

                    if(lastLine == 0) { config.append(normalComment).append("\n"); }
                    else { config.append("\n").append(normalComment).append("\n"); }

                    /***********************/

                    lastLine = 0;
                }
 
            } else { config.append(line).append("\n"); lastLine = 1; }
 
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

        /*********************************/

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(configuration);
            writer.flush();
            writer.close();
 
        } catch(IOException e) { e.printStackTrace(System.err); }
    }

    @SuppressWarnings("deprecation")
    public static String getPluginName() { return UtilityMain.getInstance().getDescription().getName(); }
 
    /*
    * Copie la ressource du flux d'entrée dans le fichier
    * @param resource - Ressource de .jar
    * @param file - Fichier à écrire
    */
    private static void copyResource(InputStream resource, File file) {
 
        try {

            OutputStream out = new FileOutputStream(file);

            /*************************************/

            int lenght;
            byte[] buf = new byte[1024];

            /*************************************/

            while((lenght = resource.read(buf)) > 0) { out.write(buf, 0, lenght); }

            /*************************************/

            out.close();
            resource.close();
 
        } catch(Exception e) { e.printStackTrace(System.err); }
    }
}