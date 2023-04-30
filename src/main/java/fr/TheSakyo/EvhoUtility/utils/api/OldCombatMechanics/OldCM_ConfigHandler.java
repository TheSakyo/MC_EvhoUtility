package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.minecraft.ChatFormatting;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class OldCM_ConfigHandler {
    private final UtilityMain plugin;

    public OldCM_ConfigHandler(UtilityMain instance) { this.plugin = instance; }

    public void upgradeConfig() {

        File configFile = getFile("/OldCombatCfg.yml");

        if(doesConfigExist()) {

            //D'abord, nous changeons le nom de l'ancienne configuration
            File backup = getFile("/OldCombatCfg-BACKUP.yml");
            if(backup.exists()) backup.delete();

            configFile.renameTo(backup); // Renomme l'ancienne configuration

            //Puis, nous sauvegardons la nouvelle version
            plugin.saveResource("OldCombatCfg.yml", false);
        }

        if(!configFile.exists()) plugin.saveResource("OldCombatCfg.yml", false);
    }

    public void setupConfig() { if(!getFile("/OldCombatCfg.yml").exists()) setupConfig("OldCombatCfg.yml"); }

    private void setupConfig(String fileName) {

        plugin.saveResource(fileName, false);
        plugin.console.sendMessage(plugin.prefix + ChatFormatting.DARK_GREEN + "Fichier de Configuration " + fileName + " rechargé !");
    }

    public YamlConfiguration getConfig(String fileName) { return YamlConfiguration.loadConfiguration(getFile(fileName)); }

    public File getFile(String fileName) { return new File(plugin.getDataFolder(), fileName.replace('/', File.separatorChar)); }

    public boolean doesConfigExist() { return getFile("/OldCombatCfg.yml").exists(); }
}
