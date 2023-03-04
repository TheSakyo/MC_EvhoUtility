package fr.TheSakyo.EvhoUtility.commands.entities;

import dependancies.emoji4j.EmojiUtils;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.managers.NPCManager;
import fr.TheSakyo.EvhoUtility.runnable.RunAnimationNPC;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPC;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCGlobal;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCPersonal;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCUtils;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.entity.entities.NPCEntity;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;


import java.lang.reflect.Field;
import java.util.*;

public class NPCCommand implements CommandExecutor {

    /* Récupère la class "Main" */
	private UtilityMain main;
	public NPCCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */


	// Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //
	String header = ChatColor.GRAY + "============== " + ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "NPC" + ChatColor.GRAY + " ==============";
	String footer = ChatColor.GRAY + "=============================";
	//Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //


	/* Variables Utiles d'Arguments */
	String help = "/npc help ou ?";
	String list = "/npc list";
	String reload = "/npc reload [<npcname>]";
	String forcereload = "/npc forcereload [<npcname>]";
	String create = "/npc create <npcname>";
	String remove = "/npc remove <npcname>";
	String tp = "/npc tp <npcname>";
	String tpto = "/npc tpto <npcname>";
	String rotate = "/npc rotate <npcname>";
	String lookplayer = "/npc lookplayer <npcname>";
	String skin = "/npc skin <skinName> <npcname>";
	String pose = "/npc pose <stand, fly, sleep ou crouch> <npcname>";
	String equipment = "/npc equipment <main, off, feet, legs, chest ou head> <itemname> <npcname>";
	String animation = "/npc animation <mainswing, offswing, damage, criticeffect, criticmagiceffect ou stop> [<true ou false>] <npcname>";
	String animationWithThirdArgument = "/npc animation <mainswing, offswing, damage, criticeffect, criticmagiceffect ou stop> <true ou false> <npcname>";
	String skinstatus = "/npc skinstatus <all, hat, rightpants, leftpants, rightsleeve, leftsleeve, jacket ou cape> [<true ou false>] <npcname>";
	String skinstatusWithThirdArgument = "/npc skinstatus <all, hat, rightpants, leftpants, rightsleeve, leftsleeve, jacket ou cape> <true ou false> <npcname>";
	String interactMessage = "/npc interactmessage <npcname> <customName> <message>";
	/* Variables Utiles d'Arguments */


    /*************************************************************/
    /* PARTIE COMMANDE POUR CRÉER UN NPC (personnage non-joueur) */
    /*************************************************************/

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        if(sender instanceof Player p) {

			final Location location = p.getLocation(); // Récupère l'Emplacement du Joueur
			NPCGlobal npc = null; // Permettra de récupérer le NPC

			if(!p.hasPermission("evhoutility.npc")) { p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !"); return true; }

			if(args.length == 0) {

				p.sendMessage(header);
				p.sendMessage("");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + help + ChatColor.WHITE + " : " + ChatColor.GRAY + "Affiche la liste des commandes des NPCs.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + list + ChatColor.WHITE + " : " + ChatColor.GRAY + "Affiche la liste des NPCs dans le Serveur.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + reload + ChatColor.WHITE + " : " + ChatColor.GRAY + "Recharge tous les NPCs associés au joueur ou un NPC spécifique.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + forcereload + ChatColor.WHITE + " : " + ChatColor.GRAY + "Force le rechargement de tous les NPCs associés au joueur ou un NPC spécifique à partir du fichier de configuration.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + create + ChatColor.WHITE + " : " + ChatColor.GRAY + "Créer un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + remove + ChatColor.WHITE + " : " + ChatColor.GRAY + "Supprime un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + tp + ChatColor.WHITE + " : " + ChatColor.GRAY + "Se Téléporte à un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + tpto + ChatColor.WHITE + " : " + ChatColor.GRAY + "Téléporte un NPC à la location du joueur.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + rotate + ChatColor.WHITE + " : " + ChatColor.GRAY + "Change la Rotation d'un NPC à partir de la même Rotation du Joueur.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + lookplayer + ChatColor.WHITE + " : " + ChatColor.GRAY + "Change la Rotation de tête d'un NPC et suis le joueur s'il est proche.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + skin + ChatColor.WHITE + " : "  + ChatColor.GRAY + "Change le Skin du NPC éxistant");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + pose + ChatColor.WHITE + " : " + ChatColor.GRAY + "Définit une pose à un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + equipment + ChatColor.WHITE + " : " + ChatColor.GRAY + "Définit l'Équipement d'un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + animation + ChatColor.WHITE + " : " + ChatColor.GRAY + "Joue une animation à un NPC (true|false - pour jouer l'animation à l'infinie ou non [false par défaut]).");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + skinstatus + ChatColor.WHITE + " : " + ChatColor.GRAY + "Définit l'État du Skin à Affiché d'un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + interactMessage + ChatColor.WHITE + " : " + ChatColor.GRAY + "Définit un message du 'NPC' avec un Nom Customisé lorsqu'un joueur intéragira avec.");
				p.sendMessage("");
				p.sendMessage("");
				p.sendMessage(footer);
				return true;

			} else if(args.length == 1) {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { p.performCommand("npc"); return true; }
				else if(args[0].equalsIgnoreCase("list")) {

						Set<NPCGlobal> npcs = NPCUtils.getInstance().getAllGlobalNPCs(); // Liste des noms de 'NPCs' enregistrés

						// Récupère la couleur blanche avec de l'italique des codes couleurs Minecraft
						String white_italique = ChatColor.WHITE.toString() + ChatColor.ITALIC.toString();

						// Récupère la couleur gris foncé avec de l'italique des codes couleurs Minecraft
						String darkgray_italic = ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC.toString();

						// Récupère la couleur Or avec du gras des codes couleurs Minecraft
						String gold_bold = ChatColor.GOLD.toString() + ChatColor.BOLD.toString();

						if(npcs.size() == 0) {

							p.sendMessage(main.prefix + ChatColor.RED + "Aucun NPC(s) enregistré(s) dans le plugin !");

						} else if(npcs.size() == 1) {

							NPCGlobal firstNPC = npcs.stream().findFirst().get();
		    				p.sendMessage(main.prefix + ChatColor.GRAY + "Il y'a seulement le NPC " + gold_bold + firstNPC.getSimpleCode()
										 + white_italique + " (" + darkgray_italic + firstNPC.getCustomName() + white_italique + ")" + ChatColor.GRAY + " dans le serveur !");

						} else {

							p.sendMessage(ChatColor.GRAY + "========= " + main.prefix + ChatColor.GRAY + "=========");
			    			p.sendMessage(" ");
			    			p.sendMessage(" ");

			    			p.sendMessage(ChatColor.YELLOW.toString() + ChatColor.UNDERLINE.toString() + "Liste du/des NPC(s) dans le serveur :");

			    			for(NPCGlobal NPC : npcs) {

								p.sendMessage(" ");

								Component npcName = CustomMethod.StringToComponent(gold_bold + NPC.getSimpleCode() + white_italique + " (" + darkgray_italic + NPC.getCustomName() + white_italique + ")");

								npcName = npcName.clickEvent(ClickEvent.runCommand("/npc tp " + NPC.getSimpleCode()));
								npcName = npcName.hoverEvent(HoverEvent.showText(CustomMethod.StringToComponent("Cliquez pour vous y téléporter")));


								Component message = CustomMethod.StringToComponent(ChatColor.WHITE + "- ").append(npcName);
								p.sendMessage(message);
			    			}

			    			p.sendMessage(" ");
			    			p.sendMessage(" ");
			    			p.sendMessage(ChatColor.GRAY + "===========================");
						}

				} else if(args[0].equalsIgnoreCase("reload")) {

					List<NPCPersonal> NPCS = new ArrayList<NPCPersonal>(NPCUtils.getInstance().getPersonalNPCs(p, main));
					p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Rechargement de votre/vos NPC(s)...");

					if(NPCS.isEmpty()) { p.sendMessage(main.prefix + ChatColor.RED + "Aucun NPC(s) enregistré(s) !"); return false; }

					NPCManager.unloadNPC(p, false);
					NPCManager.loadNPC(p, false, false, false);
					return true;

				} else if(args[0].equalsIgnoreCase("forcereload")) {

					p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Forçage du rechargement de votre/vos NPC(s)...");

													/* ----------------------------------------------------- */

					if(NPCManager.getNPCsByConfig(p) != null && !NPCManager.getNPCsByConfig(p).isEmpty()) {

						NPCManager.unloadNPC(p, false);
						NPCManager.loadNPC(p, true, false, false);
						return true;

					} else p.sendMessage(main.prefix + ChatColor.RED + "Aucun NPC(s) enregistré(s) !"); return false;

				} else { p.sendMessage(Error_Argument(args[0])); return false; }

			} else if(args.length == 2) {

				final String npcname = args[1].toUpperCase(); // Permettra de récupérer le Nom du NPC
				if(args[1].length() >= 16) { p.sendMessage(main.prefix + ChatColor.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

				String npcReg = "NPC." + npcname; // Récupère la section du NPC en question

											 /*-------------------------------------*/

				if(NPCUtils.getInstance().getGlobalNPC(main, npcname) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, npcname); }

											 /*-------------------------------------*/

				if(args[0].equalsIgnoreCase("create")) {

					if(!checkNPCExists(npc, p, false)) return false;
					else {

						Map<NPC.Skin.Part, String> skinStatus = new HashMap<NPC.Skin.Part, String>();
						Map<EquipmentSlot, ItemStack> equipments = new HashMap<EquipmentSlot, ItemStack>();

										/* -------------------------------------------- */

						skinStatus.putIfAbsent(NPC.Skin.Part.CAPE, "true");
						skinStatus.putIfAbsent(NPC.Skin.Part.JACKET, "true");
						skinStatus.putIfAbsent(NPC.Skin.Part.LEFT_SLEEVE, "true");
						skinStatus.putIfAbsent(NPC.Skin.Part.RIGHT_SLEEVE, "true");
						skinStatus.putIfAbsent(NPC.Skin.Part.LEFT_PANTS, "true");
						skinStatus.putIfAbsent(NPC.Skin.Part.RIGHT_PANTS, "true");
						skinStatus.putIfAbsent(NPC.Skin.Part.HAT, "true");

										/* -------------------------------------------- */

						equipments.putIfAbsent(EquipmentSlot.HEAD, new ItemStack(Material.AIR));
						equipments.putIfAbsent(EquipmentSlot.CHEST, new ItemStack(Material.AIR));
						equipments.putIfAbsent(EquipmentSlot.LEGS, new ItemStack(Material.AIR));
						equipments.putIfAbsent(EquipmentSlot.FEET, new ItemStack(Material.AIR));
						equipments.putIfAbsent(EquipmentSlot.OFFHAND, new ItemStack(Material.AIR));
						equipments.putIfAbsent(EquipmentSlot.MAINHAND, new ItemStack(Material.AIR));


										/* -------------------------------------------- */

						npc = NPCUtils.getInstance().generateGlobalNPC(main, npcname, location); // Génère le NPC
						npc.getNPCUtils().getPluginManager(npc.getPlugin()).setUpdateGazeTicks(1); // Change le nombre de ticks pour le mouvement du NPC

										/* -------------------------------------------- */

						String customName = ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC.toString() + "Inconnu(e)";
						String format = ChatColor.WHITE.toString() + ChatColor.ITALIC.toString() + " >> " + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "...";

						npc.addMessageClickAction(customName + format); // Définit un message customisé
						npc.setCustomName("Inconnu(e)"); // Définit un Nom Customisé pour le NPC.

										/* -------------------------------------------- */

						npc.forceUpdate();

										/* -------------------------------------------- */

						NPCManager.save_NPC(npcname, location, npc.getPose().name(), skinStatus, equipments, null, npc.getSkin(),
											npc.getCustomName(), "...", false, null);
						NPCEntity.runAnimation(npc).updateConfig(false);
						main.NPCS.putIfAbsent(npcname, npc);

						p.sendMessage(main.prefix + ChatColor.GREEN + "Le NPC a été créer !");
						return true;
					}

				} else if(args[0].equalsIgnoreCase("remove")) {

					if(!checkNPCExists(npc, p, true)) return false;
					else {

						NPCManager.unsave_NPC(npcname);
						if(main.NPCS.containsKey(npcname)) main.NPCS.remove(npcname);
						NPCUtils.getInstance().removeGlobalNPC(npc);

						p.sendMessage(main.prefix + ChatColor.GREEN + "Le NPC a été supprimer !");
						return true;
					}

				} else if(args[0].equalsIgnoreCase("tp")) {

					if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					p.teleport(npc.getLocation());
					p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez été Téléporté vers le NPC !");
					return true;

				} else if(args[0].equalsIgnoreCase("tpto")) {

					if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					npc.teleport(p);
					p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez Téléporté le NPC vers vous !");

					// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
					ConfigFile.set(main.NPCconfig, npcReg + ".Location.x", String.valueOf(npc.getX()));
					ConfigFile.set(main.NPCconfig, npcReg + ".Location.y", String.valueOf(npc.getY()));
					ConfigFile.set(main.NPCconfig, npcReg + ".Location.z", String.valueOf(npc.getZ()));
					ConfigFile.set(main.NPCconfig, npcReg + ".Location.World", String.valueOf(npc.getWorld()));
					ConfigFile.set(main.NPCconfig, npcReg + ".Location.Yaw", String.valueOf(npc.getYaw()));
					ConfigFile.set(main.NPCconfig, npcReg + ".Location.Pitch", String.valueOf(npc.getPitch()));
					ConfigFile.saveConfig(main.NPCconfig);
					// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

					return true;

				} else if(args[0].equalsIgnoreCase("rotate")) {

					if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					npc.lookAt(location.getYaw(), location.getPitch(), true);
					p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez changé la Rotation du NPC !");

					// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
					ConfigFile.set(main.NPCconfig, npcReg + ".Location.Yaw", String.valueOf(location.getYaw()));
					ConfigFile.set(main.NPCconfig, npcReg + ".Location.Pitch", String.valueOf(location.getPitch()));
					ConfigFile.saveConfig(main.NPCconfig);
					// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

					return true;

				} else if(args[0].equalsIgnoreCase("lookplayer")) {

					if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					if(npc.getGazeTrackingType() == NPC.GazeTrackingType.NEAREST_PLAYER) {

						npc.setGazeTrackingType(NPC.GazeTrackingType.NONE);
						npc.getGazeTrackingType();
						p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le NPC ne va plus suivre avec sa tête les joueurs proches");

						// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
						ConfigFile.set(main.NPCconfig, npcReg + ".FollowPlayerWithHead", false);
						// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					} else {

						npc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);
						p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le NPC va désormait suivre avec sa tête les joueurs proches");

						// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
						ConfigFile.set(main.NPCconfig, npcReg + ".FollowPlayerWithHead", true);
						// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
					}

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

					ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
					return true;

				} else if(args[0].equalsIgnoreCase("reload")) {

					if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					NPCManager.unloadNPC(null, false);
					NPCManager.loadNPC(null, false, false, false);
					p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le NPC a été recharger !");
					return true;

				} else if(args[0].equalsIgnoreCase("forcereload")) {

					if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					NPCManager.unloadNPC(null, false);
					NPCManager.loadNPC(null, true, false, false);
					p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le NPC a été recharger de force !");
					return true;

				} else { p.sendMessage(Error_Argument(args[0])); }

			} else if(args.length == 3) {

				final String npcname = args[2].toUpperCase();
				if(args[2].length() >= 16) { p.sendMessage(main.prefix + ChatColor.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

				String npcReg = "NPC." + npcname; // Récupère la section du NPC en question

											 /*-------------------------------------*/

				if(NPCUtils.getInstance().getGlobalNPC(main, npcname) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, npcname); }

											 /*-------------------------------------*/

				if(args[0].equalsIgnoreCase("animation")) {

					String animationMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "L'Animation choisit du NPC a été joué !";
					RunAnimationNPC animationRunnable = NPCEntity.runAnimation(npc);

					if(args[1].equalsIgnoreCase("mainswing")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_MAIN_ARM, 0);
						npc.playAnimation(NPC.Animation.SWING_MAIN_ARM);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("offswing")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_OFF_HAND, 0);
						npc.playAnimation(NPC.Animation.SWING_OFF_HAND);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("damage")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.TAKE_DAMAGE, 0);
						npc.playAnimation(NPC.Animation.TAKE_DAMAGE);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("criticeffect")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.CRITICAL_EFFECT, 0);
						npc.playAnimation(NPC.Animation.CRITICAL_EFFECT);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("criticmagiceffect")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.MAGICAL_CRITICAL_EFFECT, 0);
						npc.playAnimation(NPC.Animation.MAGICAL_CRITICAL_EFFECT);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("stop")) {

						BukkitTask task = animationRunnable.getTask();

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_MAIN_ARM, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.SWING_OFF_HAND, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.TAKE_DAMAGE, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.CRITICAL_EFFECT, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.MAGICAL_CRITICAL_EFFECT, 0);

						animationRunnable.updateConfig(false);
						if(task != null && !task.isCancelled()) animationRunnable.getTask().cancel();

						p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Toutes les Animations du NPC ont été arrêtées !");

						// ⬇️ Recharge le NPC ⬇️ //
						npc.update();
						// ⬆️ Recharge le NPC ⬆️ //

						return true;

					} else p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + animation + " !");

				} else if(args[0].equalsIgnoreCase("pose")) {

					String poseMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "La Posture du NPC a été effectuée !";
					String alreadyPoseMessage = main.prefix + ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "Le NPC a déjà cette Posture !";

					if(args[1].equalsIgnoreCase("stand")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						Pose STAND = Pose.STANDING; // Posture du NPC

						if(npc.getPose() == STAND) { p.sendMessage(alreadyPoseMessage); return false; }
						else {

							npc.setPose(STAND);
							p.sendMessage(poseMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.Pose", npc.getPose().name());
							ConfigFile.saveConfig(main.NPCconfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.update();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;
						}

					} else if(args[1].equalsIgnoreCase("fallfly")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						Pose FALL_FLY = Pose.FALL_FLYING; // Posture du NPC

						if(npc.getPose() == FALL_FLY) { p.sendMessage(alreadyPoseMessage); return false; }
						else {

							npc.setPose(FALL_FLY);
							p.sendMessage(poseMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.Pose", npc.getPose().name());
							ConfigFile.saveConfig(main.NPCconfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.update();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;
						}

					} else if(args[1].equalsIgnoreCase("sleep")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						Pose SLEEP = Pose.SLEEPING; // Posture du NPC

						if(npc.getPose() == SLEEP) { p.sendMessage(alreadyPoseMessage); return false; }
						else {

							npc.setPose(SLEEP);
							p.sendMessage(poseMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.Pose", npc.getPose().name());
							ConfigFile.saveConfig(main.NPCconfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.update();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;
						}

					} else if(args[1].equalsIgnoreCase("crouch")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						Pose CROUCH = Pose.CROUCHING; // Posture du NPC

						if(npc.getPose() == CROUCH) { p.sendMessage(alreadyPoseMessage); return false; }
						else {

							npc.setPose(CROUCH);
							p.sendMessage(poseMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.Pose", npc.getPose().name());
							ConfigFile.saveConfig(main.NPCconfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.update();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;
						}

					} else p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + pose + " !");

				} else if(args[0].equalsIgnoreCase("skinstatus")) {

					NPC.Skin.Parts skinParts = npc.getSkinParts();

					String skinStatusOnMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le Status du Skin en question a été affiché au NPC !";
					String allSkinStatusOnMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez affiché tous les Status du Skin du NPC !";

					String skinStatusOffMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le Status du Skin en question a été enlevé du NPC !";
					String allSkinStatusOffMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez enlevé tous les Status du Skin du NPC !";

					if(args[1].equalsIgnoreCase("all")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.getInvisibleParts().isEmpty()) {

							skinParts.disableAll();
							p.sendMessage(allSkinStatusOffMessage);

							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.CAPE", "true");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.JACKET", "true");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "true");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "true");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "true");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "true");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.HAT", "true");

						} else {

							skinParts.enableAll();
							p.sendMessage(allSkinStatusOnMessage);

							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.CAPE", "false");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.JACKET", "false");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "false");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "false");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "false");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "false");
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.HAT", "false");
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("hat")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isHat()) {

							skinParts.setHat(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.HAT", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setHat(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.HAT", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("rightpants")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isRightPants()) {

							skinParts.setRightPants(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setRightPants(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("leftpants")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isLeftPants()) {

							skinParts.setLeftPants(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setLeftPants(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("rightsleeve")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isRightSleeve()) {

							skinParts.setRightSleeve(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setRightSleeve(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("leftsleeve")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isLeftSleeve()) {

							skinParts.setLeftSleeve(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setLeftSleeve(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("jacket")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isJacket()) {

							skinParts.setJacket(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.JACKET", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setJacket(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.JACKET", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("cape")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isCape()) {

							skinParts.setCape(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.CAPE", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setCape(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.CAPE", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + skinstatus + " !");

				} else if(args[0].equalsIgnoreCase("skin")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						UUID uuid = PlayerEntity.getUUIDByPlayerName(args[1], null); // Récupère l'UUID du pseudo définit
						Skin skin = Skin.get(uuid); // Récupère le Skin à partir de l'UUID du pseudo définit

						// Créer une texture customisée à partir de du Skin récupéré
						NPC.Skin skinNPC = NPC.Skin.createCustomTextureSkin(skin.getValue(), skin.getSignature());

						if(!npc.getSkin().equals(skinNPC)) {

							npc.setSkin(skinNPC);
							p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez changé le Skin du NPC !");

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCconfig, npcReg + ".Skin.Texture", skinNPC.getTexture());
							ConfigFile.set(main.NPCconfig, npcReg + ".Skin.Signature", skinNPC.getSignature());

							ConfigFile.saveConfig(main.NPCconfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatColor.RED + "Le Skin du NPC est déjà celui que vous voulez attribué !"); return false; }

				} else { p.sendMessage(Error_Argument(args[0])); }

			} else if(args.length == 4) {

				final String npcname = args[3].toUpperCase();
				if(args[3].length() >= 16) { p.sendMessage(main.prefix + ChatColor.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

				String npcReg = "NPC." + npcname; // Récupère la section du NPC en question

											 /*-------------------------------------*/

				if(NPCUtils.getInstance().getGlobalNPC(main, npcname) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, npcname); }

											 /*-------------------------------------*/

				if(args[0].equalsIgnoreCase("equipment")) {

					String itemname = args[2].toUpperCase();
					ItemStack item = new ItemStack(Material.getMaterial(itemname));

					if(args[1].equalsIgnoreCase("main")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.MAINHAND).equals(item)) {

							npc.setItem(EquipmentSlot.MAINHAND, item);
							p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "La Main Principale du NPC a été équipée !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "MAINHAND", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatColor.RED + "Le NPC a déjà cette Item d'Équipé dans sa Main Principale !"); return false; }

					} else if(args[1].equalsIgnoreCase("off")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.OFFHAND).equals(item)) {

							npc.setItem(EquipmentSlot.OFFHAND, item);
							p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "La Main Secondaire du NPC a été équipée !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "OFFHAND", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatColor.RED + "Le NPC a déjà cette Item d'Équipé dans sa Main Secondaire !"); return false; }

					} else if(args[1].equalsIgnoreCase("feet")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.FEET).equals(item)) {

							npc.setItem(EquipmentSlot.FEET, item);
							p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Les Bottes du NPC ont été équipées !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "BOOTS", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatColor.RED + "Le NPC a déjà cette Item d'Équipé au niveau de ses Bottes !"); return false; }

					} else if(args[1].equalsIgnoreCase("legs")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.LEGS).equals(item)) {

							npc.setItem(EquipmentSlot.LEGS, item);
							p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Les jambières du NPC ont été équipées !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "LEGGINGS", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatColor.RED + "Le NPC a déjà cette Item d'Équipé au niveau de ses Jambières !"); return false; }

					} else if(args[1].equalsIgnoreCase("chest")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.CHEST).equals(item)) {

							npc.setItem(EquipmentSlot.CHEST, item);
							p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le Plastron du NPC a été équipées !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "CHESTPLATE", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatColor.RED + "Le NPC a déjà cette Item d'Équipé au niveau de son Plastron !"); return false; }

					} else if(args[1].equalsIgnoreCase("head")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.HEAD).equals(item)) {

							npc.setItem(EquipmentSlot.HEAD, item);
							p.sendMessage(main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le Casque du NPC a été équipées !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "HEAD", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatColor.RED + "Le NPC a déjà cette Item d'Équipé au niveau de son Casque !"); return false; }

					} else p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + equipment + " !");

				} else if(args[0].equalsIgnoreCase("animation")) {

					RunAnimationNPC animationRunnable = NPCEntity.runAnimation(npc);
					BukkitTask task = animationRunnable.getTask();

					String animationMessage = null;
					Integer valueInfinite = null;

					if(args[1].equalsIgnoreCase("stop")) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /npc animation stop <name> !"); return false; }

					if(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {

						boolean infinite = Boolean.parseBoolean(args[2]);

						if(infinite) {

							animationMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "L'Animation choisit du NPC sera joué en boucle !";
							valueInfinite = Integer.valueOf(1);

						} else {

							animationMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "L'Animation choisit du NPC sera joué une seule fois !";
							valueInfinite = Integer.valueOf(0);
						}

					} else {

						p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + animationWithThirdArgument + " !");
						return false;
					}

									/* ------------------------------------------------------------------------------------ */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_MAIN_ARM, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.SWING_OFF_HAND, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.TAKE_DAMAGE, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.CRITICAL_EFFECT, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.MAGICAL_CRITICAL_EFFECT, 0);

									/* ------------------------------------------------------------------------------------ */

					if(args[1].equalsIgnoreCase("mainswing")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_MAIN_ARM, valueInfinite);
						if(valueInfinite.equals(Integer.valueOf(0))) npc.playAnimation(NPC.Animation.SWING_MAIN_ARM);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(Integer.valueOf(1))) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("offswing")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_OFF_HAND, valueInfinite);
						if(valueInfinite.equals(Integer.valueOf(0))) npc.playAnimation(NPC.Animation.SWING_OFF_HAND);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(Integer.valueOf(1))) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("damage")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.TAKE_DAMAGE, valueInfinite);
						if(valueInfinite.equals(Integer.valueOf(0))) npc.playAnimation(NPC.Animation.TAKE_DAMAGE);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(Integer.valueOf(1))) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("criticeffect")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.CRITICAL_EFFECT, valueInfinite);
						if(valueInfinite.equals(Integer.valueOf(0))) npc.playAnimation(NPC.Animation.CRITICAL_EFFECT);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(Integer.valueOf(1))) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("criticmagiceffect")) {

						if(!checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.MAGICAL_CRITICAL_EFFECT, valueInfinite);
						if(valueInfinite.equals(Integer.valueOf(0))) npc.playAnimation(NPC.Animation.MAGICAL_CRITICAL_EFFECT);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(Integer.valueOf(1))) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + animationWithThirdArgument + " !");

				} else if(args[0].equalsIgnoreCase("skinstatus")) {

					NPC.Skin.Parts skinParts = npc.getSkinParts();

					String skinStatusMessage = null;
					String allSkinStatusMessage = null;
					String alreadySkinStatusMessage = null;
					String alreadyAllSkinStatusMessage = null;

					boolean hasEnabled = true;

					if(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {

						boolean state = Boolean.parseBoolean(args[2]);

						if(state) {

							hasEnabled = true;

							skinStatusMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le Status du Skin en question a été affiché au NPC !";
							alreadySkinStatusMessage = main.prefix + ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "Le Status du Skin que vous demandez est déjà affiché sur le NPC !";
							allSkinStatusMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez affiché tous les Status du Skin du NPC !";
							alreadyAllSkinStatusMessage = main.prefix + ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "Le NPC possède dèjà tous les Status de Skin !";

						} else {

							hasEnabled = false;

							skinStatusMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Le Status du Skin en question a été enlevé du NPC !";
							alreadySkinStatusMessage = main.prefix + ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "Le Status du Skin que vous demandez n'est pas sur le NPC !";
							allSkinStatusMessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez enlevé tous les Status du Skin du NPC !";
							alreadyAllSkinStatusMessage = main.prefix + ChatColor.RED.toString() + ChatColor.ITALIC.toString() + "Le NPC ne possède pas tous les Status de Skin !";
						}

					} else {

						p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + skinstatusWithThirdArgument + " !");
						return false;
					}

					if(args[1].equalsIgnoreCase("all")) {

						if(!checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.getInvisibleParts().isEmpty()) {

							if(hasEnabled) p.sendMessage(alreadyAllSkinStatusMessage);
							else {

								skinParts.enableAll();
								p.sendMessage(allSkinStatusMessage);

								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.CAPE", "true");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.JACKET", "true");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "true");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "true");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "true");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "true");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.HAT", "true");

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadyAllSkinStatusMessage);
							else {

								skinParts.disableAll();
								p.sendMessage(allSkinStatusMessage);

								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.CAPE", "false");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.JACKET", "false");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "false");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "false");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "false");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "false");
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.HAT", "false");

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("hat")) {

						if(!checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isHat()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setHat(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.HAT", "false");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setHat(true);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.HAT", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("rightpants")) {

						if(!checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isRightPants()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setRightPants(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "false");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setRightPants(true);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("leftpants")) {

						if(!checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isLeftPants()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setLeftPants(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "false");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setLeftPants(true);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("rightsleeve")) {

						if(!checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isRightSleeve()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setRightSleeve(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "false");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setRightSleeve(true);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("leftsleeve")) {

						if(!checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isLeftSleeve()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setLeftSleeve(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "false");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setLeftSleeve(true);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("jacket")) {

						if(!checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isJacket()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setJacket(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.JACKET", "false");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setJacket(true);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.JACKET", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("cape")) {

						if(!checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isCape()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setCape(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.CAPE", "false");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setCape(true);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCconfig, npcReg + ".Data.SkinStatus.CAPE", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCconfig); // Enregistre le fichier de configuration
						return true;

					} else p.sendMessage(main.prefix + ChatColor.RED + "Essayez " + skinstatusWithThirdArgument + " !");

				} else if(args[0].equalsIgnoreCase("interactmessage")) {

					if(args[1].length() >= 16) { p.sendMessage(main.prefix + ChatColor.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

					npcReg = "NPC." + args[1].toUpperCase(); // Récupère la section du NPC en question

											 /*-------------------------------------*/

					if(NPCUtils.getInstance().getGlobalNPC(main, args[1].toUpperCase()) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, args[1].toUpperCase()); }

											 /*-------------------------------------*/

					// Message de succès
					String interactmessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez changé le message d'interaction du NPC.";

					if(!checkNPCExists(npc, p, true)) return false;

									/* ------------------------------------------- */

					// ⬇️ On enlève les actions du NPC comportant des Messages Customisés ⬇️ //
					npc.getClickActions().forEach(clickAction -> {

						if(clickAction.getActionType() == NPC.Interact.Actions.Type.SEND_MESSAGE) clickAction.getNPC().removeClickAction(clickAction);
					});
					// ⬆️ On enlève les actions du NPC comportant des Messages Customisés ⬆️ //

									/* ------------------------------------------- */

					npc.setCustomName(args[2]);
					String customName = ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC.toString() + args[2];
					String format = ChatColor.WHITE.toString() + ChatColor.ITALIC.toString() + " >> " + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + args[3];

					npc.addMessageClickAction(customName + format); // Définit un message customisé
					p.sendMessage(interactmessage);

					// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
					ConfigFile.set(main.NPCconfig, npcReg + ".InteractEvent.Sender", args[2]);
					ConfigFile.set(main.NPCconfig, npcReg + ".InteractEvent.Message", args[3]);

					ConfigFile.saveConfig(main.NPCconfig);
					// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

				} else { p.sendMessage(Error_Argument(args[0])); }

			} else {

				if(args[0].equalsIgnoreCase("interactmessage")) {

					final String npcname = args[1].toUpperCase();
					if(args[1].length() >= 16) { p.sendMessage(main.prefix + ChatColor.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

					String npcReg = "NPC." + npcname; // Récupère la section du NPC en question

											 /*-------------------------------------*/

					if(NPCUtils.getInstance().getGlobalNPC(main, npcname) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, npcname); }

											 /*-------------------------------------*/

					// On combine tous les arguments restant en message //
					StringBuilder sb = new StringBuilder();
					for(int i = 3; i < args.length; i++) {

						sb.append(args[i]);
						sb.append(" ");
					}
					String combinedArgs = sb.toString();
					// On combine tous les arguments restant en message //

					// Message de succès
					String interactmessage = main.prefix + ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Vous avez changé le message d'interaction du NPC.";

					if(!checkNPCExists(npc, p, true)) return false;


									/* ------------------------------------------- */

					// ⬇️ On enlève les actions du NPC comportant des Messages Customisés ⬇️ //
					List<NPC.Interact.ClickAction> clickActionList = new ArrayList<NPC.Interact.ClickAction>();

					npc.getClickActions().forEach(clickActionList::add);
					clickActionList.forEach(clickAction -> {

						if(clickAction.getActionType() == NPC.Interact.Actions.Type.SEND_MESSAGE) clickAction.getNPC().removeClickAction(clickAction);
					});
					clickActionList.clear();
					// ⬆️ On enlève les actions du NPC comportant des Messages Customisés ⬆️ //

									/* ------------------------------------------- */


					npc.setCustomName(args[2]);
					String customName = ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC.toString() + args[2];
					String format = ChatColor.WHITE.toString() + ChatColor.ITALIC.toString() + " >> " + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + combinedArgs;


					// Convertie le message en question à envoyer
					String messageConverted = p.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(customName + format) : customName + format;

					npc.addMessageClickAction(messageConverted);
					p.sendMessage(interactmessage);

					// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
					ConfigFile.set(main.NPCconfig, npcReg + ".InteractEvent.Sender", args[2]);
					ConfigFile.set(main.NPCconfig, npcReg + ".InteractEvent.Message", combinedArgs);

					ConfigFile.saveConfig(main.NPCconfig);
					// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

					return true;

				} else p.sendMessage(Error_Argument(args[0]));
			}

		} else {

			if(args.length == 0) {

				sender.sendMessage(header);
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + help + ChatColor.WHITE + " : " + ChatColor.GRAY + "Affiche la liste des commandes des NPCs.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + list + ChatColor.WHITE + " : " + ChatColor.GRAY + "Affiche la liste des NPCs dans le Serveur.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + "/npc reload" + ChatColor.WHITE + " : " + ChatColor.GRAY + "Recharge tous les NPCs dans le Serveur.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + "/npc forcereload" + ChatColor.WHITE + " : " + ChatColor.GRAY + "Force le rechargement de tous les NPCs dans le Serveur à partir du fichier de configuration.");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(footer);
				return true;

			} else if(args.length == 1) {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { Bukkit.getServer().dispatchCommand(sender, "npc"); }
				else if(args[0].equalsIgnoreCase("list")) {

						Set<String> npcs = main.NPCS.keySet();

						if(npcs.size() == 0) {

							sender.sendMessage(main.prefix + ChatColor.RED + "Aucun NPC(s) enregistré(s) dans le plugin !");

						} else if(npcs.size() == 1) {

		    				sender.sendMessage(main.prefix + ChatColor.GRAY + "Il y'a seulement le NPC " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + npcs.toArray()[0] + ChatColor.GRAY + " dans le serveur !");

						} else {

							sender.sendMessage(ChatColor.GRAY + "========= " + main.prefix + ChatColor.GRAY + "=========");
			    			sender.sendMessage(" ");
			    			sender.sendMessage(" ");

			    			sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.UNDERLINE.toString() + "Liste des NPC(s) dans le serveur :");

			    			for(String names : npcs) {

			    				sender.sendMessage(" ");
			    				sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + names);
			    			}

			    			sender.sendMessage(" ");
			    			sender.sendMessage(" ");
			    			sender.sendMessage(ChatColor.GRAY + "===========================");

						}

						return true;

				} else if(args[0].equalsIgnoreCase("reload")) {

					List<NPCGlobal> NPCS = new ArrayList<NPCGlobal>(NPCUtils.getInstance().getAllGlobalNPCs(main));
					sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Rechargement du/des NPC(s)...");

					if(NPCS.isEmpty()) { sender.sendMessage(main.prefix + ChatColor.RED + "Aucun NPC(s) enregistré(s) dans le plugin !"); return false; }

					NPCManager.unloadNPC(null, false);
					NPCManager.loadNPC(null, false, false, false);
					return true;

				} else if(args[0].equalsIgnoreCase("forcereload")) {

					sender.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Forçage du rechargement du/des NPC(s)...");

													/* ----------------------------------------------------- */

					if(NPCManager.getNPCsByConfig(null) != null && !NPCManager.getNPCsByConfig(null).isEmpty()) {

						NPCManager.unloadNPC(null, false);
						NPCManager.loadNPC(null, true, false, false);
						return true;

					} else sender.sendMessage(main.prefix + ChatColor.RED + "Aucun NPC(s) enregistré(s) dans le plugin !"); return false;

				} else { sender.sendMessage(main.prefix + ChatColor.RED + "Argument incorrect, Veuillez voir les arguments accessible en faisant : " + help + " !"); }

			} else {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("list")) sender.sendMessage(Error_Argument(args[0]));
				else sender.sendMessage(main.prefix + ChatColor.RED + "Argument incorrect, Veuillez voir les arguments accessible en faisant : " + help + " !");
			}
		}

        return false;
    }
    /*************************************************************/
    /* PARTIE COMMANDE POUR CRÉER UN NPC (personnage non-joueur) */
    /*************************************************************/


	/**
	 * Renvoie un message d'erreur selon les arguments qui lui ont été passé
	 *
	 * @param arg L'argument utilisé par le joueur de la commande '/NPC'.
	 *
	 * @return Renvoie un message d'erreur.
	 */
	private String Error_Argument(String arg) {

		String result = null;

		Map<String, String> messages = new HashMap<String, String>();

		try {

			for(Field f : NPCCommand.class.getDeclaredFields()) {

				StringBuilder sb = new StringBuilder();

				f.setAccessible(true);
				String fName = f.getName();
				if(fName.equalsIgnoreCase("main") || fName.equalsIgnoreCase("header") || fName.equalsIgnoreCase("footer")) result = null;
				else messages.put(f.getName(), sb.append(f.get(this)).toString());
			}

			for(String argument : messages.keySet()) {

				if(arg.equalsIgnoreCase(argument)) { result = main.prefix + ChatColor.RED + "Essayez " +  messages.get(argument) + " !"; break; }
				else result = main.prefix + ChatColor.RED + "Essayez /npc ? ou /npc help pour vérifier comment utiliser la commande '/npc' !";
			}

		} catch(IllegalAccessException e) { e.printStackTrace(); }

		return result;
	}

					/* --------------------------------------------------------------------------------*/

	/**
	 * Vérifie si un {@link NPCGlobal NPC Global} demandé existe ou n'existe pas.
	 *
	 * @param npc Le {@link NPCGlobal NPC Global} en question
	 * @param p Le Joueur qui recevra le message d'erreur en cas de NPC non existant ou existant en fonction de ce qui a été demandé
	 * @param checkNotExist Doit-on vérifier si le NPC n'existe pas ('true') ou s'il existe ('false')
	 *
	 * @return Une valeur Booléenne
	 */
	private boolean checkNPCExists(NPCGlobal npc, Player p, boolean checkNotExist) {

		if(checkNotExist) {

			if(npc == null || !NPCEntity.isCreated(npc) && !npc.getPersonal(p).isCreated()) { p.sendMessage(main.prefix + ChatColor.RED + "Le NPC n'éxiste pas !"); return false; }

									/* ----------------------------------------------------- */

			NPCEntity.checkToCreate(npc);
			return true;

		} else {

			if(npc != null && NPCEntity.isCreated(npc) && npc.getPersonal(p).isCreated()) { p.sendMessage(main.prefix + ChatColor.RED + "Le NPC éxiste déjà !"); return false; }

									/* ----------------------------------------------------- */

			NPCEntity.checkToDestroy(npc);
			return true;
		}
	}
}
