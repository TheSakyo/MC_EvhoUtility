package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class ImageMap {
	
	// Variables Utiles //
	
	private UUID uuid;
	
	private String name;
	
	private String path;
	
	private int mapID;
	
	private ItemStack item;
	
	// Variables Utiles //
	
	
	// Constructeur de la class "ImageMap" //
	public ImageMap(UUID uuid, String name, String path, int mapID, ItemStack item) {
		
		this.uuid = uuid;
		this.name = name;
	    this.path = path;
	    this.mapID = mapID;
	    this.item = item;
	}
	
	// Constructeur de la class "ImageMap" //
	
	
	public UUID getUUID() { return uuid; } //Récupère l'uuid de "l'imagemap"
	
	
	public String getName() { return name; } //Récupère le nom de "l'imagemap" qui a été définit à la création
	
	
	public String getPath() { return path; } //Récupère le lien ou le fichier de "l'imagemap" qui a été définit à la création
	
	
	public int getMapID() { return mapID; }  //Récupère l'ID de "l'imagemap"
	
	
	public ItemStack getitem() { return item; }  //Récupère l'item de "l'imagemap"
}
