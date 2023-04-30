package fr.TheSakyo.EvhoUtility.runnable.pdaytime;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.DescParseTickFormat;

import java.util.UUID;


/*****************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE MIDI */ 
/*****************************************************/

public class TimesNoonPlayer extends BukkitRunnable {
	
	private final UUID playerUUID;
	
	public TimesNoonPlayer(UUID uuid) { this.playerUUID = uuid; }
	
	@Override
	public void run() {

		if(UtilityMain.playerTimes != null && !UtilityMain.playerTimes.isEmpty()) {

			for(UUID uuid : UtilityMain.playerTimes) {
				
				if(uuid == playerUUID) {

					UtilityMain.getInstance().userTime.setUserTime(Bukkit.getServer().getPlayer(uuid), DescParseTickFormat.parse("noon"), true);
				}
				
			} 
			
		}
		
	}

}

/*****************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE MIDI */ 
/*****************************************************/
