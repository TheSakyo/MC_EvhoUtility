package fr.TheSakyo.EvhoUtility.utils.custom.methods;

import java.util.List;
import java.util.UUID;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.luckperms.api.model.user.User;

public class VanishUtils {

	/* Récupère la class "Main" */
	private UtilityMain main;
	public VanishUtils(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
  
  
  
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* PARTIE METHODES POUR LE SYSTEME DE VANISH */ 
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */	

	/******************************************************/
	/* MÉTHODE "BOOLEAN" POUR VÉRIFIER LE JOUEUR "VANISH" */
	/******************************************************/
	@SuppressWarnings("all")
	public boolean PlayerVanished(Player p) {

      User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

	  //Si le joueur contient la permission du mode "Vanish"
	  if(CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished") == true) {

		  //Ajoute le joueur dans listes des joueurs étant "vanish", s'il y est pas
		  if(!main.VANISHED.contains(p.getUniqueId())) main.VANISHED.add(p.getUniqueId());

		 //Appel la méthode pour vérfifier le "vanish" du joueur (devient invisible)
		  GetVanishedPlayer(main.VANISHED, p);

		  return true;
	   }

	  //sinon si le joueur ne contient pas la permission du mode "Vanish", s'il y est
	  else {

		//Enl�ve le joueur dans listes des joueurs étant "vanish"
		if(main.VANISHED.contains(p.getUniqueId())) main.VANISHED.remove(p.getUniqueId());

		//Appel la méthode pour vérfifier le "Vanish" du joueur (devient visible)
		GetVanishedPlayer(main.VANISHED, p);

		return false;
	  }
	}
	
	/******************************************************/
	/* MÉTHODE "BOOLEAN" POUR VÉRIFIER LE JOUEUR "VANISH" */
	/******************************************************/
	
	
   /*********************************************************************************/
   /* METHODE "BOOLEAN" POUR CACHER OU AFFICHER LE JOUEUR "VANISH" [méthode locale] */
   /*********************************************************************************/
   @SuppressWarnings("all")
   private boolean GetVanishedPlayer(List<UUID> vanish, Player p) {
	    
	    BukkitScheduler Run = Bukkit.getServer().getScheduler();

		  if(vanish.contains(p.getUniqueId())) {
			  
			  	Run.scheduleSyncDelayedTask(main, new Runnable() {
				   
				   @Override
				   public void run() {
					   
					for(Player players : Bukkit.getServer().getOnlinePlayers()) { 
						
					  
					  if(players != p) { if(players.canSee(p)) players.hidePlayer(main, p); }
					}
				  }
			   }, 0L);
			   
			 return true;
			 
		  } else {
			 
			  	Run.scheduleSyncDelayedTask(main, new Runnable() {
				   
				   @Override
				  	public void run() {
					   
					  for(Player players : Bukkit.getServer().getOnlinePlayers()) { 						  
						  
						  if(players != p) { if(!players.canSee(p)) players.showPlayer(main, p); }   
					  }
				  }
			   }, 0L);

			 return false;
		}
   }
   /*********************************************************************************/
   /* METHODE "BOOLEAN" POUR CACHER OU AFFICHER LE JOUEUR "VANISH" [méthode locale] */
   /*********************************************************************************/
  
  
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* PARTIE METHODES POUR LE SYSTEME DE VANISH */ 
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	
}
