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
	private final UtilityMain main;
	public VanishUtils(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
  

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* PARTIE MÉTHODES POUR LE SYSTÈME DE VANISH */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */	

	/******************************************************/
	/* MÉTHODE "BOOLEAN" POUR VÉRIFIER LE JOUEUR "VANISH" */
	/******************************************************/

	@SuppressWarnings("all")
	public boolean playerVanished(Player player) { return playerVanished(player.getUniqueId()); }


	public boolean playerVanished(UUID uuid) {

		User user = CustomMethod.getLuckPermUserOffline(uuid);

		/*************************************/

		//Si le joueur contient la permission du mode "Vanish"
		if(CustomMethod.hasLuckPermission(uuid, "EvhoProxy.vanished")) {

			//Ajoute le joueur dans la liste des joueurs étant "vanish", s'il y est pas
			if(!main.VANISHED.contains(uuid)) main.VANISHED.add(uuid);

			/***********************/

			//Appel la méthode pour verifier le "vanish" du joueur (devient invisible)
			GetVanishedPlayer(main.VANISHED, uuid);
			return true;

			//sinon si le joueur ne contient pas la permission du mode "Vanish", s'il y est
		} else {

			//Enlève le joueur dans la liste des joueurs étant "vanish"
            main.VANISHED.remove(uuid);

			/***********************/

			//Appel la méthode pour verifier le "Vanish" du joueur (devient visible)
			GetVanishedPlayer(main.VANISHED, uuid);
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
   private boolean GetVanishedPlayer(List<UUID> vanish, UUID uuid) {
	    
	    BukkitScheduler Run = Bukkit.getServer().getScheduler();

		  if(vanish.contains(uuid)) {
			  
			  	Run.scheduleSyncDelayedTask(main, new Runnable() {
				   
				   @Override
				   public void run() {
					   
					for(Player players : Bukkit.getServer().getOnlinePlayers()) { 

					  if(players.getUniqueId() != uuid) {

						  Player targetPlayer = Bukkit.getPlayer(uuid);
						  if(targetPlayer != null && players.canSee(targetPlayer)) players.hidePlayer(main, targetPlayer);
					  }
					}
				  }
			   }, 0L);
			   
			 return true;
			 
		  } else {
			 
			  	Run.scheduleSyncDelayedTask(main, new Runnable() {
				   
				   @Override
				  	public void run() {
					   
					  for(Player players : Bukkit.getServer().getOnlinePlayers()) {

						  if(players != null && players.getUniqueId() != uuid) {

							  Player targetPlayer = Bukkit.getPlayer(uuid);
							  if(targetPlayer != null || !players.canSee(targetPlayer)) players.showPlayer(main, targetPlayer);
						  }
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
  /* PARTIE MÉTHODES POUR LE SYSTÈME DE VANISH */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	
}
