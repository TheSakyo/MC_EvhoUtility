package fr.TheSakyo.EvhoUtility.events;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.event.player.PlayerQuitEvent;

public class DeathOrAchievementListener implements Listener {

	/* Récupère la class "Main" */
	private static final UtilityMain mainInstance = UtilityMain.getInstance();
	/* Récupère la class "Main" */

 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
 /* PARTIE ÉVÈNEMENT DE LA MORT OU D'un ACHIEVEMENT D'UN JOUEUR */
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	
   /*************************************/
   /* ÉVÈNEMENT QUAND LE JOUEUR MEURT */
   /*************************************/
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        
      if(ConfigFile.getString(mainInstance.DeathOrAchievementConfig, "DeathMessage").equalsIgnoreCase("off")) {

          e.deathMessage(CustomMethod.StringToComponent(""));
      }
    }
    /*************************************/
    /* ÉVÈNEMENT QUAND LE JOUEUR MEURT */
    /*************************************/


    
    /******************************************************/
    /* ÉVÈNEMENT QUAND LE JOUEUR OBTIENT UN ACHIEVEMENT */
    /******************************************************/

    @EventHandler(priority=EventPriority.NORMAL)
    public void AdvancementDoneEvent(PlayerAdvancementDoneEvent e) {
    	
        Player p = e.getPlayer();
        Advancement advancement = e.getAdvancement();        
        
        if(ConfigFile.getString(mainInstance.DeathOrAchievementConfig, "AchievementMessage").equalsIgnoreCase("off")) {
        	
        	for(String c : advancement.getCriteria()) {

                p.getAdvancementProgress(advancement).revokeCriteria(c);

            }
        }
    }


    @EventHandler(priority=EventPriority.NORMAL)
    public void AdvancementCriterionGrantEvent(PlayerAdvancementCriterionGrantEvent e) {

        if(ConfigFile.getString(mainInstance.DeathOrAchievementConfig, "AchievementMessage").equalsIgnoreCase("off")) {

        	e.setCancelled(true); //Annule l'action par défaut
        }
    }

    /******************************************************/
    /* ÉVÈNEMENT QUAND LE JOUEUR OBTIENT UN ACHIEVEMENT */
    /******************************************************/
    
    
    
    /**************************************/
    /* ÉVÈNEMENT QUAND LE JOUEUR REJOINT */
    /************************************/
    public static void onJoin(PlayerJoinEvent e) {
    	
        Player p = e.getPlayer();
        
       //Essaie d'ajouter le joueur à la gestion des achievements "AdvancementsManager", s'il y est pas
       if(!mainInstance.advManager.hasPlayer(p)) mainInstance.advManager.addPlayer(p);
	   
    }
    /**************************************/
    /* ÉVÈNEMENT QUAND LE JOUEUR REJOINT */
    /************************************/


    /************************************/
    /* ÉVÈNEMENT QUAND LE JOUEUR QUITTE */
    /***********************************/
    public static void onQuit(PlayerQuitEvent e) {

        Player p = e.getPlayer();

       //Essaie de supprimer le joueur à la gestion des achievements "AdvancementsManager", s'il y est pas
       if(mainInstance.advManager.hasPlayer(p)) mainInstance.advManager.removePlayer(p);
    }
    /************************************/
    /* ÉVÈNEMENT QUAND LE JOUEUR QUITTE */
    /***********************************/

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
    /* PARTIE ÉVÈNEMENT DE LA MORT OU D'un ACHIEVEMENT D'UN JOUEUR */
    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
}

