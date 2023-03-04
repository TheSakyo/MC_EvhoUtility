package fr.TheSakyo.EvhoUtility.commands.weathers.weathersglobal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class ThunderCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public ThunderCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	/**************************************************/
	/* PARTIE COMMANDE POUR LE TEMPS ORAGEUX DU MONDE */ 
	/**************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.weather")) {
				
				if (args.length == 0) {
					
					p.getWorld().setStorm(true);
					p.getWorld().setThundering(true);
					p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit la météo étant un temps pluvieux et orageux dans votre monde");
					
				} else if (args.length != 0) {
					
					p.sendMessage(main.prefix + ChatColor.RED + "Essayez /thunder sans arguments");
				}
				
			} else {
				
				p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !");
			}
		
		} else {
			
			sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez étre en jeux !");
		}
		
		return false;
	}
	
	/**************************************************/
	/* PARTIE COMMANDE POUR LE TEMPS ORAGEUX DU MONDE */ 
	/**************************************************/
	
}
