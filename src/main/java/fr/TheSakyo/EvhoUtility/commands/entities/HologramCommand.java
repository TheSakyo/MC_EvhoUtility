package fr.TheSakyo.EvhoUtility.commands.entities;

import java.util.List;
import java.util.Set;

import dependancies.emoji4j.EmojiUtils;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.managers.HologramManager;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import fr.TheSakyo.EvhoUtility.utils.entity.entities.HologramEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class HologramCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public HologramCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	

	// Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //
	String header = ChatColor.GRAY + "============== " + ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "Hologramme" + ChatColor.GRAY + " ==============";
	String footer = ChatColor.GRAY + "=============================";
	//Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //


	/* Variables Utiles d'Arguments */
	String help = "/hologram help ou ?";
	String list = "/hologram list";
	String add = "/hologram add <title> <message>";
	String modify = "/hologram modify <title> <message>";
	String remove = "/hologram remove <title>";
	String tp = "/hologram tp <title>";
	String teleport = "/hologram teleport <title>";
	String load = "/hologram load <title>";

	String errorArgs = ChatColor.RED + "Veuillez entrez des arguments ! <help/?, add, remove, modify, load, tp ou list> [<title>] [<message>]";
	/* Variables Utiles d'Arguments */


   /********************************************************************************/
   /* 	 PARTIE COMMANDE POUR LA CREATION/SUPPRESSION/TELEPORTAION D'HOLOGRAMMES   */ 
   /* INFO : main.holo.containsKey() = Détecte si la variable map contient tel nom */
   /********************************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

		HologramEntity hologram = null; // Permettra de récupérer l'hologramme
		String holoname = null; // Permettra de récupérer le Nom de l'hologramme

		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.hologram")) {
				
				if(args.length == 0) {
					
				sender.sendMessage(header);
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "Alias:" + ChatColor.RESET + " /hg");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + help + ChatColor.WHITE + " : " + ChatColor.GRAY + "Affiche la liste des commandes des Hologrammes.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + add + ChatColor.WHITE + " : " + ChatColor.GRAY + "Créer un Hologramme.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + modify + ChatColor.WHITE + " : " + ChatColor.GRAY + "Modifie un Hologramme existant.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + remove + ChatColor.WHITE + " : " + ChatColor.GRAY + "Supprime un Hologramme.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + tp + ChatColor.WHITE + " : " + ChatColor.GRAY + "Se Téléporte à un Hologramme.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + teleport + ChatColor.WHITE + " : " + ChatColor.GRAY + "Téléporte l'Hologramme vers l'emplacement où se trouve le Joueur.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + load + ChatColor.WHITE + " : " + ChatColor.GRAY + "Recharge un Hologramme existant.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + list + ChatColor.WHITE + " : " + ChatColor.GRAY + "Affiche la liste des Hologrammes dans le Serveur.");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(footer);
				return true;
					
				} else if(args.length == 1) {

					if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { p.performCommand("hologram"); }
					else if(args[0].equalsIgnoreCase("list")) {

						Set<String> holograms = main.HOLOGRAMS.keySet(); // Liste des noms d'hologrammes enregistrés

						if(holograms.size() == 0) {

							p.sendMessage(main.prefix + ChatColor.RED + "Aucun hologramme(s) enregistré(s) !");

						} else if(holograms.size() == 1) {

		    				p.sendMessage(main.prefix + ChatColor.GRAY + "Il y'a seulement l'hologramme " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + holograms.toArray()[0] + ChatColor.GRAY + " dans le serveur !");

						} else {

							p.sendMessage(ChatColor.GRAY + "========= " + main.prefix + ChatColor.GRAY + "=========");
			    			p.sendMessage(" ");
			    			p.sendMessage(" ");

			    			p.sendMessage(ChatColor.AQUA.toString() + ChatColor.UNDERLINE.toString() + "Liste des hologramme(s) dans le serveur :");

			    			for(String HOLOGRAM : holograms) {

			    				p.sendMessage(" ");

								Component hologramName = CustomMethod.StringToComponent(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + HOLOGRAM);

								hologramName = hologramName.clickEvent(ClickEvent.runCommand("/hologram tp " + HOLOGRAM));
								hologramName = hologramName.hoverEvent(HoverEvent.showText(CustomMethod.StringToComponent("Cliquez pour vous y téléporter")));


								Component message = CustomMethod.StringToComponent(ChatColor.WHITE + "- ").append(hologramName);
								p.sendMessage(message);
			    			}

			    			p.sendMessage(" ");
			    			p.sendMessage(" ");
			    			p.sendMessage(ChatColor.GRAY + "===========================");
						}

					} else if(args[0].equalsIgnoreCase("remove")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + remove + " !"); }
					else if(args[0].equalsIgnoreCase("load")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + load + " !"); }
					else if(args[0].equalsIgnoreCase("tp")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + tp + " !"); }
					else if(args[0].equalsIgnoreCase("teleport")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + teleport + " !"); }
					else if(args[0].equalsIgnoreCase("modify")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + modify + " !"); }
					else if(args[0].equalsIgnoreCase("add")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + add + " !"); }
					else { p.sendMessage(main.prefix + errorArgs); }

				} else if(args.length == 2) {

					holoname = args[1].toUpperCase();

					if(main.HOLOGRAMS.containsKey(holoname)) { hologram = main.HOLOGRAMS.get(holoname).get(0); }
					else { p.sendMessage(main.prefix + ChatColor.RED + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.RED + " n'éxiste pas !"); return true; }


					 if(args[0].equalsIgnoreCase("remove")) {

						if(hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatColor.RED + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.RED + " n'éxiste pas !"); return true; }
						else {

							HologramManager.unloadHologram(null, holoname, true); // On Supprime toutes les instances de l'hologramme en question

							p.sendMessage(main.prefix + ChatColor.GREEN + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.GREEN + " a été Détruit !");
						}

					} else if(args[0].equalsIgnoreCase("tp")) {

						if(hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatColor.RED + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.RED + " n'éxiste pas !"); return true; }

						else {

							p.teleport(hologram.getLocation());
							p.sendMessage(main.prefix + ChatColor.GREEN + "Vous avez été téléporter vers l'hologramme " + ChatColor.GOLD + holoname + ChatColor.GREEN + " !");

						}

					} else if(args[0].equalsIgnoreCase("teleport")) {

						if(hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatColor.RED + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.RED + " n'éxiste pas !"); return true; }
						else {

							final Location location = p.getLocation(); // Récupère la localisation actuelle du Joueur
							final String message = hologram.getText(); // Récupère la localisation de l'hologramme a modifié

							HologramManager.unloadHologram(null, holoname, true); // On Supprime toutes les instances de l'hologramme en question

							final HologramEntity hologramEntity = hologram; // Récupère l'hologramme en question

							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
								@Override
								public void run() {

									String holoname = args[1].toUpperCase();

												/* -------------------------------------------- */

									Integer ID = Integer.valueOf(hologramEntity.getEntity().getId());

									// Créer l'hologramme en question
									HologramEntity hologram = new HologramEntity(main, null, ID, holoname, ColorUtils.format(message), location, true, true);
									main.HOLOGRAMS.putIfAbsent(holoname, hologram.getLine()); // On ajoute toutes les instances d'hologrammes construits
									if(main.HOLOGRAMS.containsKey(holoname)) main.HOLOGRAMS.replace(holoname, hologram.getLine()); // Remplace l'enregistrement de l'hologramme

									p.sendMessage(main.prefix + ChatColor.GREEN + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.GREEN + " a été téléporter vers vous !");

												/* -------------------------------------------- */
								}
							}, 20);
							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
						}

					} else if(args[0].equalsIgnoreCase("load")) {

						if(hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatColor.RED + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.RED + " n'éxiste pas !"); return true; }

						else {

							HologramManager.unloadHologram(null, holoname, false); // On Supprime toutes les instances de l'hologramme en question

							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
								@Override
								public void run() {

									String holoname = args[1].toUpperCase();

												/* -------------------------------------------- */

									HologramManager.loadHologram(null, holoname, true); // On recharge l'hologramme en question
									p.sendMessage(main.prefix + ChatColor.GREEN + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.GREEN + " a été rechargé !");

												/* -------------------------------------------- */
								}
							}, 20);
							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
						}

					} else if(args[0].equalsIgnoreCase("modify")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + modify + " !"); }
					 else if(args[0].equalsIgnoreCase("add")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + add + " !"); }
					 else if(args[0].equalsIgnoreCase("list")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + list + " !"); }
					 else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + help + " !"); }
					 else { p.sendMessage(main.prefix + errorArgs); }

				} else if(args.length >= 3) {

					holoname = args[1].toUpperCase();
					if(main.HOLOGRAMS.containsKey(holoname)) { hologram = main.HOLOGRAMS.get(holoname).get(0); }


					// Transforme tous les arguments en un "string" //
				    StringBuilder message = new StringBuilder();
			        for(int i = 2; i < args.length; i++) {
			            if (i > 2) message.append(" ");
			            message.append(args[i]);
			        }
					// Transforme tous les arguments en un "string" //

					if(args[0].equalsIgnoreCase("add")) {

						if(hologram == null || hologram.isDestroyed() == Boolean.TRUE || hologram.isDestroyed() == null) {

							if(holoname.contains("&")) {

								p.sendMessage(main.prefix +  ChatColor.RED + "''" + ChatColor.YELLOW + "&" + ChatColor.RED + "'' n'est pas un caractère valide comme titre !");
								return true;
							}

							// Convertie le message en question
							String messageConverted = p.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(message.toString()) : message.toString();

							// Créer l'hologramme en question
							hologram = new HologramEntity(main, null, null, holoname, ColorUtils.format(messageConverted), p.getLocation(), true, true);
							main.HOLOGRAMS.putIfAbsent(holoname, hologram.getLine()); // On ajoute toutes les instances d'hologrammes construits
							if(main.HOLOGRAMS.containsKey(holoname)) main.HOLOGRAMS.replace(holoname, hologram.getLine()); // Remplace l'enregistrement de l'hologramme

							// Détecte si le messae de succés est null ou pas pour l'afficher ensuite (ou pas) //
							if(main.SuccessHolograms != null) {

								p.sendMessage(main.SuccessHolograms);
								main.SuccessHolograms = null;

							} else if(main.SuccessHolograms == null) { p.sendMessage(main.prefix + ChatColor.RED + "La création de l'hologramme a échoué !"); }
							// Détecte si le messae de succés est null ou pas pour l'afficher ensuite (ou pas) //

							//Détecte si le messae de ligne d'erreur est null ou pas pour l'afficher ensuite
							//(S'Exécute si le nombre de lignes est trop élevé)
							if(main.ErrorLineHolograms != null) { p.sendMessage(main.ErrorLineHolograms); main.ErrorLineHolograms = null; }

						} else p.sendMessage(main.prefix + ChatColor.RED + "L'Hologramme éxiste déja !");

					} else if(args[0].equalsIgnoreCase("modify")) {

						if(hologram == null || hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatColor.RED + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.RED + " n'éxiste pas !"); }
						else {

							final Location location = hologram.getLocation(); // Récupère la localisation de l'hologramme a modifié

							HologramManager.unloadHologram(null, holoname, true); // On Supprime toutes les instances de l'hologramme en question

							final HologramEntity hologramEntity = hologram; // Récupère l'hologramme en question

							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
								@Override
								public void run() {

									String holoname = args[1].toUpperCase();

												/* -------------------------------------------- */


									Integer ID = Integer.valueOf(hologramEntity.getEntity().getId());

									// Convertie le message en question
									String messageConverted = p.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(message.toString()) : message.toString();

									// Créer l'hologramme en question
									HologramEntity hologram = new HologramEntity(main, null, ID, holoname, ColorUtils.format(messageConverted), location, true, true);
									main.HOLOGRAMS.putIfAbsent(holoname, hologram.getLine()); // On ajoute toutes les instances d'hologrammes construits
									if(main.HOLOGRAMS.containsKey(holoname)) main.HOLOGRAMS.replace(holoname, hologram.getLine()); // Remplace l'enregistrement de l'hologramme

									p.sendMessage(main.prefix + ChatColor.GREEN + "L'Hologramme " + ChatColor.GOLD + holoname + ChatColor.GREEN + " a été modifié !");

												/* -------------------------------------------- */
								}
							}, 20);
							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //

						}

					} else if(args[0].equalsIgnoreCase("remove")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + remove + " !"); }
					else if(args[0].equalsIgnoreCase("tp")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + tp + " !"); }
					else if(args[0].equalsIgnoreCase("teleport")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + teleport + " !"); }
					else if(args[0].equalsIgnoreCase("load")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + load + " !"); }
					else if(args[0].equalsIgnoreCase("list")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + list + " !"); }
					else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + help + " !"); }
					else { p.sendMessage(main.prefix + errorArgs); }
				} 
				
			} else { p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !"); }
		
		} else {
			
			if(args.length == 0) {

				sender.sendMessage(header);
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "Alias:" + ChatColor.RESET + " /hg");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + help + ChatColor.WHITE + " : " + ChatColor.GRAY + "Affiche la liste des commandes des Hologrammes.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "- " + ChatColor.GREEN + list + ChatColor.WHITE + " : " + ChatColor.GRAY + "Affiche la liste des Hologrammes dans le Serveur.");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(footer);
				return true;
				
			} else if(args.length == 1) {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { Bukkit.getServer().dispatchCommand(sender, "hologram"); }
				else if(args[0].equalsIgnoreCase("list")) {
						
					Set<String> Setkey = ConfigFile.getConfigurationSection(main.holoconfig, "Holograms").getKeys(false);

					List<String> keyList = Setkey.stream().toList();
					
					if(keyList.size() == 0) {
	    				
						sender.sendMessage(main.prefix + ChatColor.RED + "Aucun hologramme(s) enregistré(s) !");
	    			
					} else if(keyList.size() == 1) {
	    				
						sender.sendMessage(main.prefix + ChatColor.GRAY + "Il y'a seulement l'hologramme " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + keyList.get(0) + ChatColor.GRAY + " dans le serveur !");
	    			
					} else {
					
						sender.sendMessage(ChatColor.GRAY + "========= " + main.prefix + ChatColor.GRAY + "=========");
						sender.sendMessage(" ");
		    			sender.sendMessage(" ");
		    			
		    			sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.UNDERLINE.toString() + "Liste des hologramme(s) dans le serveur :");
		    			
		    			for(String key : keyList) {
		    				
		    				sender.sendMessage(" ");
		    				sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + key);
		    			}
		    			
		    			sender.sendMessage(" ");
		    			sender.sendMessage(" ");
		    			sender.sendMessage(ChatColor.GRAY + "===========================");
					
					}

				} else { sender.sendMessage(main.prefix + ChatColor.RED + "Argument incorrect, Veuillez voir les arguments accessible en faisant : " + help + " !"); }

			} else {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))  { sender.sendMessage(main.prefix + ChatColor.RED + "Essayez " + help + " !"); }
				else if(args[0].equalsIgnoreCase("list")) { sender.sendMessage(main.prefix + ChatColor.RED + "Essayez " + list + " !"); }
				else sender.sendMessage(main.prefix + ChatColor.RED + "Argument incorrect, Veuillez voir les arguments accessible en faisant : " + help + " !");
			}
		}
		
		return false;
	}
	
	/********************************************************************************/
	/* 	 PARTIE COMMANDE POUR LA CREATION/SUPPRESSION/TELEPORTAION D'HOLOGRAMMES   */
	/* INFO : main.holo.containsKey() = Détecte si la variable map contient tel nom */
	/********************************************************************************/

}
