package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.io.File;
import java.io.IOException;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class DataLoader {
	

	/*****************************************************************/
	/* MÉTHODE POUR RECHARGER LES DIFFERENTES IMAGEMAPS ENREGISTRÉES */
	/*****************************************************************/
	
	public static void loadMaps() throws IOException {
		
		final File imageDir = new File(UtilityMain.getInstance().getDataFolder() + "/utils/maps/images/");
		
		final File imageMapDir = new File(UtilityMain.getInstance().getDataFolder() + "/utils/maps/saved_maps/");
		
		
		
		if(!imageDir.exists()) { if(!imageDir.mkdirs()) { imageDir.createNewFile(); } }
		
		
		if(!imageMapDir.exists()) { if(!imageMapDir.mkdirs()) { imageMapDir.createNewFile(); } }
			
		
		

		
		final File[] files = imageMapDir.listFiles(); //Vérifie tous les fichiers du dossier "ImageMapDir" avec une variable "files"
		
		// Vérifie si la variable n'est pas "NULL" puis met a jour les différents fichiers "imagemap" //
		if(files != null) {
			
			ImageMap imagemap;
			
			ImageMapYML config;
			
			for(File file : files) {
				
				if(file.getName().endsWith(".yml")) {
					
					String filename = file.getName().replaceAll(".yml", "");
					
					config = new ImageMapYML(filename);
					
					imagemap = config.read();
					
					
					UtilityMain.getInstance().mapmanager.addImageMap(filename, imagemap);
					
					new TaskUpdateImage(imagemap).runTaskAsynchronously(UtilityMain.getInstance());
					
				}
			}
		}
		
		// Vérifie si la variable n'est pas "NULL" puis met a jour les différents fichiers "imagemap" //
		
	}
	
	/*****************************************************************/
	/* MÉTHODE POUR RECHARGER LES DIFFERENTES IMAGEMAPS ENREGISTRÉES */
	/*****************************************************************/
}
