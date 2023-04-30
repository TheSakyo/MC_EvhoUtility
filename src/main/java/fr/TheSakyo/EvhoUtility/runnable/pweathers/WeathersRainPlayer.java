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

	private final UUID playerUUID;

	public WeathersRainPlayer(UUID uuid) { this.playerUUID = uuid; }

	@Override
	public void run() {

    	if(UtilityMain.playerWeathers != null && !UtilityMain.playerWeathers.isEmpty()) {

			for(UUID uuid : UtilityMain.playerWeathers) {

				if(uuid == playerUUID) {

					Player p = Bukkit.getServer().getPlayer(uuid);
					if(p.getPlayerWeather() != WeatherType.DOWNFALL) p.setPlayerWeather(WeatherType.DOWNFALL);
				}
			}
		}
	}
}
/****************************************************************/
/* BOUCLE QUI CHANGE LE TEMPS DU JOUEUR VERS LE LEVER DU SOLEIL */ 
/****************************************************************/
