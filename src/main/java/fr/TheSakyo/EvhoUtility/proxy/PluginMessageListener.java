package fr.TheSakyo.EvhoUtility.proxy;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dependancies.emoji4j.EmojiUtils;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.utils.api.Advancements.*;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import net.luckperms.api.model.user.User;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public PluginMessageListener(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */

	/************************************************************/
	/* MÉTHODE POUR RÉCUPÉRER DES INFORMATIONS VENANT DE BUNGEE */
	/************************************************************/
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {

		// On vérifie si le canal est celui par défaut de BungeeCoord ou celui connecté au Serveur Proxy
		if (channel.equalsIgnoreCase(UtilityMain.channel) || channel.equalsIgnoreCase(UtilityMain.channelCustom)) {

			// Récupère les données envoyées par le Serveur Proxy
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

			String server; // Variable 'null' par défaut qui nous permettra de récupérer le Serveur envoyé par le Serveur Proxy

			// ---------------------------------------------------------------------------------- //
			// ~~~ # ⬇️ ON ESSAIE DE RÉCUPÉRÉ LES MESSAGES ENVOYÉS PAR LE SERVEUR PROXY ⬇️ # ~~~ //
			// ---------------------------------------------------------------------------------- //

			try {

				String subChannel = in.readUTF(); // Récupère le canal lié avec le Serveur actuel et le Serveur Proxy

				switch (subChannel) {

					case "Advancements":

						String[] achievement = in.readUTF().split(", "); // Récupère les paramètres envoyés

						/* ~~~ On récupère les différents paramètres ~~~ */

						try {

							String player_adv = achievement[0];
							boolean PlayerWithGrade = Boolean.parseBoolean(achievement[1]);
							String title = achievement[2];
							String description = achievement[3];
							boolean showToast = Boolean.parseBoolean(achievement[4]);
							boolean announceChat = Boolean.parseBoolean(achievement[5]);
							AdvancementFrame frame = AdvancementFrame.valueOf(achievement[6]);
							AdvancementVisibility visibility = AdvancementVisibility.parseVisibility(achievement[7]);
							Sound sound = Sound.valueOf(achievement[8]);
							boolean AdminOnly = Boolean.parseBoolean(achievement[9]);

							/* ⬇️ On vérifie si le joueur et d'autres paramètres récupérés éxiste bien,
							  si ce n'est pas le cas, on ne retourne rien et on affiche une erreur à la console. ⬇️ */
							Player playerAdv = Bukkit.getServer().getPlayerExact(player_adv);
							if (playerAdv == null || visibility == null) {

								main.console.sendMessage(main.prefix + ChatFormatting.GOLD + "Certains paramètres sont null dans le sous-canal 'Advancements' du \"PluginMessageReceived\" !");
								return;
							}
							/* ⬆️ On vérifie si le joueur et d'autres paramètres récupérés éxiste bien,
							  si ce n'est pas le cas, on ne retourne rien et on affiche une erreur à la console. ⬆️ */

							/* ~~~ On récupère les différents paramètres ~~~ */


							ItemStack head = Skin.PlayerHead(null, playerAdv, player_adv); //Partie Récupération Tête du joueur

							// Récupère la couleur du grade du joueur avec (LuckPerms) //
							User user = CustomMethod.getLuckPermUserOffline(playerAdv.getUniqueId());
							String ColorGrade = ColorUtils.format(user.getCachedData().getMetaData().getSuffix());
							// Récupère la couleur du grade du joueur avec (LuckPerms) //


							// ~~~ ⬇️ Affiche un message personnalisé sous système d'un achievement ⬇️ ~~~ //

							// Récupère la couleur du Grade du Joueur et son Nom Customisé si on a défini cette option sur 'Vrai', sinon on ne récupère rien
							String sendPlayerCustomName = PlayerWithGrade ? ColorGrade + CustomMethod.ComponentToString(playerAdv.customName()) : "";

							// Créer le message en question en type d'un achievement //
							AdvancementDisplay JoinDisplay = new AdvancementDisplay(head, sendPlayerCustomName + title, description, frame, showToast, announceChat, visibility);
							Advancement JoinMessage = new Advancement(null, new NameKey("join", "root"), JoinDisplay);
							// Créer le message en question en type d'un achievement //

							// On Vérifie si l'action doit se faire uniquement pour les joueurs Administrateurs
							if (AdminOnly) {

								// Si c'est le cas, on vérifie donc si le Joueur en question est bien un administrateur pour envoyer, le message personnalisé
								if (CustomMethod.hasAdminGrade(playerAdv)) {

									for (Player player : Bukkit.getServer().getOnlinePlayers()) {

										// Si un Son a été défini et éxiste donc bien, on joue le Son à tous les joueurs
										player.playSound(player.getLocation(), sound, 1f, 1f);
										JoinMessage.displayToast(player); // Envoie le Message Customisé
									}
								}

								// Sinon, on envoie le message personnalisé, sans vérification
							} else {

								for (Player player : Bukkit.getServer().getOnlinePlayers()) {

									// Si un Son a été défini et éxiste donc bien, on joue le Son à tous les joueurs
									player.playSound(player.getLocation(), sound, 1f, 1f);
									JoinMessage.displayToast(player);
								}
							}

							// ~~~ ⬆️ Affiche un message personnalisé sous système d'un achievement ⬆️ ~~~ //

						} catch (NoClassDefFoundError ignored) {
						}

						break;

					case "BungeeCount":

						int BungeeCount = in.readInt(); // Récupère le paramètre clé envoyé (joueurs en ligne)
						String serverBungeeName = in.readUTF(); // Récupère le paramètre clé envoyé (Nom du Serveur)
						boolean isBungeeServer = in.readBoolean(); // Récupère le paramètre clé envoyé (true/false)

						// Si vrai, Retourne le résultat souhaité (Nombre de Joueurs Total connecté sur le Proxy).
						if (isBungeeServer) UtilityMain.BungeePlayerOnline = BungeeCount;

							// Sinon, Retourne le résultat souhaité (Nombre de Joueurs Total connecté sur le Serveur Récupéré).
						else {

							if (UtilityMain.ServerPlayerOnline.containsKey(serverBungeeName))
								UtilityMain.ServerPlayerOnline.putIfAbsent(serverBungeeName, BungeeCount);
							else UtilityMain.ServerPlayerOnline.replace(serverBungeeName, BungeeCount);
						}

						break;

					case "Command":

						// Variable permettant d'informer si rien ne se passe, tous est normal :)
						String ifnothing = ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "(Si rien ne se passe, c'est qu'une erreur est survenue à la détection de votre commande)";

						String command = in.readUTF(); // Récupère le paramètre clé envoyé

						// On vérifie si c'est bien le Joueur qui effectue la commande vers le Proxy
						if (main.useProxyCMD.containsKey(p.getUniqueId()) && main.useProxyCMD.get(p.getUniqueId())) {

							// On affiche un message au Joueur lui donnant sa Commande entrée
							p.sendMessage(main.prefix + ChatFormatting.AQUA + "Commande : " + ChatFormatting.GOLD + "/" + command);

							// On lui informe quand vérifie la Commande
							p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vérification de la commande vers le Serveur Proxy...");
							p.sendMessage(ifnothing); // Affiche le message permettant d'informer que si rien ne se passe, tous est normal :)

							/* (!) [Le reste du code s'effectue dans le plugin du Serveur Proxy 'EvhoProxy'] (!) */

							// On enlève le joueur de ceux qui effectue une commande vers le Proxy
							main.useProxyCMD.replace(p.getUniqueId(), false);
						}

						break;

					case "ErrorConnectServer":

						String ErrorConnecting = in.readUTF(); // Récupère le paramètre clé envoyé
						p.sendMessage(ErrorConnecting); // On affiche un message d'Erreur au Joueur essayant de se conencter sur un Serveur inexistant !

						break;

					case "Freezed":

						String[] freezed = in.readUTF().split(", "); // Récupère les paramètres clés envoyés

						// Variable 'commandSender', permettant par la suite de récupérer l'Envoyeur de la Commande
						CommandSender commandSender;

						/* ~~~ On récupère les différents paramètres ~~~ */

						String freezeTitleString = freezed[0];
						String freezedPlayer = freezed[1];
						String sender = freezed[2];

						/* ~~~ On récupère les différents paramètres ~~~ */

						// Si le paramètre récupéré 'sender' est égal à la Console, on récupère la Console du Serveur
						if (sender.equalsIgnoreCase("console")) {
							commandSender = main.console;
						}

						// Sinon, on récupère le Joueur en question
						else {
							commandSender = Bukkit.getServer().getPlayerExact(sender);
						}


						/* ⬇️ On vérifie si le Joueur et l'Envoyeur de la Commande récupérés éxistent bien,
						  si ce n'est pas le cas, on ne retourne rien et on affiche une erreur à la console. ⬇️ */
						Player playerFreezed = Bukkit.getServer().getPlayerExact(freezedPlayer);
						if (playerFreezed == null || commandSender == null) {

							main.console.sendMessage(main.prefix + ChatFormatting.RED + "Une erreur est survenue sur le sous-canal 'Freezed' du \"PluginMessageReceived\", le Joueur ou l'Envoyeur de la Commande est null !");
							return;
						}
						/* ⬆️ On vérifie si le Joueur et l'Envoyeur de la Commande récupérés éxistent bien,
						  si ce n'est pas le cas, on ne retourne rien et on affiche une erreur à la console. ⬆️ */

						User userFreezed = CustomMethod.getLuckPermUserOffline(playerFreezed.getUniqueId()); // Récupère l'Utilisateur LuckPerms du Joueur 'Freeze'
						Map<String, Boolean> permissions = userFreezed.getCachedData().getPermissionData().getPermissionMap(); // Récupère les permissions de l'Utilisateur LuckPerms du Joueur 'Freeze'


						// --- Les différents messages à envoyer au Joueur à 'Freeze' et à l'Envoyeur de la Commande //

						String freezeOnPlayer = freezeTitleString + ChatFormatting.GRAY + "Un Administrateur vient de vous Freeze !";
						String freezeOffPlayer = freezeTitleString + ChatFormatting.GRAY + "Un Administrateur vient de vous UnFreeze !";

						String freezeOnSuccess = freezeTitleString + ChatFormatting.GREEN + "Vous avez Freeze " + ChatFormatting.GOLD + playerFreezed.getName() + ChatFormatting.GREEN + " !";
						String freezeOffSuccess = freezeTitleString + ChatFormatting.GREEN + "Vous avez UnFreeze " + ChatFormatting.GOLD + playerFreezed.getName() + ChatFormatting.GREEN + " !";

						// --- Les différents messages à envoyer au Joueur à 'Freeze' et à l'Envoyeur de la Commande //


						// ⬇️ Si le Joueur à 'Freeze' n'est pas dans la liste, on lui ajoute, puis on affiche les différents Messages tout en ajoutant au Joueur la permission de 'Freeze' ⬇️ //
						if (!main.freezeP.contains(playerFreezed.getUniqueId())) {

							playerFreezed.sendMessage(freezeOnPlayer);
							commandSender.sendMessage(freezeOnSuccess);
							main.freezeP.add(playerFreezed.getUniqueId());

							permissions.putIfAbsent("EvhoProxy.freezed", Boolean.TRUE); // Ajoute la Permission de 'Freeze' au Joueur
						}
						// ⬆️ Si le Joueur à 'Freeze' n'est pas dans la liste, on lui ajoute, puis on affiche les différents Messages tout en ajoutant au Joueur la permission de 'Freeze' ⬆️ //

						/* ----------------------------------- */

						// ⬇️ Sinon, on lui enlève, puis on affiche les différents Messages tout en enlevant au Joueur la permission de 'Freeze' ⬇️ //
						else {

							playerFreezed.sendMessage(freezeOffPlayer);
							commandSender.sendMessage(freezeOffSuccess);
							main.freezeP.remove(playerFreezed.getUniqueId());

							// Enlève la Permission de 'Freeze' au Joueur, si elle existe
							permissions.remove("EvhoProxy.freezed");
						}
						// ⬆️ Sinon, on lui enlève, puis on affiche les différents Messages tout en enlevant au Joueur la permission de 'Freeze' ⬆️ //

						break;

					case "GetServer":

						String servername = in.readUTF(); // Récupère le paramètre clé envoyé

						String servernameConfString = ConfigFile.getString(main.serverNameConfig, "server_name"); // Récupère le Nom du Serveur qui a été récupéré

						// Si le Nom du Serveur récupéré n'éxiste pas, on met par défaut 'Example' sur la configuration du Plugin 'serverNameConfig.yml'
						if (servernameConfString == null) {

							ConfigFile.set(main.serverNameConfig, "server_name", "Example");
							ConfigFile.saveConfig(main.serverNameConfig);

							// Sinon, on met par défaut le nom du Serveur récupéré
						} else {

							ConfigFile.set(main.serverNameConfig, "server_name", servername);
							ConfigFile.saveConfig(main.serverNameConfig);
						}

						break;

					case "Nickname":

						String[] nickname = in.readUTF().split(", "); // Récupère lse paramètres envoyés

						/* On récupère les différents paramètres */

						String nick_player = nickname[0];
						String nick = nickname[1];

						/* On récupère les différents paramètres */

						// On vérifie si le joueur, si ce n'est pas le cas, on ne retourne rien et on affiche une erreur //
						Player playerNick = Bukkit.getServer().getPlayerExact(nick_player);
						if (playerNick == null) {

							main.console.sendMessage(main.prefix + ChatFormatting.RED + "Une erreur est survenue sur le sous-canal 'NickName' du \"PluginMessageReceived\", Le Joueur récupéré est inexistant !");
							return;
						}
						// On vérifie si le joueur, si ce n'est pas le cas, on ne retourne rien et on affiche une erreur //

						String nickConverted = p.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(nick) : nick; // Convertie le pseudo modifié à envoyer

						// Recharge le Joueur avec son nouveau Nom
						new PlayerEntity(playerNick).update(CustomMethod.StringToComponent(nickConverted), true, true, false);

						break;

					case "PlayerCount":

						// Essaie de récupérer le Serveur récupéré en paramètre, Sinon affiche une erreur disant que le Serveur est inexistant //
						try {
							server = in.readUTF();
						} catch (Exception e) {
							p.sendMessage(main.prefix + ChatFormatting.RED + "Le serveur demandé n'éxiste pas !");
							break;
						}
						// Essaie de récupérer le Serveur récupéré en paramètre, Sinon affiche une erreur disant que le Serveur est inexistant //

						int playercount = in.readInt(); // Récupère le nombre de joueurs connecté sur le Serveur récupéré en paramètre

						// Si le Serveur demandé est pour tous les Serveurs
						if (server.equalsIgnoreCase("ALL")) {

							// On affiche un message disant le nombre de Joueurs connecté en tous
							p.sendMessage(main.prefix + ChatFormatting.GREEN + playercount + ChatFormatting.GRAY + " joueur(s) connecté(s) dans le serveur en globalité !");

							// Sinon, on affiche un message disant le nombre de Joueurs connecté sur le Serveur en question
						} else {
							p.sendMessage(main.prefix + ChatFormatting.GREEN + playercount + ChatFormatting.GRAY + " joueur(s) connecté(s) dans le serveur " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + server + ChatFormatting.GRAY + " !");
						}

						break;

					case "PlayerList":

						// Essaie de récupérer le Serveur récupéré en paramètre, Sinon affiche une erreur disant que le Serveur est inexistant //
						try {
							server = in.readUTF();
						} catch (Exception e) {
							p.sendMessage(main.prefix + ChatFormatting.RED + "Le serveur demandé n'éxiste pas !");
							break;
						}
						// Essaie de récupérer le Serveur récupéré en paramètre, Sinon affiche une erreur disant que le Serveur est inexistant //

						String[] players = in.readUTF().split(", "); // Récupère les Joueurs connectés sur le Serveur récupéré en paramètre

						// Ajoute les joueurs connectés sur le Serveur récupéré en paramètre dans une liste
						List<String> playersList = new LinkedList<>(Arrays.asList(players));

						CustomMethod.RemoveNullList(playersList); //Supprime les Joueurs étant 'NULL'

						// Si le Serveur demandé est pour tous les Serveurs
						if (server.equalsIgnoreCase("ALL")) {


							// Si aucuns joueurs a été récupéré(s), on affiche qu'il n'y a pas de joueur(s) connecté.
							if (playersList.isEmpty()) {
								p.sendMessage(main.prefix + ChatFormatting.RED + "Aucun joueur(s) connecté(s) dans le serveur en globalité !");
							}

							// Sinon, s'il y a qu'un seul joueur récupéré, on affiche qu'il y a un joueur connecté.
							else if (playersList.size() == 1) {

								String playersOnly = playersList.get(0); // Récupère le joueur dans la liste

								// Affiche qu'il y a le Joueur 'playersOnly' qui est connecté
								p.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + playersOnly + ChatFormatting.GRAY + " dans le serveur en globalité !");

								// Sinon, on affiche le nom de chaques Joueurs en question //
							} else {

								p.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
								p.sendMessage(" ");
								p.sendMessage(" ");

								p.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des joueurs dans le serveur en globalité :");

								for (String stringPlayers : players) {

									p.sendMessage(" ");
									p.sendMessage(ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + stringPlayers);
								}

								p.sendMessage(" ");
								p.sendMessage(" ");
								p.sendMessage(ChatFormatting.GRAY + "===========================");
							}
							// Sinon, on affiche le nom de chaques Joueurs en question //

							// Sinon, on affiche le nom des Joueurs en question sur le Serveur demandé //
						} else {

							// Si aucuns joueurs a été récupéré(s), on affiche qu'il n'y a pas de joueur(s) connecté.
							if (playersList.isEmpty()) {

								p.sendMessage(main.prefix + ChatFormatting.RED + "Aucun joueur(s) connecté(s) dans le serveur " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + server + ChatFormatting.RED + " !");

								// Sinon, s'il y a qu'un seul joueur récupéré, on affiche qu'il y a un joueur connecté.
							} else if (playersList.size() == 1) {

								String playersOnly = playersList.get(0); // Récupère le joueur dans la liste

								// Affiche qu'il y a le Joueur 'playersOnly' qui est connecté
								p.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + playersOnly + ChatFormatting.GRAY + " dans le serveur " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + server + ChatFormatting.GRAY + " !");

								// Sinon, on affiche le nom de chaques Joueurs en question //
							} else {

								p.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
								p.sendMessage(" ");
								p.sendMessage(" ");

								p.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des joueurs dans le serveur " + server + " :");

								for (String stringPlayers : players) {

									p.sendMessage(" ");
									p.sendMessage(ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + stringPlayers);
								}

								p.sendMessage(" ");
								p.sendMessage(" ");
								p.sendMessage(ChatFormatting.GRAY + "===========================");
							}
							// Sinon, on affiche le nom de chaques Joueurs en question //

						}

						break;

					case "SkinName":

						String[] skinName = in.readUTF().split(", "); // Récupère lse paramètres envoyés

						/* On récupère les différents paramètres */

						String targetPlayer = skinName[0];
						String skin = skinName[1];

						/* On récupère les différents paramètres */

						// On vérifie si le joueur, si ce n'est pas le cas, on ne retourne rien et on affiche une erreur
						Player playerToChangeSkin = Bukkit.getServer().getPlayerExact(targetPlayer);

						PlayerEntity targetEntity = new PlayerEntity(playerToChangeSkin); // Récupère une Instance PlayerEntity du Joueur

						// ⬇️ Ajoute dans le fichier de configuration le nouveau skin ou pas du Joueur ⬇️ //
						if (skin != null && !skin.isBlank()) {

							if (skin.equalsIgnoreCase(targetEntity.getActualName()))
								ConfigFile.removeKey(main.playerSkinConfig, "SKIN." + targetEntity.getActualUUID().toString());
							else
								ConfigFile.set(main.playerSkinConfig, "SKIN." + targetEntity.getActualUUID().toString(), skin);

						} else
							ConfigFile.removeKey(main.playerSkinConfig, "SKIN." + targetEntity.getActualUUID().toString());
						// ⬆️ Ajoute dans le fichier de configuration le nouveau skin ou pas du Joueur ⬆️ //

						ConfigFile.saveConfig(main.playerSkinConfig); // Sauvegarde le fichier de configuration

						// Recharge le Joueur avec son Skin
						targetEntity.update(null, true, false, false);

						break;

					default:

						if (p == null) {
							main.console.sendMessage(main.prefix + ChatFormatting.RED + "Une erreur est survenue !");
						} else {
							p.sendMessage(main.prefix + ChatFormatting.RED + "Une erreur est survenue !");
						}

						break;
				}

			} catch (IOException e) {
				e.printStackTrace(System.err);
			}

			// ---------------------------------------------------------------------------------- //
			// ~~~ # ⬆️ ON ESSAIE DE RÉCUPÉRÉ LES MESSAGES ENVOYÉS PAR LE SERVEUR PROXY ⬆️ # ~~~ //
			// ---------------------------------------------------------------------------------- //
		}
	}
	/************************************************************/
	/* MÉTHODE POUR RÉCUPÉRER DES INFORMATIONS VENANT DE BUNGEE */
	/************************************************************/
}
