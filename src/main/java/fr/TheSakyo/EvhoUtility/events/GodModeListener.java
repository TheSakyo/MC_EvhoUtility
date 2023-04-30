package fr.TheSakyo.EvhoUtility.events;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class GodModeListener implements Listener {
	
	/* Récupère la class "Main" */
    private static final UtilityMain mainInstance = UtilityMain.getInstance();
	/* Récupère la class "Main" */

	
	
   /**************************************************************************/
   /* PARTIE ÉVÈNEMENT LORSQUE LE JOUEUR EST EN GODMODE (Devient Invincible) */
   /**************************************************************************/
	
	 // Annule les dommages du joueur ou remet la vie MAX du joueur s'il a perdu des vies //
	
	  @EventHandler(priority = EventPriority.MONITOR)
	  public void onDamage(EntityDamageEvent e) {
		  
	    if(e.getEntity() instanceof Player && mainInstance.GODS.contains(e.getEntity().getUniqueId())) {
	    	
	  	    e.setCancelled(true);
	  	    
		      if(((Player) e.getEntity()).getHealth() != ((Player) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) {
	    	       
			      ((Player) e.getEntity()).setHealth(((Player) e.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
		      }
	    }
	     
	 }
	  
	// Annule les dommages du joueur ou remet la vie MAX du joueur s'il a perdu des vies //
	  
	  
	  
	 // Annule la faim du joueur ou remet la faim au niveau MAX du joueur s'il a perdu de la bouffe //
	  
	  @EventHandler(priority = EventPriority.MONITOR)
	  public void onFood(FoodLevelChangeEvent e) {
		  
	    if(e.getEntity() instanceof Player && mainInstance.GODS.contains(e.getEntity().getUniqueId())) {
	    	
		      e.setCancelled(true);
		      if(e.getEntity().getFoodLevel() != 20) e.getEntity().setFoodLevel(20);
		    }
	  }
	  
	  // Annule la faim du joueur ou remet la faim au niveau MAX du joueur s'il a perdu de la bouffe //
	  
	  
	  
	  // Enlève le joueur du "GODMODE" s'il rejoint ou quitte le serveur //
	  public static void onJoin(PlayerJoinEvent e) { mainInstance.GODS.remove(e.getPlayer().getUniqueId()); }

	  public static void onLeave(PlayerQuitEvent e) { mainInstance.GODS.remove(e.getPlayer().getUniqueId()); }
	  
	  // Enlève le joueur du "GODMODE" s'il rejoint ou quitte le serveur //

	/**************************************************************************/
	/* PARTIE ÉVÈNEMENT LORSQUE LE JOUEUR EST EN GODMODE (Devient Invincible) */
	/**************************************************************************/
}
