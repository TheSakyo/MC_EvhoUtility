package fr.TheSakyo.EvhoUtility.commands.basic;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.sanctions.SanctionUtils;

public class StaffCommand implements CommandExecutor {
	
	
	/* Récupère la class "Main" */
    UtilityMain main;
    public StaffCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */

    
    
    /**************************************************/
	/* PARTIE COMMANDE POUR AFFICHER LE MENU DE STAFF */ 
    /**************************************************/
    
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {	
    	
    	if(sender instanceof Player p) {

			if(args.length == 0) {
    			
    			if(sender.hasPermission("evhoutility.staff")) { p.openInventory(SanctionUtils.sanctionInv); } 
    				
    			else { sender.sendMessage(SanctionUtils.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !"); }
    			
    		} else if(args.length != 0){

    		 sender.sendMessage(SanctionUtils.prefix + ChatColor.RED + "Essayez /staff sans arguments");
    		}
    		
    	} 
    	
    	if(sender instanceof ConsoleCommandSender) {
    		
    	   sender.sendMessage(SanctionUtils.prefix + ChatColor.RED + "Vous devez être en jeux pour utiliser le Menu !");
    	}
    	return false;
    }
	
	/**************************************************/
	/* PARTIE COMMANDE POUR AFFICHER LE MENU DE STAFF */ 
    /**************************************************/

}
