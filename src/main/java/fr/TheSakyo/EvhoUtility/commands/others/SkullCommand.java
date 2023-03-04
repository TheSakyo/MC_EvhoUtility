package fr.TheSakyo.EvhoUtility.commands.others;

import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;



public class SkullCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private UtilityMain main;
	public SkullCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */

	
	
	
   /****************************************************/
   /* PARTIE COMMANDE POUR OBTENIR LA TÊTE D'UN JOUEUR */
   /****************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.skull")) {

				Inventory pinv = p.getInventory();
				
				if(args.length == 0) { pinv.addItem(Skin.PlayerHead(p, p, null)); }

				else if(args.length != 0) {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.skull.other")) { pinv.addItem(Skin.PlayerHead(p, p, args[0])); }

						else { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /skull"); }
						
					} else {
						
						if(p.hasPermission("evhoutility.ligthning.other")) {
							
							p.sendMessage(main.prefix + ChatColor.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						
						} else { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /skull"); }
					}
				}
				
			} else { p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !"); }

		} else {

		 sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux pour obtenir une tête d'un joueur !");
		}
		return false;
	}
	
	/****************************************************/
	/* PARTIE COMMANDE POUR OBTENIR LA TÊTE D'UN JOUEUR */
	/***************************************************/
}
