package fr.TheSakyo.EvhoUtility.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class TabListener implements Listener {
	
   /* Récupère la class "Main" */
   private static UtilityMain mainInstance = UtilityMain.getInstance();
   /* Récupère la class "Main" */
	  
   
   
   // Evenement quand le joueur rejoint ou quitte le serveur //
   // Pour recharer le tabList avec la méthode "SetupTablist" de la class "TabManager" //

   public static void onQuit(PlayerQuitEvent e) { mainInstance.tab.setupTablist(e.getPlayer()); }

   public static void onJoin(PlayerJoinEvent e) { mainInstance.tab.setupTablist(e.getPlayer()); }
   
   // Evenement quand le joueur rejoint ou quitte le serveur //
   // Pour recharer le tabList avec la méthode "SetupTablist" de la class "TabManager" //
   
}
