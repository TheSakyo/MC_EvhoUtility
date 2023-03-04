package fr.TheSakyo.EvhoUtility.runnable.pnighttime;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.DescParseTickFormat;

import java.util.UUID;


/****************************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE LEVER DU SOLEIL */ 
/****************************************************************/

public class TimesSunrisePlayer extends BukkitRunnable {
	
	private UUID playerUUID;
	
	public TimesSunrisePlayer(UUID uuid) { this.playerUUID = uuid; }
	
	@Override
	public void run() {
		
    	if(UtilityMain.playertimes == null) { return; }
		
		else if(UtilityMain.playertimes != null && UtilityMain.playertimes.isEmpty()) { return; }
    	
		else { 
			
			for(UUID uuid : UtilityMain.playertimes) {
				
				if(uuid == playerUUID) {
					
					UtilityMain.getInstance().usertime.setUserTime(Bukkit.getServer().getPlayer(uuid), DescParseTickFormat.parse("sunrise"), true); }
				
			} 
			
		}
		
	}

}

/****************************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE LEVER DU SOLEIL */ 
/****************************************************************/