package fr.TheSakyo.EvhoUtility.runnable.pdaytime;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.DescParseTickFormat;

import java.util.UUID;


/******************************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE COUCHER DU SOLEIL */ 
/******************************************************************/

public class TimesSunsetPlayer extends BukkitRunnable {
	
	private UUID playerUUID;
	
	public TimesSunsetPlayer(UUID uuid) { this.playerUUID = uuid; }

	@Override
	public void run() {
		
    	if(UtilityMain.playertimes == null) { return; }
		
		else if(UtilityMain.playertimes != null && UtilityMain.playertimes.isEmpty()) { return; }
    	
		else { 
			
			for(UUID uuid : UtilityMain.playertimes) {
				
				if(uuid == playerUUID) { UtilityMain.getInstance().usertime.setUserTime(Bukkit.getServer().getPlayer(uuid), DescParseTickFormat.parse("sunset"), true); }
				
			} 
			
		}
		
	}

}

/******************************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE COUCHER DU SOLEIL */ 
/******************************************************************/
