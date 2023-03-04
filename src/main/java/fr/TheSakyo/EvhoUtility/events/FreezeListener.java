package fr.TheSakyo.EvhoUtility.events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.help.HelpTopic;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;

public class FreezeListener implements Listener {

	/* Récupère la class "Main" */
	private UtilityMain main; 
	public FreezeListener(UtilityMain pluginMain, LuckPerms luckperms) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	 

	
	// Petit évènement quand le joueur bouge on envoie un message d'erreur si le joueur est "freeze" //
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		
		Player p = e.getPlayer();
		User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

					/* ------------------------ */

		if(main.freezeP.contains(p)) {

			if(e.getFrom() != e.getTo()) {

				p.teleport(e.getFrom());
				p.sendMessage(ChatColor.RED + "Vous ne pouvez pas bouger, vous êtes Freeze !");
				return;
			}
		}

	}
	
	// Petit évènement quand le joueur bouge on envoie un message d'erreur si le joueur est "freeze" //
	
	
	
	// Petite évènementn, on envoie un message d'erreur si le joueur éxécute une commande et si le joueur est "freeze" //
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerChat(PlayerCommandPreprocessEvent e) { 
    	
		Player p = e.getPlayer();
		
		if(main.freezeP.contains(p)) {
			
			List<String> commands = Arrays.asList("/op","/deop","/reload","/rl","/restart","/restore","/stop","/proxycmd","/proxycommand","/freeze","/staff");

			if(CustomMethod.startsWithIgnoreCase(e.getMessage(), commands)) { return; }

			HelpTopic htopic = Bukkit.getServer().getHelpMap().getHelpTopic(e.getMessage().split(" ")[0]);

			if(htopic != null) {

				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "Impossible d'éxécuter cette commande en êtant Freeze !");
			}
      }
  }
    
 // Petite évènementn, on envoie un message d'erreur si le joueur éxécute une commande et si le joueur est "freeze" //
    
    
    
    
    // Petit évènement quand le joueur casse un bloc, on envoie un message d'erreur si le joueur est "freeze" //
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
    	
    	Player p = e.getPlayer();
      
      	if(main.freezeP.contains(p)) {
  		
  			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Vous ne pouvez pas casser de bloc en êtant Freeze !");
			return;
  		}
    }
    
    // Petit évènement quand le joueur casse un bloc, on envoie un message d'erreur si le joueur est "freeze" //
    
    
    
    
    // Petit évènement quand le joueur pose un bloc, on envoie un message d'erreur si le joueur est "freeze"  //
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
    	
    	Player p = e.getPlayer();
        
      	if(main.freezeP.contains(p)) {
  		
  			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "Vous ne pouvez pas faire ceci en êtant Freeze !");
			return;
  		}
    }
    
    // Petit évènement quand le joueur pose un bloc, on envoie un message d'erreur si le joueur est "freeze" //
	
	/***************************************/
    /* PARTIE EVENEMENENT POUR LE "FREEZE" */ 
    /***************************************/	
	
}
