package fr.TheSakyo.EvhoUtility.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;

public class VanishListener implements Listener {

	/* Rècupére la class "Main" + un évènement compatible LuckPerms */
	private UtilityMain main; 
	public VanishListener(UtilityMain pluginMain, LuckPerms luckperms) { 
		
    	//Récupération class "Main"
        this.main = pluginMain;
        
        //Récupération de chargement d'évènement LuckPerms
        EventBus eventBus = luckperms.getEventBus();
        
        
        //Récupération de l'évènement "UserDataRecalculateEvent"
        eventBus.subscribe(UserDataRecalculateEvent.class, this::UserLoadPermission);
	}
	/* Récupère la class "Main" + un évènement compatible LuckPerms */
	 
	
	
	
	/***************************************/
    /* PARTIE EVENEMENENT POUR LE "VANISH" */ 
    /***************************************/			

	// Evenement quand le joueur change de permission (Compatible LuckPerms) [Niveau "Vanish"] //
	public void UserLoadPermission(UserDataRecalculateEvent e) {

		while(e.getUser() == null) {

			Player player = Bukkit.getServer().getPlayer(e.getUser().getUniqueId());
			CachedDataManager data = e.getData();

			//Vérifie si le serveur fonctionne sous bungee
			if(main.hasBungee() == false) { return; }

			if(data.getPermissionData().getPermissionMap().containsKey("EvhoProxy.vanished")) { main.methodvanish.PlayerVanished(player); }

			else { main.methodvanish.PlayerVanished(player); }
		}
	}
	// Evenement quand le joueur change de permission (Compatible LuckPerms) [Niveau "Vanish"] //
	
	/***************************************/
    /* PARTIE EVENEMENENT POUR LE "VANISH" */ 
    /***************************************/	
	
}
