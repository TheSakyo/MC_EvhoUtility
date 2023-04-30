package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.bukkit.Bukkit;

import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class TaskUpdateImage extends BukkitRunnable {

	private final ImageMap imagemap; //Variable pour récupérer "l'imagemap"

	
	public TaskUpdateImage(ImageMap imagemap) { this.imagemap = imagemap; } //Constructeur de la class "TaskUpdateImage"
	

	
   /***********************************/
   /* RECHARGE LE RENDU DE L'IMAGEMAP */
   /***********************************/
	
	@Override
	public void run() {
		
		// Essaie de recharger le rendu de "l'imagemap", sinon une erreur est afficher //
		try {
			
			final BufferedImage image = ImageHelper.getImage(imagemap.getPath(), null); //Variable "image" qui récupère l'image de "l'imagemap"
			
			MapView map; //Variable MapView (item de la map avec ses informations)
			 
			map = Bukkit.getServer().getMap(imagemap.getMapID()); //Récupère la map dans le serveur ayant l'id de "l'imagemap" enregistrée
    		
    		map = RenderHelper.resetRenderers(map); //Supprime toutes les informations de la map
    		
    		map.setScale(MapView.Scale.FAR); //Définit l'échelle de la map
    		
    		map.setUnlimitedTracking(false); //Supprime les joueurs afficher dans la map
    		
    		map.addRenderer(new ImageMapRenderer(ImageUtils.scale(image, 128, 128))); //Ajoute le rendu redimensionné de la variable "image"
				
		} catch(IOException e) {
			
			// Message d'erreur //
			UtilityMain.getInstance().getLogger().warning("L'Image suivant n'a pas réussie à être recharger : " + imagemap.getPath());
			UtilityMain.getInstance().getLogger().warning(e.getMessage());
			// Message d'erreur //
		}
		// Essaie de recharger le rendu de "l'imagemap", sinon une erreur est afficher //
	}
	
	/***********************************/
	/* RECHARGE LE RENDU DE L'IMAGEMAP */
	/***********************************/

}
