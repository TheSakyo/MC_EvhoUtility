package fr.TheSakyo.EvhoUtility.managers;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.runnable.RunningAFK;
import net.luckperms.api.model.user.User;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.UUID;


public class AFKManager {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public AFKManager(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */

	
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* PARTIE CONFIGURATION POUR LE SYSTEM DE AFK/AUTOAFK */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	  
	  /*********************************************************/
	  /* METHODE POUR PERMETTRE DE FAIRE FONCTIONNER L'AUTOAFK */ 
	  /*********************************************************/
	  
	  public void runAFK(UUID uuid, Boolean afk, int time) {

		final Player player = Bukkit.getServer().getPlayer(uuid);
		final User user = CustomMethod.getLuckPermUserOffline(player.getUniqueId());

		  			/* ---------------------------------------------- */

    	// Annule la boucle et enlève le joueur de la boucle //
    	if(main.AFKRun.containsKey(uuid)) {

            //Enlève le joueur du temps de la boucle "AutoAFK" //
            main.time.remove(uuid);
		    if(!main.AFKRun.get(uuid).isCancelled()) { main.AFKRun.get(uuid).cancel(); main.AFKRun.remove(uuid); }
			//Enlève le joueur du temps de la boucle "AutoAFK" //
		}
    	// Annule la boucle et enlève le joueur de la boucle //

		  			/* ---------------------------------------------- */

	  	// On essaie de démarrer une boucle avec une variable de type "MAP" pour attribuer un timer a un joueur pour l'autoAFK //
	  	// Sinon une erreur est envoyée à la console //
		try { main.AFKRun.put(uuid, new RunningAFK(uuid, user, afk, time).runTaskTimerAsynchronously(main, 0L, 20L));
		} catch(NullPointerException e) { e.printStackTrace(System.err); }
		catch (IllegalPluginAccessException ignored) {}
		// On essaie de démarrer une boucle avec une variable de type "MAP" pour attribuer un timer à chaque joueur pour l'autoAFK //
	  	// Sinon une erreur est envoyée à la console //
	  }
	  
	  /*********************************************************/
	  /* METHODE POUR PERMETTRE DE FAIRE FONCTIONNER L'AUTOAFK */ 
	  /*********************************************************/
	  
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* PARTIE CONFIGURATION POUR LE SYSTEM DE AFK/AUTOAFK */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */	
}
