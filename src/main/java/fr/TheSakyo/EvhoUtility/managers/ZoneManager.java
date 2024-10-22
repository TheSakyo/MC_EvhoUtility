package fr.TheSakyo.EvhoUtility.managers;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;


/*******************************************************************************************************/
/* ~~~ GESTIONNAIRE DE ZONE DU SERVEUR ; EN LIEN AVEC LE FICHIER DE CONFIGURATION 'ZONECONFIG.YML' ~~~ */
/*******************************************************************************************************/
public class ZoneManager {

    private static final UtilityMain mainInstance = UtilityMain.getInstance(); /* Récupère la class "Main" */

    public static Map<UUID, Location> previousLocation = new HashMap<>(); // Récupèrera la localisation précédente du joueur (utile pour l'évènement quand le joueur entre ou sort d'une région)

    /************************************************************/
    /* MÉTHODE PERMETTANT D'ACTUALISER UNE ZONE DANS LE SERVEUR */
    /************************************************************/
    private static void update(String newZoneName, World world, Location firstPosition, Location secondPosition, List<Group> allowedGroups) {

        create(newZoneName);

                /* --------------- */

        if(isExist(newZoneName)) {

            if(world != null) setWorld(newZoneName, world.getName());

                /* ----------------- */

            if(firstPosition != null) {

                double firstX = firstPosition.getX();
                double firstY = firstPosition.getY();
                double firstZ = firstPosition.getZ();

                setFirstPos(newZoneName, firstX, firstY, firstZ);
            }

                /* ----------------- */

            if(secondPosition != null && getFirstPos(newZoneName) != null && getWorld(newZoneName) != null) {

                double secondX = secondPosition.getX();
                double secondY = secondPosition.getY();
                double secondZ = secondPosition.getZ();

                setSecondPos(newZoneName, secondX, secondY, secondZ);
            }
                          /* ----------------- */

            ZoneManager.resetGroupsForZone(newZoneName, true);

                            /* ----------------- */

            for(Group group : allowedGroups) { ZoneManager.addGroupForZone(newZoneName, group.getName()); }
        }
    }
    /************************************************************/
    /* MÉTHODE PERMETTANT D'ACTUALISER UNE ZONE DANS LE SERVEUR */
    /************************************************************/


    /*****************************************/
    /* MÉTHODE PERMETTANT DE CRÉER UNE ZONE */
    /****************************************/
    public static void create(String zoneName) {

        resetGroupsForZone(zoneName, true);
        ConfigFile.createSection(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase());
        ConfigFile.saveConfig(mainInstance.zoneConfig);
    }
    /*****************************************/
    /* MÉTHODE PERMETTANT DE CRÉER UNE ZONE */
    /****************************************/


    /*********************************************/
    /* MÉTHODE PERMETTANT DE SUPPRIMER UNE ZONE */
    /********************************************/
    public static void delete(String zoneName) {

        ConfigFile.removeKey(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase());
        ConfigFile.saveConfig(mainInstance.zoneConfig);
    }
    /*********************************************/
    /* MÉTHODE PERMETTANT DE SUPPRIMER UNE ZONE */
    /********************************************/


    /********************************************/
    /* MÉTHODE PERMETTANT DE RENOMMER UNE ZONE */
    /*******************************************/
    public static void rename(String zoneName, String newZoneName) {

       if(isExist(zoneName) && hasRegion(zoneName)) {

           Location firstPosition = ZoneManager.getFirstLocationPos(zoneName);
           Location secondPosition = ZoneManager.getSecondLocationPos(zoneName);

           World world = getWorld(zoneName);

           List<Group> allowGroups = getGroupsForZone(zoneName);

                    /* ----------------- */

           update(newZoneName, world, firstPosition, secondPosition, allowGroups);
           delete(zoneName);
       }
    }
    /********************************************/
    /* MÉTHODE PERMETTANT DE RENOMMER UNE ZONE */
    /*******************************************/


    /*********************************************/
    /* MÉTHODE VÉRIFIANT SI LA ZONE EXISTE BIEN */
    /********************************************/
    public static boolean isExist(String zoneName) { return ConfigFile.getConfigurationSection(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase()) != null; }
    /*********************************************/
    /* MÉTHODE VÉRIFIANT SI LA ZONE EXISTE BIEN */
    /********************************************/

    /********************************************************************************************************************/
    /* MÉTHODE VÉRIFIANT SI DANS LA LISTE DES ZONES RÉCUPÉRÉES EN PARAMÈTRES, ON ARRIVE A RÉCUPÉRER AU MOINS UNE RÉGION */
    /********************************************************************************************************************/
    public static boolean zoneListContainsRegions(Set<String> zones) {

        boolean zoneExisted = false;

        for(String ZONE : zones) { if(hasRegion(ZONE)) { zoneExisted = true; } }
        return zoneExisted;
    }
    /********************************************************************************************************************/
    /* MÉTHODE VÉRIFIANT SI DANS LA LISTE DES ZONES RÉCUPÉRÉES EN PARAMÈTRES, ON ARRIVE A RÉCUPÉRER AU MOINS UNE RÉGION */
    /********************************************************************************************************************/


    /**************************************************/
    /* MÉTHODE VÉRIFIANT SI LA ZONE A BIEN UNE RÉGION */
    /**************************************************/
    public static boolean hasRegion(String zoneName) { return getFirstPos(zoneName) != null && getSecondPos(zoneName) != null && getWorld(zoneName) != null; }
    /**************************************************/
    /* MÉTHODE VÉRIFIANT SI LA ZONE A BIEN UNE RÉGION */
    /**************************************************/


    /*******************************************************************************************/
    /* MÉTHODE VÉRIFIANT SI LA JOUEUR A LE GRADE AYANT LA PERMISSION POUR ENTRER DANS LA ZONE */
    /******************************************************************************************/
    public static boolean hasPerm(String zoneName, Player p) {

        boolean isOK = false;

        ConfigurationSection groupSection = ConfigFile.getConfigurationSection(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".groups");

        GroupManager groupManager = mainInstance.luckApi.getGroupManager();
        groupManager.loadAllGroups();

        for(Group group : groupManager.getLoadedGroups()) {

            if(ConfigFile.getBoolean(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".groups." + group.getName())) {

                if(group.getName().equalsIgnoreCase("default") || mainInstance.formatGrade.isPlayerInGroup(p, group.getName())) isOK = true; break;
            }
        }
        return isOK;
    }
    /*******************************************************************************************/
    /* MÉTHODE VÉRIFIANT SI LA JOUEUR A LE GRADE AYANT LA PERMISSION POUR ENTRER DANS LA ZONE */
    /******************************************************************************************/


    /*************************************************************************************************/
    /* MÉTHODE PERMETTANT DE RÉINITIALISER LES ACCÈS A LA ZONE A TOUS LES GRADES OU DE LES RECHARGER */
    /*************************************************************************************************/
    public static void resetGroupsForZone(String zoneName, boolean resetAll) {

        if(isExist(zoneName)) {

            GroupManager groupManager = mainInstance.luckApi.getGroupManager();
            groupManager.loadAllGroups();

            for(Group loadedGroup : groupManager.getLoadedGroups()) {

                String Path = "ZONE." + zoneName.toUpperCase() + ".groups." + loadedGroup.getName();

                if(resetAll) { ConfigFile.set(mainInstance.zoneConfig, Path, false); }
                else {

                    if(!ConfigFile.contains(mainInstance.zoneConfig, Path)) { ConfigFile.set(mainInstance.zoneConfig, Path, false); }
                }
            }
                    /* -------------------------------------- */

            Set<String> keysGroupsZoneSectionCfg = ConfigFile.getConfigurationSection(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".groups").getKeys(false);

            if(!keysGroupsZoneSectionCfg.isEmpty()) {

                for(String key : keysGroupsZoneSectionCfg) {

                    String Path = "ZONE." + zoneName.toUpperCase() + ".groups." + key;

                    Group group = groupManager.getGroup(key);
                    if(group == null) { ConfigFile.removeKey(mainInstance.zoneConfig, Path); }
                }
            }
            ConfigFile.saveConfig(mainInstance.zoneConfig);
        }
    }
    /*************************************************************************************************/
    /* MÉTHODE PERMETTANT DE RÉINITIALISER LES ACCÈS A LA ZONE A TOUS LES GRADES OU DE LES RECHARGER */
    /*************************************************************************************************/


    /******************************************************************/
    /* MÉTHODE RÉCUPÉRANT UNE LISTE DES GRADES AYANT ACCÈS A LA ZONE */
    /*****************************************************************/
    public static List<Group> getGroupsForZone(String zoneName) {

        List<Group> groupList = new ArrayList<>();

        /*****************************/

        if(isExist(zoneName)) {

            GroupManager groupManager = mainInstance.luckApi.getGroupManager();
            groupManager.loadAllGroups();

            for(Group loadedGroup : groupManager.getLoadedGroups()) {

                String Path = "ZONE." + zoneName.toUpperCase() + ".groups." + loadedGroup.getName();

                 if(ConfigFile.getBoolean(mainInstance.zoneConfig, Path)) { groupList.add(loadedGroup); }
            }
        }

        return groupList;
    }
    /******************************************************************/
    /* MÉTHODE RÉCUPÉRANT UNE LISTE DES GRADES AYANT ACCÈS A LA ZONE */
    /*****************************************************************/


    /*************************************************************************/
    /* MÉTHODE PERMETTANT D'AJOUTER UN GROUPE EN PARTICULIER DANS LA RÉGION */
    /***********************************************************************/
    public static void addGroupForZone(String zoneName, String groupName) {

        if(isExist(zoneName)) {

            GroupManager groupManager = mainInstance.luckApi.getGroupManager();
            Group group = groupManager.getGroup(groupName);

            group.getName();
            ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".groups." + group.getName(), true);
            ConfigFile.saveConfig(mainInstance.zoneConfig);
        }
    }
    /*************************************************************************/
    /* MÉTHODE PERMETTANT D'AJOUTER UN GROUPE EN PARTICULIER DANS LA RÉGION  */
    /************************************************************************/


    /****************************************************************************/
    /* MÉTHODE PERMETTANT DE SUPPRIMER UN GROUPE EN PARTICULIER DANS LA RÉGION */
    /*************************************************************************/
    public static void removeGroupForZone(String zoneName, String groupName) {

        if(isExist(zoneName)) {

            GroupManager groupManager = mainInstance.luckApi.getGroupManager();
            Group group = groupManager.getGroup(groupName);

            group.getName();
            ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".groups." + group.getName(), false);
            ConfigFile.saveConfig(mainInstance.zoneConfig);
        }
    }
    /****************************************************************************/
    /* MÉTHODE PERMETTANT DE SUPPRIMER UN GROUPE EN PARTICULIER DANS LA RÉGION */
    /**************************************************************************/


    /***************************************************************/
    /* MÉTHODE RÉCUPÉRANT LA PREMIÈRE POSITION DÉFINIT DE LA ZONE */
    /*************************************************************/
    public static List<Double> getFirstPos(String zoneName) {

        String XValue = ConfigFile.getString(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".firstposition.x");
        String YValue = ConfigFile.getString(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".firstposition.y");
        String ZValue = ConfigFile.getString(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".firstposition.z");

        if(CustomMethod.isDouble(XValue) && CustomMethod.isDouble(YValue) && CustomMethod.isDouble(ZValue)) {

            return List.of(Double.parseDouble(XValue), Double.parseDouble(YValue), Double.parseDouble(ZValue));
        }

        return null;
    }
    /***************************************************************/
    /* MÉTHODE RÉCUPÉRANT LA PREMIÈRE POSITION DÉFINIT DE LA ZONE */
    /*************************************************************/


    /************************************************************/
    /* MÉTHODE PERMETTANT DE DÉFINIR LA PREMIÈRE POSITION ZONE */
    /**********************************************************/
    public static void setFirstPos(String zoneName, double x, double y, double z) {

        ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".firstposition.x", String.valueOf(x));
        ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".firstposition.y", String.valueOf(y));
        ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".firstposition.z", String.valueOf(z));

        ConfigFile.saveConfig(mainInstance.zoneConfig);
    }
    /************************************************************/
    /* MÉTHODE PERMETTANT DE DÉFINIR LA PREMIÈRE POSITION ZONE */
    /**********************************************************/


    /**************************************************************/
    /* MÉTHODE RÉCUPÉRANT LA DEUXIÈME POSITION DÉFINIT DE LA ZONE */
    /*************************************************************/
    public static List<Double> getSecondPos(String zoneName) {

        String XValue = ConfigFile.getString(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".secondposition.x");
        String YValue = ConfigFile.getString(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".secondposition.y");
        String ZValue = ConfigFile.getString(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".secondposition.z");

        if(CustomMethod.isDouble(XValue) && CustomMethod.isDouble(YValue) && CustomMethod.isDouble(ZValue)) {

            return List.of(Double.parseDouble(XValue), Double.parseDouble(YValue), Double.parseDouble(ZValue));
        }

        return null;
    }
    /**************************************************************/
    /* MÉTHODE RÉCUPÉRANT LA DEUXIÈME POSITION DÉFINIT DE LA ZONE */
    /*************************************************************/


    /************************************************************/
    /* MÉTHODE PERMETTANT DE DÉFINIR LA DEUXIÈME POSITION ZONE */
    /**********************************************************/
    public static void setSecondPos(String zoneName, double x, double y, double z) {

        ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".secondposition.x", String.valueOf(x));
        ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".secondposition.y", String.valueOf(y));
        ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".secondposition.z", String.valueOf(z));

        ConfigFile.saveConfig(mainInstance.zoneConfig);
    }
    /************************************************************/
    /* MÉTHODE PERMETTANT DE DÉFINIR LA DEUXIÈME POSITION ZONE */
    /**********************************************************/


    /***************************************************/
    /* MÉTHODE RÉCUPÉRANT LE MONDE DÉFINIT DE LA ZONE */
    /*************************************************/
    public static World getWorld(String zoneName) {

        String worldName = ConfigFile.getString(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".world");
        if(worldName == null) { worldName = " "; }

        return Bukkit.getServer().getWorld(worldName);
    }
    /***************************************************/
    /* MÉTHODE RÉCUPÉRANT LE MONDE DÉFINIT DE LA ZONE */
    /*************************************************/


    /******************************************************/
    /* MÉTHODE PERMETTANT DE DÉFINIR LE MONDE DE LA ZONE */
    /****************************************************/
    public static void setWorld(String zoneName, String WorldName) {

        ConfigFile.set(mainInstance.zoneConfig, "ZONE." + zoneName.toUpperCase() + ".world", WorldName);

        ConfigFile.saveConfig(mainInstance.zoneConfig);
    }
    /*****************************************************/
    /* MÉTHODE PERMETTANT DE DÉFINIR LE MONDE DE LA ZONE */
    /****************************************************/


    /***********************************************************************************/
    /* MÉTHODE PERMETTANT RÉCUPÉRER LA LOCALISATION DE LA PREMIÈRE POSITION DE LA ZONE */
    /**********************************************************************************/
    public static Location getFirstLocationPos(String zoneName) {

        World world = getWorld(zoneName);
        List<Double> firstPos = getFirstPos(zoneName);

        if(firstPos == null) return null;

        double X = firstPos.get(0);
        double Y = firstPos.get(1);
        double Z = firstPos.get(2);

        return new Location(world, X, Y, Z);
    }
    /***********************************************************************************/
    /* MÉTHODE PERMETTANT RÉCUPÉRER LA LOCALISATION DE LA PREMIÈRE POSITION DE LA ZONE */
    /**********************************************************************************/


    /**************************************************************************/
    /* MÉTHODE RÉCUPÉRANT LA LOCALISATION DE LA DEUXIÈME POSITION DE LA ZONE */
    /*************************************************************************/
    public static Location getSecondLocationPos(String zoneName) {

        World world = getWorld(zoneName);
        List<Double> secondPos = getSecondPos(zoneName);

        if(secondPos == null) return null;

        double X = secondPos.get(0);
        double Y = secondPos.get(1);
        double Z = secondPos.get(2);

        return new Location(world, X, Y, Z);
    }
    /**************************************************************************/
    /* MÉTHODE RÉCUPÉRANT LA LOCALISATION DE LA DEUXIÈME POSITION DE LA ZONE */
    /*************************************************************************/


    /****************************************************************************/
    /* MÉTHODE PERMETTANT DE RÉCUPÉRER LE CENTRE DE LA RÉGION, S'IL ELLE EXISTE */
    /****************************************************************************/
    public static Location getCenterRegion(String zoneName) {

        if(hasRegion(zoneName)) {

            Location firstPoint = new Location(getWorld(zoneName), getFirstPos(zoneName).get(0), getFirstPos(zoneName).get(1), getFirstPos(zoneName).get(2));
            Location secondPoint = new Location(getWorld(zoneName), getSecondPos(zoneName).get(0), getSecondPos(zoneName).get(1), getSecondPos(zoneName).get(2));

            return firstPoint.toVector().getMidpoint(secondPoint.toVector()).toLocation(getWorld(zoneName));
        }

        return null;
    }
    /****************************************************************************/
    /* MÉTHODE PERMETTANT DE RÉCUPÉRER LE CENTRE DE LA RÉGION, S'IL ELLE EXISTE */
    /****************************************************************************/
}
/*******************************************************************************************************/
/* ~~~ GESTIONNAIRE DE ZONE DU SERVEUR ; EN LIEN AVEC LE FICHIER DE CONFIGURATION 'ZONECONFIG.YML' ~~~ */
/*******************************************************************************************************/