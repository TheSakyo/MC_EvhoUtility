package fr.TheSakyo.EvhoUtility.commands.entities;

import java.util.ArrayList;
import java.util.List;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class EvhoLagCommand implements CommandExecutor {
	
  
  //Timer pour la suppression automatique des entitées
  public static int AutoRemove = 600;
  
  //Listes des Entitées
  public static List<Entity> entities = new ArrayList<Entity>();
  
  
  // Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //
  String evholag = ChatColor.GRAY + "========= " + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "Evho" + ChatColor.DARK_GREEN.toString() + ChatColor.BOLD.toString() + "Lag" + ChatColor.GRAY + " ========="; 
  String footer = ChatColor.GRAY + "===========================";
  //Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //
  
  // Préfix "EvhoLag" //
  private static String prefixlag = ChatColor.WHITE + "[" + ChatColor.RED + "EvhoLag" + ChatColor.WHITE + "]" + " ";
  // Préfix "EvhoLag" //
  
  
  
  
  /***************************************************************************************/
  /* PARTIE COMMANDE POUR LA PARTIE "EVHOLAGG" (Supression d'entitées, corrige les lags) */
  /***************************************************************************************/
  
  public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
	  
	  if(sender instanceof Player p) {

		  if(!p.hasPermission("evhoutility.lag")) {
			  
			  p.sendMessage(UtilityMain.getInstance().prefix + ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + " - " + ChatColor.RED + "Vous n'avez pas la permission !");
			  return true;
		  } 
	  }
			  
	  if(args.length == 0) {
		  
		  sender.sendMessage(evholag);
		  sender.sendMessage("");
		  sender.sendMessage(ChatColor.GREEN + "Alias : " + ChatColor.GOLD + "/elag ou /lag");
		  sender.sendMessage("");
		  sender.sendMessage(ChatColor.LIGHT_PURPLE + "/evhoLag " + ChatColor.AQUA + "help " + ChatColor.WHITE + ":" + ChatColor.GRAY + " Affiche la listes des commandes d'EvhoLag.");
		  sender.sendMessage(ChatColor.LIGHT_PURPLE + "/evhoLag " + ChatColor.AQUA + "info " + ChatColor.WHITE + ":" + ChatColor.GRAY + " Information sur le Serveur.");
		  sender.sendMessage(ChatColor.LIGHT_PURPLE + "/evhoLag " + ChatColor.AQUA + "clear " + ChatColor.WHITE + ":" + ChatColor.GRAY + " Suppression d'Entité(s).");
		  sender.sendMessage("");
		  sender.sendMessage(footer);
	 
	  } else if(args.length == 1) {
		  
		  if(args[0].equalsIgnoreCase("help")) Bukkit.dispatchCommand(sender, "evholag"); 

		  else if(args[0].equalsIgnoreCase("info")) {
			  
			  Runtime run = Runtime.getRuntime();
			  
			  sender.sendMessage(evholag);
			  sender.sendMessage("");
			  sender.sendMessage(ChatColor.GOLD + "Ram Utilisée(s) : " + ChatColor.RED.toString() + ChatColor.BOLD.toString() + ((run.totalMemory() - run.freeMemory()) / 1046576L) + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + " MB.");
			  sender.sendMessage(ChatColor.GOLD + "Ram Disponible(s) : " + ChatColor.DARK_GREEN.toString() + ChatColor.BOLD.toString() + (run.maxMemory() / 1046576L - (run.totalMemory() - run.freeMemory()) / 1046576L) + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + " MB.");
			  sender.sendMessage(ChatColor.GOLD + "Ram Max : " + ChatColor.DARK_AQUA.toString() + ChatColor.BOLD.toString() + (run.maxMemory() / 1046576L) + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + " MB.");
			  sender.sendMessage(ChatColor.GOLD + "Entité(s) Supprimée(s) Dans : " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + AutoRemove + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + " Secondes.");
			  sender.sendMessage("");
			  sender.sendMessage(footer);
		  
		  } else if(args[0].equalsIgnoreCase("clear")) {
			  
			 if(sender instanceof Player p) { ItemRemoved(null, p); }
			 else if (sender instanceof CommandSender) { ItemRemoved(sender, null); }
			 
		  } else { Bukkit.dispatchCommand(sender, "evholag"); }
			
	  } else { Bukkit.dispatchCommand(sender, "evholag"); }

	  return false;
  	}
  
   /***************************************************************************************/
   /* PARTIE COMMANDE POUR LA PARTIE "EVHOLAGG" (Supression d'entitées, corrige les lags) */
   /***************************************************************************************/
  	
  
  
  
    // Méthode rapide pour la partie de suppression d'entitées //
  
  	public static void ItemRemoved(CommandSender sender, Player p) {
  		
  		int count = 0;
  		
  		if(!entities.isEmpty()) entities.clear();
  		

  		for(World entity : Bukkit.getServer().getWorlds()) {
  			
  			for(Entity current : entity.getEntities()) { 
  				
  				if(current instanceof Item) { 
  					
  					count++;
  				
	  				if(entities.isEmpty()) { entities = entity.getEntities(); }
	  				
	  				else { if(!entities.contains(current)) entities.add(current); }
  				} 
  				
  			}
  			
  		}
  		

  		if(count == 0 || count == 1) {
  			
  			if(sender != null && p == null) { 
				
			    Bukkit.getServer().broadcast(CustomMethod.StringToComponent(prefixlag + ChatColor.GOLD + count + ChatColor.GRAY + " Entité a été supprimer par " + ChatColor.YELLOW + "La Console" + ChatColor.GRAY + " !"));

			} else if(sender == null && p != null) {
					
			    Bukkit.getServer().broadcast(CustomMethod.StringToComponent(prefixlag + ChatColor.GOLD + count + ChatColor.GRAY + " Entité a été supprimer par " + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + " !"));
					
			} else if(sender == null && p == null) {
				
				for(Player player : Bukkit.getServer().getOnlinePlayers()) {
					
					player.sendMessage(prefixlag + ChatColor.GOLD + count + ChatColor.GRAY + " Entité a été supprimer !");
					
				}
			}
  			
  		} else {
  			
			if(sender != null && p == null) { 
				
			    Bukkit.getServer().broadcast(CustomMethod.StringToComponent(prefixlag + ChatColor.GOLD + count + ChatColor.GRAY + " Entités ont été supprimer par " + ChatColor.YELLOW + "La Console" + ChatColor.GRAY + " !"));

			} else if(sender == null && p != null) {
					
			    Bukkit.getServer().broadcast(CustomMethod.StringToComponent(prefixlag + ChatColor.GOLD + count + ChatColor.GRAY + " Entités ont été supprimer par " + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + " !"));
					
			} else if(sender == null && p == null) {
				
				for(Player player : Bukkit.getServer().getOnlinePlayers()) {
					
					player.sendMessage(prefixlag + ChatColor.GOLD + count + ChatColor.GRAY + " Entités ont été supprimer !");
					
				}
			}
  		}
  		
  			
  		for(Entity current : entities) { if(current instanceof Item) { current.remove(); } }
  		
  		entities.clear();
  		
  	}
  	
   // Méthode rapide pour la partie de suppression d'entitées //
  	
  	
  	
  	
  	
  	
   // Timer pour la supression d'entitées AUTOMATIQUE //
  	
   public static void AutoRemove() {
	
	Bukkit.getScheduler().scheduleSyncRepeatingTask(UtilityMain.getInstance(), new Runnable() {

		public void run() {

			if(AutoRemove > 0) {
				
  				if(AutoRemove == 300)
  					for(Player player : Bukkit.getServer().getOnlinePlayers()) {
  					  player.sendMessage(prefixlag + ChatColor.GRAY + "Suppression d'Entité(s) dans " + ChatColor.GOLD + "5 Minutes" + ChatColor.GRAY + ".");
  					}
				else if(AutoRemove == 60)
					for(Player player : Bukkit.getServer().getOnlinePlayers()) {
  					  player.sendMessage(prefixlag + ChatColor.GRAY + "Suppression d'Entité(s) dans " + ChatColor.GOLD + "1 Minute" + ChatColor.GRAY + ".");
  					}
				else if((AutoRemove == 30) || (AutoRemove == 10) || (AutoRemove <= 5 && AutoRemove >= 2)) {
					for(Player player : Bukkit.getServer().getOnlinePlayers()) {
						player.sendMessage(prefixlag + ChatColor.GRAY + "Suppression d'Entité(s) dans " + ChatColor.GOLD + AutoRemove + " Secondes" + ChatColor.GRAY + ".");
					}
				}
				else if(AutoRemove == 1)
					for(Player player : Bukkit.getServer().getOnlinePlayers()) {
  					  player.sendMessage(prefixlag + ChatColor.GRAY + "Suppression d'Entité(s) dans " + ChatColor.GOLD + AutoRemove + " Seconde" + ChatColor.GRAY + ".");
  					}
				AutoRemove--;
			} 

			if(AutoRemove == 0) {
				ItemRemoved(null, null);
				AutoRemove = 600;
			} 
		 }
	  },  20L, 20L);
   }
   
   // Timer pour la supression d'entitées AUTOMATIQUE //
}