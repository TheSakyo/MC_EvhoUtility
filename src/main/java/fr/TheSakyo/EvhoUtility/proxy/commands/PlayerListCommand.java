package fr.TheSakyo.EvhoUtility.proxy.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class PlayerListCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public PlayerListCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	/***********************************************************************************/
	/* PARTIE COMMANDE POUR RÉUPERER LA LISTES DES JOUEURS DANS UN SERVEUR SPÉCIFIQUE */
	/***********************************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if((sender instanceof Player p)) {

			if(!p.hasPermission("evhoutility.playerlist")) {
	        	  
               p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas la permission !");
               return true;
	         } 
            
    		if(args.length == 0) {
    			
    			int OnlineSize = Bukkit.getServer().getOnlinePlayers().size();
    			
    			if(OnlineSize == 0) {
    				
    				p.sendMessage(main.prefix + ChatColor.RED + "Aucun joueur(s) connecté(s) dans le serveur actuel !");
    			
				} else if(OnlineSize == 1) {
    				
    				String playersOnly = Iterables.getFirst(Bukkit.getServer().getOnlinePlayers(), null).getName();
    				
    				p.sendMessage(main.prefix + ChatColor.GRAY + "Il y'a seulement le joueur " + ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + playersOnly + ChatColor.GRAY + " dans le serveur actuel !");
    			
				} else {
    				
	    			p.sendMessage(ChatColor.GRAY + "========= " + main.prefix + ChatColor.GRAY + "=========");
	    			p.sendMessage(" ");
	    			p.sendMessage(" ");
	    			
	    			p.sendMessage(ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + "Liste des joueurs dans le serveur actuel :");
	    			
	    			for(Player players : Bukkit.getServer().getOnlinePlayers()) {
	    				
	    				p.sendMessage(" ");
	    				p.sendMessage(ChatColor.WHITE + "- " +ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + players.getName());
	    			}
	    			
	    			p.sendMessage(" ");
	    			p.sendMessage(" ");
	    			p.sendMessage(ChatColor.GRAY + "===========================");
    			}
    			
    		} else if(args.length == 1){
    			
    			final String[] arguments = Arrays.copyOfRange(args, 0, args.length);

    			main.sendMessagePlugin(UtilityMain.channel, "PlayerList", p, arguments);
    		
    		} else {
    			
    			p.sendMessage(main.prefix + ChatColor.RED + "Essayez /playerlist ou /playerlist [<server>]");
    		}
            return true;
            
		} 
		if((sender instanceof CommandSender)) {
			
			if(args.length == 0) {
    			
				int OnlineSize = Bukkit.getServer().getOnlinePlayers().size();
    			
    			if(OnlineSize == 0) {
    				
    				sender.sendMessage(main.prefix + ChatColor.RED + "Aucun joueur(s) connecté(s) dans le serveur actuel !");
    			
				} else if(OnlineSize == 1) {
    				
    				String playersOnly = Iterables.getFirst(Bukkit.getServer().getOnlinePlayers(), null).getName();
    				
    				sender.sendMessage(main.prefix + ChatColor.GRAY + "Il y'a seulement le joueur " + ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + playersOnly + ChatColor.GRAY + " dans le serveur actuel !");
    			
				} else {
    				
					sender.sendMessage(ChatColor.GRAY + "========= " + main.prefix + ChatColor.GRAY + "=========");
					sender.sendMessage(" ");
					sender.sendMessage(" ");
	    			
					sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.UNDERLINE.toString() + "Liste des joueurs dans le serveur actuel :");
	    			
	    			for(Player players : Bukkit.getServer().getOnlinePlayers()) {
	    				
	    				sender.sendMessage(" ");
	    				sender.sendMessage(ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString() + players.getName());
	    			}
	    			
	    			sender.sendMessage(" ");
	    			sender.sendMessage(" ");
	    			sender.sendMessage(ChatColor.GRAY + "===========================");
    			}
    			
    		} else {
				
				sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux pour récupérer la liste des joueurs dans un serveur spécifique ou en globalité !");
			}
			
		}
		return false;
	}
	
	/***********************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPERER LA LISTES DES JOUEURS DANS UN SERVEUR SPÉCIFIQUE */
	/***********************************************************************************/

}