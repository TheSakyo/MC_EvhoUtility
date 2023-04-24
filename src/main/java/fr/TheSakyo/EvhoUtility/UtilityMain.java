package fr.TheSakyo.EvhoUtility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import fr.TheSakyo.EvhoUtility.PaperMC.PaperPlugin;
import fr.TheSakyo.EvhoUtility.commands.others.WorldCommand;
import fr.TheSakyo.EvhoUtility.managers.*;
import fr.TheSakyo.EvhoUtility.registering.CustomEntitiesInitialized;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCGlobal;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCUtils;
import fr.TheSakyo.EvhoUtility.utils.entity.entities.HologramEntity;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.InfoIP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.TheSakyo.EvhoUtility.proxy.PluginMessageListener;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.config.ConfigFileManager;
import fr.TheSakyo.EvhoUtility.registering.UtilityCommandsCreated;
import fr.TheSakyo.EvhoUtility.registering.UtilityEventsCreated;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.InventoryUpdate;
import fr.TheSakyo.EvhoUtility.utils.api.ImageMaps.DataLoader;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.GradeFormatUtils;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.VanishUtils;
import fr.TheSakyo.EvhoUtility.utils.sanctions.SanctionUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.model.user.User;
import org.spigotmc.SpigotConfig;

public class UtilityMain extends PaperPlugin {

	/*****************************/
	/* PARTIE VARIABLES GLOBALES */
	/*****************************/

	// Variable pour récupérer la méthode "PluginManager" (sert à gérer le plugin)
	public static PluginManager pm = Bukkit.getServer().getPluginManager();


	// Variable pour les fichiers config personnalisée //
	public ConfigFile servernameconfig;

	public ConfigFile infoIPKeyconfig;
	public ConfigFile advconfig;
	public ConfigFile chatconfig;
	public ConfigFile holoconfig;
	public ConfigFile NPCconfig;
	public ConfigFile tabconfig;
	public ConfigFile DeathOrAchievementconfig;
	public ConfigFile worldconfig;
	public ConfigFile zoneconfig;
	public ConfigFile playerSkinconfig;
	// Variable pour les fichiers config personnalisée //


	// Variable avec les différentes instances globales //
	private static UtilityMain instance;
	public LuckPerms luckapi;
	// Variable avec les différentes instances globales //


	// Variable pour Charger les class du package "registering" //
	UtilityCommandsCreated commands = new UtilityCommandsCreated(this);
	UtilityEventsCreated events = new UtilityEventsCreated(this);
	// Variable pour Charger les class du package "registering" //


	// *** Variable pour récupérer les différentes class utiles  *** //

	//Variable pour récupérer la class "AdvancementsManager"
	public AdvancementsManager advManager = new AdvancementsManager();

	//Variable pour récupérer la class "UserTimeManager"
	public UserTimeManager usertime = new UserTimeManager();

	//Variable pour récupérer la class "ImageMapManager"
	public ImageMapManager mapmanager = new ImageMapManager();


	//Variable pour récupérer la class "TabManager"
	public TabManager tab = new TabManager(this);

	//Variable pour récupérer la class "AFKManager"
	public AFKManager afk = new AFKManager(this);

	//Variable pour récupérer la class "CustomMethod"
	public VanishUtils methodvanish = new VanishUtils(this);

	//Variable pour récupérer la class "GradeFormatManager"
	public GradeFormatUtils formatgrade = new GradeFormatUtils(this);

	//Variable pour récupérer la class "InventoryUpdater"
	public InventoryUpdate invUpdate = new InventoryUpdate(this);

	// *** Variable pour récupérer les différentes class utiles *** //


	// Variable pour Détecter la Console //
	public ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	// Variable pour Détecter la Console //


	// Variable pour le Nom du Plugin //
	public String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "Evho" + ChatColor.DARK_AQUA + "Utility" + ChatColor.WHITE + "]" + " ";
	// Variable pour le Nom du Plugin //


	// Variable d'erreur au niveau des commandes éxécutables de la Console //

	public String argsNull = prefix + ChatColor.DARK_RED + ChatColor.UNDERLINE.toString() + "Erreur de Code :" + ChatColor.RED + "' args' est NULL";
	public String errorArgs = ChatColor.WHITE + "Quelque chose a mal tourner, vous devez obligatoirement, préciser un argument 'args' !";


			public String senderNull = prefix + ChatColor.DARK_RED + ChatColor.UNDERLINE.toString() + "Erreur de Code :" + ChatColor.RED + "' sender' est NULL";
			public String errorSender = ChatColor.WHITE + "Quelque chose a mal tourner, vous devez obligatoirement, préciser la console 'sender' en cas d'éxécution avec la Console"
					+ " ou le joueur 'sender' en cas d'éxécution en tant que Joueur !";


			public String targetNull = prefix + ChatColor.DARK_RED + ChatColor.UNDERLINE.toString() + "Erreur de Code :" + ChatColor.RED + "' target' est NULL";
			public String errorTarget = ChatColor.WHITE + "Quelque chose a mal tourner, vous devez obligatoirement, préciser le joueur 'target' en cas d'éxécution avec la Console !";

			// Variable d'erreur au niveau des commandes éxécutables de la Console //


	// Variable pour le nom du "tablist" du joueur avec comme suffix "[AFK]" //
	public String getAfkList = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + " [AFK]";
	// Variable pour le nom du "tablist" du joueur avec comme suffix "[AFK]" //


	// Variable pour l'AutoAFK //
	public Map<UUID, Integer> time = new HashMap<UUID, Integer>();
	public Map<UUID, BukkitTask> AFKRun = new HashMap<UUID, BukkitTask>();
	// Variable pour l'AutoAFK //

	// Variable pour les joueurs en "GODMODE" //
	public List<UUID> GODS = new ArrayList<UUID>();
	// Variable pour les joueurs en "GODMODE" //


	// Variable pour les joueurs en mode "VANISH" //
	public List<UUID> VANISHED = new ArrayList<UUID>();
	// Variable pour les joueurs en mode "VANISH" //


	// Variable pour changer le temps/la météo du monde à un joueur précis //
	public static List<UUID> playertimes = new ArrayList<UUID>();
	public static List<UUID> playerweathers = new ArrayList<UUID>();
	// Variable pour changer le temps/la météo du monde à un joueur précis //


	// Variable liste pour les différents 'NPC' //
	public Map<String, NPCGlobal> NPCS = new HashMap<String, NPCGlobal>();
	// Variable liste pour les différents 'NPC' //


	// Variable pour les hologrammes //
	public Map<String, List<HologramEntity>> HOLOGRAMS = new HashMap<String, List<HologramEntity>>();
	public String ErrorLineHolograms = null;
	public String SuccessHolograms = null;
	// Variable pour les hologrammes //

	// Variable pour la liste des joueurs "Freeze" //
	public List<UUID> freezeP = new ArrayList<UUID>();
	// Variable pour la liste des joueurs "Freeze" //


	// Variable pour récupérer les joueurs en ligne (Bungee) [Utile pour "EvhoMenu"] //
	public static int BungeePlayerOnlines = 0;
	public static Map<String, Integer> ServerPlayerOnlines = new HashMap<String, Integer>();
	// Variable pour récupérer les joueurs en ligne (Bungee) [Utile pour "EvhoMenu"] //



	//Nom de canal de base 'BungeeCord' pour envoyer des informations entre serveurs
	public static String channel = "BungeeCord";


	//Nom de canal customisé 'evhonia:evhoproxy' pour envoyer des informations entre serveurs
	public static String channelcustom = "evhonia:evhoproxy";


	//Vérifie si un achievement customisé utilise un "UUID"
	public static boolean useUUID;

	//Variable pour empécher la répétition du message de la commande "proxycmd" (Fix-Bug)
	public Map<UUID, Boolean> useproxycmd = new HashMap<UUID, Boolean>();


	//Variable pour Récupérer les serveurs "hubs"
	public List<String> hubs = List.of("hub");


	// Variables utiles pour certaines données à récupérer dans les fichiers de configurations //
	public ConfigurationSection npckeys;
	public ConfigurationSection holokeys;
	// Variables utiles pour certaines données à récupérer dans les fichiers de configurations //


	// Variable pour récupérer les Joueurs 'PlayerEntity' //
	public HashMap<UUID, PlayerEntity> entityPlayers = new HashMap<UUID, PlayerEntity>();
	// Variable pour récupérer les Joueurs 'PlayerEntity' //

	// Variable pour savoir si les Joueurs seront cachés ou non //
	public HashMap<UUID, Boolean> hidePlayers = new HashMap<UUID, Boolean>();
	// Variable pour savoir si les Joueurs seront cachés ou non //


	//Cache pour les informations du joueur (Utile pour récup l'heure locale du joueur en question)
	public static Map<UUID, InfoIP> cacheInfo = new ConcurrentHashMap<>();

	/*****************************/
	/* PARTIE VARIABLES GLOBALES */
	/*****************************/




	/******************************************************/
	/* CREATIONS D'INSTANCES POUR LIRE DES 'CLASS' UTILES */
	/******************************************************/

	// Instance et Imporation Plugin "LuckPerms" (utile pour lire ses class ensuite) //

	private void LuckPermsInstance() {

		// Enregistre l'api dans le plugin actuel pour pouvoir utiliser ses fonctions //
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if(provider != null) { luckapi = provider.getProvider(); }
		// Enregistre l'api dans le plugin actuel pour pouvoir utiliser ses fonctions //
	}

	//Vérifie si le plugin "LuckPerms" éxiste dans le serveur
	public Plugin getLuckPermsPlugin() { return pm.getPlugin("LuckPerms"); }

	// Instance et Imporation Plugin "LuckPerms" (utile pour lire ses class ensuite) //



	//Instance de la class "Main" //
	public static UtilityMain getInstance() { return instance; }
	//Instance de la class "Main" //

	/******************************************************/
	/* CREATIONS D'INSTANCES POUR LIRE DES 'CLASS' UTILES */
	/******************************************************/




	/*************************************************************/
	/* PARTIE AVEC ACTIVATION/DÉSACTIVATION/CHARGEMENT DU PLUGIN */
	/************************************************************/

	/* Constructeur de la Class Main */
	public UtilityMain() {

		super(ServerVersion.VERSION_1_17, ServerVersion.VERSION_1_17_1, ServerVersion.VERSION_1_18, ServerVersion.VERSION_1_18_1, ServerVersion.VERSION_1_18_2, ServerVersion.VERSION_1_19, ServerVersion.VERSION_1_19_1, ServerVersion.VERSION_1_19_2, ServerVersion.VERSION_1_19_3);
    	plugin = (Plugin)this; // Récupère le plugin
		instance = this;
	}
	/* Constructeur de la Class Main */

																		/* ----------------------------- */
																		/* ----------------------------- */

	/*** Initialisation (Pour bien démarrer le Plugin) ***/
	private void init() {

		//Récupère un fichier de configuration "servername.yml" (pour enregistrer le nom du serveur situé dans la config BungeeCord)
		servernameconfig = ConfigFileManager.getNewConfig(this.getDataFolder(), "/utils/servername.yml", ConfigFileManager.headerServerName);

		ConfigFileManager.LoadUtilityConfig(); //Recharge les fichiers de configurations du Plugin utiles pour le serveur

		//Récupère dans le fichier de configuration "holograms.yml" les données des différents hologrammes enregistrés //
		this.holokeys = ConfigFile.getConfigurationSection(this.holoconfig, "Holograms");

		// Récupère dans le fichier de configuration "npc.yml" les données des différents 'NPC' enregistrés
		this.npckeys = ConfigFile.getConfigurationSection(this.NPCconfig, "NPC");

		//Vérifie si le serveur fonctionne sous bungee
		if(hasBungee() == true) {

			// Enregistre les canaux bungee //
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, channel);
			this.getServer().getMessenger().registerIncomingPluginChannel(this, channel, new PluginMessageListener(this));
			// Enregistre les canaux bungee //

			// Enregistre les canaux customisés (système de commande NPCGlobal) //
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, channelcustom);
			this.getServer().getMessenger().registerIncomingPluginChannel(this, channelcustom, new PluginMessageListener(this));
			// Enregistre les canaux customisés (système de commande NPCGlobal) //


			//Récupère le nom du serveur définit dans le serveur Bungee (fonctionnel s'il y a au moins un joueur connecté)
			if(Bukkit.getServer().getOnlinePlayers().size() >= 1) sendMessagePlugin(channel, "GetServer", null, new String[]{""});
		}

		/*NPCEntity.initEventHandler(this); // Définit l'Évènement de la class 'NPCEntity' pour le plugin*/


		// Pour tous les joueurs en lignes, on recharge leur format de leur grade qu'ils possèdent (Utilise "LuckPerms") //
		// + activation de l'AutoAFK du joueur et ajoute le joueur au gestionnaire d'achivements, s'il est pas //
		// + affichage du Tablist personnalisé //
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {

			User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId()); //Récupère le joueur en question avec LuckPerms
			CachedDataManager data = user.getCachedData(); //Récupère les données LuckPerms du joueur en question


			cacheInfo.putIfAbsent(p.getUniqueId(), InfoIP.get(p)); //Enregistre les informations 'IP' du joueur en question (Utile pour récupérer son heure local)
			new PlayerEntity(p).update(null, true, true, true); // Recharge les Informations du Joueur


			//Essait d'ajouter le joueur à la gestion d'achievemets "AdvancementsManager", s'il y est pas
			if(!advManager.hasPlayer(p)) { advManager.addPlayer(p); }

			afk.runAFK(p.getUniqueId(), null, 0); //Activation AutoAFK du joueur

			// ⬇️ Récupère les joueurs "Freeze", et le remet en freeze, sinon l'enlève du freeze ⬇️  //
			if(data.getPermissionData().getPermissionMap().containsKey("EvhoProxy.freezed")) { if(!freezeP.contains(p.getUniqueId())) freezeP.add(p.getUniqueId()); }
			else { if(freezeP.contains(p.getUniqueId())) freezeP.remove(p.getUniqueId()); }
			// ⬆️ Récupère les joueurs "Freeze", et le remet en freeze, sinon l'enlève du freeze ⬆️ //

			tab.setupTablist(p); //Configure un TabList personnalisé
		}
		// Pour tous les joueurs en lignes, on recharge leur format de leur grade qu'ils possèdent (Utilise "LuckPerms") //
		// + activation de l'AutoAFK du joueur et ajoute le joueur au gestionnaire d'achivements, s'il est pas //
		// + affichage du Tablist personnalisé //

		WorldCommand.UtilityConfigWorld(); //Recharge le fichier de configuration des mondes du Plugin

		// ⬇️ Essait de recharger les "imagemaps" enregistrée sinon une erreur est affichée ⬇️ //
		try { DataLoader.loadMaps(); }
		catch(IOException e) { e.printStackTrace(); }
		// ⬆️ Essait de recharger les "imagemaps" enregistrée sinon une erreur est affichée ⬆️ //


		// ⬇️ Recharge tous les groupes à la Zone/Région du Serveur en question (EvhoZone) ⬇️ //
		Set<String> keysZoneSectionCfg = ConfigFile.getConfigurationSection(zoneconfig, "ZONE").getKeys(false);

		if(keysZoneSectionCfg != null && !keysZoneSectionCfg.isEmpty()) {

			for(String key : keysZoneSectionCfg) { ZoneManager.resetGroupsForZone(key, false); }
		}
		// ⬆️ Recharge tous les groupes à la Zone/Région du Serveur en question (EvhoZone) ⬆️ //

	}
	/*** Initialisation (Pour bien démarrer le Plugin) ***/


	// ** Activation du Plugin ** //
	public void enable() {

		plugin = (Plugin)this; // Récupère le plugin
		instance = this; // Récupère l'instance du plugin en question

		//Vérifie si trouve bien le plugin "LuckPerms" dans le serveur
		if(getLuckPermsPlugin().isEnabled()) {

			//Récupère l'instance du plugin "LuckPerms" avec la méthode "LuckPermsInstance()" (API)
			if(this.luckapi == null) { LuckPermsInstance(); }

			new NPCUtils(this).registerPlugin(this); // Enregistre la librairie des NPCs

			this.init(); //Fait Appel à une méthode pour bien intialiser le Plugin

			// Recharges les différentes commandes et évenements + Quelques Entités Customisés par le Plugin //
			commands.getCommands();
			events.getEvents();
			new CustomEntitiesInitialized(this).init(false);
			// Recharges les différentes commandes et évenements + Quelques Entités Customisés par le Plugin //

			/*EvhoLagCommand.AutoRemove(); // Fait appel vers une boucle pour un système de "ClearLag*/

			// Création Menu Staff //
			SanctionUtils.SetItemForOne();
			SanctionUtils.InventoryStaff();
			// Création Menu Staff //

			OldCM.onEnable(); // Active l'API PvP 1.8

			//Message disant que le plugin est activé
			console.sendMessage(prefix + ChatColor.GREEN + "Utilities Enabled");

			//Sinon, s'il ne trouve pas le plugin "LuckPerms" dans le serveur
		} else {

			//Demande le plugin "LuckPerms" pour le fonctionnement du plugin
			console.sendMessage("");
			console.sendMessage(prefix + ChatColor.RED + "Veuillez nous excuser, ce plugin requiert un plugin de permission spécifique !");
			console.sendMessage(prefix + ChatColor.RED + "Le plugin est le suivant : " + ChatColor.YELLOW + "LuckPerms");
			console.sendMessage("");

			//Vérifie si le serveur fonctionne sous bungee
			//si c'est le cas, on informe qu'il faut bien paramétrer le plugin "LuckPerms" (Précision : Synchronisation entre serveurs)
			if(hasBungee() == true) {

				String infoTitle = ChatColor.WHITE + "[" + ChatColor.GOLD + "INFO" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";

				String check = ChatColor.RED + "Vérifiez bien de paramètrer la synchronisation du plugin " + ChatColor.YELLOW + "LuckPerms" + ChatColor.RED + " pour chaque serveur en plus du proxy !";

				console.sendMessage(prefix + ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + " - " + infoTitle + check);
				console.sendMessage("");

			}

			//Désactive le plugin
			pm.disablePlugin(plugin);
		}
	}
	// ** Activation du Plugin ** //



	// ** Désactivation du Plugin ** //
	public void disable() {

		instance = null;
		plugin = null;

		//Fait Appel à une méthode pour supprimer les Entitées enregistré dans le serveur
		new CustomEntitiesInitialized(this).init(true);

		new NPCUtils(this).unregisterPlugin(this); // Enregistre la librairie des NPCs

		//Message disant que le plugin est désactivé
		console.sendMessage(prefix + ChatColor.DARK_RED + "Utilities Disabled");

		OldCM.onDisable(); // Désactive l'API PvP 1.8
	}
	// ** Désactivation du Plugin ** //



	/*******************************************************/
	/****** ⬇️ PARTIE AVEC RECHARGEMENT DU PLUGIN ⬇️ ******/
	/******************************************************/

	public void reloadPlugin() {	

		//Fait Appel à une méthode pour supprimer les Entitées enregistré dans le serveur
		new CustomEntitiesInitialized(this).init(true);
		this.init(); //Fait Appel à une méthode pour bien intialiser le Plugin
	}

	/*******************************************************/
	/****** ⬆️ PARTIE AVEC RECHARGEMENT DU PLUGIN ⬆️ ******/
	/******************************************************/


	/*************************************************************/
	/* PARTIE AVEC ACTIVATION/DÉSACTIVATION/CHARGEMENT DU PLUGIN */
	/************************************************************/


	/************************************************************/
	/* METHODE GLOBALE POUR RECUPERER LE PLUGIN DE EVHOUTILITY */
	/**********************************************************/

	/**
	 * Récupère Le plugin actuel de {@link UtilityMain}
	 *
	 *
	 * @return Le plugin actuel de {@link UtilityMain}
	 */
	public Plugin getPlugin() { return plugin; }

	/************************************************************/
	/* METHODE GLOBALE POUR RECUPERER LE PLUGIN DE EVHOUTILITY */
	/**********************************************************/


	/********************************************************/
	/* METHODE GLOBALE POUR RECUPERER LES MESSAGES DU PROXY */
	/********************************************************/

	// Méthode par défaut pour envoyer des messages au serveur Bungee //
	public void sendMessagePlugin(String canal, String sub, Player p, String args[]) {

		//Vérifie si le serveur fonctionne sous bungee
		if(hasBungee() == false) { return; }

		//Oblige le paramètre "p" de contenir un joueur (Info : Le système de message Bungee requiert obligatoirement un joueur)
		if(p == null) p = Iterables.getFirst(Bukkit.getServer().getOnlinePlayers(), null);

		//Utilisations de l'interface google "ByteArrayDataOutput" pour récupérer un message sous forme binaire
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		//Récupère le paramètre "sub" en utilisant "writeUTF" pour conserver les accents etc... ("sub" doit contenir un sous channel Bungee)
		out.writeUTF(sub);

		//Récupère les différents arguments du message récupérer
		for(String string : args) out.writeUTF(string);

		//Fait appel à une méthode pour envoyer les messages venant de "BungeeCord"
		p.getServer().sendPluginMessage(this, canal, out.toByteArray());
	}
	// Méthode par défaut pour envoyer des messages au serveur Bungee //



	// Méthode utile pour envoyer des messages au serveur Bungee avec un canal customisé (evhonia:EvhoProxy) //
	public void sendToBungee(String sub, String string) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			out.writeUTF(sub);

			if(string != null) out.writeUTF(string);

		} catch(IOException e) { e.printStackTrace(); }

		Bukkit.getServer().sendPluginMessage(this, UtilityMain.channelcustom, stream.toByteArray());
	}
	// Méthode utile pour envoyer des messages au serveur Bungee avec un canal customisé (evhonia:EvhoProxy) //

	/********************************************************/
	/* METHODE GLOBALE POUR RECUPERER LES MESSAGES DU PROXY */
	/********************************************************/




	/**************************************************************************/
	/* METHODE GLOBALE "BOOLEAN" POUR VEFIFIER SI LE SERVEUR EST SOUS BUNGEE  */
	/**************************************************************************/

	public boolean hasBungee() {

		//Boolean "bungee" ; Récupère la section "BungeeCord" dans la configuration du serveur "spigot.yml"
		boolean bungee = SpigotConfig.bungee;

		//Boolean "onlineMode" ; Récupère la section "onlineMode" dans la configuiration du serveur "server.properties"
		boolean onlineMode = Bukkit.getServer().getOnlineMode();

		//Si "bungee" est vrai et "onlineMode" est faux, ça retourne en vrai (Le Serveur est bien sous BungeeCord)
		//Info : "onlineMode" doit etre faux pour récupérer le serveur Bungee.
		//(La configuration du mode "Online" se fera depuis le serveur BungeeCord)
		if(bungee && (!onlineMode)) { return true; }

		//Sinon, ça retourne en faux (Le Serveur n'est pas sous BungeeCord)
		else { return false; }

	}
	/**************************************************************************/
	/* METHODE GLOBALE "BOOLEAN" POUR VEFIFIER SI LE SERVEUR EST SOUS BUNGEE  */
	/**************************************************************************/

}
