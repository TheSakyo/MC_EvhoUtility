package fr.TheSakyo.EvhoUtility.managers;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class UserTimeManager {
	
	
	  /*********************************************************************************/
	  /* MÉTHODE POUR PERMETTRE DE DEFINIR LE TEMPS DU MONDE POUR UN JOUEUR SPECIFIQUE */
	  /*********************************************************************************/

	  public void setUserTime(final Player player, final Long ticks, final Boolean relative) {
		
		//Si le temps en ticks est null, on remet le temps par défaut
		if(ticks == null) { player.resetPlayerTime(); } 

	    //Sinon, on traduit le temps en ticks et on lui met le temps précisé
	    else {
	  
		  final World world = player.getWorld();
		  long time = player.getPlayerTime();
		  time -= time % 24000;
		  time += 24000 + ticks;
		  if(relative) { time -= world.getTime(); }
		  
		  //On donne le temps au joueur
		  player.setPlayerTime(time, relative);
	    }
	  }
	  
	  /*********************************************************************************/
	  /* MÉTHODE POUR PERMETTRE DE DEFINIR LE TEMPS DU MONDE POUR UN JOUEUR SPECIFIQUE */
	  /*********************************************************************************/

}
