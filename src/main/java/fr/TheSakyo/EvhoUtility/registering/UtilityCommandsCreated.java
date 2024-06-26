package fr.TheSakyo.EvhoUtility.registering;

import fr.TheSakyo.EvhoUtility.commands.entities.*;
import fr.TheSakyo.EvhoUtility.commands.inventory.*;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.proxy.commands.*;
import fr.TheSakyo.EvhoUtility.commands.basic.*;
import fr.TheSakyo.EvhoUtility.commands.others.*;
import fr.TheSakyo.EvhoUtility.commands.speed.*;
import fr.TheSakyo.EvhoUtility.commands.times.*;
import fr.TheSakyo.EvhoUtility.commands.times.timesglobal.daytimes.*;
import fr.TheSakyo.EvhoUtility.commands.times.timesglobal.nighttimes.*;
import fr.TheSakyo.EvhoUtility.commands.weathers.*;
import fr.TheSakyo.EvhoUtility.commands.weathers.weathersglobal.*;


public class UtilityCommandsCreated {
	
	 /* Récupère la class "Main" */
	 private final UtilityMain main;
	 public UtilityCommandsCreated(UtilityMain pluginMain) { this.main = pluginMain; }
	 /* Récupère la class "Main" */
	 
	 
	 /**********************************/
	 /* CHARGEMENT DES CLASS COMMANDES */ 
	 /**********************************/
	 
	 public void getCommands() {
		 
		//Commandes Reload du Plugin 
		main.getCommand("utilityreload").setExecutor(new ReloadCommand(main)); //Rechargement plugin
		main.getCommand("utilityrl").setExecutor(new ReloadCommand(main)); //Rechargement plugin


		//Commande de contournement de certaine(s) action(s) bloquée(s) (pour admin)
		main.getCommand("bypass").setExecutor(new BypassCommand(main));


		//Commandes Essentials
		main.getCommand("world").setExecutor(new WorldCommand(main)); //Création de monde
		main.getCommand("speed").setExecutor(new SpeedCommand(main)); //Vitesse de marche
		main.getCommand("fly").setExecutor(new FlyCommand(main)); //Mode vol
		main.getCommand("flyspeed").setExecutor(new FlySpeedCommand(main)); //Vitesse de vol
		main.getCommand("heal").setExecutor(new HealCommand(main)); //Soignement
		main.getCommand("food").setExecutor(new FoodCommand(main)); //Rassasiement
		main.getCommand("AFK").setExecutor(new AFKKickCommand(main)); //AFK
		main.getCommand("skull").setExecutor(new SkullCommand(main)); //Récupération tête de joueur
		
		main.getCommand("lightning").setExecutor(new LightningCommand(main)); //Eclair
		main.getCommand("light").setExecutor(new LightningCommand(main)); //Eclair
		 
		//Commandes Essentials [Management de l'inventaire]
		main.getCommand("clearinventory").setExecutor(new ClearInventoryCommand(main)); //Suppression inventaire
		main.getCommand("clearinv").setExecutor(new ClearInventoryCommand(main)); //Suppression inventaire
		
		main.getCommand("cleararmor").setExecutor(new ClearArmorCommand(main)); //Suppression armure
		
		main.getCommand("invsee").setExecutor(new InvseeCommand(main)); //Affichage d'un inventaire
		
		main.getCommand("enderchest").setExecutor(new EnderChestCommand(main)); //Affichage coffre du néant
		main.getCommand("ender").setExecutor(new EnderChestCommand(main)); //Affichage coffre du néant
		
		main.getCommand("more").setExecutor(new MoreItemCommand(main)); //Ajout d'item dans la main principale
		main.getCommand("repair").setExecutor(new RepairItemCommand(main)); //Réparation d'item dans la main principale
		
		//Commandes Essentials [Mode Dieux (GodMode)]
		main.getCommand("godmode").setExecutor(new GodModeCommand(main)); //Mode dieux (invincible)
		main.getCommand("god").setExecutor(new GodModeCommand(main)); //Mode dieux (invincible)

		
		//Commandes Essentials [Mode de jeux]
		main.getCommand("gamemode").setExecutor(new GamemodeCommand(main)); //Mode de jeux du joueur
		main.getCommand("gm").setExecutor(new GamemodeCommand(main)); //Mode de jeux du joueur
		
		
		//Commandes Essentials [Temps Joueurs]
		main.getCommand("ptime").setExecutor(new PtimeCommand(main)); //Temp du joueur uniquement
		
		
		//Commandes Essentials [Temps Joueurs]
		main.getCommand("pweather").setExecutor(new PweatherCommand(main)); //Temp du joueur uniquement
		
		
		//Commandes Essentials [Temps Journées]
		main.getCommand("day").setExecutor(new DayCommand(main)); //Temp du monde (matin)
		main.getCommand("noon").setExecutor(new NoonCommand(main)); //Temp du monde (midi)
		main.getCommand("sunset").setExecutor(new SunsetCommand(main)); //Temp du monde (coucher du soleil)
		
		
		//Commandes Essentials [Temps Soirées]
		main.getCommand("night").setExecutor(new NightCommand(main)); //Temp du monde (nuit)
		main.getCommand("midnight").setExecutor(new MidnightCommand(main)); //Temp du monde (minuit)
		main.getCommand("sunrise").setExecutor(new SunriseCommand(main)); //Temp du monde (lever du soleil)
		
		
		//Commandes Essentials [Temps Météo]
		main.getCommand("sun").setExecutor(new SunCommand(main)); //Météo du monde (ensoleillé)
		main.getCommand("rain").setExecutor(new RainCommand(main)); //Météo du monde (pluvieux)
		main.getCommand("thunder").setExecutor(new ThunderCommand(main)); //Météo du monde (pluvieux + orageux)
		
		
		//Commandes ClearLag [Suppression d'Entités] (EvhoLag)
	    main.getCommand("evholag").setExecutor(new EvhoLagCommand()); //"ClearLag" Suppression Entitée(s)
	    main.getCommand("elag").setExecutor(new EvhoLagCommand()); //"ClearLag" Suppression Entitée(s)
	    main.getCommand("lag").setExecutor(new EvhoLagCommand()); //"ClearLag" Suppression Entitée(s)
	    
	   
		//Commandes Hologram
	    main.getCommand("hologram").setExecutor(new HologramCommand(main)); //Gestion hologramme(s)
	    main.getCommand("hg").setExecutor(new HologramCommand(main)); //Gestion hologramme(s)
	    
	    
	    //Commande image
	    main.getCommand("image").setExecutor(new ImageCommand(main)); //Importation d'image(s)
	    
	    
	    //Commandes Message de mort
	    main.getCommand("deathmessage").setExecutor(new DeathMessageCommand(main)); //Activation ou désactivation message de mort
	    main.getCommand("dm").setExecutor(new DeathMessageCommand(main)); //Activation ou désactivation message de mort
	    
	    
	    //Commandes d'un achievement
	    main.getCommand("achievementsmessage").setExecutor(new AchievementsMessageCommand(main)); //Activation ou désactivation des achievements
	    main.getCommand("advancementsmessage").setExecutor(new AchievementsMessageCommand(main)); //Activation ou désactivation des achievements
	    main.getCommand("am").setExecutor(new AchievementsMessageCommand(main)); //Activation ou désactivation des achievements

	    //Commande menue sanction
	    main.getCommand("staff").setExecutor(new StaffCommand());

	 	//Commande pour remettre à zéro le Scoreboard des joueurs
	 	main.getCommand("resetboard").setExecutor(new ResetBoardCommand(main));

	 	//Commande pour sélectionner une Zone et ensuite donner un accès quelconque à un grade
	 	main.getCommand("zone").setExecutor(new ZoneCommand(main));

		//Commande permettant de réinitialiser entièrement un joueur
		main.getCommand("resetplayer").setExecutor(new ResetPlayerCommand(main));

		//Commande pour créer des 'NPC'
		main.getCommand("npc").setExecutor(new NPCCommand(main));
	    
	    // Commandes fonctionnelles si le serveur est sous bungee //
	    if(main.hasBungee()) {
	    	
	    	//Récupérer le nombre de joueurs dans un serveur spécifique,
	    	main.getCommand("playercount").setExecutor(new PlayerCountCommand(main)); //Liste nombre de joueurs dans un serveur
	    	
	    	//Récupérer la liste des joueurs dans un serveur spécifique,
	    	main.getCommand("playerlist").setExecutor(new PlayerListCommand(main)); //Liste joueur(s) dans un serveur
	   
	    	// Récupérer la liste des joueurs dans un serveur spécifique, //
	    	main.getCommand("proxycmd").setExecutor(new ProxyCMDCommand(main)); //Exécute des commandes d'autre(s) serveur(s)
	    	main.getCommand("proxycommand").setExecutor(new ProxyCMDCommand(main)); //Exécute des commandes d'autre(s) serveur(s)
	    	// Récupérer la liste des joueurs dans un serveur spécifique, //
	    }  
	    // Commandes fonctionnelles si le serveur est sous bungee //
	    
	}
	 
	/**********************************/
	/* CHARGEMENT DES CLASS COMMANDES */ 
	/**********************************/
}
