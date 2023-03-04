package fr.TheSakyo.EvhoUtility.commands.inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class EnderChestCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public EnderChestCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	/******************************************************/
	/* PARTIE COMMANDE POUR VOIR L'ENDERCHEST D'UN JOUEUR */ 
    /******************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p){

			if(p.hasPermission("evhoutility.enderchest")) {
				
				if(args.length == 0) {
					
					p.openInventory(p.getEnderChest());
					
					main.invUpdate.updateInventory(p, ChatColor.WHITE + "Votre EnderChest");

				} else if(args.length != 0) {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.enderchest.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								
								if(target.getName().equalsIgnoreCase(p.getName())) {
									
									p.openInventory(p.getEnderChest());
									
									main.invUpdate.updateInventory(p, ChatColor.WHITE + "Votre EnderChest");
									return true;
								}
								
								p.openInventory(target.getEnderChest());
								
								main.invUpdate.updateInventory(p, ChatColor.WHITE + "EnderChest de " + ChatColor.GOLD + target.getName());
								
							} else {
								
								p.sendMessage(main.prefix + ChatColor.RED + "Le joueur est introuvable !");
							}
						
						} else { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /enderchest ou /ender sans arguments"); }
						
					} else {
						
						if(p.hasPermission("evhoutility.enderchest.other")) {
							
							p.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas entrer plus d'un argument !");
							p.sendMessage(" ");
							p.sendMessage(main.prefix + ChatColor.YELLOW.toString() + ChatColor.UNDERLINE.toString() + "INFO :" + ChatColor.RED + " Le premier argument demande un joueur");
						
						} else { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /enderchest ou /ender sans arguments"); }
					}
					
				}
				
			} else {
				
				p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !");
			}
		
		} else {
			
			sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux pour voir votre enderchest ou celui d'un joueur !");
		}
		
		return false;
	}
	
	/******************************************************/
	/* PARTIE COMMANDE POUR VOIR L'ENDERCHEST D'UN JOUEUR */ 
    /******************************************************/

}
