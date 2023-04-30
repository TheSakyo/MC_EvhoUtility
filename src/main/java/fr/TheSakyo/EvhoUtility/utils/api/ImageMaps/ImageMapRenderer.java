package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageMapRenderer extends MapRenderer {
	
	// Variables Utiles //
	
	private boolean hasRendered;
	
	private BufferedImage image;
	
	// Variables Utiles //
	
	
	// Constructeur de la class "ImageMapRenderer" //
	public ImageMapRenderer(BufferedImage image) {
		
		this.image = image;
		this.hasRendered = true;
	}
	// Constructeur de la class "ImageMapRenderer" //
	
	
	// Méthode pour déssiner l'image récupérée par le joueur dans l'item de map //
	@Override
	public void render(MapView mapview, MapCanvas mapcanvas, Player p) {
		
		if(hasRendered) {
			
			mapcanvas.drawImage(0, 0, image);
			hasRendered = false;
		}
	}
	// Méthode pour déssiner l'image récupérée par le joueur dans l'item de map //
	
	
	
	public void setShouldRender(boolean shouldRender) { this.hasRendered = shouldRender; } //Méthode pour définir le type "boolean" 'hasRenderer'
	
	
	public void setImage(BufferedImage image) { this.image = image; } //Méthode pour définir l'image

}
