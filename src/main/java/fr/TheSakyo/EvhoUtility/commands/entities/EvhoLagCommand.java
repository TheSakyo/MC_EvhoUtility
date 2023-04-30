package fr.TheSakyo.EvhoUtility.commands.entities;

import java.util.ArrayList;
import java.util.List;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class EvhoLagCommand implements CommandExecutor {
	
  
  //Timer pour la suppression automatique des entités
  public static int AutoRemove = 600;
  
  //Listes des Entités
  public static List<Entity> entities = new ArrayList<>();
  
  
  // Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //
  String evholag = ChatFormatting.GRAY + "========= " + ChatFormatting.DARK_PURPLE.toString() + ChatFormatting.BOLD.toString() + "Evho" + ChatFormatting.DARK_GREEN.toString() + ChatFormatting.BOLD.toString() + "Lag" + ChatFormatting.GRAY + " =========";
  String footer = ChatFormatting.GRAY + "===========================";
  //Mise En Page En-Tête et Pied De Page de la partie "help" ou "info" //
  
  // Préfix "EvhoLag" //
  private static final String prefixlag = ChatFormatting.WHITE + "[" + ChatFormatting.RED + "EvhoLag" + ChatFormatting.WHITE + "]" + " ";
  // Préfix "EvhoLag" //
  

  
  /***************************************************************************************/
  /* PARTIE COMMANDE POUR LA PARTIE "EVHOLAGG" (Suppression d'entités, corrige les lags) */
  /***************************************************************************************/
  
  public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
	  
	  if(sender instanceof Player p) {

		  if(!p.hasPermission("evhoutility.lag")) {
			  
			  p.sendMessage(UtilityMain.getInstance().prefix + ChatFormatting.GRAY.toString() + ChatFormatting.BOLD.toString() + " - " + ChatFormatting.RED + "Vous n'avez pas la permission !");
			  return true;
		  } 
	  }
			  
	  if(args.length == 0) {
		  
		  sender.sendMessage(evholag);
		  sender.sendMessage("");
		  sender.sendMessage(ChatFormatting.GREEN + "Alias : " + ChatFormatting.GOLD + "/elag ou /lag");
		  sender.sendMessage("");
		  sender.sendMessage(ChatFormatting.LIGHT_PURPLE + "/evhoLag " + ChatFormatting.AQUA + "help " + ChatFormatting.WHITE + ":" + ChatFormatting.GRAY + " Affiche la listes des commandes d'EvhoLag.");
		  sender.sendMessage(ChatFormatting.LIGHT_PURPLE + "/evhoLag " + ChatFormatting.AQUA + "info " + ChatFormatting.WHITE + ":" + ChatFormatting.GRAY + " Information sur le Serveur.");
		  sender.sendMessage(ChatFormatting.LIGHT_PURPLE + "/evhoLag " + ChatFormatting.AQUA + "clear " + ChatFormatting.WHITE + ":" + ChatFormatting.GRAY + " Suppression d'Entité(s).");
		  sender.sendMessage("");
		  sender.sendMessage(footer);
	 
	  } else if(args.length == 1) {
		  
		  if(args[0].equalsIgnoreCase("help")) Bukkit.dispatchCommand(sender, "evholag"); 

		  else if(args[0].equalsIgnoreCase("info")) {
			  
			  Runtime run = Runtime.getRuntime();
			  
			  sender.sendMessage(evholag);
			  sender.sendMessage("");
			  sender.sendMessage(ChatFormatting.GOLD + "Ram Utilisée(s) : " + ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + ((run.totalMemory() - run.freeMemory()) / 1046576L) + ChatFormatting.WHITE.toString() + ChatFormatting.BOLD.toString() + " MB.");
			  sender.sendMessage(ChatFormatting.GOLD + "Ram Disponible(s) : " + ChatFormatting.DARK_GREEN.toString() + ChatFormatting.BOLD.toString() + (run.maxMemory() / 1046576L - (run.totalMemory() - run.freeMemory()) / 1046576L) + ChatFormatting.WHITE.toString() + ChatFormatting.BOLD.toString() + " MB.");
			  sender.sendMessage(ChatFormatting.GOLD + "Ram Max : " + ChatFormatting.DARK_AQUA.toString() + ChatFormatting.BOLD.toString() + (run.maxMemory() / 1046576L) + ChatFormatting.WHITE.toString() + ChatFormatting.BOLD.toString() + " MB.");
			  sender.sendMessage(ChatFormatting.GOLD + "Entité(s) Supprimée(s) Dans : " + ChatFormatting.GREEN.toString() + ChatFormatting.BOLD.toString() + AutoRemove + ChatFormatting.WHITE.toString() + ChatFormatting.BOLD.toString() + " Secondes.");
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
   /* PARTIE COMMANDE POUR LA PARTIE "EVHOLAGG" (Suppression d'entités, corrige les lags) */
   /***************************************************************************************/
  	
  
  
  
    // Méthode rapide pour la partie de suppression d'entités //
  
  	public static void ItemRemoved(CommandSender sender, Player p) {
  		
  		int count = 0;
  		if(!entities.isEmpty()) entities.clear();

		/**************************************************/

  		for(World entity : Bukkit.getServer().getWorlds()) {
  			
  			for(Entity current : entity.getEntities()) { 
  				
  				if(current instanceof Item) { 
  					
  					count++;
  				
	  				if(entities.isEmpty()) { entities = entity.getEntities(); }
	  				else { if(!entities.contains(current)) entities.add(current); }
  				} 
  				
  			}
  			
  		}

		/**************************************************/

  		if(count == 0 || count == 1) {
  			
  			if(sender != null && p == null) { 
				
			    Bukkit.getServer().broadcast(CustomMethod.StringToComponent(prefixlag + ChatFormatting.GOLD + count + ChatFormatting.GRAY + " Entité a été supprimer par " + ChatFormatting.YELLOW + "La Console" + ChatFormatting.GRAY + " !"));

			} else if(sender == null && p != null) {
					
			    Bukkit.getServer().broadcast(CustomMethod.StringToComponent(prefixlag + ChatFormatting.GOLD + count + ChatFormatting.GRAY + " Entité a été supprimer par " + ChatFormatting.YELLOW + p.getName() + ChatFormatting.GRAY + " !"));
					
			} else if(sender == null) {
				
				for(Player player : Bukkit.getServer().getOnlinePlayers()) {
					
					player.sendMessage(prefixlag + ChatFormatting.GOLD + count + ChatFormatting.GRAY + " Entité a été supprimer !");
				}
			}
  			
  		} else {
  			
			if(sender != null && p == null) { 
				
			    Bukkit.getServer().broadcast(CustomMethod.StringToComponent(prefixlag + ChatFormatting.GOLD + count + ChatFormatting.GRAY + " Entités ont été supprimer par " + ChatFormatting.YELLOW + "La Console" + ChatFormatting.GRAY + " !"));

			} else if(sender == null && p != null) {
					
			    Bukkit.getServer().broadcast(CustomMethod.StringToComponent(prefixlag + ChatFormatting.GOLD + count + ChatFormatting.GRAY + " Entités ont été supprimer par " + ChatFormatting.YELLOW + p.getName() + ChatFormatting.GRAY + " !"));
					
			} else if(sender == null) {
				
				for(Player player : Bukkit.getServer().getOnlinePlayers()) {
					
					player.sendMessage(prefixlag + ChatFormatting.GOLD + count + ChatFormatting.GRAY + " Entités ont été supprimer !");
				}
			}
  		}

		/**********************************************************/
  			
  		for(Entity current : entities) { if(current instanceof Item) { current.remove(); } }
  		entities.clear();
  	}
  	
   // Méthode rapide pour la partie de suppression d'entités //
  	

  	
  	
   // Timer pour la suppression d'entités AUTOMATIQUE //
  	
   public static void AutoRemove() {
	
	Bukkit.getScheduler().scheduleSyncRepeatingTask(UtilityMain.getInstance(), () -> {

        switch(AutoRemove) {

            case 300:

                sendMessageToPlayers("Suppression d'Entité(s) dans " + ChatFormatting.GOLD + "5 Minutes" );
                AutoRemove--;
                break;

            case 60:

                sendMessageToPlayers("Suppression d'Entité(s) dans " + ChatFormatting.GOLD + "1 Minute");
                AutoRemove--;
                break;

            case 30: case 10: case 5:
            case 4: case 3: case 2:

                sendMessageToPlayers("Suppression d'Entité(s) dans " + ChatFormatting.GOLD + AutoRemove + " Secondes");
                AutoRemove--;
                break;

            case 1:

                sendMessageToPlayers("Suppression d'Entité(s) dans " + ChatFormatting.GOLD + AutoRemove + " Seconde");
                AutoRemove--;
                break;
        }

        /******************************************/

        if(AutoRemove == 0) {

            ItemRemoved(null, null);
            AutoRemove = 600;
        }
     },  20L, 20L);
   }
   
   // Timer pour la suppression d'entités AUTOMATIQUE //

	/******************************************/
	/******************************************/

	// Méthode pour envoyer le message du 'clearlag' à tous les joueurs //
	private static void sendMessageToPlayers(String message) {

		for(Player player : Bukkit.getServer().getOnlinePlayers()) {

			player.sendMessage(prefixlag + ChatFormatting.GRAY + message + ChatFormatting.GRAY + ".");
		}
	}
	// Méthode pour envoyer le message du 'clearlag' à tous les joueurs //
}