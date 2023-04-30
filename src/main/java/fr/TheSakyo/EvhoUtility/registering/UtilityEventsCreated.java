package fr.TheSakyo.EvhoUtility.registering;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.events.*;
import fr.TheSakyo.EvhoUtility.utils.sanctions.AnvilCreation;
import fr.TheSakyo.EvhoUtility.utils.sanctions.SanctionUtils;
import fr.TheSakyo.EvhoUtility.utils.sanctions.ScrollInventory;

public class UtilityEventsCreated {
	
	 /* Récupère la class "Main" */
	 private final UtilityMain main;
	 public UtilityEventsCreated(UtilityMain pluginMain) { this.main = pluginMain; }
	 /* Récupère la class "Main" */
	
	 
	 /***********************************/
	 /* CHARGEMENT DES CLASS ÉVÈNEMENTS */
	 /***********************************/	 
	 
	 public void getEvents() {

		//Évènement de Connexion/Déconnexion (fonctionne si le serveur est sous bungee)
	    if(main.hasBungee()) { main.getServer().getPluginManager().registerEvents(new ConnectionListener(main), main); }

	    
	    //Évènement Vanish
	    main.getServer().getPluginManager().registerEvents(new VanishListener(main, main.luckApi), main);
	    
	    
	    //Évènement Freeze
	    main.getServer().getPluginManager().registerEvents(new FreezeListener(main, main.luckApi), main);

		//Évènement quand le Joueur bouge
		main.getServer().getPluginManager().registerEvents(new NPCListener(), main);
	    
	    //Évènement Pancarte
	    main.getServer().getPluginManager().registerEvents(new SignsListener(), main);
		
	    
		//Évènements Essentials
		main.getServer().getPluginManager().registerEvents(new GodModeListener(), main); //Évènement mode Dieux
		main.getServer().getPluginManager().registerEvents(new AFKKickListener(), main); //Évènement AFK
		main.getServer().getPluginManager().registerEvents(new FormatGradeListener(main, main.luckApi), main); //Évènement format du grade
		

		//Évènements de Chat
		main.getServer().getPluginManager().registerEvents(new ChatListener(main), main);

		//Évènement Tablist
		main.getServer().getPluginManager().registerEvents(new TabListener(), main);
		
		
		//Évènement Message de mort ou Achievement
		main.getServer().getPluginManager().registerEvents(new DeathOrAchievementListener(), main);

		//Évènement quand le Joueur essaie une action dans une Zone définit par le plugin
		main.getServer().getPluginManager().registerEvents(new ZoneListener(main), main);

		// Évènement Menu Staff //
		main.getServer().getPluginManager().registerEvents(new SanctionUtils(main), main); //Évènement Utile pour le Menu Staff/Sanction
		
		main.getServer().getPluginManager().registerEvents(new ScrollInventory(), main); //Évènement Scroll d'inventaire
		main.getServer().getPluginManager().registerEvents(new InteractMenuStaffListener(main), main); //Évènement Intéraction d'inventaire
		main.getServer().getPluginManager().registerEvents(new AnvilCreation(main), main); //Évènement Création Enclume Customisé (Pour système de recherche)
		// Évènement Menu Staff //
	}
	 
	/***********************************/
	/* CHARGEMENT DES CLASS ÉVÈNEMENTS */
	/***********************************/
}
