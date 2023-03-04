package fr.TheSakyo.EvhoUtility.proxy.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class PlayerCountCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private UtilityMain main;
	public PlayerCountCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	/**********************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPERER LE NOMBRE DE JOUEURS DANS UN SERVEUR SPÉCIFIQUE */
	/**********************************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if((sender instanceof Player p)) {

			if(!p.hasPermission("evhoutility.playercount")) {
	        	  
               p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas la permission !");
               return true;
	         } 
            
    		if(args.length == 0) {
    			
    			int OnlineSize = Bukkit.getServer().getOnlinePlayers().size();
    			
    			p.sendMessage(main.prefix + ChatColor.GREEN + OnlineSize + ChatColor.GRAY + " joueur(s) connecté(s) dans le serveur actuel !");
    			
    		} else if(args.length == 1) {
    			
    			final String[] arguments = Arrays.copyOfRange(args, 0, args.length);

    			main.sendMessagePlugin(UtilityMain.channel, "PlayerCount", p, arguments);
    		
    		} else {
    			
    			p.sendMessage(main.prefix + ChatColor.RED + "Essayez /playercount ou /playercount [<server>]");
    		}
            return true;
            
		} 
		if((sender instanceof CommandSender)) {
			
			if(args.length == 0) {
    			
				int OnlineSize = Bukkit.getServer().getOnlinePlayers().size();
    			
    			sender.sendMessage(main.prefix + ChatColor.GREEN + OnlineSize + ChatColor.GRAY + " joueur(s) connecté(s) dans le serveur actuel !");
    			
    		} else {
				
				sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux pour récupérer le nombres des joueurs dans un serveur spécifique ou en globalité !");
			}
			
		}
		return false;
	}
	
	/**********************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPERER LE NOMBRE DE JOUEURS DANS UN SERVEUR SPÉCIFIQUE */
	/**********************************************************************************/

}
