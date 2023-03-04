package fr.TheSakyo.EvhoUtility.registering;

import fr.TheSakyo.EvhoUtility.commands.entities.*;
import fr.TheSakyo.EvhoUtility.commands.inventory.*;
import org.bukkit.command.CommandExecutor;

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
	 private UtilityMain main;
	 public UtilityCommandsCreated(UtilityMain pluginMain) { this.main = pluginMain; }
	 /* Récupère la class "Main" */
	 
	 
	 /**********************************/
	 /* CHARGEMENT DES CLASS COMMANDES */ 
	 /**********************************/
	 
	 public void getCommands() {
		 
		//Commandes Reload du Plugin 
		main.getCommand("utilityreload").setExecutor((CommandExecutor)new ReloadCommand(main)); //Rechargement plugin
		main.getCommand("utilityrl").setExecutor((CommandExecutor)new ReloadCommand(main)); //Rechargement plugin


		//Commande de contournement de certaine(s) action(s) bloquée(s) (pour admin)
		main.getCommand("bypass").setExecutor((CommandExecutor)new BypassCommand(main));


		//Commandes Essentials
		main.getCommand("world").setExecutor((CommandExecutor)new WorldCommand(main)); //Création de monde
		main.getCommand("speed").setExecutor((CommandExecutor)new SpeedCommand(main)); //Vitesse de marche
		main.getCommand("fly").setExecutor((CommandExecutor)new FlyCommand(main)); //Mode vol
		main.getCommand("flyspeed").setExecutor((CommandExecutor)new FlySpeedCommand(main)); //Vitesse de vol
		main.getCommand("heal").setExecutor((CommandExecutor)new HealCommand(main)); //Soignement
		main.getCommand("food").setExecutor((CommandExecutor)new FoodCommand(main)); //Rassasiement
		main.getCommand("AFK").setExecutor((CommandExecutor)new AFKKickCommand(main)); //AFK
		main.getCommand("skull").setExecutor((CommandExecutor)new SkullCommand(main)); //Récupération tête de joueur
		
		main.getCommand("lightning").setExecutor((CommandExecutor)new LightningCommand(main)); //Eclair
		main.getCommand("light").setExecutor((CommandExecutor)new LightningCommand(main)); //Eclair
		 
		//Commandes Essentials [Management de l'inventaire]
		main.getCommand("clearinventory").setExecutor((CommandExecutor)new ClearInventoryCommand(main)); //Suppression inventaire
		main.getCommand("clearinv").setExecutor((CommandExecutor)new ClearInventoryCommand(main)); //Suppression inventaire
		
		main.getCommand("cleararmor").setExecutor((CommandExecutor)new ClearArmorCommand(main)); //Suppression armure
		
		main.getCommand("invsee").setExecutor((CommandExecutor)new InvseeCommand(main)); //Affichage d'un inventaire
		
		main.getCommand("enderchest").setExecutor((CommandExecutor)new EnderChestCommand(main)); //Affichage coffre du néant
		main.getCommand("ender").setExecutor((CommandExecutor)new EnderChestCommand(main)); //Affichage coffre du néant
		
		main.getCommand("more").setExecutor((CommandExecutor)new MoreItemCommand(main)); //Ajout d'item dans la main principale
		main.getCommand("repair").setExecutor((CommandExecutor)new RepairItemCommand(main)); //R�paration d'item dans la main principale
		
		//Commandes Essentials [Mode Dieux (GodMode)]
		main.getCommand("godmode").setExecutor((CommandExecutor)new GodModeCommand(main)); //Mode dieux (invinsible)
		main.getCommand("god").setExecutor((CommandExecutor)new GodModeCommand(main)); //Mode dieux (invinsible)
		
		
		//Commandes Essentials [Mode de jeux]
		main.getCommand("gamemode").setExecutor((CommandExecutor)new GamemodeCommand(main)); //Mode de jeux du joueur
		main.getCommand("gm").setExecutor((CommandExecutor)new GamemodeCommand(main)); //Mode de jeux du joueur
		
		
		//Commandes Essentials [Temps Joueurs]
		main.getCommand("ptime").setExecutor((CommandExecutor)new PtimeCommand(main)); //Temp du joueur uniquement
		
		
		//Commandes Essentials [Temps Joueurs]
		main.getCommand("pweather").setExecutor((CommandExecutor)new PweatherCommand(main)); //Temp du joueur uniquement
		
		
		//Commandes Essentials [Temps Journées]
		main.getCommand("day").setExecutor((CommandExecutor)new DayCommand(main)); //Temp du monde (matin)
		main.getCommand("noon").setExecutor((CommandExecutor)new NoonCommand(main)); //Temp du monde (midi)
		main.getCommand("sunset").setExecutor((CommandExecutor)new SunsetCommand(main)); //Temp du monde (coucher du soleil)
		
		
		//Commandes Essentials [Temps Soirées]
		main.getCommand("night").setExecutor((CommandExecutor)new NightCommand(main)); //Temp du monde (nuit)
		main.getCommand("midnight").setExecutor((CommandExecutor)new MidnightCommand(main)); //Temp du monde (minuit)
		main.getCommand("sunrise").setExecutor((CommandExecutor)new SunriseCommand(main)); //Temp du monde (lever du soleil)
		
		
		//Commandes Essentials [Temps Météo]
		main.getCommand("sun").setExecutor((CommandExecutor)new SunCommand(main)); //Météo du monde (ensoleillé)
		main.getCommand("rain").setExecutor((CommandExecutor)new RainCommand(main)); //Météo du monde (pluvieux)
		main.getCommand("thunder").setExecutor((CommandExecutor)new ThunderCommand(main)); //Météo du monde (pluvieux + orageux)
		
		
		//Commandes ClearLag [Suppression d'Entitées] (EvhoLag)
	    main.getCommand("evholag").setExecutor((CommandExecutor)new EvhoLagCommand()); //"ClearLag" Supression Entitée(s)
	    main.getCommand("elag").setExecutor((CommandExecutor)new EvhoLagCommand()); //"ClearLag" Supression Entitée(s)
	    main.getCommand("lag").setExecutor((CommandExecutor)new EvhoLagCommand()); //"ClearLag" Supression Entitée(s)
	    
	   
		//Commandes Hologram
	    main.getCommand("hologram").setExecutor((CommandExecutor)new HologramCommand(main)); //Gestion hologramme(s)
	    main.getCommand("hg").setExecutor((CommandExecutor)new HologramCommand(main)); //Gestion hologramme(s)
	    
	    
	    //Commande image
	    main.getCommand("image").setExecutor((CommandExecutor)new ImageCommand(main)); //Imporation d'image(s)
	    
	    
	    //Commandes Message de mort
	    main.getCommand("deathmessage").setExecutor((CommandExecutor)new DeathMessageCommand(main)); //Activation ou désactivation message de mort
	    main.getCommand("dm").setExecutor((CommandExecutor)new DeathMessageCommand(main)); //Activation ou désactivation message de mort
	    
	    
	    //Commandes d'achievement
	    main.getCommand("achievementsmessage").setExecutor((CommandExecutor)new AchievementsMessageCommand(main)); //Activation ou désactivation des achievements
	    main.getCommand("advancementsmessage").setExecutor((CommandExecutor)new AchievementsMessageCommand(main)); //Activation ou désactivation des achievements
	    main.getCommand("am").setExecutor((CommandExecutor)new AchievementsMessageCommand(main)); //Activation ou désactivation des achievements

	    //Commande menue sanction
	    main.getCommand("staff").setExecutor((CommandExecutor)new StaffCommand(main));

	 	//Commande pour remettre à zéro le Scoreboard des joueurs
	 	main.getCommand("resetboard").setExecutor((CommandExecutor)new ResetBoardCommand(main));

	 	//Commande pour selectionner une Zone et ensuite donner un accés quelconque à un grade
	 	main.getCommand("zone").setExecutor((CommandExecutor)new ZoneCommand(main));


		//Commande pour créer des 'NPC'
		main.getCommand("npc").setExecutor((CommandExecutor)new NPCCommand(main));
	    
	    // Commandes fonctionnelles si le serveur est sous bungee //
	    if(main.hasBungee() == true) {
	    	
	    	//Récupérer le nombre de joueurs dans un serveur spécifique,
	    	main.getCommand("playercount").setExecutor((CommandExecutor)new PlayerCountCommand(main)); //Liste nombre de joueurs dans un serveur
	    	
	    	//Récupérer la liste des joueurs dans un serveur spécifique,
	    	main.getCommand("playerlist").setExecutor((CommandExecutor)new PlayerListCommand(main)); //Liste joueur(s) dans un serveur
	   
	    	// Récupérer la liste des joueurs dans un serveur spécifique, //
	    	main.getCommand("proxycmd").setExecutor((CommandExecutor)new ProxyCMDCommand(main)); //Exécute des commandes d'autre(s) serveur(s)
	    	main.getCommand("proxycommand").setExecutor((CommandExecutor)new ProxyCMDCommand(main)); //Exécute des commandes d'autre(s) serveur(s)
	    	// Récupérer la liste des joueurs dans un serveur spécifique, //
	    }  
	    // Commandes fonctionnelles si le serveur est sous bungee //
	    
	}
	 
	/**********************************/
	/* CHARGEMENT DES CLASS COMMANDES */ 
	/**********************************/
}
