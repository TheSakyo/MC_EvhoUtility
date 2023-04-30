package fr.TheSakyo.EvhoUtility.events;


import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.managers.HologramManager;
import fr.TheSakyo.EvhoUtility.managers.NPCManager;
import fr.TheSakyo.EvhoUtility.utils.ParticleUtil;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.InfoIP;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.user.User;
import net.minecraft.ChatFormatting;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ConnectionListener implements Listener {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public ConnectionListener(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */


	//Variable pour défini un préfix "[EvhoGames]"
	String miniGame = ChatFormatting.WHITE + "[" + ChatFormatting.LIGHT_PURPLE + "Evho" + ChatFormatting.GOLD + "Games" + ChatFormatting.WHITE + "]" + ChatFormatting.RESET + " ";


	//Variable pour défini un préfix "[Vanish]"
	String vanishTitle = ChatFormatting.WHITE.toString() + ChatFormatting.BOLD.toString() + "[" + ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "VANISH" + ChatFormatting.WHITE.toString() + ChatFormatting.BOLD.toString() + "]" + ChatFormatting.RESET + " ";

	//Variable pour afficher un message disant que le mode "Vanish" est activé
	String vanishedOn = vanishTitle + ChatFormatting.GRAY + "Vous en vanish !";



	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	/* PARTIE ÉVÈNEMENT DE LA CONNEXION DU JOUEUR */
	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */


	/************************************************/
	/* ÉVÈNEMENT QUAND LE JOUEUR REJOINT LE SERVEUR */
	/************************************************/
	@EventHandler(priority = EventPriority.LOWEST)
	public void OnConnect(PlayerJoinEvent e) {

		Player p = e.getPlayer();
		Component emptyMessage = CustomMethod.StringToComponent("");

			/* -------------------------------------------- */

		//Enregistre les informations 'IP' du joueur en question (Utile pour récupérer son heure local)
		UtilityMain.cacheInfo.putIfAbsent(p.getUniqueId(), InfoIP.get(p));

		// Recharge toutes les Informations du Joueur Connecté
		new PlayerEntity(p).update(null, true, false, true);

		// Recharge toutes les Informations des Joueurs en ligne
		Bukkit.getServer().getOnlinePlayers().forEach(player -> {

			PlayerEntity playerEntity = new PlayerEntity(player);

			playerEntity.forReceiver(p).update(null, true, true, false);
			playerEntity.forReceiver(null);
		});

					/* ------------------------------------------------------------------- */

		//Vérifie si la section "sever_name" dans le fichier de configuration "servername.yml" n'éxiste pas, on envoie un message d'erreur à la connexion
		if(ConfigFile.getString(main.serverNameConfig, "server_name") == null) {

			e.joinMessage(emptyMessage); //Désactive le message de connexion

			p.sendMessage(main.prefix + ChatFormatting.RED + "Une erreur s'est produite au niveau du message de connexion !");

			if(CustomMethod.hasAdminGrade(p)) {

				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatFormatting.RED + "Elle provient de la détection du nom du serveur dans le fichier de configuration " + ChatFormatting.DARK_RED + "/utils/servername.yml");
				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez de recharger le plugin, pour qu'il récupère bien le nom du serveur !");

			} else {

				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatFormatting.RED + "Contactez un administrateur pour régler cette erreur !");

			}

			//Vérifie le joueur ne contient pas la permission du mode "Vanish", le code s'arrête donc là
			if(!CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished")) {

				main.methodVanish.playerVanished(p);
				return;
			}


			//Sinon, si la section "sever_name" dans le fichier de configuration "servername.yml" éxiste bien, mais est par défaut ("Example"),
			//On demande à l'administrateur de recharger le plugin
		} else if(ConfigFile.getString(main.serverNameConfig, "server_name").equalsIgnoreCase("Example")) {

			e.joinMessage(emptyMessage); //Désactive le message de connexion

			p.sendMessage(main.prefix + ChatFormatting.RED + "La configuration du message de connexion est par défaut !");

			if(CustomMethod.hasAdminGrade(p)) {

				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatFormatting.RED + "Pour changer la configuration du message, essayez de recharger le plugin, pour qu'il récupère bien le nom du serveur !");

			} else {

				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatFormatting.RED + "contactez un administrateur pour configurer le message de connexion !");

			}

			//Vérifie si le joueur ne contient pas la permission du mode "Vanish", le code s'arrête donc là
			if(!CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished")) { main.methodVanish.playerVanished(p); return; }
		}

		/* ----------------------------- INITIALISE LES AUTRES ÉVÈNEMENTS DE CONNEXION -------------------------- */

		// Aprés une seconde, on recharge les 'NPCs' & hologrammes pour le Joueur //
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {

                     /* -------------------------------------------- */

        // Recharge tous les 'NPC' pour le Joueur
        if(!main.npcKeys.getKeys(false).isEmpty() || !main.NPCS.isEmpty()) NPCManager.loadNPC(p, false, true, false);

        //Décharge tous les hologrammes pour le Joueur
        if(!main.holoKeys.getKeys(false).isEmpty() || !main.HOLOGRAMS.isEmpty()) HologramManager.loadHolograms(p, false);

                     /* -------------------------------------------- */

        }, 20);
		// Aprés une seconde, on recharge les 'NPCs' & hologrammes pour le Joueur //

		TabListener.onJoin(e);
		AFKKickListener.onPlayerJoin(e);
		GodModeListener.onJoin(e);
		DeathOrAchievementListener.onJoin(e);

		/* ----------------------------- INITIALISE LES AUTRES ÉVÈNEMENTS DE CONNEXION -------------------------- */

		//Vérifie si le joueur à la permission du mode "Vanish"
		//Aucun message de connexion est donc affiché
		//Puis affirme aux joueurs qu'il est en vanish
		if(CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished")) {

			//Vérifie le mode "vanish" du joueur pour l'ajouter
			main.methodVanish.playerVanished(p);

			p.sendMessage(" ");
			p.sendMessage(vanishedOn);

			return; //Le code s'arrête là

			//Sinon, si le joueur n'a pas la permission "Vanish", Vérifie le mode "vanish" du joueur pour l'enlever
		} else { main.methodVanish.playerVanished(p); }

		// Pour tous les serveurs hubs //
		for(String hub : main.hubs) {

			// Vérifie si le nom du serveur est celui d'un serveur hub //
			if(CustomMethod.isServerHub(hub)) {

				e.joinMessage(emptyMessage); //Désactive le message de connexion
				return;
			}
			// Vérifie si le nom du serveur est celui d'un serveur hub //

		}
		// Pour tous les serveurs hubs //

		// Message de connexion pour les mini-jeux
		String joinGame = miniGame + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + p.getName() + ChatFormatting.GREEN + " a rejoint la partie !";
		e.joinMessage(CustomMethod.StringToComponent(joinGame));

				/* -------------------------------------------------------------------------------- */
	}

	/************************************************/
	/* ÉVÈNEMENT QUAND LE JOUEUR REJOINT LE SERVEUR */
	/************************************************/




	/***********************************************/
	/* ÉVÈNEMENT QUAND LE JOUEUR QUITTE LE SERVEUR */
	/***********************************************/

	@EventHandler(priority = EventPriority.LOWEST)
	public void OnDisconnect(PlayerQuitEvent e) {

		Player p = e.getPlayer();
		User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

		Component emptyMessage = CustomMethod.StringToComponent("");


				/* -------------------------------------------------------------------------------------------------------- */

		ParticleUtil.removeAllTask(p.getUniqueId()); // On annule et on supprime toutes les tâches actuelle associée au joueur dans le gestionnaire de particulière

				/* ----------------------------- INITIALISE LES AUTRES ÉVÈNEMENTS DE DÉCONNEXION -------------------------- */

		// Aprés une seconde, on décharge les 'NPCs' & hologrammes pour le Joueur //
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {

                     /* -------------------------------------------- */

        if(!main.npcKeys.getKeys(false).isEmpty() || !main.NPCS.isEmpty()) NPCManager.unloadNPC(p, false);  //Décharge tous les 'NPC' pour le Joueur
        if(!main.holoKeys.getKeys(false).isEmpty() || !main.HOLOGRAMS.isEmpty()) HologramManager.unloadHolograms(p, false); //Décharge tous les hologrammes pour le Joueur

                     /* -------------------------------------------- */

        }, 20);
		// Aprés une seconde, on décharge les 'NPCs' & hologrammes pour le Joueur //

		TabListener.onQuit(e);
		AFKKickListener.onPlayerLeave(e);
		GodModeListener.onLeave(e);
		DeathOrAchievementListener.onQuit(e);

					/* ----------------------------- INITIALISE LES AUTRES ÉVÈNEMENTS DE DÉCONNEXION -------------------------- */


		// Sinon, si la section "sever_name" dans le fichier de configuration "servername.yml" éxiste bien, mais est par défaut ("Example"), Aucun message de déconnexion s'affiche
		if(ConfigFile.getString(main.serverNameConfig, "server_name") == null || ConfigFile.getString(main.serverNameConfig, "server_name").equalsIgnoreCase("Example")) {

			e.quitMessage(emptyMessage);
			return; //Le code s'arrête là
		}


		// Vérifie si le joueur la permission du mode "Vanish"
		// Aucun message de déconnexion est donc affiché
		if(CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished")) {

			e.quitMessage();
			return; /* Le code s'arrête là */
		}


		// Pour tous les serveurs hubs //
		for(String hub : main.hubs) {

			// Vérifie si le nom du serveur est celui d'un serveur hub //
			if(CustomMethod.isServerHub(hub)) {

				// Message de déconnexion désactivé
				e.quitMessage(emptyMessage);
				return; //Le code s'arrête là

			}
			// Vérifie si le nom du serveur est celui d'un serveur hub //
		}
		// Pour tous les serveurs hubs //

		// Message de déconnexion pour les mini-jeux
		String quitGame = miniGame + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + p.getName() + ChatFormatting.RED + " a quitté(e) la partie !";
		e.quitMessage(CustomMethod.StringToComponent(quitGame));
	}
	/***********************************************/
	/* ÉVÈNEMENT QUAND LE JOUEUR QUITTE LE SERVEUR */
	/***********************************************/


	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	/* PARTIE ÉVÈNEMENT DE LA CONNEXION DU JOUEUR */
	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
}