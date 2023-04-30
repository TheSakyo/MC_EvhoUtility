package fr.TheSakyo.EvhoUtility.commands.entities;

import java.io.File;
import java.util.List;
import java.util.Set;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.ImageMaps.TaskRenderImage;
import fr.TheSakyo.EvhoUtility.utils.api.ImageMaps.TaskUpdateImage;
 
public class ImageCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
    public ImageCommand(UtilityMain pluginMain) { this.main = pluginMain; }
    /* Récupère la class "Main" */
    
    
 
    /*******************************************************/
    /* PARTIE COMMANDE POUR IMPORTER UNE IMAGE DANS LE JEU */ 
    /*******************************************************/
    
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.image")) {
				
				if(args.length == 0) {
					
					p.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez entrez des arguments ! <import, remove, load, tp ou list> [<name>] [<URL>]");
					
				} else if(args.length == 1) { 
					
					if(args[0].equalsIgnoreCase("list")) {
						
						Set<String> setKey = main.mapManager.getImageMaps().keySet();

						List<String> images = setKey.stream().toList(); // Liste des noms d'images enregistrés
						
						if(images.isEmpty()) {
		    				
							p.sendMessage(main.prefix + ChatFormatting.RED + "Aucune image(s) importée(s) enregistrer !");
		    			
						} else if(images.size() == 1) {
		    				
		    				p.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement l'image importée " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + images.get(0) + ChatFormatting.GRAY + " dans le serveur !");
		    			
						} else {
						
							p.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
			    			p.sendMessage(" ");
			    			p.sendMessage(" ");
			    			
			    			p.sendMessage(ChatFormatting.AQUA.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des images dans le serveur :");
			    			
			    			for(String IMAGE : images) {

			    				p.sendMessage(" ");

								Component imgName = CustomMethod.StringToComponent(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + IMAGE);

								imgName = imgName.clickEvent(ClickEvent.runCommand("/image tp " + IMAGE));
								imgName = imgName.hoverEvent(HoverEvent.showText(CustomMethod.StringToComponent("Cliquez pour vous y téléporter")));


								Component message = CustomMethod.StringToComponent(ChatFormatting.WHITE + "- ").append(imgName);
								p.sendMessage(message);
			    			}
			    			
			    			p.sendMessage(" ");
			    			p.sendMessage(" ");
			    			p.sendMessage(ChatFormatting.GRAY + "===========================");
						}
						
					} else if(args[0].equalsIgnoreCase("remove")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image remove <name>");
				 	else if(args[0].equalsIgnoreCase("load")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image load <name>");
				    else if(args[0].equalsIgnoreCase("tp")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image tp <name>");
					else if(args[0].equalsIgnoreCase("import")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image import <name> <URL>");
					else p.performCommand("image");

				} else if(args.length == 2) {
					
					 if(args[0].equalsIgnoreCase("remove")) {
						 
						  final String name = args[1];
		                	
		                  if(main.mapManager.getImageMaps().containsKey(name)) {
		                		
		                	  //Supprime l'image
		                	  main.mapManager.DeleteImageMap(p, name, new File(UtilityMain.getInstance().getDataFolder() + "/utils/maps/saved_maps/", name + ".yml"));
		                	  p.sendMessage(main.prefix + ChatFormatting.GREEN + "L'Image " + ChatFormatting.GOLD + name + ChatFormatting.GREEN + " a été supprimer");
		                	  
		                  } else p.sendMessage(main.prefix + ChatFormatting.RED + "L'Image demandée n'éxiste pas !");
						
					} else if(args[0].equalsIgnoreCase("tp")) {
						
						 final String name = args[1];
						 boolean isTeleported = false;

						 /********************************/

						 if(main.mapManager.getImageMaps().containsKey(name)) {
							 
							 final ItemStack item = main.mapManager.getImageMap(name).getItem();
		                		
							 for(World world : Bukkit.getServer().getWorlds()) {
									
									for(Entity entities : Bukkit.getServer().getWorld(world.getName()).getEntities()) {
										
										if(entities instanceof ItemFrame) { //Vérifie si l'entité est un cadre
											
											if(((ItemFrame) entities).getItem().equals(item)) {
												
												p.teleport(entities); //Téléporte le joueur au cadre possédant l'image
												isTeleported = true;
											}
										}
									}
								}
		                	  
		                	  if(isTeleported) p.sendMessage(main.prefix + ChatFormatting.GREEN + "Vous avez été téléporter vers l'image " + ChatFormatting.GOLD + args[1] + ChatFormatting.GREEN + " !");
							  else p.sendMessage(main.prefix + ChatFormatting.RED + "Impossible de vous téléportez vers l'image demandée !");

		                  } else p.sendMessage(main.prefix + ChatFormatting.RED + "L'Image demandée n'éxiste pas !");
					
					} else if(args[0].equalsIgnoreCase("load")) {
						
						final String name = args[1];
		              	
		              	if(main.mapManager.getImageMaps().containsKey(name)) {

		                  	final ItemStack item = main.mapManager.getImageMap(name).getItem();

		              		if(!p.getInventory().contains(item)) { 
		              		 	
		              		    //Recharge l'image
		              			new TaskUpdateImage(main.mapManager.getImageMap(name)).runTaskAsynchronously(UtilityMain.getInstance());

		              			//Donne l'image au joueur
		              			p.getInventory().addItem(item);
		              			p.sendMessage(main.prefix + ChatFormatting.GREEN + "L'Image " + ChatFormatting.GOLD + name + ChatFormatting.GREEN + " a été recharger, vous pouvez maintenant le replacer si besoin");
		              		
		              		} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous possédez déja l'image dans votre inventaire." + ChatFormatting.GOLD + " Vous pouvez l'utiliser pour le replacer si besoin");
		              		
		              	} else p.sendMessage(main.prefix + ChatFormatting.RED + "L'Image demandée n'éxiste pas, essayez de le créer en faisant /image import <name> <URL>");
					
					} else if(args[0].equalsIgnoreCase("import")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image import <name> <URL>");
					else if(args[0].equalsIgnoreCase("list")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image list");
					else p.performCommand("image");
						
				} else if(args.length == 3) {
					
					if(args[0].equalsIgnoreCase("import")) {
						
						final String path = args[2];
	                	final String name = args[1];
	                	
	                	if(main.mapManager.getImageMaps().containsKey(name)) p.sendMessage(main.prefix + ChatFormatting.RED + "L'Image éxiste déja, essayez de le récupèrer en faisant /image load <name>");
	                	else new TaskRenderImage(p, path, name).runTaskAsynchronously(main); //Importe une image
						
					} else if(args[0].equalsIgnoreCase("remove")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image remove <name>");
					else if(args[0].equalsIgnoreCase("tp")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image tp <name>");
					else if(args[0].equalsIgnoreCase("load")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image load <name>");
					else if(args[0].equalsIgnoreCase("list")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image list");
					else p.performCommand("image");
					
				} else {
					 	
					if(args[0].equalsIgnoreCase("list")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image list");
					else if(args[0].equalsIgnoreCase("remove")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image remove <name>");
					else if(args[0].equalsIgnoreCase("load")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image load <name>");
					else if(args[0].equalsIgnoreCase("tp")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image tp <name>");
					else if(args[0].equalsIgnoreCase("import")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image import <name> <URL>");
					else p.performCommand("image");
				} 
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez entrez des arguments ! <remove ou list> [<name>] [<URL>]");
				sender.sendMessage(" ");
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Les arguments <import, tp et load> fonctionnent qu'en jeux !");
				
			} else if(args.length == 1) { 
				
				if(args[0].equalsIgnoreCase("list")) {
					
					Set<String> setKey = main.mapManager.getImageMaps().keySet();
					List<String> keyList = setKey.stream().toList();
					
					if(keyList.isEmpty()) sender.sendMessage(main.prefix + ChatFormatting.RED + "Aucune image(s) importée(s) enregistrer");
					else if(keyList.size() == 1) {
	    				
						sender.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement l'image importée " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + keyList.get(0) + ChatFormatting.GRAY + " dans le serveur !");
	    			
					} else {
					
						sender.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
						sender.sendMessage(" ");
						sender.sendMessage(" ");
		    			
						sender.sendMessage(ChatFormatting.AQUA.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des images dans le serveur :");

						/********************************/

		    			for(String key : keyList) {
		    				
		    				sender.sendMessage(" ");
		    				sender.sendMessage(ChatFormatting.WHITE + "- " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + key);
		    			}

						/********************************/

						sender.sendMessage(" ");
		    			sender.sendMessage(" ");
		    			sender.sendMessage(ChatFormatting.GRAY + "===========================");
					}
					
			   } else if(args[0].equalsIgnoreCase("remove")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image remove <name>");
				else if(args[0].equalsIgnoreCase("load")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour recharger une image importée !");
				else if(args[0].equalsIgnoreCase("tp")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous téléportez vers une image importée !");
				else if(args[0].equalsIgnoreCase("import")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour importer une image !");
				else Bukkit.getServer().dispatchCommand(sender, "image");
				
			} else if(args.length == 2) {
				
				 if(args[0].equalsIgnoreCase("remove")) {

					  final String name = args[1];

	                  if(main.mapManager.getImageMaps().containsKey(name)) {

	                	  //Supprime l'image
	                	  main.mapManager.DeleteImageMap(null, name, new File(UtilityMain.getInstance().getDataFolder() + "/utils/maps/saved_maps/", name + ".yml"));
	                	  sender.sendMessage(main.prefix + ChatFormatting.GREEN + "L'Image " + ChatFormatting.GOLD + name + ChatFormatting.GREEN + " a été supprimer");

	                  } else { sender.sendMessage(main.prefix + ChatFormatting.RED + "L'Image demandée n'éxiste pas !"); }

				 } else if(args[0].equalsIgnoreCase("list")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image list");
				 else if(args[0].equalsIgnoreCase("load")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour recharger une image importée !");
			     else if(args[0].equalsIgnoreCase("tp")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous téléportez vers une image importée !");
				 else if(args[0].equalsIgnoreCase("import")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour importer une image !");
				 else Bukkit.getServer().dispatchCommand(sender, "image");
					
			} else {
				
				if(args[0].equalsIgnoreCase("remove")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image remove <name>");
			 	else if(args[0].equalsIgnoreCase("list")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /image list");
				else if(args[0].equalsIgnoreCase("load")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour recharger une image importée !");
			    else if(args[0].equalsIgnoreCase("tp")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous téléportez vers une image importée !");
				else if(args[0].equalsIgnoreCase("import")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour importer une image !");
				else Bukkit.getServer().dispatchCommand(sender, "image");
			}
		}
		
		return false;
	}
    
    /*******************************************************/
    /* PARTIE COMMANDE POUR IMPORTER UNE IMAGE DANS LE JEU */ 
    /*******************************************************/
 
}
