package fr.TheSakyo.EvhoUtility.commands.times;

import java.util.*;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.runnable.pdaytime.TimesDayPlayer;
import fr.TheSakyo.EvhoUtility.runnable.pdaytime.TimesNoonPlayer;
import fr.TheSakyo.EvhoUtility.runnable.pdaytime.TimesSunsetPlayer;
import fr.TheSakyo.EvhoUtility.runnable.pnighttime.TimesMidnightPlayer;
import fr.TheSakyo.EvhoUtility.runnable.pnighttime.TimesNightPlayer;
import fr.TheSakyo.EvhoUtility.runnable.pnighttime.TimesSunrisePlayer;

public class PtimeCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public PtimeCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	// Variable Ticks (int) pour définir une boucle pour changer le temps du joueur //
	
	public Map<UUID, BukkitTask> DayRun = new HashMap<>();
	
	public Map<UUID, BukkitTask> NoonRun = new HashMap<>();
	
	public Map<UUID, BukkitTask> SunsetRun = new HashMap<>();
	
	public Map<UUID, BukkitTask> NightRun = new HashMap<>();
	
	public Map<UUID, BukkitTask> MidnightRun = new HashMap<>();
	
	public Map<UUID, BukkitTask> SunriseRun = new HashMap<>();
	
	// Variable Ticks (int) pour définir une boucle pour changer le temps du joueur //
	
	
	//Variable montrant les différents arguments possibles pour la commande
	String times = ChatFormatting.GREEN + "day" + ChatFormatting.RED + "/" + ChatFormatting.GREEN + "noon" + ChatFormatting.RED + "/" + ChatFormatting.GREEN + "sunset" + ChatFormatting.RED + "/" + ChatFormatting.GREEN + "night" + ChatFormatting.RED + "/" + ChatFormatting.GREEN + "midnight" + ChatFormatting.RED + "/" + ChatFormatting.GREEN + "sunrise" + ChatFormatting.RED + "/" + ChatFormatting.YELLOW + "reset";
	
	
	/*************************************************/
	/*   PARTIE COMMANDE POUR LE TEMPS D'UN JOUEUR   */ 
	/* (Une Boucle se créer selon, le temp précisé) */
	/*************************************************/	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.ptime")) {
				
				if(args.length == 0) {
					
					p.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez essayer /ptime avec les arguments suivant :");
					sender.sendMessage(" ");
				    sender.sendMessage(ChatFormatting.WHITE + "(" + times + ChatFormatting.WHITE + ")");
					
					if(p.hasPermission("evhoutility.ptime.other")) {

						p.sendMessage(" ");
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous pouvez également préciser le joueur en second argument !");
					}
					
				} else {
					
					if(args.length == 1) SetPlayerTime(p, null, args[0]);
					else if(args.length == 2) {
						
						if(p.hasPermission("evhoutility.ptime.other")) {
							
							if(Bukkit.getServer().getPlayer(args[1]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[1]);
								SetPlayerTime(p, target, args[0]);
							
							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
							
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises pour attribuer un temps à un joueur !");
						
					} else {
						
						if(p.hasPermission("evhoutility.ptime.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
						else p.performCommand("ptime");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0 || args.length == 1) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour attribuer votre propre temps !");
				sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "INFO : " + ChatFormatting.RED + "Vous pouvez sinon préciser le joueur au niveau du second argument !");
			    sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez dans ce cas là marquer au niveau du premier argument un des arguments suivants :");
			    sender.sendMessage(" ");
			    sender.sendMessage(ChatFormatting.WHITE + "(" + times + ChatFormatting.WHITE + ")");
			
			} else if(args.length == 2) { 
				
				if(Bukkit.getServer().getPlayer(args[1]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[1]);
					SetPlayerTime(sender, target, args[0]);
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");

		}
		
		return false;
	}
	
	/*************************************************/
	/*   PARTIE COMMANDE POUR LE TEMPS D'UN JOUEUR   */ 
	/* (Une Boucle se créer selon, le temp précisé) */
	/*************************************************/	
	
	
	
	
	
	// Méthode rapide, pour remettre à 0 la boucle du temps précis (en cas de changements de temps) //
	
	private void CheckRunnableTimes(UUID uuid, List<Map<UUID, BukkitTask>> listMap) {
		
		if(listMap != null && !listMap.isEmpty()) {
			
			for(Map<UUID, BukkitTask> maps : listMap) {
				
			   if(maps.containsKey(uuid) && !maps.get(uuid).isCancelled()) {
				
					maps.get(uuid).cancel();
					maps.remove(uuid);
			   }
			}
		}
	}

	// Méthode rapide, pour remettre à 0 la boucle du temps précis (en cas de changements de temps) //
	

	
	
	
	// Méthode pour changer le temps du monde au joueur ou a un joueur spécifique //
	
	private void SetPlayerTime(CommandSender sender, Player target, String args) {
		
		List<Map<UUID, BukkitTask>> listMap = Arrays.asList(DayRun, NoonRun, SunsetRun, NightRun, MidnightRun, SunriseRun);
		
		if(args == null) {
			
			main.console.sendMessage(main.argsNull);
			main.console.sendMessage(main.errorArgs);
			return;
		}

		/********************************************************************/
		/********************************************************************/

		if(args.equalsIgnoreCase("day")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else {
					
					if(!UtilityMain.playerTimes.contains(target.getUniqueId())) UtilityMain.playerTimes.add(target.getUniqueId());

					/**************************************************/

					if(sender instanceof Player p) {

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant le matin");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le matin");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant le matin");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le matin");
					}

					/**************************************************/

					resetTime(target, listMap);
					DayRun.put(target.getUniqueId(), new TimesDayPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerTimes.contains(p.getUniqueId())) UtilityMain.playerTimes.add(p.getUniqueId());

						/**********************************/

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous le temps étant le matin");
						resetTime(p, listMap);
						DayRun.put(p.getUniqueId(), new TimesDayPlayer(p.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));

					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
					}
				}
			}
			
		} else if(args.equalsIgnoreCase("noon")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(!UtilityMain.playerTimes.contains(target.getUniqueId())) UtilityMain.playerTimes.add(target.getUniqueId());

					/**************************************************/

					if(sender instanceof Player p) {
						
						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant le midi");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le midi");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant le midi");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le temps étant le midi");
					}

					/**************************************************/

					resetTime(target, listMap);
					NoonRun.put(target.getUniqueId(), new TimesNoonPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerTimes.contains(p.getUniqueId())) UtilityMain.playerTimes.add(p.getUniqueId());

						/**********************************/

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous le temps étant le midi");
						resetTime(p, listMap);
						NoonRun.put(p.getUniqueId(), new TimesNoonPlayer(p.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));

					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
					}
				}	
			}

		} else if(args.equalsIgnoreCase("sunset")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(!UtilityMain.playerTimes.contains(target.getUniqueId())) UtilityMain.playerTimes.add(target.getUniqueId());

					/**************************************************/

					if(sender instanceof Player p) {

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY + " le temps étant le coucher du soleil");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le coucher du soleil");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant le coucher du soleil");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le coucher du soleil");
					}

					/**************************************************/

					resetTime(target, listMap);
					SunsetRun.put(target.getUniqueId(), new TimesSunsetPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else {
					
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerTimes.contains(p.getUniqueId())) UtilityMain.playerTimes.add(p.getUniqueId());

						/**********************************/

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous le temps étant le coucher du soleil");
						resetTime(p, listMap);
						SunsetRun.put(p.getUniqueId(), new TimesSunsetPlayer(p.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
					}
				}
				
			}
			
		} else if(args.equalsIgnoreCase("night")) {							
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(!UtilityMain.playerTimes.contains(target.getUniqueId())) UtilityMain.playerTimes.add(target.getUniqueId());

					/**************************************************/

					if(sender instanceof Player p) {

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant la nuit");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant la nuit");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant la nuit");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant la nuit");
					}

					/**************************************************/

					resetTime(target, listMap);
					NightRun.put(target.getUniqueId(), new TimesNightPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerTimes.contains(p.getUniqueId())) UtilityMain.playerTimes.add(p.getUniqueId());

						/**********************************/

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous le temps étant la nuit");
						resetTime(p, listMap);
						NightRun.put(p.getUniqueId(), new TimesNightPlayer(p.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
					}
				}

			}
			
		} else if(args.equalsIgnoreCase("midnight")) {

			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(!UtilityMain.playerTimes.contains(target.getUniqueId())) UtilityMain.playerTimes.add(target.getUniqueId());

					/**************************************************/

					if(sender instanceof Player p) {

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant minuit");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant minuit");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps étant minuit");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant minuit");
					}

					/**************************************************/

					resetTime(target, listMap);
					MidnightRun.put(target.getUniqueId(), new TimesMidnightPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerTimes.contains(p.getUniqueId())) UtilityMain.playerTimes.add(p.getUniqueId());

						/**********************************/

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous le temps étant minuit");
						resetTime(p, listMap);
						MidnightRun.put(p.getUniqueId(), new TimesMidnightPlayer(p.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
					
					} else {
						
					   sender.sendMessage(main.targetNull);
					   sender.sendMessage(main.errorTarget);
					}
				}
			}
			
		} else if(args.equalsIgnoreCase("sunrise")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else { 
					
					if(!UtilityMain.playerTimes.contains(target.getUniqueId())) UtilityMain.playerTimes.add(target.getUniqueId());

					/**************************************************/

					if(sender instanceof Player p) {

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le lever du soleil");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le lever du soleil");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le lever du soleil");
						target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le lever du soleil");
					}

					/**************************************************/

					resetTime(target, listMap);
					SunriseRun.put(target.getUniqueId(), new TimesSunrisePlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(!UtilityMain.playerTimes.contains(p.getUniqueId())) UtilityMain.playerTimes.add(p.getUniqueId());

						/**********************************/

						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous le lever du soleil");
						resetTime(p, listMap);
						SunriseRun.put(p.getUniqueId(), new TimesSunrisePlayer(p.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
					
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
					
					if(target.getPlayerTime() == target.getWorld().getFullTime()) {

							sender.sendMessage(main.prefix + ChatFormatting.RED + "Le temps de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " est déja par défaut");
		
					} else {

                        UtilityMain.playerTimes.remove(target.getUniqueId());
						resetTime(target, listMap);

						/**********************************/

						if(sender instanceof Player p) {

							p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps par défaut");
							target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le temps par défaut");
							
						} else { 
							
							sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " le temps par défaut");
							target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a définit votre temps étant le temps par défaut");
						}
					}
				
				}
				
			} else {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p) {

						if(p.getPlayerTime() == p.getWorld().getFullTime()) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Votre temps est déja par défaut");
							
						} else {

                            UtilityMain.playerTimes.remove(p.getUniqueId());
							resetTime(p, listMap);

							/**********************************/

							p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit pour vous le temps par défaut");
							
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
				
			if(sender instanceof Player p) p.performCommand("ptime");
			else {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez essayez au niveau du premier argument les arguments suivants :"); 
				sender.sendMessage(" ");
			    sender.sendMessage(ChatFormatting.WHITE + "(" + times + ChatFormatting.WHITE + ")");
			}
			
		}
	}

	// Méthode pour changer le temps du monde au joueur ou a un joueur spécifique //
	

	
	
	// Méthode rapide, "reset" le temps du monde au joueur ou a un joueur spécifique //
	
	private void resetTime(Player p, List<Map<UUID, BukkitTask>> listMap) {
		
		CheckRunnableTimes(p.getUniqueId(), listMap);
		p.resetPlayerTime();
	}
	
	// Méthode rapide, "reset" le temps du monde au joueur ou a un joueur spécifique //
}
