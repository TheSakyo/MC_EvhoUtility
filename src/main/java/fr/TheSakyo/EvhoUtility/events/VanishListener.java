package fr.TheSakyo.EvhoUtility.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

import java.util.UUID;

public class VanishListener implements Listener {

	/* Récupère la class "Main" + un évènement compatible LuckPerms */
	private final UtilityMain main;
	public VanishListener(UtilityMain pluginMain, LuckPerms luckperms) { 
		
    	//Récupération class "Main"
        this.main = pluginMain;
        
        //Récupération de chargement d'évènement LuckPerms
        EventBus eventBus = luckperms.getEventBus();
        
        //Récupération de l'évènement "UserDataRecalculateEvent"
        eventBus.subscribe(UserDataRecalculateEvent.class, this::UserLoadPermission);
	}
	/* Récupère la class "Main" + un évènement compatible LuckPerms */
	 
	
	
	
	/*************************************/
    /* PARTIE ÉVÈNEMENT POUR LE "VANISH" */
    /*************************************/

	// Évènement quand le joueur change de permission (Compatible LuckPerms) [Niveau "Vanish"] //
	public void UserLoadPermission(UserDataRecalculateEvent e) {
		
		UUID uuid = e.getUser().getUniqueId();
		CachedDataManager data = e.getData();

		/************************************************/

		//Vérifie si le serveur fonctionne sous bungee
		if(!main.hasBungee()) return;

		/************************************************/

		if(data.getPermissionData().getPermissionMap().containsKey("EvhoProxy.vanished")) { main.methodVanish.playerVanished(uuid); }
		else { main.methodVanish.playerVanished(uuid); }
	}
	// Évènement quand le joueur change de permission (Compatible LuckPerms) [Niveau "Vanish"] //

	/*************************************/
	/* PARTIE ÉVÈNEMENT POUR LE "VANISH" */
	/*************************************/
}
