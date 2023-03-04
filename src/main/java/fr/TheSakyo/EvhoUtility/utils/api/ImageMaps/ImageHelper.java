package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class ImageHelper {
	
	// Vérifie si l'argument est un lien //
	public static boolean isURL(String path)  { return path.startsWith("http://") || path.startsWith("https://"); }
	// Vérifie si l'argument est un lien //
	
	
	
	// Méthode pour récupérer l'image //
	public static BufferedImage getImage(String path, Player player) throws IOException {
		
	  //Si l'argument "path" est un lien, on lit l'image  //	
	  if(isURL(path)) {
		  
		  final URL url = new URL(path);
		  
		  return ImageIO.read(url);
	  
	  //Sinon, si l'argument "path" est un l'emplacement d'un fichier, on essaie de le trouver dans le dossier '/utils/maps/images/' //
	  } else {
		  
		  final File ImageFile = new File(UtilityMain.getInstance().getDataFolder() + "/utils/maps/images/" + path);
		  
		  if(ImageFile.exists()) { return ImageIO.read(ImageFile); }
		  
		  else { 
			  
			  if(player == null) { 
				  
				  throw new IOException(ChatColor.RED + "L'Image spécifiée est introuvable dans /utils/maps/images/ ou le lien est incorrect !");
			 
			  } else { 
			
				 player.sendMessage(UtilityMain.getInstance().prefix + ChatColor.RED + "L'Image spécifiée est introuvable dans /utils/maps/images/ ou le lien est incorrect !");
				 
				 throw new IOException(player.getName() + " n'a pas réussi à charger l'image demandée !");
			  }
		  }
	  }
	}
	// Méthode pour récupérer l'image //
}
