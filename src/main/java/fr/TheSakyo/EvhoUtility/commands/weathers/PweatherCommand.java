package fr.TheSakyo.EvhoUtility.commands.weathers;

import java.util.*;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.runnable.pweathers.WeathersRainPlayer;
import fr.TheSakyo.EvhoUtility.runnable.pweathers.WeathersSunPlayer;

public class PweatherCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public PweatherCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	// Variable Ticks (int) pour définir une boucle pour changer la météo du joueur //
	
	public Map<UUID, BukkitTask> SunRun = new HashMap<>();
	
	public Map<UUID, BukkitTask> RainRun = new HashMap<>();
	
	// Variable Ticks (int) pour définir une boucle pour changer la météo du joueur //
	
	
	//Variable montrant les différents arguments possibles pour la commande
	String weathers = ChatFormatting.GREEN + "sun" + ChatFormatting.RED + "/" + ChatFormatting.GREEN + "rain" + ChatFormatting.RED + "/" + ChatFormatting.YELLOW + "reset";
	
	
	/*****************************************************/
	/*   PARTIE COMMANDE POUR LA MÉTÉO D'UN JOUEUR      */
	/* (Une Boucle se créer selon, la météo précisée)   */
	/****************************************************/

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.pweather")) {
				
				if (args.length == 0) {
					
					p.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez essayer /pweather avec les arguments suivant :");
					sender.sendMessage(" ");
				    sender.sendMessage(ChatFormatting.WHITE + "(" + weathers + ChatFormatting.WHITE + ")");
					
					if(p.hasPermission("evhoutility.pweather.other")) {

						p.sendMessage(" ");
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous pouvez également préciser le joueur en second argument !");
					}
					
				} else {
					
					if(args.length == 1) SetPlayerWeather(p, null, args[0]);
					else if(args.length == 2) {
						
						if(p.hasPermission("evhoutility.pweather.other")) {
							
							if(Bukkit.getServer().getPlayer(args[1]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[1]);
								SetPlayerWeather(p, target, args[0]);
							
							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises pour attribuer la météo à un joueur !");
						
					} else {
						
						if(p.hasPermission("evhoutility.pweather.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
						else p.performCommand("pweather");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0 || args.length == 1) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour attribuer votre propre météo !");
				sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "INFO : " + ChatFormatting.RED + "Vous pouvez sinon préciser le joueur au niveau du second argument !");
			    sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez dans ce cas là marquer au niveau du premier argument un des arguments suivants :");
			    sender.sendMessage(" ");
			    sender.sendMessage(ChatFormatting.WHITE + "(" + weathers + ChatFormatting.WHITE + ")");
			
			} else if(args.length == 2) { 
				
				if(Bukkit.getServer().getPlayer(args[1]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[1]);
					SetPlayerWeather(sender, target, args[0]);
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
		}
		
		return false;
	}
	
	/*****************************************************/
	/*   PARTIE COMMANDE POUR LA MÉTÉO D'UN JOUEUR      */
	/* (Une Boucle se créer selon, la météo précisée)   */
	/****************************************************/
	
	
	
	
	
	// Méthode rapide, pour remettre à 0 la boucle de la météo précis (en cas de changements de météo) //
	
	private void CheckRunnableWeathers(UUID uuid, List<Map<UUID, BukkitTask>> listMap) {
		
		if(listMap != null && !listMap.isEmpty()) {
			
			for(Map<UUID, BukkitTask> maps : listMap) {
			  
				if(maps.containsKey(uuid) && !maps.get(uuid).isCancelled()) {
					
				  maps.get(uuid).cancel();
				  maps.remove(uuid);
			    }
			}
		}
	}

	// Méthode rapide, pour remettre à 0 la boucle de la météo précis (en cas de changements de météo) //
	

	
	
	
	// Méthode pour changer la météo du monde au joueur ou a un joueur spécifique //
	
	private void SetPlayerWeather(CommandSender sender, Player target, String args) {
		
		List<Map<UUID, BukkitTask>> listMap = Arrays.asList(SunRun, RainRun);
		
		if(args == null) {
			
			main.console.sendMessage(main.argsNull);
			main.console.sendMessage(main.errorArgs);
			return;
		}

		/********************************************************************/
		/********************************************************************/

		if(args.equalsIgnoreCase("sun")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else {
					
					if(!UtilityMain.playerWeathers.contains(target.getUniqueId())) UtilityMain.playerWeathers.add(target.getUniqueId());

					/**************************************************/

					if(sender instanceof Player p) {

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " la météo avec un beau temps");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre météo étant le beau temps");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " la météo avec un beau temps");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre météo étant le beau temps");
					}

					/**************************************************/

					resetWeather(target, listMap);
					SunRun.put(target.getUniqueId(), new WeathersSunPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
					
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerWeathers.contains(p.getUniqueId())) UtilityMain.playerWeathers.add(p.getUniqueId());

						/**********************************/

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous la météo étant un beau temps");
						resetWeather(p, listMap);
						SunRun.put(p.getUniqueId(), new WeathersSunPlayer(p.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
					}
				}
			}
			
		} else if(args.equalsIgnoreCase("rain")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(!UtilityMain.playerWeathers.contains(target.getUniqueId())) UtilityMain.playerWeathers.add(target.getUniqueId());

					/**************************************************/

					if(sender instanceof Player p) {

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " la météo avec un temps pluvieux");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre météo étant un temps pluvieux");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " la météo avec un temps pluvieux");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre météo étant un temps pluvieux");
					}

					/**************************************************/

					resetWeather(target, listMap);
					RainRun.put(target.getUniqueId(), new WeathersRainPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerWeathers.contains(p.getUniqueId())) UtilityMain.playerWeathers.add(p.getUniqueId());

						/**********************************/

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous la météo étant un temps pluvieux");
						resetWeather(p, listMap);
						RainRun.put(p.getUniqueId(), new WeathersRainPlayer(p.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
					}
				}	
			}

		} else if(args.equalsIgnoreCase("thunder")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Impossible de définir au joueur la météo pluvieux et orageux ; Essayez /pweather rain !");
				}
			
			} else {
					
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						p.sendMessage(main.prefix + ChatFormatting.RED + "Impossible de vous définir le temps pluvieux et orageux ; Essayez /pweather rain !");
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
					}
				}
				
			}
			
		} else if(args.equalsIgnoreCase("reset")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(!UtilityMain.playerWeathers.contains(target.getUniqueId())) {

						sender.sendMessage(main.prefix + ChatFormatting.RED + "La météo de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déja par défaut");
		
					} else {
						
						UtilityMain.playerWeathers.remove(target.getUniqueId());
						resetWeather(target, listMap);

						/**********************************/

						if(sender instanceof Player p) {

							p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " la météo par défaut");
							target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant la météo par défaut");
							
						} else { 
							
							sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " la météo par défaut");
							target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant la météo par défaut");
						}
					}
				
				}
				
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerWeathers.contains(p.getUniqueId())) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Votre météo est déja par défaut");
							
						} else {
							
						    UtilityMain.playerWeathers.remove(p.getUniqueId());
							resetWeather(p, listMap);

							/**********************************/

							p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous la météo par défaut");
							
						}
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
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
					sender.sendMessage(main.errorTarget);
					return;
				}
			}

			/******************************************/

			if(sender instanceof Player p) p.performCommand("pweather");
			else {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez essayez au niveau du premier argument les arguments suivants :"); 
				sender.sendMessage(" ");
			    sender.sendMessage(ChatFormatting.WHITE + "(" + weathers + ChatFormatting.WHITE + ")");
			}
			
		}
	}

	// Méthode pour changer la météo du monde au joueur ou a un joueur spécifique //

		
		
		
	
	// Méthode rapide, "reset" la météo du monde au joueur ou a un joueur spécifique //
	
	private void resetWeather(Player p, List<Map<UUID, BukkitTask>> listMap) {
		
		CheckRunnableWeathers(p.getUniqueId(), listMap);
		p.resetPlayerWeather();
	}

	// Méthode rapide, "reset" la météo du monde au joueur ou a un joueur spécifique //
}