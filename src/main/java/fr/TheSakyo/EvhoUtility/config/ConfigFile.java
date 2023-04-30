package fr.TheSakyo.EvhoUtility.config;

import java.io.File;
import java.io.Reader;
import java.util.List;
import java.util.Set;
 
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.inventory.ItemStack;

public class ConfigFile {

    private static ConfigFileManager manager;

    private final File file;
    private FileConfiguration config;

    private int comments;

    public ConfigFile(Reader configStream, File configFile, int comments, UtilityMain pluginMain) {

    	//Class "Main"
        /* Récupère la class "Main" et les différentes options pour faire fonctionner le fichier de configuration */

        this.comments = comments;

        this.file = configFile;
        
        this.config = YamlConfiguration.loadConfiguration(configStream);
    }
    /* Récupère la class "Main" et les différentes options pour faire fonctionner le fichier de configuration */


    /**************************************************/
    /* PARTIE GESTION D'UN FICHIER DE CONFIGURATION */
    /**************************************************/

    public static Object get(ConfigFile config, String path) { return config.config.get(path); }
 
    public static Object get(ConfigFile config, String path, Object def) { return config.config.get(path, def); }
 
    public static String getString(ConfigFile config, String path) { return config.config.getString(path); }
 
    public static String getString(ConfigFile config, String path, String def) { return config.config.getString(path, def); }
 
    public static int getInt(ConfigFile config, String path) { return config.config.getInt(path); }
 
    public static int getInt(ConfigFile config, String path, int def) { return config.config.getInt(path, def); }
 
    public static boolean getBoolean(ConfigFile config, String path) { return config.config.getBoolean(path); }
 
    public static boolean getBoolean(ConfigFile config, String path, boolean def) { return config.config.getBoolean(path, def); }
 
    public static void createSection(ConfigFile config, String path) { config.config.createSection(path); }
 
    public static ConfigurationSection getConfigurationSection(ConfigFile config, String path) { return config.config.getConfigurationSection(path); }
 
    public static double getDouble(ConfigFile config, String path) { return config.config.getDouble(path); }
 
    public static double getDouble(ConfigFile config, String path, double def) { return config.config.getDouble(path, def); }

    public static List<?> getList(ConfigFile config, String path) { return config.config.getList(path); }
 
    public static List<?> getList(ConfigFile config, String path, List<?> def) { return config.config.getList(path, def); }

    public static ItemStack getItemStack(ConfigFile config, String path) { return config.config.getItemStack(path); }

    public static ItemStack getItemStack(ConfigFile config, String path, ItemStack def) { return config.config.getItemStack(path, def); }

    public static Object getObject(ConfigFile config, String path, Class<Object> object) { return config.config.getObject(path, object); }

    public static Object getObject(ConfigFile config, String path, Object def, Class<Object> object) { return config.config.getObject(path, object, def); }

    public static boolean contains(ConfigFile config, String path) { return config.config.contains(path); }
 
    public static void removeKey(ConfigFile config, String path) { config.config.set(path, null); }
 
    public static void set(ConfigFile config, String path, Object value) { config.config.set(path, value); }
 
    public static void set(ConfigFile config, String path, Object value, String comment) {

        if(!config.config.contains(path)) {

            config.config.set(ConfigFileManager.getPluginName() + "_COMMENT_" + config.comments, " " + comment);
            config.comments++;
        }
        config.config.set(path, value);
    }
 
    public static void set(ConfigFile config, String path, Object value, String[] comment) {
 
        for(String comm : comment) {
 
            if(!config.config.contains(path)) {

                config.config.set(ConfigFileManager.getPluginName() + "_COMMENT_" + config.comments, " " + comm);
                config.comments++;
            }
        }
 
        config.config.set(path, value);
    }
 
    public static void setHeader(ConfigFile config, String[] header) {

        ConfigFileManager.setHeader(config.file, header);
        config.comments = header.length + 2;
        reloadConfig(config);
    }
 
    public static void reloadConfig(ConfigFile config) { config.config = YamlConfiguration.loadConfiguration(ConfigFileManager.getConfigContent(config.file)); }
 
    public static void saveConfig(ConfigFile config) {

        String cfg = config.config.saveToString();
        ConfigFileManager.saveConfig(cfg, config.file);
    }
 
    public static Set<String> getKeys(ConfigFile config) { return config.config.getKeys(false); }

    /***************************************************/
    /* PARTIE GESTION D'UN FICHIER DE CONFIGURATION */
    /**************************************************/
 
}

