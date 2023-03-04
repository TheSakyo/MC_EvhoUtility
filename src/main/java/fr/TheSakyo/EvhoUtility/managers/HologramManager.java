package fr.TheSakyo.EvhoUtility.managers;

import java.util.*;

import dependancies.emoji4j.EmojiUtils;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.utils.entity.entities.HologramEntity;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import io.papermc.paper.adventure.PaperAdventure;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.entity.Player;

public class HologramManager {

	/* Récupère la class "Main" */
	private static UtilityMain mainInstance = UtilityMain.getInstance();
	/* Récupère la class "Main" */

	private static ConfigFile HG_CONFIG = mainInstance.holoconfig;

 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
 /* PARTIE CONFIGURATION HOLOGRAMME */ 
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

	/**********************************************************************/
	/* PARTIE SUPRESSION DE LA SAUVEGARDE D'UN HOLOGRAMME EN PARTICULIER */
	/********************************************************************/

	/**
	 * Supprime un enregistrement d'hologrammes dans son fichier de configuration correspondant.
	 *
	 * @param title Le Libellé de l'enregistrement.
	 */
	public static void unsaveHologram(String title) {

		if(ConfigFile.contains(HG_CONFIG, "Holograms." + title.toUpperCase())) ConfigFile.removeKey(HG_CONFIG, "Holograms." + title.toUpperCase());
		ConfigFile.saveConfig(HG_CONFIG);
	}

	/*********************************************************************/
	/* PARTIE SUPRESSION DE LA SAUVEGARDE D'UN HOLOGRAMM EN PARTICULIER */
	/*******************************************************************/

			/* ----------------------------------------- */

	/***************************************************************/
	/* PARTIE SUPRESSION DE LA SAUVEGARDE DE TOUS LES HOLOGRAMMES */
	/*************************************************************/

	/**
	 * Supprime plusieurs enregistrements d'hologrammes dans leur fichier de configuration correspondant.
	 *
	 * @param holograms Des enregistrements de plusieurs hologrammes a déchargé ({@literal String} : Son Libellé et {@literal List<HologramEntity>} : Ses différents hologrammes associés)
	 */
	public static void unsaveHolograms(Set<String> holograms) { holograms.forEach(HologramManager::unsaveHologram); }

	/***************************************************************/

	/* PARTIE SUPRESSION DE LA SAUVEGARDE DE TOUS LES HOLOGRAMMES */
	/*************************************************************/
	
	
	
	/***************************************************************************************/
	/* PARTIE CRÉATION DE LA SAUVEGARDE D'UN HOLOGRAMMES DANS UN FICHIER DE CONFIGURATION */
	/***************************************************************************************/

	/**
	 * Sauvegarde un enregistrement d'hologrammes dans son fichier de configuration correspondant.
	 *
	 * @param title Le Libellé de l'enregistrement.
	 * @param holograms Les hologrammes associés au Libellé.
	 */
	public static void saveHologram(String title, List<HologramEntity> holograms) {

		int index = 0;
		for(HologramEntity hg : holograms) {

			String content = CustomMethod.ComponentToString(PaperAdventure.asAdventure(hg.getEntity().getCustomName()));
			Location location = hg.getLocation();

			ConfigFile.set(HG_CONFIG, "Holograms." + title.toUpperCase() + ".lines." + index + ".id", hg.getEntity().getId());
			ConfigFile.set(HG_CONFIG, "Holograms." + title.toUpperCase() + ".lines." + index + ".text", content);
			ConfigFile.set(HG_CONFIG, "Holograms." + title.toUpperCase() + ".lines." + index + ".x", location.getX());
			ConfigFile.set(HG_CONFIG, "Holograms." + title.toUpperCase() + ".lines." + index + ".y", location.getY());
			ConfigFile.set(HG_CONFIG, "Holograms." + title.toUpperCase() + ".lines." + index + ".z", location.getZ());
			ConfigFile.set(HG_CONFIG, "Holograms." + title.toUpperCase() + ".lines." + index + ".pitch", String.valueOf(location.getPitch()));
			ConfigFile.set(HG_CONFIG, "Holograms." + title.toUpperCase() + ".lines." + index + ".yaw", String.valueOf(location.getYaw()));
			ConfigFile.set(HG_CONFIG, "Holograms." + title.toUpperCase() + ".lines." + index + ".world", location.getWorld().getName());

			index++;
		}

		ConfigFile.saveConfig(HG_CONFIG);
	}
	
	/***************************************************************************************/
	/* PARTIE CRÉATION DE LA SAUVEGARDE D'UN HOLOGRAMMES DANS UN FICHIER DE CONFIGURATION */
	/***************************************************************************************/


						/* ----------------------------------------- */

	/***********************************************************************************************/
	/* PARTIE CRÉATION DE LA SAUVEGARDE DE TOUS LES HOLOGRAMMES DANS UN FICHIER DE CONFIGURATION  */
	/**********************************************************************************************/

	/**
	 * Sauvegarde plusieurs enregistrements d'hologrammes dans leur fichier de configuration correspondant.
	 *
	 * @param holograms Des enregistrements de plusieurs hologrammes a sauvegardé ({@literal String} : Son Libellé et {@literal List<HologramEntity>} : Ses différents hologrammes associés)
	 */
	public static void saveHolograms(Map<String, List<HologramEntity>> holograms) { holograms.forEach(HologramManager::saveHologram); }

	/***********************************************************************************************/

	/* PARTIE CRÉATION DE LA SAUVEGARDE DE TOUS LES HOLOGRAMMES DANS UN FICHIER DE CONFIGURATION  */
	/**********************************************************************************************/
	
							/* ----------------------------------------- */
							/* ----------------------------------------- */
							/* ----------------------------------------- */

	/********************************************************************************************************/
	/* PARTIE CHARGEMENT/DÉCHARGEMENT DES SAUVEGARDES D'HOLOGRAMME DEFINIT SUR UN FICHIER DE CONFIGURATION */
	/*******************************************************************************************************/

	/**
	 * Recharge les hologrammes enregistrés dans le fichier de configuration des hologrammes.
	 *
	 * @param p Le joueur qui subira les changements
	 * @param saveConfig Si oui ou non, on recharge l'hologramme et son enregistrement dans le fichier de configuration
	 */
	public static void loadHolograms(Player p, boolean saveConfig) {

		ConfigurationSection configSection = mainInstance.holokeys;
		List<String> keyList = configSection.getKeys(false).stream().toList();

						/* ----------------------------------------- */

		keyList.forEach(key -> loadHologram(p, key.toUpperCase(), saveConfig));
	}

	/**
	 * Recharge un hologramme enregistré dans le fichier de configuration des hologrammes.
	 *
	 * @param p Le joueur qui subira les changements
	 * @param key L'enregistrement de l'hologramme en question
	 * @param saveConfig Si oui ou non, on recharge l'hologramme et son enregistrement dans le fichier de configuration
	 */
	public static void loadHologram(Player p, String key, boolean saveConfig) {

		if(!ConfigFile.contains(HG_CONFIG, "Holograms." +  key.toUpperCase())) return;

						/* ----------------------------------------- */

		List<String> lineOriginal = new ArrayList<String>();


		ConfigurationSection keyHologram = ConfigFile.getConfigurationSection(HG_CONFIG, "Holograms." + key + ".lines");
		List<String> listHologram = keyHologram.getKeys(false).stream().toList();

						/* ----------------------------- */

		int id = ConfigFile.getInt(HG_CONFIG, "Holograms." + key.toUpperCase() + ".lines." + 0 + ".id");
		double locationX = ConfigFile.getDouble(HG_CONFIG, "Holograms." + key.toUpperCase() + ".lines." + 0 + ".x");
		double locationY = ConfigFile.getDouble(HG_CONFIG, "Holograms." + key.toUpperCase() + ".lines." + 0 + ".y");
		double locationZ = ConfigFile.getDouble(HG_CONFIG, "Holograms." + key.toUpperCase() + ".lines." + 0 + ".z");
		float locationPITCH = Float.parseFloat(ConfigFile.getString(HG_CONFIG, "Holograms." + key.toUpperCase() + ".lines." + 0 + ".pitch"));
		float locationYAW = Float.parseFloat(ConfigFile.getString(HG_CONFIG, "Holograms." + key.toUpperCase() + ".lines." + 0 + ".yaw"));
		String worldName = ConfigFile.getString(HG_CONFIG, "Holograms." + key.toUpperCase() + ".lines." + 0 + ".world");

		World world = Bukkit.getServer().getWorld(worldName);
		Location location = new Location(world, locationX, locationY, locationZ);

								/* ----------------------------- */

		listHologram.forEach(hg -> {

			String content = ConfigFile.getString(HG_CONFIG, "Holograms." + key.toUpperCase() + ".lines." + hg + ".text");
			lineOriginal.add(content);
		});

								/* ----------------------------- */

		if(saveConfig) unsaveHologram(key.toUpperCase());

		String lines = EmojiUtils.emojify(String.join("//n", lineOriginal)); // Essaie de convertir le texte a affiché

		HologramEntity hologram = new HologramEntity(mainInstance, p, Integer.valueOf(id), key.toUpperCase(), lines, location, saveConfig, true);
		mainInstance.HOLOGRAMS.putIfAbsent(key.toUpperCase(), hologram.getLine()); // On ajoute toutes les instances d'hologrammes construits
		if(mainInstance.HOLOGRAMS.containsKey(key.toUpperCase())) mainInstance.HOLOGRAMS.replace(key.toUpperCase(), hologram.getLine()); // Remplace l'enregistrement de l'hologramme
	}

					/* ------------------------------------------------------------------------------------------------------------- */

	/**
	 * Décharge les hologrammes enregistrés dans le Serveur.
	 *
	 * @param p Le joueur qui subira les changements
	 * @param unsaveConfig Si oui ou non, on supprime l'enregistrement de l'hologramme dans le fichier de configuration
	 */
	public static void unloadHolograms(Player p, boolean unsaveConfig) { mainInstance.HOLOGRAMS.keySet().forEach(holoTitle -> unloadHologram(p, holoTitle, unsaveConfig)); }

	/**
	 * Décharge un hologramme enregistrés dans le Serveur.
	 *
	 * @param p Le joueur qui subira les changements
	 * @param unsaveConfig Si oui ou non, on supprime l'enregistrement de l'hologramme dans le fichier de configuration
	 */
	public static void unloadHologram(Player p, String key, boolean unsaveConfig) {

		List<HologramEntity> hologramEntities = new ArrayList<HologramEntity>();

		mainInstance.HOLOGRAMS.get(key).forEach(hologramEntities::add);
		hologramEntities.forEach(hologramEntity -> hologramEntity.remove(p, unsaveConfig));
		hologramEntities.get(0).clearLine();
		hologramEntities.clear();

					/* --------------------------------------------------- */

		// Si le Joueur est null, on essaie de supprimer l'hologramme de la liste des hologrammes
		if(p == null) if(mainInstance.HOLOGRAMS.containsKey(key)) mainInstance.HOLOGRAMS.remove(key);
	}

	/********************************************************************************************************/
	/* PARTIE CHARGEMENT/DÉCHARGEMENT DES SAUVEGARDES D'HOLOGRAMME DEFINIT SUR UN FICHIER DE CONFIGURATION */
	/*******************************************************************************************************/

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* PARTIE CONFIGURATION HOLOGRAMME */ 
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	
}
