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
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import org.bukkit.Bukkit;
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
	private final UtilityMain main;
	public NPCCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */


	// Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //
	String header = ChatFormatting.GRAY + "============== " + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "NPC" + ChatFormatting.GRAY + " ==============";
	String footer = ChatFormatting.GRAY + "=============================";
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

			if(!p.hasPermission("evhoutility.npc")) { p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !"); return true; }

			if(args.length == 0) {

				p.sendMessage(header);
				p.sendMessage("");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + help + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Affiche la liste des commandes des NPCs.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + list + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Affiche la liste des NPCs dans le Serveur.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + reload + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Recharge tous les NPCs associés au joueur ou un NPC spécifique.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + forcereload + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Force le rechargement de tous les NPCs associés au joueur ou un NPC spécifique à partir du fichier de configuration.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + create + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Créer un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + remove + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Supprime un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + tp + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Se Téléporte à un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + tpto + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Téléporte un NPC à la location du joueur.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + rotate + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Change la Rotation d'un NPC à partir de la même Rotation du Joueur.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + lookplayer + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Change la Rotation de tête d'un NPC et suis le joueur s'il est proche.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + skin + ChatFormatting.WHITE + " : "  + ChatFormatting.GRAY + "Change le Skin du NPC éxistant");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + pose + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Définit une pose à un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + equipment + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Définit l'Équipement d'un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + animation + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Joue une animation à un NPC (true|false - pour jouer l'animation à l'infinie ou non [false par défaut]).");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + skinstatus + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Définit l'État du Skin à Affiché d'un NPC.");
				p.sendMessage("");
				p.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + interactMessage + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Définit un message du 'NPC' avec un Nom Customisé lorsqu'un joueur intéragira avec.");
				p.sendMessage("");
				p.sendMessage("");
				p.sendMessage(footer);
				return true;

			} else if(args.length == 1) {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { p.performCommand("npc"); return true; }
				else if(args[0].equalsIgnoreCase("list")) {

						Set<NPCGlobal> npcs = NPCUtils.getInstance().getAllGlobalNPCs(); // Liste des noms de 'NPCs' enregistrés

						// Récupère la couleur blanche avec de l'italique des codes couleurs Minecraft
						String white_italique = ChatFormatting.WHITE.toString() + ChatFormatting.ITALIC.toString();

						// Récupère la couleur gris foncé avec de l'italique des codes couleurs Minecraft
						String darkgray_italic = ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC.toString();

						// Récupère la couleur Or avec du gras des codes couleurs Minecraft
						String gold_bold = ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString();

						if(npcs.isEmpty()) {

							p.sendMessage(main.prefix + ChatFormatting.RED + "Aucun NPC(s) enregistré(s) dans le plugin !");

						} else if(npcs.size() == 1) {

							NPCGlobal firstNPC = npcs.stream().findFirst().get();
		    				p.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement le NPC " + gold_bold + firstNPC.getSimpleCode()
										 + white_italique + " (" + darkgray_italic + firstNPC.getCustomName() + white_italique + ")" + ChatFormatting.GRAY + " dans le serveur !");

						} else {

							p.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
			    			p.sendMessage(" ");
			    			p.sendMessage(" ");

			    			p.sendMessage(ChatFormatting.YELLOW.toString() + ChatFormatting.UNDERLINE.toString() + "Liste du/des NPC(s) dans le serveur :");

			    			for(NPCGlobal NPC : npcs) {

								p.sendMessage(" ");

								Component npcName = CustomMethod.StringToComponent(gold_bold + NPC.getSimpleCode() + white_italique + " (" + darkgray_italic + NPC.getCustomName() + white_italique + ")");

								npcName = npcName.clickEvent(ClickEvent.runCommand("/npc tp " + NPC.getSimpleCode()));
								npcName = npcName.hoverEvent(HoverEvent.showText(CustomMethod.StringToComponent("Cliquez pour vous y téléporter")));


								Component message = CustomMethod.StringToComponent(ChatFormatting.WHITE + "- ").append(npcName);
								p.sendMessage(message);
			    			}

			    			p.sendMessage(" ");
			    			p.sendMessage(" ");
			    			p.sendMessage(ChatFormatting.GRAY + "===========================");
						}

				} else if(args[0].equalsIgnoreCase("reload")) {

					List<NPCPersonal> NPCS = new ArrayList<>(NPCUtils.getInstance().getPersonalNPCs(p, main));
					p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Rechargement de votre/vos NPC(s)...");

					if(NPCS.isEmpty()) { p.sendMessage(main.prefix + ChatFormatting.RED + "Aucun NPC(s) enregistré(s) !"); return false; }

					NPCManager.unloadNPC(p, false);
					NPCManager.loadNPC(p, false, false, false);
					return true;

				} else if(args[0].equalsIgnoreCase("forcereload")) {

					p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Forçage du rechargement de votre/vos NPC(s)...");

													/* ----------------------------------------------------- */

					if(NPCManager.getNPCsByConfig(p) != null && !NPCManager.getNPCsByConfig(p).isEmpty()) {

						NPCManager.unloadNPC(p, false);
						NPCManager.loadNPC(p, true, false, false);
						return true;

					} else p.sendMessage(main.prefix + ChatFormatting.RED + "Aucun NPC(s) enregistré(s) !"); return false;

				} else { p.sendMessage(Error_Argument(args[0])); return false; }

			} else if(args.length == 2) {

				final String npcname = args[1].toUpperCase(); // Permettra de récupérer le Nom du NPC
				if(args[1].length() >= 16) { p.sendMessage(main.prefix + ChatFormatting.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

				String npcReg = "NPC." + npcname; // Récupère la section du NPC en question

											 /*-------------------------------------*/

				if(NPCUtils.getInstance().getGlobalNPC(main, npcname) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, npcname); }

											 /*-------------------------------------*/

				if(args[0].equalsIgnoreCase("create")) {

					if(checkNPCExists(npc, p, false)) return false;
					else {

						Map<NPC.Skin.Part, String> skinStatus = new HashMap<>();
						Map<EquipmentSlot, ItemStack> equipments = new HashMap<>();

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

						String customName = ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC.toString() + "Inconnu(e)";
						String format = ChatFormatting.WHITE.toString() + ChatFormatting.ITALIC.toString() + " >> " + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "...";

						npc.addMessageClickAction(customName + format); // Définit un message customisé
						npc.setCustomName("Inconnu(e)"); // Définit un Nom Customisé pour le NPC.

										/* -------------------------------------------- */

						npc.forceUpdate();

										/* -------------------------------------------- */

						NPCManager.save_NPC(npcname, location, npc.getPose().name(), skinStatus, equipments, null, npc.getSkin(),
											npc.getCustomName(), "...", false, null);
						NPCEntity.runAnimation(npc).updateConfig(false);
						main.NPCS.putIfAbsent(npcname, npc);

						p.sendMessage(main.prefix + ChatFormatting.GREEN + "Le NPC a été créer !");
						return true;
					}

				} else if(args[0].equalsIgnoreCase("remove")) {

					if(checkNPCExists(npc, p, true)) return false;
					else {

						NPCManager.unSave_NPC(npcname);
                        main.NPCS.remove(npcname);
						NPCUtils.getInstance().removeGlobalNPC(npc);

						p.sendMessage(main.prefix + ChatFormatting.GREEN + "Le NPC a été supprimer !");
						return true;
					}

				} else if(args[0].equalsIgnoreCase("tp")) {

					if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					p.teleport(npc.getLocation());
					p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez été Téléporté vers le NPC !");
					return true;

				} else if(args[0].equalsIgnoreCase("tpto")) {

					if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					npc.teleport(p);
					p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez Téléporté le NPC vers vous !");

					// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
					ConfigFile.set(main.NPCConfig, npcReg + ".Location.x", String.valueOf(npc.getX()));
					ConfigFile.set(main.NPCConfig, npcReg + ".Location.y", String.valueOf(npc.getY()));
					ConfigFile.set(main.NPCConfig, npcReg + ".Location.z", String.valueOf(npc.getZ()));
					ConfigFile.set(main.NPCConfig, npcReg + ".Location.World", String.valueOf(npc.getWorld()));
					ConfigFile.set(main.NPCConfig, npcReg + ".Location.Yaw", String.valueOf(npc.getYaw()));
					ConfigFile.set(main.NPCConfig, npcReg + ".Location.Pitch", String.valueOf(npc.getPitch()));
					ConfigFile.saveConfig(main.NPCConfig);
					// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

					return true;

				} else if(args[0].equalsIgnoreCase("rotate")) {

					if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					npc.lookAt(location.getYaw(), location.getPitch(), true);
					p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez changé la Rotation du NPC !");

					// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
					ConfigFile.set(main.NPCConfig, npcReg + ".Location.Yaw", String.valueOf(location.getYaw()));
					ConfigFile.set(main.NPCConfig, npcReg + ".Location.Pitch", String.valueOf(location.getPitch()));
					ConfigFile.saveConfig(main.NPCConfig);
					// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

					return true;

				} else if(args[0].equalsIgnoreCase("lookplayer")) {

					if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					if(npc.getGazeTrackingType() == NPC.GazeTrackingType.NEAREST_PLAYER) {

						npc.setGazeTrackingType(NPC.GazeTrackingType.NONE);
						npc.getGazeTrackingType();
						p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le NPC ne va plus suivre avec sa tête les joueurs proches");

						// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
						ConfigFile.set(main.NPCConfig, npcReg + ".FollowPlayerWithHead", false);
						// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					} else {

						npc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);
						p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le NPC va désormait suivre avec sa tête les joueurs proches");

						// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
						ConfigFile.set(main.NPCConfig, npcReg + ".FollowPlayerWithHead", true);
						// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
					}

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

					ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
					return true;

				} else if(args[0].equalsIgnoreCase("reload")) {

					if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					NPCManager.unloadNPC(null, false);
					NPCManager.loadNPC(null, false, false, false);
					p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le NPC a été recharger !");
					return true;

				} else if(args[0].equalsIgnoreCase("forcereload")) {

					if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

					NPCManager.unloadNPC(null, false);
					NPCManager.loadNPC(null, true, false, false);
					p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le NPC a été recharger de force !");
					return true;

				} else { p.sendMessage(Error_Argument(args[0])); }

			} else if(args.length == 3) {

				final String npcname = args[2].toUpperCase();
				if(args[2].length() >= 16) { p.sendMessage(main.prefix + ChatFormatting.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

				String npcReg = "NPC." + npcname; // Récupère la section du NPC en question

											 /*-------------------------------------*/

				if(NPCUtils.getInstance().getGlobalNPC(main, npcname) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, npcname); }

											 /*-------------------------------------*/

				if(args[0].equalsIgnoreCase("animation")) {

					String animationMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "L'Animation choisit du NPC a été joué !";
					RunAnimationNPC animationRunnable = NPCEntity.runAnimation(npc);

					if(args[1].equalsIgnoreCase("mainswing")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_MAIN_ARM, 0);
						npc.playAnimation(NPC.Animation.SWING_MAIN_ARM);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("offswing")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_OFF_HAND, 0);
						npc.playAnimation(NPC.Animation.SWING_OFF_HAND);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("damage")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.TAKE_DAMAGE, 0);
						npc.playAnimation(NPC.Animation.TAKE_DAMAGE);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("criticeffect")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.CRITICAL_EFFECT, 0);
						npc.playAnimation(NPC.Animation.CRITICAL_EFFECT);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("criticmagiceffect")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.MAGICAL_CRITICAL_EFFECT, 0);
						npc.playAnimation(NPC.Animation.MAGICAL_CRITICAL_EFFECT);

						animationRunnable.updateConfig(false);
						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("stop")) {

						BukkitTask task = animationRunnable.getTask();

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_MAIN_ARM, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.SWING_OFF_HAND, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.TAKE_DAMAGE, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.CRITICAL_EFFECT, 0);
						animationRunnable.setAnimationStatus(NPC.Animation.MAGICAL_CRITICAL_EFFECT, 0);

						animationRunnable.updateConfig(false);
						if(task != null && !task.isCancelled()) animationRunnable.getTask().cancel();

						p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Toutes les Animations du NPC ont été arrêtées !");

						// ⬇️ Recharge le NPC ⬇️ //
						npc.update();
						// ⬆️ Recharge le NPC ⬆️ //

						return true;

					} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + animation + " !");

				} else if(args[0].equalsIgnoreCase("pose")) {

					String poseMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "La Posture du NPC a été effectuée !";
					String alreadyPoseMessage = main.prefix + ChatFormatting.RED.toString() + ChatFormatting.ITALIC.toString() + "Le NPC a déjà cette Posture !";

					if(args[1].equalsIgnoreCase("stand")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						Pose STAND = Pose.STANDING; // Posture du NPC

						if(npc.getPose() == STAND) { p.sendMessage(alreadyPoseMessage); return false; }
						else {

							npc.setPose(STAND);
							p.sendMessage(poseMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.Pose", npc.getPose().name());
							ConfigFile.saveConfig(main.NPCConfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.update();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;
						}

					} else if(args[1].equalsIgnoreCase("fallfly")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						Pose FALL_FLY = Pose.FALL_FLYING; // Posture du NPC

						if(npc.getPose() == FALL_FLY) { p.sendMessage(alreadyPoseMessage); return false; }
						else {

							npc.setPose(FALL_FLY);
							p.sendMessage(poseMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.Pose", npc.getPose().name());
							ConfigFile.saveConfig(main.NPCConfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.update();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;
						}

					} else if(args[1].equalsIgnoreCase("sleep")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						Pose SLEEP = Pose.SLEEPING; // Posture du NPC

						if(npc.getPose() == SLEEP) { p.sendMessage(alreadyPoseMessage); return false; }
						else {

							npc.setPose(SLEEP);
							p.sendMessage(poseMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.Pose", npc.getPose().name());
							ConfigFile.saveConfig(main.NPCConfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.update();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;
						}

					} else if(args[1].equalsIgnoreCase("crouch")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						Pose CROUCH = Pose.CROUCHING; // Posture du NPC

						if(npc.getPose() == CROUCH) { p.sendMessage(alreadyPoseMessage); return false; }
						else {

							npc.setPose(CROUCH);
							p.sendMessage(poseMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.Pose", npc.getPose().name());
							ConfigFile.saveConfig(main.NPCConfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.update();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;
						}

					} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + pose + " !");

				} else if(args[0].equalsIgnoreCase("skinstatus")) {

					NPC.Skin.Parts skinParts = npc.getSkinParts();

					String skinStatusOnMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le Status du Skin en question a été affiché au NPC !";
					String allSkinStatusOnMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez affiché tous les Status du Skin du NPC !";

					String skinStatusOffMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le Status du Skin en question a été enlevé du NPC !";
					String allSkinStatusOffMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez enlevé tous les Status du Skin du NPC !";

					if(args[1].equalsIgnoreCase("all")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.getInvisibleParts().isEmpty()) {

							skinParts.disableAll();
							updateNPCSkinStatus(p, npcReg, allSkinStatusOffMessage, true);

						} else {

							skinParts.enableAll();
							updateNPCSkinStatus(p, npcReg, allSkinStatusOffMessage, false);
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("hat")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isHat()) {

							skinParts.setHat(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.HAT", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setHat(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.HAT", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("rightpants")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isRightPants()) {

							skinParts.setRightPants(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setRightPants(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("leftpants")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isLeftPants()) {

							skinParts.setLeftPants(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setLeftPants(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("rightsleeve")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isRightSleeve()) {

							skinParts.setRightSleeve(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setRightSleeve(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("leftsleeve")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isLeftSleeve()) {

							skinParts.setLeftSleeve(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setLeftSleeve(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("jacket")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isJacket()) {

							skinParts.setJacket(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.JACKET", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setJacket(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.JACKET", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("cape")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(skinParts.isCape()) {

							skinParts.setCape(false);
							p.sendMessage(skinStatusOffMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.CAPE", "false");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

						} else {

							skinParts.setCape(true);
							p.sendMessage(skinStatusOnMessage);

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.CAPE", "true");
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //
						}

						// ⬇️ Recharge le NPC ⬇️ //
						npc.forceUpdate();
						// ⬆️ Recharge le NPC ⬆️ //

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + skinstatus + " !");

				} else if(args[0].equalsIgnoreCase("skin")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						UUID uuid = PlayerEntity.getUUIDByPlayerName(args[1], null); // Récupère l'UUID du pseudo définit
						Skin skin = Skin.get(uuid); // Récupère le Skin à partir de l'UUID du pseudo définit

						// Créer une texture customisée à partir de du Skin récupéré
						NPC.Skin skinNPC = NPC.Skin.createCustomTextureSkin(skin.getValue(), skin.getSignature());

						if(!npc.getSkin().equals(skinNPC)) {

							npc.setSkin(skinNPC);
							p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez changé le Skin du NPC !");

							// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
							ConfigFile.set(main.NPCConfig, npcReg + ".Skin.Texture", skinNPC.getTexture());
							ConfigFile.set(main.NPCConfig, npcReg + ".Skin.Signature", skinNPC.getSignature());

							ConfigFile.saveConfig(main.NPCConfig);
							// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Le Skin du NPC est déjà celui que vous voulez attribué !"); return false; }

				} else { p.sendMessage(Error_Argument(args[0])); }

			} else if(args.length == 4) {

				final String npcname = args[3].toUpperCase();
				if(args[3].length() >= 16) { p.sendMessage(main.prefix + ChatFormatting.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

				String npcReg = "NPC." + npcname; // Récupère la section du NPC en question

											 /*-------------------------------------*/

				if(NPCUtils.getInstance().getGlobalNPC(main, npcname) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, npcname); }

											 /*-------------------------------------*/

				if(args[0].equalsIgnoreCase("equipment")) {

					String itemname = args[2].toUpperCase();
					ItemStack item = new ItemStack(Material.getMaterial(itemname));

					if(args[1].equalsIgnoreCase("main")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.MAINHAND).equals(item)) {

							npc.setItem(EquipmentSlot.MAINHAND, item);
							p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "La Main Principale du NPC a été équipée !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "MAINHAND", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Le NPC a déjà cette Item d'Équipé dans sa Main Principale !"); return false; }

					} else if(args[1].equalsIgnoreCase("off")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.OFFHAND).equals(item)) {

							npc.setItem(EquipmentSlot.OFFHAND, item);
							p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "La Main Secondaire du NPC a été équipée !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "OFFHAND", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Le NPC a déjà cette Item d'Équipé dans sa Main Secondaire !"); return false; }

					} else if(args[1].equalsIgnoreCase("feet")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.FEET).equals(item)) {

							npc.setItem(EquipmentSlot.FEET, item);
							p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Les Bottes du NPC ont été équipées !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "BOOTS", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Le NPC a déjà cette Item d'Équipé au niveau de ses Bottes !"); return false; }

					} else if(args[1].equalsIgnoreCase("legs")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.LEGS).equals(item)) {

							npc.setItem(EquipmentSlot.LEGS, item);
							p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Les jambières du NPC ont été équipées !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "LEGGINGS", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Le NPC a déjà cette Item d'Équipé au niveau de ses Jambières !"); return false; }

					} else if(args[1].equalsIgnoreCase("chest")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.CHEST).equals(item)) {

							npc.setItem(EquipmentSlot.CHEST, item);
							p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le Plastron du NPC a été équipées !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "CHESTPLATE", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Le NPC a déjà cette Item d'Équipé au niveau de son Plastron !"); return false; }

					} else if(args[1].equalsIgnoreCase("head")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						if(!npc.getEquipment(EquipmentSlot.HEAD).equals(item)) {

							npc.setItem(EquipmentSlot.HEAD, item);
							p.sendMessage(main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le Casque du NPC a été équipées !");

							// ⬇️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬇️ //
							NPCManager.saveEquipment(npcname, item, "HEAD", true);
							// ⬆️ Enregistre dans le fichier de configuration l'Équipement en question du NPC ⬆️ //

							// ⬇️ Recharge le NPC ⬇️ //
							npc.forceUpdate();
							// ⬆️ Recharge le NPC ⬆️ //

							return true;

						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Le NPC a déjà cette Item d'Équipé au niveau de son Casque !"); return false; }

					} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + equipment + " !");

				} else if(args[0].equalsIgnoreCase("animation")) {

					RunAnimationNPC animationRunnable = NPCEntity.runAnimation(npc);
					BukkitTask task = animationRunnable.getTask();

					String animationMessage;
					Integer valueInfinite;

					if(args[1].equalsIgnoreCase("stop")) { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /npc animation stop <name> !"); return false; }

					if(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {

						boolean infinite = Boolean.parseBoolean(args[2]);

						if(infinite) {

							animationMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "L'Animation choisit du NPC sera joué en boucle !";
							valueInfinite = 1;

						} else {

							animationMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "L'Animation choisit du NPC sera joué une seule fois !";
							valueInfinite = 0;
						}

					} else {

						p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + animationWithThirdArgument + " !");
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

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_MAIN_ARM, valueInfinite);
						if(valueInfinite.equals(0)) npc.playAnimation(NPC.Animation.SWING_MAIN_ARM);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(1)) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("offswing")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.SWING_OFF_HAND, valueInfinite);
						if(valueInfinite.equals(0)) npc.playAnimation(NPC.Animation.SWING_OFF_HAND);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(1)) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);
						return true;

					} else if(args[1].equalsIgnoreCase("damage")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.TAKE_DAMAGE, valueInfinite);
						if(valueInfinite.equals(0)) npc.playAnimation(NPC.Animation.TAKE_DAMAGE);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(1)) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("criticeffect")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.CRITICAL_EFFECT, valueInfinite);
						if(valueInfinite.equals(0)) npc.playAnimation(NPC.Animation.CRITICAL_EFFECT);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(1)) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else if(args[1].equalsIgnoreCase("criticmagiceffect")) {

						if(checkNPCExists(npc, p, true)) return false;

										/* ------------------------------------------- */

						animationRunnable.setAnimationStatus(NPC.Animation.MAGICAL_CRITICAL_EFFECT, valueInfinite);
						if(valueInfinite.equals(0)) npc.playAnimation(NPC.Animation.MAGICAL_CRITICAL_EFFECT);

						animationRunnable.updateConfig(false);

						if(valueInfinite.equals(1)) {

							if(task == null || task.isCancelled()) animationRunnable.run();
							else { task.cancel(); animationRunnable.run(); }
						}

						p.sendMessage(animationMessage);

						return true;

					} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + animationWithThirdArgument + " !");

				} else if(args[0].equalsIgnoreCase("skinstatus")) {

					NPC.Skin.Parts skinParts = npc.getSkinParts();

					String skinStatusMessage;
					String allSkinStatusMessage;
					String alreadySkinStatusMessage;
					String alreadyAllSkinStatusMessage;

					boolean hasEnabled;

					if(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {

						boolean state = Boolean.parseBoolean(args[2]);

						if(state) {

							hasEnabled = true;

							skinStatusMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le Status du Skin en question a été affiché au NPC !";
							alreadySkinStatusMessage = main.prefix + ChatFormatting.RED.toString() + ChatFormatting.ITALIC.toString() + "Le Status du Skin que vous demandez est déjà affiché sur le NPC !";
							allSkinStatusMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez affiché tous les Status du Skin du NPC !";
							alreadyAllSkinStatusMessage = main.prefix + ChatFormatting.RED.toString() + ChatFormatting.ITALIC.toString() + "Le NPC possède dèjà tous les Status de Skin !";

						} else {

							hasEnabled = false;

							skinStatusMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Le Status du Skin en question a été enlevé du NPC !";
							alreadySkinStatusMessage = main.prefix + ChatFormatting.RED.toString() + ChatFormatting.ITALIC.toString() + "Le Status du Skin que vous demandez n'est pas sur le NPC !";
							allSkinStatusMessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez enlevé tous les Status du Skin du NPC !";
							alreadyAllSkinStatusMessage = main.prefix + ChatFormatting.RED.toString() + ChatFormatting.ITALIC.toString() + "Le NPC ne possède pas tous les Status de Skin !";
						}

					} else {

						p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + skinstatusWithThirdArgument + " !");
						return false;
					}

					if(args[1].equalsIgnoreCase("all")) {

						if(checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.getInvisibleParts().isEmpty()) {

							if(hasEnabled) p.sendMessage(alreadyAllSkinStatusMessage);
							else {

								skinParts.enableAll();
								updateNPCSkinStatus(p, npcReg, allSkinStatusMessage, true);

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}

						} else {

							if(!hasEnabled) p.sendMessage(alreadyAllSkinStatusMessage);
							else {

								skinParts.disableAll();
								updateNPCSkinStatus(p, npcReg, allSkinStatusMessage, false);

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("hat")) {

						if(checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isHat()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setHat(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.HAT", "false");
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
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.HAT", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("rightpants")) {

						if(checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isRightPants()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setRightPants(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "false");
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
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("leftpants")) {

						if(checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isLeftPants()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setLeftPants(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "false");
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
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("rightsleeve")) {

						if(checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isRightSleeve()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setRightSleeve(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "false");
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
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("leftsleeve")) {

						if(checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isLeftSleeve()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setLeftSleeve(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "false");
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
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("jacket")) {

						if(checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isJacket()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setJacket(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.JACKET", "false");
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
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.JACKET", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else if(args[1].equalsIgnoreCase("cape")) {

						if(checkNPCExists(npc, p, true)) return false;

												/* ------------------------------------------- */

						if(skinParts.isCape()) {

							if(hasEnabled) p.sendMessage(alreadySkinStatusMessage);
							else {

								skinParts.setCape(false);
								p.sendMessage(skinStatusMessage);

								// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.CAPE", "false");
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
								ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.CAPE", "true");
								// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

								// ⬇️ Recharge le NPC ⬇️ //
								npc.forceUpdate();
								// ⬆️ Recharge le NPC ⬆️ //
							}
						}

						ConfigFile.saveConfig(main.NPCConfig); // Enregistre le fichier de configuration
						return true;

					} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + skinstatusWithThirdArgument + " !");

				} else if(args[0].equalsIgnoreCase("interactmessage")) {

					if(args[1].length() >= 16) { p.sendMessage(main.prefix + ChatFormatting.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

					npcReg = "NPC." + args[1].toUpperCase(); // Récupère la section du NPC en question

											 /*-------------------------------------*/

					if(NPCUtils.getInstance().getGlobalNPC(main, args[1].toUpperCase()) != null) { npc = NPCUtils.getInstance().getGlobalNPC(main, args[1].toUpperCase()); }

											 /*-------------------------------------*/

					// Message de succès
					String interactmessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez changé le message d'interaction du NPC.";

					if(checkNPCExists(npc, p, true)) return false;

									/* ------------------------------------------- */

					// ⬇️ On enlève les actions du NPC comportant des Messages Customisés ⬇️ //
					npc.getClickActions().forEach(clickAction -> {

						if(clickAction.getActionType() == NPC.Interact.Actions.Type.SEND_MESSAGE) clickAction.getNPC().removeClickAction(clickAction);
					});
					// ⬆️ On enlève les actions du NPC comportant des Messages Customisés ⬆️ //

									/* ------------------------------------------- */

					npc.setCustomName(args[2]);
					String customName = ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC.toString() + args[2];
					String format = ChatFormatting.WHITE.toString() + ChatFormatting.ITALIC.toString() + " >> " + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + args[3];

					npc.addMessageClickAction(customName + format); // Définit un message customisé
					p.sendMessage(interactmessage);

					// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
					ConfigFile.set(main.NPCConfig, npcReg + ".InteractEvent.Sender", args[2]);
					ConfigFile.set(main.NPCConfig, npcReg + ".InteractEvent.Message", args[3]);

					ConfigFile.saveConfig(main.NPCConfig);
					// ⬆️ Enregistre dans le fichier de configuration ⬆️ //

					// ⬇️ Recharge le NPC ⬇️ //
					npc.update();
					// ⬆️ Recharge le NPC ⬆️ //

				} else { p.sendMessage(Error_Argument(args[0])); }

			} else {

				if(args[0].equalsIgnoreCase("interactmessage")) {

					final String npcname = args[1].toUpperCase();
					if(args[1].length() >= 16) { p.sendMessage(main.prefix + ChatFormatting.RED + "Le Nom peut pas faire plus de 16 caractères !"); return false; }

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
					String interactmessage = main.prefix + ChatFormatting.GREEN.toString() + ChatFormatting.ITALIC.toString() + "Vous avez changé le message d'interaction du NPC.";

					if(checkNPCExists(npc, p, true)) return false;


									/* ------------------------------------------- */

					// ⬇️ On enlève les actions du NPC comportant des Messages Customisés ⬇️ //
                    List<NPC.Interact.ClickAction> clickActionList = new ArrayList<>(npc.getClickActions());
					clickActionList.forEach(clickAction -> {

						if(clickAction.getActionType() == NPC.Interact.Actions.Type.SEND_MESSAGE) clickAction.getNPC().removeClickAction(clickAction);
					});
					clickActionList.clear();
					// ⬆️ On enlève les actions du NPC comportant des Messages Customisés ⬆️ //

									/* ------------------------------------------- */


					npc.setCustomName(args[2]);
					String customName = ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC.toString() + args[2];
					String format = ChatFormatting.WHITE.toString() + ChatFormatting.ITALIC.toString() + " >> " + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + combinedArgs;


					// Convertie le message en question à envoyer
					String messageConverted = p.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(customName + format) : customName + format;

					npc.addMessageClickAction(messageConverted);
					p.sendMessage(interactmessage);

					// ⬇️ Enregistre dans le fichier de configuration ⬇️ //
					ConfigFile.set(main.NPCConfig, npcReg + ".InteractEvent.Sender", args[2]);
					ConfigFile.set(main.NPCConfig, npcReg + ".InteractEvent.Message", combinedArgs);

					ConfigFile.saveConfig(main.NPCConfig);
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
				sender.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + help + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Affiche la liste des commandes des NPCs.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + list + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Affiche la liste des NPCs dans le Serveur.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + "/npc reload" + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Recharge tous les NPCs dans le Serveur.");
				sender.sendMessage("");
				sender.sendMessage(ChatFormatting.YELLOW + "- " + ChatFormatting.GOLD + "/npc forcereload" + ChatFormatting.WHITE + " : " + ChatFormatting.GRAY + "Force le rechargement de tous les NPCs dans le Serveur à partir du fichier de configuration.");
				sender.sendMessage("");
				sender.sendMessage("");
				sender.sendMessage(footer);
				return true;

			} else if(args.length == 1) {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { Bukkit.getServer().dispatchCommand(sender, "npc"); }
				else if(args[0].equalsIgnoreCase("list")) {

						Set<String> npcs = main.NPCS.keySet();

						/****************************************/

						if(npcs.isEmpty()) sender.sendMessage(main.prefix + ChatFormatting.RED + "Aucun NPC(s) enregistré(s) dans le plugin !");
						else if(npcs.size() == 1) sender.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement le NPC " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + npcs.toArray()[0] + ChatFormatting.GRAY + " dans le serveur !");
						else {

							sender.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
			    			sender.sendMessage(" ");
			    			sender.sendMessage(" ");

			    			sender.sendMessage(ChatFormatting.AQUA.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des NPC(s) dans le serveur :");

			    			for(String names : npcs) {

			    				sender.sendMessage(" ");
			    				sender.sendMessage(ChatFormatting.WHITE + "- " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + names);
			    			}

			    			sender.sendMessage(" ");
			    			sender.sendMessage(" ");
			    			sender.sendMessage(ChatFormatting.GRAY + "===========================");

						}

						return true;

				} else if(args[0].equalsIgnoreCase("reload")) {

					List<NPCGlobal> NPCS = new ArrayList<>(NPCUtils.getInstance().getAllGlobalNPCs(main));
					sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Rechargement du/des NPC(s)...");

					if(NPCS.isEmpty()) {

						sender.sendMessage(main.prefix + ChatFormatting.RED + "Aucun NPC(s) enregistré(s) dans le plugin !");
						return false;
					}

					NPCManager.unloadNPC(null, false);
					NPCManager.loadNPC(null, false, false, false);
					return true;

				} else if(args[0].equalsIgnoreCase("forcereload")) {

					sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Forçage du rechargement du/des NPC(s)...");

													/* ----------------------------------------------------- */

					if(NPCManager.getNPCsByConfig(null) != null && !NPCManager.getNPCsByConfig(null).isEmpty()) {

						NPCManager.unloadNPC(null, false);
						NPCManager.loadNPC(null, true, false, false);
						return true;

					} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Aucun NPC(s) enregistré(s) dans le plugin !"); return false;

				} else { sender.sendMessage(main.prefix + ChatFormatting.RED + "Argument incorrect, Veuillez voir les arguments accessible en faisant : " + help + " !"); }

			} else {

				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("list")) sender.sendMessage(Error_Argument(args[0]));
				else sender.sendMessage(main.prefix + ChatFormatting.RED + "Argument incorrect, Veuillez voir les arguments accessible en faisant : " + help + " !");
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

		Map<String, String> messages = new HashMap<>();

		try {

			for(Field f : NPCCommand.class.getDeclaredFields()) {

				StringBuilder sb = new StringBuilder();

				f.setAccessible(true);
				String fName = f.getName();
				if(fName.equalsIgnoreCase("main") || fName.equalsIgnoreCase("header") || fName.equalsIgnoreCase("footer")) result = null;
				else messages.put(f.getName(), sb.append(f.get(this)).toString());
			}

			for(String argument : messages.keySet()) {

				if(arg.equalsIgnoreCase(argument)) { result = main.prefix + ChatFormatting.RED + "Essayez " +  messages.get(argument) + " !"; break; }
				else result = main.prefix + ChatFormatting.RED + "Essayez /npc ? ou /npc help pour vérifier comment utiliser la commande '/npc' !";
			}

		} catch(IllegalAccessException e) { e.printStackTrace(System.err); }

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

			if(npc == null || !NPCEntity.isCreated(npc) && !npc.getPersonal(p).isCreated()) {

				p.sendMessage(main.prefix + ChatFormatting.RED + "Le NPC n'éxiste pas !");
				return true;
			}

									/* ----------------------------------------------------- */

			NPCEntity.checkToCreate(npc);

        } else {

			if(npc != null && NPCEntity.isCreated(npc) && npc.getPersonal(p).isCreated()) {

				p.sendMessage(main.prefix + ChatFormatting.RED + "Le NPC éxiste déjà !");
				return true;
			}

									/* ----------------------------------------------------- */

			NPCEntity.checkToDestroy(npc);
        }
        return false;
    }

	/**
	 * Met à jour les statuts de la peau d'un NPC dans le fichier de configuration
	 * et envoie un message au joueur spécifié.
	 *
	 * @param player Le joueur auquel envoie le message.
	 * @param npcReg La clé du registre du NPC à mettre à jour.
	 */
	public void updateNPCSkinStatus(Player player, String npcReg, String allSkinStatusMessage, boolean state) {

		// Envoyer un message au joueur
		player.sendMessage(allSkinStatusMessage);

		// Mettre à jour les statuts de la peau dans le fichier de configuration
		ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.CAPE", state ? "true" : "false");
		ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.JACKET", state ? "true" : "false");
		ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE", state ? "true" : "false");
		ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE", state ? "true" : "false");
		ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_PANTS", state ? "true" : "false");
		ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS", state ? "true" : "false");
		ConfigFile.set(main.NPCConfig, npcReg + ".Data.SkinStatus.HAT", state ? "true" : "false");
	}
}
