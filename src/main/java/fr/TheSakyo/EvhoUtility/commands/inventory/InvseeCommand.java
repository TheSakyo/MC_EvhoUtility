package fr.TheSakyo.EvhoUtility.commands.inventory;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class InvseeCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public InvseeCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */

	
	/******************************************************/
	/* PARTIE COMMANDE POUR VOIR L'INVENTAIRE D'UN JOUEUR */ 
    /******************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.invsee")) {
				
				if(args.length == 0) {
					
				   p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /invsee <player>");
				   
				} else if(args.length == 1) {
					
					if(Bukkit.getServer().getPlayer(args[0]) != null) {
						
						Player target = Bukkit.getServer().getPlayer(args[0]);
						
						if(target.getName().equalsIgnoreCase(p.getName())) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous pouvez voir votre propre inventaire autrement qu'avec cette commande !");
							return true;
						}
						
						p.openInventory(target.getInventory());
						main.invUpdate.updateInventory(p, ChatFormatting.WHITE + "Inventaire de " + ChatFormatting.GOLD + target.getName());
					
					} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
					
				} else p.performCommand("invsee");
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");

		} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour voir l'inventaire d'un joueur !");
		
		return false;
	}
	
	/******************************************************/
	/* PARTIE COMMANDE POUR VOIR L'INVENTAIRE D'UN JOUEUR */ 
    /******************************************************/

}
