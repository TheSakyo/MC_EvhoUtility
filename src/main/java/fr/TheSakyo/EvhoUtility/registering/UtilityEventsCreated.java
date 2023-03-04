package fr.TheSakyo.EvhoUtility.registering;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.events.*;
import fr.TheSakyo.EvhoUtility.utils.sanctions.AnvilCreation;
import fr.TheSakyo.EvhoUtility.utils.sanctions.SanctionUtils;
import fr.TheSakyo.EvhoUtility.utils.sanctions.ScrollInventory;

public class UtilityEventsCreated {
	
	 /* Récupère la class "Main" */
	 private UtilityMain main; 	
	 public UtilityEventsCreated(UtilityMain pluginMain) { this.main = pluginMain; }
	 /* Récupère la class "Main" */
	
	 
	 /***********************************/
	 /* CHARGEMENT DES CLASS EVENEMENTS */ 
	 /***********************************/	 
	 
	 public void getEvents() {

		//Evènement de Connexion/Déconnexion (fonctionne si le serveur est sous bungee)
	    if(main.hasBungee() == true) { main.getServer().getPluginManager().registerEvents(new ConnectionListener(main), main); }

	    
	    //Evènement Vanish
	    main.getServer().getPluginManager().registerEvents(new VanishListener(main, main.luckapi), main);
	    
	    
	    //Evènement Freeze
	    main.getServer().getPluginManager().registerEvents(new FreezeListener(main, main.luckapi), main);

		//Évènement quand le Joueur bouge
		main.getServer().getPluginManager().registerEvents(new NPCListener(main), main);
	    
	    //Evènement Pancarte
	    main.getServer().getPluginManager().registerEvents(new SignsListener(main), main);
		
	    
		//Evènements Essentials
		main.getServer().getPluginManager().registerEvents(new GodModeListener(), main); //Evènement mode Dieux
		main.getServer().getPluginManager().registerEvents(new AFKKickListener(), main); //Evènement AFK
		main.getServer().getPluginManager().registerEvents(new FormatGradeListener(main, main.luckapi), main); //Evènement format du grade
		

		//Évènements de Chat
		main.getServer().getPluginManager().registerEvents(new ChatListener(main), main);

		//Evènement Tablist
		main.getServer().getPluginManager().registerEvents(new TabListener(), main);
		
		
		//Evènement Message de mort ou Achievement
		main.getServer().getPluginManager().registerEvents(new DeathOrAchievementListener(), main);

		//Evènement quand le Joueur essait une action dans une Zone définit par le plugin
		main.getServer().getPluginManager().registerEvents(new ZoneListener(main), main);

		// Evènement Menu Staff //
		main.getServer().getPluginManager().registerEvents(new SanctionUtils(main), main); //Evènement Utile pour le Menu Staff/Sanction
		
		main.getServer().getPluginManager().registerEvents(new ScrollInventory(main), main); //Evènement Scroll d'inventaire
		main.getServer().getPluginManager().registerEvents(new InteractMenuStaffListener(main), main); //Evènement Intéraction d'inventaire
		main.getServer().getPluginManager().registerEvents(new AnvilCreation(main), main); //Evènement Création Enclume Customisé (Pour système de recherche)
		// Evènement Menu Staff //
	}
	 
	/***********************************/
	/* CHARGEMENT DES CLASS EVENEMENTS */ 
	/***********************************/	

}
