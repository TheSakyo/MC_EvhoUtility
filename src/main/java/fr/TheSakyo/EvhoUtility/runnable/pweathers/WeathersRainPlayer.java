package fr.TheSakyo.EvhoUtility.runnable.pweathers;

import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.TheSakyo.EvhoUtility.UtilityMain;

import java.util.UUID;

/****************************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE LEVER DU SOLEIL */ 
/****************************************************************/

public class WeathersRainPlayer extends BukkitRunnable {

	private UUID playerUUID;

	public WeathersRainPlayer(UUID uuid) { this.playerUUID = uuid; }

	@Override
	public void run() {

    	if(UtilityMain.playerweathers == null) { return; }

		else if(UtilityMain.playerweathers != null && UtilityMain.playerweathers.isEmpty()) { return; }

		else {

			for(UUID uuid : UtilityMain.playerweathers) {

				if(uuid == playerUUID) {

					Player p = Bukkit.getServer().getPlayer(uuid);

					if(p.getPlayerWeather() != WeatherType.DOWNFALL) p.setPlayerWeather(WeatherType.DOWNFALL);
					else { return; }
				}
			}
		}
	}
}
/****************************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE LEVER DU SOLEIL */ 
/****************************************************************/
