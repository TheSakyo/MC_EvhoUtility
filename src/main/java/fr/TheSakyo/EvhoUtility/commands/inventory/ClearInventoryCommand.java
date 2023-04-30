package fr.TheSakyo.EvhoUtility.commands.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ClearInventoryCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public ClearInventoryCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	//Variable liste pour détecter les items de l'inventaire du joueur
	public List<ItemStack> invItem = new ArrayList<>();
	
	
	
	/**********************************************************/
	/* PARTIE COMMANDE POUR SUPPRIMER L'INVENTAIRE DU JOUEUR */ 
    /*********************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.clear")) {
				
				if(args.length == 0) {
					
					for(ItemStack item : p.getInventory().getStorageContents()) {
						
				        if(item != null) p.getInventory().removeItem(item); invItem.add(item);
				    }
					
					if(invItem.isEmpty()) p.sendMessage(main.prefix + ChatFormatting.RED + "Votre inventaire est déjà vide");
					else {
						
						invItem.clear();
						p.sendMessage(main.prefix + ChatFormatting.GRAY + "Votre inventaire a été supprimer");
					}

				} else {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.clear.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								for(ItemStack item : target.getInventory().getStorageContents()) {
									
							        if(item != null) target.getInventory().removeItem(item); invItem.add(item);
							    }
								
								if(invItem.isEmpty()) { 

									p.sendMessage(main.prefix + ChatFormatting.RED + "L'Inventaire de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà vide");
								
								} else {
									
									invItem.clear();
									
									p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "L'Inventaire de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a été supprimer");
									target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre inventaire a été supprimer par " + ChatFormatting.GOLD + p.getName());
								}
							
							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
						
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /clearinventory ou /clearinv sans arguments");
						
					} else {
						
						if(p.hasPermission("evhoutility.clear.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /clearinventory ou /clearinv sans arguments");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour supprimer votre inventaire, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					for(ItemStack item : target.getInventory().getStorageContents()) {
						
				        if(item != null) { target.getInventory().removeItem(item); invItem.add(item); }
				    }
					
					if(invItem.isEmpty()) { 

						sender.sendMessage(main.prefix + ChatFormatting.RED + "L'Inventaire de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà vide");
					
					} else {
						
						invItem.clear();
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "L'Inventaire de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a été supprimer");
						target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre inventaire a été supprimer par " + ChatFormatting.GOLD + "La Console");
					}
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
		}
		
		return false;
	}
	
	/**********************************************************/
	/* PARTIE COMMANDE POUR SUPPRIMER L'INVENTAIRE DU JOUEUR */ 
    /*********************************************************/
}
