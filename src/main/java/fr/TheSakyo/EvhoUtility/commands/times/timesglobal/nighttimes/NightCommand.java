package fr.TheSakyo.EvhoUtility.commands.times.timesglobal.nighttimes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class NightCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public NightCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	/*****************************************************/
	/* PARTIE COMMANDE POUR METTRE LA NUIT DANS LE MONDE */ 
	/*****************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p){

			if(p.hasPermission("evhoutility.time")) {
				
				if(args.length == 0) {
					
					p.getWorld().setTime(13000);
					p.sendMessage(main.prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous avez définit le temps étant la nuit dans votre monde");
					
				} else if(args.length != 0) {
					
					p.sendMessage(main.prefix + ChatColor.RED + "Essayez /night sans arguments");
				}
				
			} else {
				
				p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !");
			}
		
		} else {
			
			sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux !");
		}
		
		return false;
	}
	
	/*****************************************************/
	/* PARTIE COMMANDE POUR METTRE LA NUIT DANS LE MONDE */ 
	/*****************************************************/

}
