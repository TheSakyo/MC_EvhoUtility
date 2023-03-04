package fr.TheSakyo.EvhoUtility.commands.times;

import java.util.*;

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
import org.bukkit.ChatColor;

public class PtimeCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public PtimeCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	// Variable Ticks (int) pour définir une boucle pour changer le temps du joueur //
	
	public Map<UUID, BukkitTask> DayRun = new HashMap<UUID, BukkitTask>();
	
	public Map<UUID, BukkitTask> NoonRun = new HashMap<UUID, BukkitTask>();
	
	public Map<UUID, BukkitTask> SunsetRun = new HashMap<UUID, BukkitTask>();
	
	public Map<UUID, BukkitTask> NightRun = new HashMap<UUID, BukkitTask>();
	
	public Map<UUID, BukkitTask> MidnightRun = new HashMap<UUID, BukkitTask>();
	
	public Map<UUID, BukkitTask> SunriseRun = new HashMap<UUID, BukkitTask>();
	
	// Variable Ticks (int) pour définir une boucle pour changer le temps du joueur //
	
	
	//Variable montrant les différents arguments possibles pour la commande
	String times = ChatColor.GREEN + "day" + ChatColor.RED + "/" + ChatColor.GREEN + "noon" + ChatColor.RED + "/" + ChatColor.GREEN + "sunset" + ChatColor.RED + "/" + ChatColor.GREEN + "night" + ChatColor.RED + "/" + ChatColor.GREEN + "midnight" + ChatColor.RED + "/" + ChatColor.GREEN + "sunrise" + ChatColor.RED + "/" + ChatColor.YELLOW + "reset";
	
	
	/*************************************************/
	/*   PARTIE COMMANDE POUR LE TEMPS D'UN JOUEUR   */ 
	/* (Une Boucle se créer selon, le temp précisé) */
	/*************************************************/	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p){

			if(p.hasPermission("evhoutility.ptime")) {
				
				if (args.length == 0) {
					
					p.sendMessage(main.prefix + ChatColor.RED + "Veuillez essayer /ptime avec les arguments suivant :");
					sender.sendMessage(" ");
				    sender.sendMessage(ChatColor.WHITE + "(" + times + ChatColor.WHITE + ")");
					
					if(p.hasPermission("evhoutility.ptime.other")) {
						p.sendMessage(" ");
						p.sendMessage(main.prefix + ChatColor.RED + "Vous pouvez également préciser le joueur en second argument !");
					}
					
				} else if(args.length != 0) {
					
					if(args.length == 1) { SetPlayerTime(p, null, args[0]); }
					
					else if(args.length == 2) {
						
						if(p.hasPermission("evhoutility.ptime.other")) {
							
							if(Bukkit.getServer().getPlayer(args[1]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[1]);
								
								SetPlayerTime(p, target, args[0]);
							
							} else {
								
								p.sendMessage(main.prefix + ChatColor.RED + "Le joueur est introuvable !");
							}
							
						} else {
							
							p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises pour attribuer un temps à un joueur !");
						}
						
					} else {
						
						if(p.hasPermission("evhoutility.ptime.other")) {
							
							p.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
						
						} else { p.performCommand("ptime"); }
					}
				}
				
			} else {
				
				p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !");
			}
		
		} else {
			
			if(args.length == 0 || args.length == 1) {
				
				sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux pour attribuer votre propre temps !");
				sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + "INFO : " + ChatColor.RED + "Vous pouvez sinon préciser le joueur au niveau du second argument !");
			    sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatColor.RED + "Veuillez dans ce cas là marquer au niveau du premier argument un des arguments suivants :");
			    sender.sendMessage(" ");
			    sender.sendMessage(ChatColor.WHITE + "(" + times + ChatColor.WHITE + ")");
			
			} else if(args.length == 2) { 
				
				if(Bukkit.getServer().getPlayer(args[1]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[1]);
					
					SetPlayerTime(sender, target, args[0]);
				
				} else {
					
					sender.sendMessage(main.prefix + ChatColor.RED + "Le joueur est introuvable !");
				}
				
			} else {
				
				sender.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
			}

		}
		
		return false;
	}
	
	/*************************************************/
	/*   PARTIE COMMANDE POUR LE TEMPS D'UN JOUEUR   */ 
	/* (Une Boucle se créer selon, le temp précisé) */
	/*************************************************/	
	
	
	
	
	
	// Méthode rapide, pour remettre à 0 la boucle du temps précis (en cas de changements de temps) //
	
	private void CheckRunnableTimes(UUID uuid, List<Map<UUID, BukkitTask>> listmap) {
		
		if(listmap == null || listmap != null && listmap.isEmpty()) { return; } 
		
		else { 
			
			for(Map<UUID, BukkitTask> maps : listmap) {
				
			   if(maps.containsKey(uuid)) {
				
				 if(!maps.get(uuid).isCancelled()) { maps.get(uuid).cancel(); maps.remove(uuid); }
			   }
			}
		}
	}

	// Méthode rapide, pour remettre à 0 la boucle du temps précis (en cas de changements de temps) //
	
	
	
	
	
	
	// Méthode pour changer le temps du monde au joueur ou a un joueur spécifique //
	
	private void SetPlayerTime(CommandSender sender, Player target, String args) {
		
		List<Map<UUID, BukkitTask>> listmap = Arrays.asList(DayRun, NoonRun, SunsetRun, NightRun, MidnightRun, SunriseRun);
		
		if(args == null) {
			
			main.console.sendMessage(main.argsNull);
			main.console.sendMessage(main.errorArgs);
			return;
		}
		
		
		if(args.equalsIgnoreCase("day")) {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);

				} else {
					
					if(!UtilityMain.playertimes.contains(target.getUniqueId())) UtilityMain.playertimes.add(target.getUniqueId());
					
					if(sender instanceof Player p){

						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant le matin");
						target.sendMessage(main.prefix + ChatColor.GOLD + p.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le matin");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant le matin");
						target.sendMessage(main.prefix + ChatColor.GOLD + "La Console" + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le matin");
					}
		
					resetTime(target, listmap);
					
					DayRun.put(target.getUniqueId(), new TimesDayPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
					
				}
			
			} else if(target == null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p){

						if(!UtilityMain.playertimes.contains(p.getUniqueId())) UtilityMain.playertimes.add(p.getUniqueId());
						
						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour vous le temps étant le matin");
						
						resetTime(p, listmap);
						
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
					
					if(!UtilityMain.playertimes.contains(target.getUniqueId())) UtilityMain.playertimes.add(target.getUniqueId());
					
					if(sender instanceof Player p){
						
						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant le midi");
						target.sendMessage(main.prefix + ChatColor.GOLD + p.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le midi");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant le midi");
						target.sendMessage(main.prefix + ChatColor.GOLD + "La Console" + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le temps étant le midi");
					}
					
					resetTime(target, listmap);
					
					NoonRun.put(target.getUniqueId(), new TimesNoonPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else if(target == null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p){

						if(!UtilityMain.playertimes.contains(p.getUniqueId())) UtilityMain.playertimes.add(p.getUniqueId());
						
						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour vous le temps étant le midi");
						
						resetTime(p, listmap);
						
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
					
					if(!UtilityMain.playertimes.contains(target.getUniqueId())) UtilityMain.playertimes.add(target.getUniqueId());
					
					if(sender instanceof Player p){

						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY + " le temps étant le coucher du soleil");
						target.sendMessage(main.prefix + ChatColor.GOLD + p.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le coucher du soleil");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant le coucher du soleil");
						target.sendMessage(main.prefix + ChatColor.GOLD + "La Console" + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le coucher du soleil");
					}
					
					resetTime(target, listmap);
					
					SunsetRun.put(target.getUniqueId(), new TimesSunsetPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else if(target == null) {
					
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p){

						if(!UtilityMain.playertimes.contains(p.getUniqueId())) UtilityMain.playertimes.add(p.getUniqueId());
						
						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour vous le temps étant le coucher du soleil");
						
						resetTime(p, listmap);
						
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
					
					if(!UtilityMain.playertimes.contains(target.getUniqueId())) UtilityMain.playertimes.add(target.getUniqueId());
					
					if(sender instanceof Player p){

						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant la nuit");
						target.sendMessage(main.prefix + ChatColor.GOLD + p.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant la nuit");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant la nuit");
						target.sendMessage(main.prefix + ChatColor.GOLD + "La Console" + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant la nuit");
					}
					
					resetTime(target, listmap);
					
					NightRun.put(target.getUniqueId(), new TimesNightPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else if(target == null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p){

						if(!UtilityMain.playertimes.contains(p.getUniqueId())) UtilityMain.playertimes.add(p.getUniqueId());
						
						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour vous le temps étant la nuit");
						
						resetTime(p, listmap);
						
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
					
					if(!UtilityMain.playertimes.contains(target.getUniqueId())) UtilityMain.playertimes.add(target.getUniqueId());
					
					if(sender instanceof Player p){

						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant minuit");
						target.sendMessage(main.prefix + ChatColor.GOLD + p.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant minuit");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps étant minuit");
						target.sendMessage(main.prefix + ChatColor.GOLD + "La Console" + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant minuit");
					}
					
					resetTime(target, listmap);
					
					MidnightRun.put(target.getUniqueId(), new TimesMidnightPlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else if(target == null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p){

						if(!UtilityMain.playertimes.contains(p.getUniqueId())) UtilityMain.playertimes.add(p.getUniqueId());
						
						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour vous le temps étant minuit");

						resetTime(p, listmap);
						
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
					
					if(!UtilityMain.playertimes.contains(target.getUniqueId())) UtilityMain.playertimes.add(target.getUniqueId());
					
					if(sender instanceof Player p){

						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le lever du soleil");
						target.sendMessage(main.prefix + ChatColor.GOLD + p.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le lever du soleil");
						
					} else { 
						
						sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le lever du soleil");
						target.sendMessage(main.prefix + ChatColor.GOLD + "La Console" + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le lever du soleil");
					}
					
					resetTime(target, listmap);
					
					SunriseRun.put(target.getUniqueId(), new TimesSunrisePlayer(target.getUniqueId()).runTaskTimerAsynchronously(main, 0L, 0L));
				}
			
			} else if(target == null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p){

						if(!UtilityMain.playertimes.contains(p.getUniqueId())) UtilityMain.playertimes.add(p.getUniqueId());
						
						p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour vous le lever du soleil");
						
						resetTime(p, listmap);
						
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
						
						if(sender instanceof Player p){

							p.sendMessage(main.prefix + ChatColor.RED + "Le temps de " + ChatColor.GOLD + target.getName() + ChatColor.RED + " est déja par défaut");
							
						} else {
							
							sender.sendMessage(main.prefix + ChatColor.RED + "Le temps de " + ChatColor.GOLD + target.getName() + ChatColor.RED + " est déja par défaut");
						}
		
					} else {
						
						if(UtilityMain.playertimes.contains(target.getUniqueId())) UtilityMain.playertimes.remove(target.getUniqueId());
						
						resetTime(target, listmap);
						
						if(sender instanceof Player p){

							p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps par défaut");
							target.sendMessage(main.prefix + ChatColor.GOLD + p.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le temps par défaut");
							
						} else { 
							
							sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour " + ChatColor.GOLD + target.getName() + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " le temps par défaut");
							target.sendMessage(main.prefix + ChatColor.GOLD + "La Console" + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " a définit votre temps étant le temps par défaut");
						}
					}
				
				}
				
			} else if(target == null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					
				} else {
					
					if(sender instanceof Player p){

						if(p.getPlayerTime() == p.getWorld().getFullTime()) {
							
							p.sendMessage(main.prefix + ChatColor.RED + "Votre temps est déja par défaut");
							
						} else {
							
							if(UtilityMain.playertimes.contains(p.getUniqueId())) UtilityMain.playertimes.remove(p.getUniqueId());
							
							resetTime(p, listmap);
							
							p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit pour vous le temps par défaut");
							
						}
					
					} else {
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
					}
					
				}
			}
		
		} else {
			
			if(target != null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					return;
				} 
				
			} else if(target == null) {
				
				if(sender == null) {
					
					main.console.sendMessage(main.senderNull);
					main.console.sendMessage(main.errorSender);
					return;
					
				} else { 
					
					if(!(sender instanceof Player)){
						
						sender.sendMessage(main.targetNull);
						sender.sendMessage(main.errorTarget);
						return;
					}	
				}
			}
				
			if(sender instanceof Player p){

				p.performCommand("ptime"); 
			
			} else { 
				
				sender.sendMessage(main.prefix + ChatColor.RED + "Veuillez essayez au niveau du premier argument les arguments suivants :"); 
				sender.sendMessage(" ");
			    sender.sendMessage(ChatColor.WHITE + "(" + times + ChatColor.WHITE + ")");
			}
			
		}
	}

	// Méthode pour changer le temps du monde au joueur ou a un joueur spécifique //
	
	
	
	
	
	// Méthode rapide, "reset" le temps du monde au joueur ou a un joueur spécifique //
	
	private void resetTime(Player p, List<Map<UUID, BukkitTask>> listmap) {
		
		CheckRunnableTimes(p.getUniqueId(), listmap);
		
		p.resetPlayerTime();
	}
	
	// Méthode rapide, "reset" le temps du monde au joueur ou a un joueur spécifique //
}
