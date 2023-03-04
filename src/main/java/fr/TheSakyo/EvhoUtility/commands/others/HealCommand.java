package fr.TheSakyo.EvhoUtility.commands.others;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class HealCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public HealCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	

	
   /**********************************************************/
   /* 			PARTIE COMMANDE POUR REGENERER LE JOUEUR     */ 
   /* INFO : Ne fonctionne pas en mode spéctateur et créatif */
   /**********************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.heal")) {
				
				if(args.length == 0) {
					
					if(p.getGameMode() == GameMode.CREATIVE) {
						
						p.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas vous soignez en mode de jeux Créatif");
						
					} else if(p.getGameMode() == GameMode.SPECTATOR) {
						
						p.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas vous soignez en mode de jeux Spéctateur");
						
					} else {
						
						if(p.getHealth() == p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) {
							
							p.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas vous soignez car vous n'avez pas des dégats");
							
						} else {
							
							p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
							p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous étes désormais soigner");
							
							
							if(p.getFoodLevel() != 20) {
								
								p.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous pouvez vous rassasier avec /food");
							}
						}
					}

				} else if(args.length != 0) {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.heal.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								
								if(target.getGameMode() == GameMode.CREATIVE) {
									
									p.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " ne peut pas être soigner en mode de jeux Créatif");
									
								} else if(target.getGameMode() == GameMode.SPECTATOR) {
									
									p.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " ne peut pas être soigner en mode de jeux Spéctateur");
									
								} else {
									
									if(target.getHealth() == target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) {
										
										p.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " ne peut pas être soigner car il n'a pas des dégats");
										
									} else {
										
										target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
										
										p.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " est désormais soigner");
										
										target.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous êtes désormais soigner par " + ChatColor.GOLD + p.getName());
										
									}
								}
							
							} else {
								
								p.sendMessage(main.prefix + ChatColor.RED + "Le joueur est introuvable !");
							}
						
						} else { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /heal sans arguments"); }
						
					} else {
						
						if(p.hasPermission("evhoutility.heal.other")) {
							
							p.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						
						} else { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /heal sans arguments"); }
					}
					
				}
				
			} else { p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !"); }

		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux pour vous soignez, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					if(target.getGameMode() == GameMode.CREATIVE) {
						
						sender.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " ne peut pas être soigner en mode de jeux Créatif");
						
					} else if(target.getGameMode() == GameMode.SPECTATOR) {
						
						sender.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " ne peut pas être soigner en mode de jeux Spéctateur");
						
					} else {
						
						if(target.getHealth() == target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) {
							
							sender.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " ne peut pas être soigner car il n'a pas des dégats");
							
						} else {
							
							target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
							
							sender.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " est désormais soigner");
							
							target.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous êtes désormais soigner par " + ChatColor.GOLD + "La Console");
							
						}
					}
				
				} else {
					
					sender.sendMessage(main.prefix + ChatColor.RED + "Le joueur est introuvable !");
				}
				
			} else {
				
				sender.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas entrer plus d'un argument !");
			}
		}
		
		return false;
	}
	
   /**********************************************************/
   /* 			PARTIE COMMANDE POUR REGENERER LE JOUEUR     */ 
   /* INFO : Ne fonctionne pas en mode spéctateur et créatif */
   /**********************************************************/
}
