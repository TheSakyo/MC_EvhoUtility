package fr.TheSakyo.EvhoUtility.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.TheSakyo.EvhoUtility.utils.api.ImageMaps.ImageMap;

public class ImageMapManager {
	
	private Map<String, ImageMap> imageMaps; //Variable "Map" qui contient des 'imagemaps'
	
	
	public ImageMapManager() { this.imageMaps = new HashMap<String, ImageMap>(); } //Constructeur de la class "ImageMapManager"
		

	public void addImageMap(String name, ImageMap imagemap) { this.imageMaps.put(name, imagemap); } //Ajoute une "imagemap"

	
	public void removeImageMap(String name) { this.imageMaps.remove(name); } //Supprime une "imagemap"
	
	
	public Map<String, ImageMap> getImageMaps() { return imageMaps; } //Récupère tous les "imagemaps"
	
	
	public ImageMap getImageMap(String name) { return getImageMaps().get(name); } //Récupère une "imagemap" spécfique
	
	
	// Petite méthode pour supprimer "imagemap" entièrement //
	public void DeleteImageMap(Player p, String name, File file) { 
		
		ItemStack item = getImageMap(name).getitem();
		
		if(p != null) {
			
		   if(p.getInventory().contains(item)) p.getInventory().removeItem(item);
		}

		removeImageMap(name);
		
		file.delete();
		
		for(World world : Bukkit.getServer().getWorlds()) {
			
			for(Entity entities : Bukkit.getServer().getWorld(world.getName()).getEntities()) {
				
				if(entities instanceof ItemFrame) {
					
					if(((ItemFrame) entities).getItem().equals(item)) {
						
						entities.remove();
						
					}
				}
			}
		}
	}
	// Petite méthode pour supprimer "imagemap" entièrement //

}
