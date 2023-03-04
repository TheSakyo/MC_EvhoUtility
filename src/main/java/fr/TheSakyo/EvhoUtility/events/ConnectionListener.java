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
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ConnectionListener implements Listener {

	/* Récupére la class "Main" */
	private UtilityMain main;
	public ConnectionListener(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupére la class "Main" */


	//Variable pour défini un préfix "[EvhoGames]"
	String minigame = ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "Evho" + ChatColor.GOLD + "Games" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";


	//Variable pour défini un préfix "[Vanish]"
	String vanishTitle = ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.RED.toString() + ChatColor.BOLD.toString() + "VANISH" + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.RESET + " ";

	//Variable pour afficher un message disant que le mode "Vanish" est activé
	String vanishedOn = vanishTitle + ChatColor.GRAY + "Vous en vanish !";



	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	/* PARTIE EVENEMENENT DE LA CONNEXION DU JOUEUR */
	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */


	/**************************************************/
	/* EVENEMENENT QUAND LE JOUEUR REJOINT LE SERVEUR */
	/**************************************************/
	@EventHandler(priority = EventPriority.LOWEST)
	public void OnConnect(PlayerJoinEvent e) {

		Player p = e.getPlayer();
		Component emptyMessage = CustomMethod.StringToComponent("");

			/* -------------------------------------------- */

		//Enregistre les informations 'IP' du joueur en question (Utile pour récupérer son heure local)
		UtilityMain.cacheInfo.putIfAbsent(p.getUniqueId(), InfoIP.get(p));
		new PlayerEntity(p).update(null, true, false, true); // Recharge les Informations du Joueur Connecté

		// Recharge les Informations des Joueurs en ligne
		Bukkit.getServer().getOnlinePlayers().forEach(player -> {

			PlayerEntity playerEntity = new PlayerEntity(player);

			playerEntity.forReceiver(p).update(null, true, true, false);
			playerEntity.forReceiver(null);
		});

					/* ------------------------------------------------------------------- */

		//Vérifie si la section "sever_name" dans le fichier de configuration "servername.yml" n'éxiste pas, on envoie un message d'erreur à la connexion
		if(ConfigFile.getString(main.servernameconfig, "server_name") == null) {

			e.joinMessage(emptyMessage); //Désactive le message de connexion

			p.sendMessage(main.prefix + ChatColor.RED + "Une erreur s'est produite au niveau du message de connexion !");

			if(CustomMethod.hasAdminGrade(p) == true) {

				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatColor.RED + "Elle provient de la détection du nom du serveur dans le fichier de configuration " + ChatColor.DARK_RED + "/utils/servername.yml");
				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatColor.RED + "Essayez de recharger le plugin, pour qu'il récupère bien le nom du serveur !");

			} else {

				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatColor.RED + "Contactez un administrateur pour régler cette erreur !");

			}

			//Vérifie le joueur ne contient pas la permission du mode "Vanish", le code s'arrête donc là
			if(CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished") == false) { main.methodvanish.PlayerVanished(p); return; }


			//Sinon, si la section "sever_name" dans le fichier de configuration "servername.yml" éxiste bien, mais est par défaut ("Example"),
			//On demande à l'administrateur de recharer le plugin
		} else if(ConfigFile.getString(main.servernameconfig, "server_name").equalsIgnoreCase("Example")) {

			e.joinMessage(emptyMessage); //Désactive le message de connexion

			p.sendMessage(main.prefix + ChatColor.RED + "La configuration du message de connexion est par défaut !");

			if(CustomMethod.hasAdminGrade(p) == true) {

				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatColor.RED + "Pour changer la configuration par dessayez de recharger le plugin, pour qu'il récupère bien le nom du serveur !");

			} else {

				p.sendMessage(" ");
				p.sendMessage(main.prefix + ChatColor.RED + "contactez un administrateur pour configurer le message de connexion !");

			}

			//Vérifie si le joueur ne contient pas la permission du mode "Vanish", le code s'arrête donc là
			if(CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished") == false) { main.methodvanish.PlayerVanished(p); return; }
		}

		/* ----------------------------- INITIALISE LES AUTRES ÉVÈNEMENTS DE CONNEXION -------------------------- */;

		// Aprés une seconde, on recharge les 'NPCs' & hologrammes pour le Joueur //
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {

						 /* -------------------------------------------- */

			// Recharge les 'NPC' pour le Joueur
			if(!main.npckeys.getKeys(false).isEmpty() || !main.NPCS.isEmpty()) NPCManager.loadNPC(p, false, true, false);

			//Décharge les hologrammes pour le Joueur
			if(!main.holokeys.getKeys(false).isEmpty() || !main.HOLOGRAMS.isEmpty()) HologramManager.loadHolograms(p, false);

						 /* -------------------------------------------- */

			}
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
		if(CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished") == true) {

			//Vérifie le mode "vanish" du joueur pour l'ajouter
			main.methodvanish.PlayerVanished(p);

			p.sendMessage(" ");
			p.sendMessage(vanishedOn);

			return; //Le code s'arrête là

			//Sinon, si le joueur n'a pas la permission "Vanish", Vérifie le mode "vanish" du joueur pour l'enlever
		} else { main.methodvanish.PlayerVanished(p); }

		// Pour tous les serveurs hubs //
		for(String hub : main.hubs) {

			// Vérifie si le nom du serveur est celui d'un serveur hub //
			if(CustomMethod.isServerHub(hub) == true) {

				e.joinMessage(emptyMessage); //Désactive le message de connexion
				return;
			}
			// Vérifie si le nom du serveur est celui d'un serveur hub //

		}
		// Pour tous les serveurs hubs //

		// Message de connexion pour le mini-jeux
		String joinGame = minigame + ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + p.getName() + ChatColor.GREEN + " a rejoint la partie !";
		e.joinMessage(CustomMethod.StringToComponent(joinGame));

		return; /* Le code s'arrête là */

				/* -------------------------------------------------------------------------------- */
	}

	/**************************************************/
	/* EVENEMENENT QUAND LE JOUEUR REJOINT LE SERVEUR */
	/**************************************************/




	/*************************************************/
	/* EVENEMENENT QUAND LE JOUEUR QUITTE LE SERVEUR */
	/*************************************************/

	@EventHandler(priority = EventPriority.LOWEST)
	public void OnDisconnect(PlayerQuitEvent e) {

		Player p = e.getPlayer();
		User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

		Component emptyMessage = CustomMethod.StringToComponent("");


				/* -------------------------------------------------------------------------------------------------------- */

		ParticleUtil.removeAllTask(p.getUniqueId()); // On annule et on supprime toutes les tâches actuelle associée au joueur dans le gestionnaire de particulière

				/* ----------------------------- INITIALISE LES AUTRES ÉVÈNEMENTS DE DÉCONNEXION -------------------------- */

		// Aprés une seconde, on décharge les 'NPCs' & hologrammes pour le Joueur //
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {

						 /* -------------------------------------------- */

			if(!main.npckeys.getKeys(false).isEmpty() || !main.NPCS.isEmpty()) NPCManager.unloadNPC(p, false);  //Décharge les 'NPC' pour le Joueur
			if(!main.holokeys.getKeys(false).isEmpty() || !main.HOLOGRAMS.isEmpty()) HologramManager.unloadHolograms(p, false); //Décharge les hologrammes pour le Joueur

						 /* -------------------------------------------- */

			}
		}, 20);
		// Aprés une seconde, on décharge les 'NPCs' & hologrammes pour le Joueur //

		TabListener.onQuit(e);
		AFKKickListener.onPlayerLeave(e);
		GodModeListener.onLeave(e);
		DeathOrAchievementListener.onQuit(e);

					/* ----------------------------- INITIALISE LES AUTRES ÉVÈNEMENTS DE DÉCONNEXION -------------------------- */


		// Sinon, si la section "sever_name" dans le fichier de configuration "servername.yml" éxiste bien, mais est par défaut ("Example"), Aucun message de déconnexion s'affiche
		if(ConfigFile.getString(main.servernameconfig, "server_name") == null || ConfigFile.getString(main.servernameconfig, "server_name").equalsIgnoreCase("Example")) {

			e.quitMessage(emptyMessage);
			return; //Le code s'arrête là
		}


		// Vérifie si le joueur la permission du mode "Vanish"
		// Aucun message de déconnexion est donc affiché
		if(CustomMethod.hasLuckPermission(p, "EvhoProxy.vanished") == true) { e.quitMessage(); return; /* Le code s'arrl*/ }


		// Pour tous les serveurs hubs //
		for(String hub : main.hubs) {

			// Vérifie si le nom du serveur est celui d'un serveur hub //
			if(CustomMethod.isServerHub(hub) == true) {

				// Message de déconnecion désactivé
				e.quitMessage(emptyMessage);
				return; //Le code s'arrête là

			}
			// Vérifie si le nom du serveur est celui d'un serveur hub //
		}
		// Pour tous les serveurs hubs //

		// Message de déconnexion pour le mini-jeux
		String quitGame = minigame + ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + p.getName() + ChatColor.RED + " a quitté(e) la partie !";
		e.quitMessage(CustomMethod.StringToComponent(quitGame));

		return; // Stop
	}
	/*************************************************/
	/* EVENEMENENT QUAND LE JOUEUR QUITTE LE SERVEUR */
	/*************************************************/


	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	/* PARTIE EVENEMENENT DE LA CONNEXION DU JOUEUR */
	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

}