package fr.TheSakyo.EvhoUtility.commands.others;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class AFKKickCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private UtilityMain main;
	public AFKKickCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	/**************************************************/
	/* PARTIE COMMANDE POUR METTRE LE JOUEUR EN "AFK" */ 
    /**************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.AFK")) {
				
				if(args.length == 0) {

					if(main.time.containsKey(p.getUniqueId())) {
						if(main.time.get(p.getUniqueId()) >= 900) { main.afk.runAFK(p.getUniqueId(), Boolean.FALSE, 0); return true; } //Annule le joueur étaht 'AFK'
					}
					main.afk.runAFK(p.getUniqueId(), Boolean.TRUE, 900); //Définit le joueur comme 'AFK'

				} else if(args.length != 0) { p.sendMessage(main.prefix + ChatColor.RED + "Essayez /AFK sans arguments"); }
				
			} else { p.sendMessage(main.prefix + ChatColor.RED + "Vous n'avez pas les permissions requises !"); }
		
		} else { sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux !"); }

		return false;
	}
	
	/**************************************************/
	/* PARTIE COMMANDE POUR METTRE LE JOUEUR EN "AFK" */ 
    /**************************************************/
}
