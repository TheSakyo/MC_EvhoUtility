package fr.TheSakyo.EvhoUtility.config;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;

import java.lang.reflect.InvocationTargetException;

/*********************************************/
/* 'CLASS' PERMETTANT DE RECHARGER LE MONDE  */
/********************************************/

public class WorldHandler {

    private UtilityMain main;

    public WorldHandler(UtilityMain pluginMain, String worldname) {this.main = pluginMain; LoadingWorlds(worldname); }

    private void LoadingWorlds(String worldname) {

        //Code couleur utile pour des informations au niveau de la console//
        String RI = ChatColor.RED.toString() + ChatColor.ITALIC.toString();
        String GI = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString();
        String YI = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString();
        //Code couleur utile pour des informations au niveau de la console//

        main.console.sendMessage(main.prefix + GI + "Rechargement du monde " + YI + worldname + GI + "......");
        main.console.sendMessage(GI + "...");
        main.console.sendMessage(GI + "...");

        WorldType type = WorldType.getByName(ConfigFile.getString(main.worldconfig, "serverworlds." + worldname + ".Type"));
        World.Environment env = World.Environment.valueOf(ConfigFile.getString(main.worldconfig, "serverworlds." + worldname + ".Environment"));
        boolean bool = Boolean.getBoolean(ConfigFile.getString(main.worldconfig, "serverworlds." + worldname + ".Structure"));

        main.console.sendMessage(main.prefix + GI + "Actualisation du monde " + YI + worldname + GI + "......");
        main.console.sendMessage(GI + "...");
        main.console.sendMessage(GI + "...");

        try {

            ChunkGenerator generator = (ChunkGenerator)Class.forName(ConfigFile.getString(main.worldconfig, "serverworlds." + worldname + ".Generator")).getDeclaredConstructor().newInstance();
            WorldCreator wc = new WorldCreator(worldname).type(type).environment(env).generateStructures(bool);
            Bukkit.getServer().createWorld(wc);

        } catch(ClassNotFoundException | NullPointerException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {

            main.console.sendMessage(main.prefix + GI + "Génération de ce monde " + RI + "introuvable" + GI + ", tentative de regénération du monde....");
            main.console.sendMessage(GI + "...");
            main.console.sendMessage(GI + "...");

            WorldCreator wc = new WorldCreator(worldname).type(type).environment(env).generateStructures(bool);
            Bukkit.getServer().createWorld(wc);
        }

        main.console.sendMessage(main.prefix + YI + worldname + ChatColor.RESET + " : " + ChatColor.GREEN + "Chargé");
    }
}
/*********************************************/
/* 'CLASS' PERMETTANT DE RECHARGER LE MONDE  */
/********************************************/
