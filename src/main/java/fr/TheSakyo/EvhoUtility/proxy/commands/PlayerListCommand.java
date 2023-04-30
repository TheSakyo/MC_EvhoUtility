package fr.TheSakyo.EvhoUtility.proxy.commands;

import java.util.Arrays;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class PlayerListCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public PlayerListCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	/***********************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPÉRER LA LISTES DES JOUEURS DANS UN SERVEUR SPÉCIFIQUE */
	/***********************************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if((sender instanceof Player p)) {

			if(!p.hasPermission("evhoutility.playerlist")) {
	        	  
               p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas la permission !");
               return true;
	         } 
            
    		if(args.length == 0) {
    			
    			int OnlineSize = Bukkit.getServer().getOnlinePlayers().size();
    			
    			if(OnlineSize == 0) {
    				
    				p.sendMessage(main.prefix + ChatFormatting.RED + "Aucun joueur(s) connecté(s) dans le serveur actuel !");
    			
				} else if(OnlineSize == 1) {
    				
    				String playersOnly = Iterables.getFirst(Bukkit.getServer().getOnlinePlayers(), null).getName();
    				
    				p.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement le joueur " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + playersOnly + ChatFormatting.GRAY + " dans le serveur actuel !");
    			
				} else {
    				
	    			p.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
	    			p.sendMessage(" ");
	    			p.sendMessage(" ");
	    			
	    			p.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des joueurs dans le serveur actuel :");
	    			
	    			for(Player players : Bukkit.getServer().getOnlinePlayers()) {
	    				
	    				p.sendMessage(" ");
	    				p.sendMessage(ChatFormatting.WHITE + "- " +ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + players.getName());
	    			}
	    			
	    			p.sendMessage(" ");
	    			p.sendMessage(" ");
	    			p.sendMessage(ChatFormatting.GRAY + "===========================");
    			}
    			
    		} else if(args.length == 1) {
    			
    			final String[] arguments = Arrays.copyOfRange(args, 0, args.length);

    			main.sendMessagePlugin(UtilityMain.channel, "PlayerList", p, arguments);
    		
    		} else {
    			
    			p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /playerlist ou /playerlist [<server>]");
    		}
            return true;
            
		} 
		if((sender instanceof CommandSender)) {
			
			if(args.length == 0) {
    			
				int OnlineSize = Bukkit.getServer().getOnlinePlayers().size();
    			
    			if(OnlineSize == 0) {
    				
    				sender.sendMessage(main.prefix + ChatFormatting.RED + "Aucun joueur(s) connecté(s) dans le serveur actuel !");
    			
				} else if(OnlineSize == 1) {
    				
    				String playersOnly = Iterables.getFirst(Bukkit.getServer().getOnlinePlayers(), null).getName();
    				
    				sender.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement le joueur " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + playersOnly + ChatFormatting.GRAY + " dans le serveur actuel !");
    			
				} else {
    				
					sender.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
					sender.sendMessage(" ");
					sender.sendMessage(" ");
	    			
					sender.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des joueurs dans le serveur actuel :");
	    			
	    			for(Player players : Bukkit.getServer().getOnlinePlayers()) {
	    				
	    				sender.sendMessage(" ");
	    				sender.sendMessage(ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + players.getName());
	    			}
	    			
	    			sender.sendMessage(" ");
	    			sender.sendMessage(" ");
	    			sender.sendMessage(ChatFormatting.GRAY + "===========================");
    			}
    			
    		} else {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour récupérer la liste des joueurs dans un serveur spécifique ou en globalité !");
			}
			
		}
		return false;
	}
	
	/***********************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPÉRER LA LISTES DES JOUEURS DANS UN SERVEUR SPÉCIFIQUE */
	/***********************************************************************************/

}