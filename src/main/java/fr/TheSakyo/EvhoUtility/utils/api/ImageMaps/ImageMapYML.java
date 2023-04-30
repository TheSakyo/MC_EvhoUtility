package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ImageMapYML {
	
	// Variables Utiles //

    private final File ConfigFile;
	
	private final YamlConfiguration yamlconfiguration;
	
	// Variables Utiles //
	
	
	
	
	/* Constructeur de la class "ImageMapYML" */
	public ImageMapYML(String ImageMapString) {

        this.ConfigFile = new File(UtilityMain.getInstance().getDataFolder() + "/utils/maps/saved_maps/", ImageMapString + ".yml"); //Créer un fichier de configuration
		this.yamlconfiguration = YamlConfiguration.loadConfiguration(ConfigFile); //Recharge la configuration
	}
	/* Constructeur de la class "ImageMapYML" */
	
	
	
	
	/* Méthode pour écrire dans la configuration du constructeur */
	public void write(ImageMap imagemap, ItemStack item) {
		
		final ConfigurationSection config = this.yamlconfiguration.createSection("image"); //Cr�er une section "image"

		/*****************************************************/

		config.set("uuid", imagemap.getUUID().toString()); //Enregistre l'uuid de "l'imagemap"
		config.set("url", imagemap.getPath()); //Enregistre le lien ou le fichier de "l'imagemap" qui a été défini à la création
		config.set("id", imagemap.getMapID()); //Enregistre l'id de "l'imagemap"
		config.set("item", item); //Enregistre l'item de "l'imagemap"

		/*****************************************************/

		save(imagemap.getName()); //Sauvegarde la configuration
	}
	/* Méthode pour écrire dans la configuration du constructeur */
	
	
	
	
	/* Méthode pour lire dans la configuration du constructeur */
	public ImageMap read() {
		
		final ConfigurationSection config = this.yamlconfiguration.getConfigurationSection("image"); //R�cup�re la section "image"

		/*****************************************************/

		final String UUIDStr = config.getString("uuid"); //Récupère l'uuid de "l'imagemap" enregistré

		final String URLStr = config.getString("url"); //Récupère le lien ou le fichier de "l'imagemap" qui a été défini à la création enregistrée
		
		final int Id = config.getInt("id"); //Récupère l'ID de "l'imagemap" enregistrée
		
		final ItemStack item = config.getItemStack("item"); //Récupère l'item de "l'imagemap" enregistré

		/*****************************************************/
		
		return new ImageMap(UUID.fromString(UUIDStr), ConfigFile.getName(), URLStr, Id, item); //Recréer "l'imagemap" avec les informations enregistrées
		
	}
	/* Méthode pour lire dans la configuration du constructeur */
	
	
	
	
	/* Méthode pour sauvegarder la configuration du constructeur */
	private void save(String name) { 
		
		// On essaie de sauvegarder sinon une erreur s'affiche //
		
		try { this.yamlconfiguration.save(this.ConfigFile); }
		catch(IOException e) {
			
		  UtilityMain.getInstance().getLogger().warning("Impossible de sauvegarder le fichier de configuration de ' " + name + ".yml" + " '");
		  e.printStackTrace(System.err);
		}
		
		// On essaie de sauvegarder sinon une erreur s'affiche //
	}
	/* Méthode pour sauvegarder la configuration du constructeur */
	
}
