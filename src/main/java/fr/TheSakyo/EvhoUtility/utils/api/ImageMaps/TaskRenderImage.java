package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class TaskRenderImage extends BukkitRunnable {
	
	// Variables Utiles //
	
	private Player player;
	
	private String path;
	
	private String name;
	
	// Variables Utiles //
	
	
	
	// Constructeur de la class "TaskRenderImage //
	public TaskRenderImage(Player player, String path, String name) {
		
		this.player = player;
		this.path = path;
		this.name = name;
	}
	// Constructeur de la class "TaskRenderImage //


	
	
   /********************************/
   /* CRÉER LE RENDU DE L'IMAGEMAP */
   /********************************/
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		
		// Essait de crÉer le rendu de "l'imagemap", sinon rien ne se passe //
		try {
			
			final BufferedImage image = ImageHelper.getImage(path, player);  //Variable "image" qui récupère l'image du lien ou du fichier définit par le joueur
			
			MapView map; //Variable MapView (item de la map avec ses informations)
			
			map = Bukkit.getServer().createMap(player.getWorld()); //Créer une nouvelle map dans le serveur
    		
    		map = RenderHelper.resetRenderers(map); //Supprime toutes les informations de la map
    		
    		map.setScale(MapView.Scale.FAR); //Définit l'échelle de la map
    		
    		map.setUnlimitedTracking(false); //Supprime les joueurs afficher dans la map
    		
    		map.addRenderer(new ImageMapRenderer(ImageUtils.scale(image, 128, 128))); //Ajoute le rendu redimensionné de la variable "image"
			
    		ItemStack item = new ItemStack(Material.FILLED_MAP); //Créer un item de map (pour le donner ensuite au joueur)
			MapMeta meta = (MapMeta) item.getItemMeta(); //Récupère les données de l'item créer
			meta.setMapId(map.getId()); //Donne l'id de la map créer comme id de l'item
			meta.setLore(List.of(ChatColor.AQUA + "ImageMap Importée")); //Ajoute une petite description
			item.setItemMeta(meta); //Définit les nouvelles donées créer à l'item créer
			
			player.getInventory().addItem(item); //Donne l'item au joueur
			
			final ImageMap imageMap = new ImageMap(UUID.randomUUID(), name, path, map.getId(), item); //Création de "l'imagemap" avec les différentes informations créer si dessus
			
			final ImageMapYML ImageMapYML = new ImageMapYML(imageMap.getName()); //Créer un fichier de configuration ayant le nom de "l'imagemap"
			
			ImageMapYML.write(imageMap, item); //Ecrit les informations de "l'imagemap" dans le fichier de configuration créé
			
			UtilityMain.getInstance().mapmanager.addImageMap(name, imageMap); //Ajoute "l'imagemap" a la class "ImageMapManager"
			
			// Message de succés au joueur //
			if(ImageHelper.isURL(path)) {
				
				player.sendMessage(UtilityMain.getInstance().prefix + ChatColor.GREEN + "Importation de l'image " + ChatColor.GOLD + name + ChatColor.GREEN + " avec l'URL suivant : " + ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + path);

			} else {
				
				player.sendMessage(UtilityMain.getInstance().prefix + ChatColor.GREEN + "Importation de l'image " + ChatColor.GOLD + name + ChatColor.GREEN + " avec le chemin suivant : " + ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "/utils/maps/images/" + path);
			}
			
			// Message de succés au joueur //
			
		} catch (IOException e) { return; }
		// Essait de créer le rendu de "l'imagemap", sinon rien ne se passe //
	}
	
   /********************************/
   /* CRÉER LE RENDU DE L'IMAGEMAP */
   /********************************/
}
