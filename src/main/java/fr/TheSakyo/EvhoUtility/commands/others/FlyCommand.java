package fr.TheSakyo.EvhoUtility.commands.others;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class FlyCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public FlyCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
   /*****************************************************************/
   /* PARTIE COMMANDE POUR ACTIVER/DESACTIVER LE MODE VOL AU JOUEUR */ 
   /*    INFO : Ne fonctionne pas en mode spéctateur et créatif     */
   /*****************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.fly")) {
				
				if(args.length == 0) {
					
					if(p.getGameMode() == GameMode.CREATIVE) {
						
						p.sendMessage(main.prefix + ChatColor.RED + "Vous utilisez déjà le Vol en mode de jeux Créatif");
						
					} else if(p.getGameMode() == GameMode.SPECTATOR) {
						
						p.sendMessage(main.prefix + ChatColor.RED + "Vous utilisez déjà le Vol en mode de jeux Spéctateur");
						
					} else {
						
						if(p.getAllowFlight()) {
							
							p.setAllowFlight(false);
							p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous n'êtes plus en mode Vol");
							
						} else if(!p.getAllowFlight()) {
							
							p.setAllowFlight(true);
							p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous êtes désormais en mode Vol");
						}
						
					}

				} else if(args.length != 0) {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.fly.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								
								if(target.getGameMode() == GameMode.CREATIVE) {
									
									p.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " utilise déjà le Vol en mode de jeux Créatif");
									
								} else if(target.getGameMode() == GameMode.SPECTATOR) {
									
									p.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " utilise déjà le Vol en mode de jeux Spéctateur");
									
								} else {
									
									if(target.getAllowFlight()) {
										
										target.setAllowFlight(false);
										
										p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Mode Vol de " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a été désactivé");
										target.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Mode Vol a été désactivé par " + ChatColor.GOLD + p.getName());
										
									} else if(!target.getAllowFlight()) {
										
										target.setAllowFlight(true);
										
										p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Mode Vol de " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a été activé");
										target.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Mode Vol a été activé par " + ChatColor.GOLD + p.getName());
									}
									
								}
							
							} else { p.sendMessage(main.prefix + ChatColor.RED + "Le joueur est introuvable !"); }
						
						} else { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /fly sans arguments"); }
						
					} else {
						
						if(p.hasPermission("evhoutility.fly.other")) {
							
							p.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						
						} else { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /fly sans arguments"); }
					}
					
				}
				
			} else {
				
				p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !");
			}
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux pour vous attribuer le mode Vol, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					if(target.getGameMode() == GameMode.CREATIVE) {
						
						sender.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " utilise déjà le Vol en mode de jeux Créatif");
						
					} else if(target.getGameMode() == GameMode.SPECTATOR) {
						
						sender.sendMessage(main.prefix + ChatColor.GOLD + target.getName() + ChatColor.RED + " utilise déjà le Vol en mode de jeux Spéctateur");
						
					} else {
						
						if(target.getAllowFlight()) {
							
							target.setAllowFlight(false);
							
							sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Mode Vol de " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a été désactivé");
							target.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Mode Vol a été désactivé par " + ChatColor.GOLD + "La Console");
							
						} else if(!target.getAllowFlight()) {
							
							target.setAllowFlight(true);
							
							sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Mode Vol de " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a été activé");
							target.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Mode Vol a été activé par " + ChatColor.GOLD + "La Console");
						}
					}

				} else { sender.sendMessage(main.prefix + ChatColor.RED + "Le joueur est introuvable !"); }
				
			} else { sender.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas entrer plus d'un argument !"); }
		}
		
		return false;
	}
	
   /*****************************************************************/
   /* PARTIE COMMANDE POUR ACTIVER/DESACTIVER LE MODE VOL AU JOUEUR */ 
   /*    INFO : Ne fonctionne pas en mode spéctateur et créatif     */
   /*****************************************************************/
	
}
