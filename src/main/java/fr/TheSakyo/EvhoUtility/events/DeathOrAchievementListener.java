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
	private static UtilityMain mainInstance = UtilityMain.getInstance();
	/* Récupère la class "Main" */
	

 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
 /* PARTIE EVENEMENENT DE LA MORT OU DE L'ACHIEVEMENT D'UN JOUEUR */ 
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */		
	
   /*************************************/
   /* EVENEMENENT QUAND LE JOUEUR MEURT */ 
   /*************************************/
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        
      if(ConfigFile.getString(mainInstance.DeathOrAchievementconfig, "DeathMessage").equalsIgnoreCase("off")) {

          e.deathMessage(CustomMethod.StringToComponent(""));
      }
    }
    /*************************************/
    /* EVENEMENENT QUAND LE JOUEUR MEURT */ 
    /*************************************/


    
    /******************************************************/
    /* EVENEMENENT QUAND LE JOUEUR OBTIENT UN ACHIEVEMENT */ 
    /******************************************************/

    @EventHandler(priority=EventPriority.NORMAL)
    public void AdvancementDoneEvent(PlayerAdvancementDoneEvent e) {
    	
        Player p = e.getPlayer();
        Advancement advancement = e.getAdvancement();        
        
        if(ConfigFile.getString(mainInstance.DeathOrAchievementconfig, "AchievementMessage").equalsIgnoreCase("off")) {
        	
        	for(String c : advancement.getCriteria()) {

                p.getAdvancementProgress(advancement).revokeCriteria(c);

            }
        }
    }


    @EventHandler(priority=EventPriority.NORMAL)
    public void AdvancementCriterionGrantEvent(PlayerAdvancementCriterionGrantEvent e) {

        if(ConfigFile.getString(mainInstance.DeathOrAchievementconfig, "AchievementMessage").equalsIgnoreCase("off")) {

        	e.setCancelled(true); //Annule l'action par défaut
        }
    }

    /******************************************************/
    /* EVENEMENENT QUAND LE JOUEUR OBTIENT UN ACHIEVEMENT */ 
    /******************************************************/
    
    
    
    /***************************************/
    /* EVENEMENENT QUAND LE JOUEUR REJOINT */ 
    /***************************************/
    public static void onJoin(PlayerJoinEvent e) {
    	
        Player p = e.getPlayer();
        
       //Essait d'ajouter le joueur à la gestion d'achievemets "AdvancementsManager", s'il y est pas
       if(!mainInstance.advManager.hasPlayer(p)) mainInstance.advManager.addPlayer(p);
	   
    }
    /***************************************/
    /* EVENEMENENT QUAND LE JOUEUR REJOINT */ 
    /***************************************/


    /***************************************/
    /* EVENEMENENT QUAND LE JOUEUR REJOINT */
    /***************************************/
    public static void onQuit(PlayerQuitEvent e) {

        Player p = e.getPlayer();

       //Essait de supprimer le joueur à la gestion d'achievemets "AdvancementsManager", s'il y est pas
       if(mainInstance.advManager.hasPlayer(p)) mainInstance.advManager.removePlayer(p);

    }
    /***************************************/
    /* EVENEMENENT QUAND LE JOUEUR REJOINT */
    /***************************************/
     
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* PARTIE EVENEMENENT DE LA MORT OU DE L'ACHIEVEMENT D'UN JOUEUR */ 
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */		
}

