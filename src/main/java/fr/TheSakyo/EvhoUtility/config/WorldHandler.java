package fr.TheSakyo.EvhoUtility.config;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.minecraft.ChatFormatting;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;

import java.lang.reflect.InvocationTargetException;

/*********************************************/
/* 'CLASS' PERMETTANT DE RECHARGER LE MONDE  */
/********************************************/

public class WorldHandler {

    private final UtilityMain main;

    public WorldHandler(UtilityMain pluginMain, String worldName) {this.main = pluginMain; LoadingWorlds(worldName); }


    /**********************************************************************************/
    /**********************************************************************************/


    private void LoadingWorlds(String worldName) {

        //Code couleur utile pour des informations au niveau de la console//
        String RI = ChatFormatting.RED.toString() + ChatFormatting.ITALIC.toString();
        String GI = ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString();
        String YI = ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString();
        //Code couleur utile pour des informations au niveau de la console//

        /*************************************************/

        main.console.sendMessage(main.prefix + GI + "Rechargement du monde " + YI + worldName + GI + "......");
        main.console.sendMessage(GI + "...");
        main.console.sendMessage(GI + "...");

        /*************************************************/

        WorldType type = WorldType.getByName(ConfigFile.getString(main.worldConfig, "serverworlds." + worldName + ".Type"));
        World.Environment env = World.Environment.valueOf(ConfigFile.getString(main.worldConfig, "serverworlds." + worldName + ".Environment"));
        boolean bool = Boolean.getBoolean(ConfigFile.getString(main.worldConfig, "serverworlds." + worldName + ".Structure"));

        /*************************************************/

        main.console.sendMessage(main.prefix + GI + "Actualisation du monde " + YI + worldName + GI + "......");
        main.console.sendMessage(GI + "...");
        main.console.sendMessage(GI + "...");

        /*************************************************/

        try {

            ChunkGenerator generator = (ChunkGenerator)Class.forName(ConfigFile.getString(main.worldConfig, "serverworlds." + worldName + ".Generator")).getDeclaredConstructor().newInstance();
            WorldCreator wc = new WorldCreator(worldName).type(type).environment(env).generateStructures(bool);
            Bukkit.getServer().createWorld(wc);

        } catch(ClassNotFoundException | NullPointerException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {

            main.console.sendMessage(main.prefix + GI + "Génération de ce monde " + RI + "introuvable" + GI + ", tentative de regénération du monde....");
            main.console.sendMessage(GI + "...");
            main.console.sendMessage(GI + "...");

            /******************************/

            WorldCreator wc = new WorldCreator(worldName).type(type).environment(env).generateStructures(bool);
            Bukkit.getServer().createWorld(wc);
        }

        /*************************************************/

        main.console.sendMessage(main.prefix + YI + worldName + ChatFormatting.RESET + " : " + ChatFormatting.GREEN + "Chargé");
    }
}
/*********************************************/
/* 'CLASS' PERMETTANT DE RECHARGER LE MONDE  */
/********************************************/
