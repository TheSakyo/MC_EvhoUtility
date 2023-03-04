package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.ModuleLoader;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM_ConfigHandler;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.EntityDamageByEntityListener;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.WeaponDamages;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config {

    private static UtilityMain plugin;
    private static FileConfiguration config;
    private static List<Material> interactive = new ArrayList<>();

    public static void initialise(UtilityMain plugin) {

        Config.plugin = plugin;
        config = new OldCM_ConfigHandler(plugin).getConfig("/OldCombatCfg.yml");

        reload();
    }

    /**
     * @return Si la configuration a été modifiée ou non
     */
    private static boolean checkConfigVersion() {

        if(!config.isInt("config-version")) {

            plugin.getLogger().warning("La version de la configuration [OldCombat] ne correspond pas, sauvegarde l'ancienne configuration et en création d'une nouvelle...");
            OldCM.upgradeConfig();
            reload();
            return true;
        }

        return false;
    }


    public static void reload() {

        if(OldCM.doesConfigExist()) {

            new OldCM_ConfigHandler(plugin).setupConfig();
            config = new OldCM_ConfigHandler(plugin).getConfig("/OldCombatCfg.yml");

        } else OldCM.upgradeConfig();

        if(checkConfigVersion()) { return; }

        Messenger.DEBUG_ENABLED = config.getBoolean("debug.enabled");

        WeaponDamages.initialise(plugin); //Recharge les dégâts des armes à partir de la configuration

        // Chargement de tous les blocs interactifs (utilisés par les modules sword blocking et elytra)
        reloadInteractiveBlocks();

        //Définit 'EntityDamagedByEntityListener' comme étant activé si l'un des modules suivants est activé
        if(EntityDamageByEntityListener.getINSTANCE() != null) EntityDamageByEntityListener.getINSTANCE().setEnabled(moduleEnabled("old-tool-damage") || moduleEnabled("old-potion-effects"));

        // Enregistre/Désenregistre dynamiquement tous les écouteurs d'événements pour une performance optimale !
        ModuleLoader.toggleModules();

        ModuleLoader.getModules().forEach(module -> {

            try { module.reload(); }
            catch(Exception e) { plugin.getLogger().log(Level.WARNING, "Erreur de rechargement du module '" + module.toString() + "'", e); }
        });
    }

    public static boolean moduleEnabled(String name, World world) {

        boolean isBlacklist = config.getBoolean("worlds-is-blacklist");
        ConfigurationSection section = config.getConfigurationSection(name);

        if(section == null) {

            plugin.getLogger().warning("Le module '" + name + "' n'existe pas !");
            return false;
        }

        if(!section.getBoolean("enabled")) return false;
        if(world == null) return true;

        final String worldName = world.getName();
        final List<String> list = section.getStringList("worlds");

        // Si la liste est vide, le module doit être activé dans tous les mondes.
        if(list.size() == 0) return true;

        boolean isInList = list.stream().anyMatch(entry -> entry.equalsIgnoreCase(worldName));
        return isBlacklist != isInList;
    }

    public static boolean moduleEnabled(String name) { return moduleEnabled(name, null); }

    public static boolean debugEnabled() { return moduleEnabled("debug", null); }

    public static List<?> getWorlds(String moduleName) { return config.getList(moduleName + ".worlds"); }

    public static boolean moduleSettingEnabled(String moduleName, String moduleSettingName) { return config.getBoolean(moduleName + "." + moduleSettingName); }

    public static void setModuleSetting(String moduleName, String moduleSettingName, boolean value) {

        config.set(moduleName + "." + moduleSettingName, value);
        plugin.saveConfig();
    }

    private static void reloadInteractiveBlocks() { interactive = ConfigUtils.loadMaterialList(config, "interactive"); }

    public static List<Material> getInteractiveBlocks() { return interactive; }
}
