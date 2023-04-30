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
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class HologramCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public HologramCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	

	// Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //
	String header = ChatFormatting.GRAY + "============== " + ChatFormatting.AQUA.toString() + ChatFormatting.BOLD.toString() + "Hologramme" + ChatFormatting.GRAY + " ==============";
	String footer = ChatFormatting.GRAY + "=============================";
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

	String errorArgs = ChatFormatting.RED + "Veuillez entrez des arguments ! <help/?, add, remove, modify, load, tp ou list> [<title>] [<message>]";
	/* Variables Utiles d'Arguments */


   /********************************************************************************/
   /* 	 PARTIE COMMANDE POUR LA CREATION/SUPPRESSION/TÉLÉPORTATION D'HOLOGRAMMES   */
   /* INFO : main.holo.containsKey() = Détecte si la variable map contient tel nom */
   /********************************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

		HologramEntity hologram = null; // Permettra de récupérer l'hologramme
		String holoName; // Permettra de récupérer le Nom de l'hologramme

		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.hologram")) {
				
				if(args.length == 0) {
					
				sender.sendMessage(header);
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.YELLOW + "Alias:" + ChatFormatting.RESET + " /hg");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + help + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Affiche la liste des commandes des Hologrammes.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + add + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Créer un Hologramme.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + modify + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Modifie un Hologramme existant.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + remove + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Supprime un Hologramme.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + tp + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Se Téléporte à un Hologramme.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + teleport + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Téléporte l'Hologramme vers l'emplacement où se trouve le Joueur.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + load + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Recharge un Hologramme existant.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + list + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Affiche la liste des Hologrammes dans le Serveur.");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(footer);
				return true;
					
				} else if(args.length == 1) {

					if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { p.performCommand("hologram"); }
					else if(args[0].equalsIgnoreCase("list")) {

						Set<String> holograms = main.HOLOGRAMS.keySet(); // Liste des noms d'hologrammes enregistrés

						if(holograms.isEmpty()) {

							p.sendMessage(main.prefix + ChatFormatting.RED + "Aucun hologramme(s) enregistré(s) !");

						} else if(holograms.size() == 1) {

		    				p.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement l'hologramme " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + holograms.toArray()[0] + ChatFormatting.GRAY + " dans le serveur !");

						} else {

							p.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
			    			p.sendMessage(" ");
			    			p.sendMessage(" ");

			    			p.sendMessage(ChatFormatting.AQUA.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des hologramme(s) dans le serveur :");

			    			for(String HOLOGRAM : holograms) {

			    				p.sendMessage(" ");

								Component hologramName = CustomMethod.StringToComponent(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + HOLOGRAM);

								hologramName = hologramName.clickEvent(ClickEvent.runCommand("/hologram tp " + HOLOGRAM));
								hologramName = hologramName.hoverEvent(HoverEvent.showText(CustomMethod.StringToComponent("Cliquez pour vous y téléporter")));


								Component message = CustomMethod.StringToComponent(ChatFormatting.WHITE + "- ").append(hologramName);
								p.sendMessage(message);
			    			}

			    			p.sendMessage(" ");
			    			p.sendMessage(" ");
			    			p.sendMessage(ChatFormatting.GRAY + "===========================");
						}

					} else if(args[0].equalsIgnoreCase("remove")) { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + remove + " !"); }
					else if(args[0].equalsIgnoreCase("load")) { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + load + " !"); }
					else if(args[0].equalsIgnoreCase("tp")) { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + tp + " !"); }
					else if(args[0].equalsIgnoreCase("teleport")) { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + teleport + " !"); }
					else if(args[0].equalsIgnoreCase("modify")) { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + modify + " !"); }
					else if(args[0].equalsIgnoreCase("add")) { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + add + " !"); }
					else { p.sendMessage(main.prefix + errorArgs); }

				} else if(args.length == 2) {

					holoName = args[1].toUpperCase();

					if(main.HOLOGRAMS.containsKey(holoName)) { hologram = main.HOLOGRAMS.get(holoName).get(0); }
					else { p.sendMessage(main.prefix + ChatFormatting.RED + "L'Hologramme " + ChatFormatting.GOLD + holoName + ChatFormatting.RED + " n'éxiste pas !"); return true; }


					 if(args[0].equalsIgnoreCase("remove")) {

						if(hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatFormatting.RED + "L'Hologramme " + ChatFormatting.GOLD + holoName + ChatFormatting.RED + " n'éxiste pas !"); return true; }
						else {

							HologramManager.unloadHologram(null, holoName, true); // On Supprime toutes les instances de l'hologramme en question

							p.sendMessage(main.prefix + ChatFormatting.GREEN + "L'Hologramme " + ChatFormatting.GOLD + holoName + ChatFormatting.GREEN + " a été Détruit !");
						}

					} else if(args[0].equalsIgnoreCase("tp")) {

						if(hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatFormatting.RED + "L'Hologramme " + ChatFormatting.GOLD + holoName + ChatFormatting.RED + " n'éxiste pas !"); return true; }

						else {

							p.teleport(hologram.getLocation());
							p.sendMessage(main.prefix + ChatFormatting.GREEN + "Vous avez été téléporter vers l'hologramme " + ChatFormatting.GOLD + holoName + ChatFormatting.GREEN + " !");

						}

					} else if(args[0].equalsIgnoreCase("teleport")) {

						if(hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatFormatting.RED + "L'Hologramme " + ChatFormatting.GOLD + holoName + ChatFormatting.RED + " n'éxiste pas !"); return true; }
						else {

							final Location location = p.getLocation(); // Récupère la localisation actuelle du Joueur
							final String message = hologram.getText(); // Récupère la localisation de l'hologramme a modifié

							HologramManager.unloadHologram(null, holoName, true); // On Supprime toutes les instances de l'hologramme en question

							final HologramEntity hologramEntity = hologram; // Récupère l'hologramme en question

							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {

                                String holoName1 = args[1].toUpperCase();

                                            /* -------------------------------------------- */

                                Integer ID = hologramEntity.getEntity().getId();

                                // Créer l'hologramme en question
                                HologramEntity hologram1 = new HologramEntity(main, null, ID, holoName1, ColorUtils.format(message), location, true, true);
                                main.HOLOGRAMS.putIfAbsent(holoName1, hologram1.getLine()); // On ajoute toutes les instances d'hologrammes construits
                                if(main.HOLOGRAMS.containsKey(holoName1)) main.HOLOGRAMS.replace(holoName1, hologram1.getLine()); // Remplace l'enregistrement de l'hologramme

                                p.sendMessage(main.prefix + ChatFormatting.GREEN + "L'Hologramme " + ChatFormatting.GOLD + holoName1 + ChatFormatting.GREEN + " a été téléporter vers vous !");

                                            /* -------------------------------------------- */
                            }, 20);
							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
						}

					} else if(args[0].equalsIgnoreCase("load")) {

						if(hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatFormatting.RED + "L'Hologramme " + ChatFormatting.GOLD + holoName + ChatFormatting.RED + " n'éxiste pas !"); return true; }

						else {

							HologramManager.unloadHologram(null, holoName, false); // On Supprime toutes les instances de l'hologramme en question

							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {

                                String holoName12 = args[1].toUpperCase();

                                            /* -------------------------------------------- */

                                HologramManager.loadHologram(null, holoName12, true); // On recharge l'hologramme en question
                                p.sendMessage(main.prefix + ChatFormatting.GREEN + "L'Hologramme " + ChatFormatting.GOLD + holoName12 + ChatFormatting.GREEN + " a été rechargé !");

                                            /* -------------------------------------------- */
                            }, 20);
							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
						}

					} else if(args[0].equalsIgnoreCase("modify")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + modify + " !");
					 else if(args[0].equalsIgnoreCase("add")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + add + " !");
					 else if(args[0].equalsIgnoreCase("list")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + list + " !");
					 else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + help + " !");
					 else p.sendMessage(main.prefix + errorArgs);

				} else {

					holoName = args[1].toUpperCase();
					if(main.HOLOGRAMS.containsKey(holoName)) { hologram = main.HOLOGRAMS.get(holoName).get(0); }


					// Transforme tous les arguments en un "string" //
				    StringBuilder message = new StringBuilder();
			        for(int i = 2; i < args.length; i++) {
			            if (i > 2) message.append(" ");
			            message.append(args[i]);
			        }
					// Transforme tous les arguments en un "string" //

					if(args[0].equalsIgnoreCase("add")) {

						if(hologram == null || hologram.isDestroyed() == Boolean.TRUE || hologram.isDestroyed() == null) {

							if(holoName.contains("&")) {

								p.sendMessage(main.prefix +  ChatFormatting.RED + "''" + ChatFormatting.YELLOW + "&" + ChatFormatting.RED + "'' n'est pas un caractère valide comme titre !");
								return true;
							}

							// Convertie le message en question
							String messageConverted = p.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(message.toString()) : message.toString();

							// Créer l'hologramme en question
							hologram = new HologramEntity(main, null, null, holoName, ColorUtils.format(messageConverted), p.getLocation(), true, true);
							main.HOLOGRAMS.putIfAbsent(holoName, hologram.getLine()); // On ajoute toutes les instances d'hologrammes construits
							if(main.HOLOGRAMS.containsKey(holoName)) main.HOLOGRAMS.replace(holoName, hologram.getLine()); // Remplace l'enregistrement de l'hologramme

							// Détecte si le message de succès est null ou pas pour l'afficher ensuite (ou pas) //
							if(main.SuccessHolograms != null) {

								p.sendMessage(main.SuccessHolograms);
								main.SuccessHolograms = null;

							} else p.sendMessage(main.prefix + ChatFormatting.RED + "La création de l'hologramme a échoué !");
							// Détecte si le message de succès est null ou pas pour l'afficher ensuite (ou pas) //

							//Détecte si le message de ligne d'erreur est null ou pas pour l'afficher ensuite
							//(S'Exécute si le nombre de lignes est trop élevé)
							if(main.ErrorLineHolograms != null) { p.sendMessage(main.ErrorLineHolograms); main.ErrorLineHolograms = null; }

						} else p.sendMessage(main.prefix + ChatFormatting.RED + "L'Hologramme éxiste déja !");

					} else if(args[0].equalsIgnoreCase("modify")) {

						if(hologram == null || hologram.isDestroyed() == Boolean.TRUE) { p.sendMessage(main.prefix + ChatFormatting.RED + "L'Hologramme " + ChatFormatting.GOLD + holoName + ChatFormatting.RED + " n'éxiste pas !"); }
						else {

							final Location location = hologram.getLocation(); // Récupère la localisation de l'hologramme a modifié

							HologramManager.unloadHologram(null, holoName, true); // On Supprime toutes les instances de l'hologramme en question

							final HologramEntity hologramEntity = hologram; // Récupère l'hologramme en question

							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {

                                String holoName13 = args[1].toUpperCase();

                                            /* -------------------------------------------- */


                                Integer ID = hologramEntity.getEntity().getId();

                                // Convertie le message en question
                                String messageConverted = p.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(message.toString()) : message.toString();

                                // Créer l'hologramme en question
                                HologramEntity hologram12 = new HologramEntity(main, null, ID, holoName13, ColorUtils.format(messageConverted), location, true, true);
                                main.HOLOGRAMS.putIfAbsent(holoName13, hologram12.getLine()); // On ajoute toutes les instances d'hologrammes construits
                                if(main.HOLOGRAMS.containsKey(holoName13)) main.HOLOGRAMS.replace(holoName13, hologram12.getLine()); // Remplace l'enregistrement de l'hologramme

                                p.sendMessage(main.prefix + ChatFormatting.GREEN + "L'Hologramme " + ChatFormatting.GOLD + holoName13 + ChatFormatting.GREEN + " a été modifié !");

                                            /* -------------------------------------------- */
                            }, 20);
							// Aprés une seconde, on recharge les 'NPCs' pour le Joueur //

						}

					} else if(args[0].equalsIgnoreCase("remove")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + remove + " !");
					else if(args[0].equalsIgnoreCase("tp")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + tp + " !");
					else if(args[0].equalsIgnoreCase("teleport")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + teleport + " !");
					else if(args[0].equalsIgnoreCase("load")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + load + " !");
					else if(args[0].equalsIgnoreCase("list")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + list + " !");
					else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + help + " !");
					else p.sendMessage(main.prefix + errorArgs);
				} 
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {

				sender.sendMessage(header);
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.YELLOW + "Alias:" + ChatFormatting.RESET + " /hg");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + help + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Affiche la liste des commandes des Hologrammes.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + list + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Affiche la liste des Hologrammes dans le Serveur.");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(footer);
				return true;
				
			} else if(args.length == 1) {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { Bukkit.getServer().dispatchCommand(sender, "hologram"); }
				else if(args[0].equalsIgnoreCase("list")) {
						
					Set<String> setKey = ConfigFile.getConfigurationSection(main.holoConfig, "Holograms").getKeys(false);

					List<String> keyList = setKey.stream().toList();
					
					if(keyList.isEmpty()) {
	    				
						sender.sendMessage(main.prefix + ChatFormatting.RED + "Aucun hologramme(s) enregistré(s) !");
	    			
					} else if(keyList.size() == 1) {
	    				
						sender.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement l'hologramme " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + keyList.get(0) + ChatFormatting.GRAY + " dans le serveur !");
	    			
					} else {
					
						sender.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
						sender.sendMessage(" ");
		    			sender.sendMessage(" ");
		    			
		    			sender.sendMessage(ChatFormatting.AQUA.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des hologramme(s) dans le serveur :");
		    			
		    			for(String key : keyList) {
		    				
		    				sender.sendMessage(" ");
		    				sender.sendMessage(ChatFormatting.WHITE + "- " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + key);
		    			}
		    			
		    			sender.sendMessage(" ");
		    			sender.sendMessage(" ");
		    			sender.sendMessage(ChatFormatting.GRAY + "===========================");
					
					}

				} else { sender.sendMessage(main.prefix + ChatFormatting.RED + "Argument incorrect, Veuillez voir les arguments accessible en faisant : " + help + " !"); }

			} else {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))  sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + help + " !");
				else if(args[0].equalsIgnoreCase("list")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + list + " !");
				else sender.sendMessage(main.prefix + ChatFormatting.RED + "Argument incorrect, Veuillez voir les arguments accessible en faisant : " + help + " !");
			}
		}
		
		return false;
	}
	
	/********************************************************************************/
	/* 	 PARTIE COMMANDE POUR LA CREATION/SUPPRESSION/TÉLÉPORTATION D'HOLOGRAMMES   */
	/* INFO : main.holo.containsKey() = Détecte si la variable map contient tel nom */
	/********************************************************************************/

}
