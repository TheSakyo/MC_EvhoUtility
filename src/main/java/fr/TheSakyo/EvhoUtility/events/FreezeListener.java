package fr.TheSakyo.EvhoUtility.events;

import java.util.Arrays;
import java.util.List;

import net.minecraft.ChatFormatting;
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

public class FreezeListener implements Listener {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public FreezeListener(UtilityMain pluginMain, LuckPerms luckperms) { this.main = pluginMain; }
	/* Récupère la class "Main" */


	/*************************************/
	/* PARTIE ÉVÈNEMENT POUR LE "FREEZE" */
	/************************************/
	
	// Petit évènement quand le joueur bouge, on envoie un message d'erreur si le joueur est "freeze" //
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		
		Player p = e.getPlayer();
		User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

					/* ------------------------ */

		if(main.freezeP.contains(p.getUniqueId())) {

			if(e.getFrom() != e.getTo()) {

				p.teleport(e.getFrom());
				p.sendMessage(ChatFormatting.RED + "Vous ne pouvez pas bouger, vous êtes Freeze !");
            }
		}

	}
	
	// Petit évènement quand le joueur bouge, on envoie un message d'erreur si le joueur est "freeze" //
	
	
	
	// Petit évènement, on envoie un message d'erreur si le joueur éxécute une commande et si le joueur est "freeze" //
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerChat(PlayerCommandPreprocessEvent e) { 
    	
		Player p = e.getPlayer();
		
		if(main.freezeP.contains(p.getUniqueId())) {
			
			List<String> commands = Arrays.asList("/op","/deop","/reload","/rl","/restart","/restore","/stop","/proxycmd","/proxycommand","/freeze","/staff");

			if(CustomMethod.startsWithIgnoreCase(e.getMessage(), commands)) { return; }

			HelpTopic htopic = Bukkit.getServer().getHelpMap().getHelpTopic(e.getMessage().split(" ")[0]);

			if(htopic != null) {

				e.setCancelled(true);
				p.sendMessage(ChatFormatting.RED + "Impossible d'exécuter cette commande en étant Freeze !");
			}
      }
  }
    
 // Petit évènement, on envoie un message d'erreur si le joueur éxécute une commande et si le joueur est "freeze" //
    
    
    
    
    // Petit évènement quand le joueur casse un bloc, on envoie un message d'erreur si le joueur est "freeze" //
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
    	
    	Player p = e.getPlayer();
      
      	if(main.freezeP.contains(p.getUniqueId())) {
  		
  			e.setCancelled(true);
			p.sendMessage(ChatFormatting.RED + "Vous ne pouvez pas casser de bloc en étant Freeze !");
        }
    }
    
    // Petit évènement quand le joueur casse un bloc, on envoie un message d'erreur si le joueur est "freeze" //
    
    
    
    
    // Petit évènement quand le joueur pose un bloc, on envoie un message d'erreur si le joueur est "freeze" //
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
    	
    	Player p = e.getPlayer();
        
      	if(main.freezeP.contains(p.getUniqueId())) {
  		
  			e.setCancelled(true);
			p.sendMessage(ChatFormatting.RED + "Vous ne pouvez pas faire ceci en étant Freeze !");
        }
    }
    
    // Petit évènement quand le joueur pose un bloc, on envoie un message d'erreur si le joueur est "freeze" //
	
	/*************************************/
    /* PARTIE ÉVÈNEMENT POUR LE "FREEZE" */
    /************************************/
}
