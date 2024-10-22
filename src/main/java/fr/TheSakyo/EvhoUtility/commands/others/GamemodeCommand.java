package fr.TheSakyo.EvhoUtility.commands.others;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class GamemodeCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public GamemodeCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	// Variable montrant les différents arguments possibles pour la commande //
	

	ChatFormatting R = ChatFormatting.RED; //Variable raccourci pour définir la couleur "Rouge"
	ChatFormatting Y = ChatFormatting.YELLOW; //Variable raccourci pour définir la couleur "Jaune"
	
	String numberArgs = Y + "0" + R + "/" + Y + "1" + R + "/" + Y + "2" + R + "/" + Y + "3";
	
	String CharArgs = Y + "survival" + R + "/" + Y + "creative" + R + "/" + Y + "adventure" + R + "/" + Y + "spectator";
	
	String IndexCharArgs = Y + "s" + R + "/" + Y + "c" + R + "/" + Y + "a" + R + "/" + Y + "spec";
	
	// Variable montrant les différents arguments possibles pour la commande //
	
	
	/************************************************************/
	/* PARTIE COMMANDE POUR CHANGER LE MODE DE JEUX D'UN JOUEUR */ 
	/************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {	
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.gamemode")) {
				
				if(args.length == 0) {
					
					p.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez essayer /gamemode ou /gm avec les arguments suivant :");
					p.sendMessage(" "); 					
					p.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "1er Choix :" + ChatFormatting.WHITE + " (" + numberArgs + ChatFormatting.WHITE + ")");
					p.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "2ème Choix :" + ChatFormatting.WHITE + " (" + CharArgs + ChatFormatting.WHITE + ")");
					p.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "3ème Choix :" + ChatFormatting.WHITE + " (" + IndexCharArgs + ChatFormatting.WHITE + ")");
					
					if(p.hasPermission("evhoutility.gamemode.other")) {

						p.sendMessage(" ");
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous pouvez également préciser le joueur en second argument !");
					}
					
				} else {
					
					if(args.length == 1) SetPlayerGameMode(p, null, args[0]);
					else if(args.length == 2) {
						
						if(p.hasPermission("evhoutility.gamemode.other")) {
							
							if(Bukkit.getServer().getPlayer(args[1]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[1]);
								SetPlayerGameMode(p, target, args[0]);
							
							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
							
						} else p.performCommand("gamemode");
						
					} else {
						
						if(p.hasPermission("evhoutility.gamemode.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
						else p.performCommand("gamemode");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");

		} else {
			
			if(args.length != 2) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour changer votre propre mode de jeux !");
				sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "INFO : " + ChatFormatting.RED + "Vous pouvez sinon préciser le joueur au niveau du second argument !");
			    sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez dans ce cas là marquer au niveau du premier argument un des arguments suivants :" );
			    sender.sendMessage(" ");					
				sender.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "1er Choix :" + ChatFormatting.WHITE + " (" + numberArgs + ChatFormatting.WHITE + ")");
				sender.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "2ème Choix :" + ChatFormatting.WHITE + " (" + CharArgs + ChatFormatting.WHITE + ")");
				sender.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "3ème Choix :" + ChatFormatting.WHITE + " (" + IndexCharArgs + ChatFormatting.WHITE + ")");
			
			} else {

				if(Bukkit.getServer().getPlayer(args[1]) != null) {

					Player target = Bukkit.getServer().getPlayer(args[1]);
					SetPlayerGameMode(sender, target, args[0]);

				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
			}
		}
		
		return false;
	}
	
   /************************************************************/
   /* PARTIE COMMANDE POUR CHANGER LE MODE DE JEUX D'UN JOUEUR */ 
   /************************************************************/	
	
	
	
	
	
	// Méthode pour changer le mode jeux au joueur ou a un joueur spécifique //
	
	private void SetPlayerGameMode(CommandSender sender, Player target, String args) {
		
		
		if(args == null) {
			
			main.console.sendMessage(main.argsNull);
			main.console.sendMessage(main.errorArgs);
			return;
		}

		/********************************************************/
		
		if(args.equalsIgnoreCase("0") || args.equalsIgnoreCase("survival") || args.equalsIgnoreCase("s")) {	
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(sender instanceof Player p) {

						if(target.getGameMode() == GameMode.SURVIVAL) {
							
							p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà en mode de jeux Survie");
							return;
						}
						
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le mode de jeux en Survie");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre mode de jeux en Survie");
						
					} else { 
						
						if(target.getGameMode() == GameMode.SURVIVAL) {
							
							sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà en mode de jeux Survie");
							return;
						}
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le mode de jeux en Survie");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre mode de jeux en Survie");
					}
					
					target.setGameMode(GameMode.SURVIVAL);
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(p.getGameMode() == GameMode.SURVIVAL) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous êtes déjà en mode de jeux Survie");
							return;
						}
						
						p.setGameMode(GameMode.SURVIVAL);
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes maintenant en mode de jeux Survie");
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.targetNull);
					}
				}	
			}

		} else if(args.equalsIgnoreCase("1") || args.equalsIgnoreCase("creative") || args.equalsIgnoreCase("c")) {	
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(sender instanceof Player p) {

						if(target.getGameMode() == GameMode.CREATIVE) {
							
							p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà en mode de jeux Créatif");
							return;
						}
						
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le mode de jeux en Créatif");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre mode de jeux en Créatif");
						
					} else { 
						
						if(target.getGameMode() == GameMode.CREATIVE) {
							
							sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà en mode de jeux Créatif");
							return;
						}
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le mode de jeux en Créatif");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a �éfinit votre mode de jeux en Créatif");
					}
					
					target.setGameMode(GameMode.CREATIVE);
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(p.getGameMode() == GameMode.CREATIVE) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous êtes déjà en mode de jeux Créatif");
							return;
						}
						
						p.setGameMode(GameMode.CREATIVE);
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes maintenant en mode de jeux Créatif");
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.targetNull);
					}
				}	
			}
			
		} else if(args.equalsIgnoreCase("2") || args.equalsIgnoreCase("adventure") || args.equalsIgnoreCase("a")) {						
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else {
					
					if(sender instanceof Player p) {

						if(target.getGameMode() == GameMode.ADVENTURE) {
							
							p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà en mode de jeux Aventure");
							return;
						}
						
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le mode de jeux en Aventure");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre mode de jeux en Aventure");
						
					} else { 
						
						if(target.getGameMode() == GameMode.ADVENTURE) {
							
							sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà en mode de jeux Aventure");
							return;
						}
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le mode de jeux en Aventure");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre mode de jeux en Aventure");
					}
					
					target.setGameMode(GameMode.ADVENTURE);
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(p.getGameMode() == GameMode.ADVENTURE) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous êtes déjà en mode de jeux Aventure");
							return;
						}
						
						p.setGameMode(GameMode.ADVENTURE);
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes maintenant en mode de jeux Aventure");
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.targetNull);
					}
				}	
			}
			
		} else if(args.equalsIgnoreCase("3") || args.equalsIgnoreCase("spectator") || args.equalsIgnoreCase("spec")) {

			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(sender instanceof Player p) {

						if(target.getGameMode() == GameMode.SPECTATOR) {
							
							p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà en mode de jeux Spéctateur");
							return;
						}
						
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le mode de jeux en Spéctateur");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre mode de jeux en Spéctateur");
						
					} else { 
						
						if(target.getGameMode() == GameMode.SPECTATOR) {
							
							sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déjà en mode de jeux Spéctateur");
							return;
						}
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le mode de jeux en Spéctateur");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre mode de jeux en Spéctateur");
					}
					
					target.setGameMode(GameMode.SPECTATOR);
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(p.getGameMode() == GameMode.SPECTATOR) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous êtes déjà en mode de jeux Spéctateur");
							return;
						}
						
						p.setGameMode(GameMode.SPECTATOR);
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes maintenant en mode de jeux Spéctateur");
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.targetNull);
					}
				}	
			}
			
		} else {

			if(sender == null) {

				main.console.sendMessage(main.senderNull);
				main.console.sendMessage(main.errorSender);
				return;

			} else {

				if(!(sender instanceof Player) && target == null) {

					sender.sendMessage(main.targetNull);
					sender.sendMessage(main.targetNull);
					return;
				}
			}

			/********************************************************/

			if(sender instanceof Player p) p.performCommand("gamemode");
			else {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez essayez au niveau du premier argument les arguments suivants :"); 
				sender.sendMessage(" ");					
				sender.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "1er Choix :" + ChatFormatting.WHITE + " (" + numberArgs + ChatFormatting.WHITE + ")");
				sender.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "2ème Choix :" + ChatFormatting.WHITE + " (" + CharArgs + ChatFormatting.WHITE + ")");
				sender.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "3ème Choix :" + ChatFormatting.WHITE + " (" + IndexCharArgs + ChatFormatting.WHITE + ")");
			}
			
		}
	}

	
	// Méthode pour changer le mode jeux au joueur ou a un joueur spécifique //

}
