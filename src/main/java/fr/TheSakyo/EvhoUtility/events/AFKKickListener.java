package fr.TheSakyo.EvhoUtility.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class AFKKickListener implements Listener {
	
	BukkitScheduler Run = Bukkit.getServer().getScheduler();
	
	/* Récupère la class "Main" */
    private static UtilityMain mainInstance = UtilityMain.getInstance();
	/* Récupère la class "Main" */
    
    
    
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
 /* PARTIE EVENEMENENT POUR LE SYSTEME D'AFK */ 
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */    
    
    /**********************************************************/
	/* APPEL D'UNE METHODE AFK AVEC CES DIFFERENTS EVENEMENTS */ 
	/**********************************************************/
    
    // Appel la méthode AFK si le joueur bouge //
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

       Player p = e.getPlayer();

       if(mainInstance.time.containsKey(p.getUniqueId())) {

           if(mainInstance.time.get(p.getUniqueId()) >= 900) { mainInstance.afk.runAFK(p.getUniqueId(), Boolean.FALSE, 0); return; } //Annule le joueur étaht 'AFK'
       }
    }
    // Appel la méthode AFK si le joueur bouge //
    
    
    
    // Appel la méthode AFK si le joueur parle dans le tchat //
    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {

        Player p = e.getPlayer();

        if(mainInstance.time.containsKey(p.getUniqueId())) {

            if(mainInstance.time.get(p.getUniqueId()) >= 900) { mainInstance.afk.runAFK(p.getUniqueId(), Boolean.FALSE, 0); return; } //Annule le joueur étaht 'AFK'
        }
    }
    // Appel la méthode AFK si le joueur parle dans le tchat //
    
    
    
    // Appel la méthode AFK si le joueur éxécute une commande, sauf si la commande est "/afk" //
    @EventHandler
    public void onPlayerCommad(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();

        if(e.getMessage().equalsIgnoreCase("/afk")) { return; }
    	else {

            if(mainInstance.time.containsKey(p.getUniqueId())) {

                if(mainInstance.time.get(p.getUniqueId()) >= 900) { mainInstance.afk.runAFK(p.getUniqueId(), Boolean.FALSE, 0); return; } //Annule le joueur étaht 'AFK'
            }
        }
    }
    // Appel la méthode AFK si le joueur éxécute une commande, sauf si la commande est "/afk" //
    
    
    
    // Appel la méthode AFK si le joueur rejoint le serveur ou annule l'autoAFK si le joueur se déconnecte ou alors est "kick" //
    public static void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if(mainInstance.time.containsKey(p.getUniqueId())) {

                if(mainInstance.time.get(p.getUniqueId()) >= 900) { mainInstance.afk.runAFK(p.getUniqueId(), null, 0); return; } //Annule le joueur étaht 'AFK'
        }
    }

    public static void onPlayerLeave(PlayerQuitEvent e) {

        Player p = e.getPlayer();
        if(mainInstance.time.containsKey(p.getUniqueId())) {

            if(mainInstance.time.get(p.getUniqueId()) >= 900) {

                mainInstance.AFKRun.get(p.getUniqueId()).cancel(); //Annule la boucle pour le joueur
                mainInstance.afk.runAFK(p.getUniqueId(), Boolean.FALSE, 0); //Annule le joueur étaht 'AFK'
                return;
            }
        }
    }

    @EventHandler										  
    public void onPlayerLeaveWithKick(PlayerKickEvent e) {

        Player p = e.getPlayer();

        if(mainInstance.time.containsKey(p.getUniqueId())) {

            if(mainInstance.time.get(p.getUniqueId()) >= 900) {

                mainInstance.AFKRun.get(p.getUniqueId()).cancel(); //Annule la boucle pour le joueur
                mainInstance.afk.runAFK(p.getUniqueId(), Boolean.FALSE, 0); //Annule le joueur étaht 'AFK'
                return;
            }
        }
    }
   // Appel la méthode AFK si le joueur rejoint le serveur ou annule l'autoAFK si le joueur se déconnecte ou alors est "kick" //
    
    
    /**********************************************************/
	/* APPEL D'UNE METHODE AFK AVEC CES DIFFERENTS EVENEMENTS */ 
	/**********************************************************/

 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
 /* PARTIE EVENEMENENT POUR LE SYSTEME D'AFK */ 
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */  
}
 
