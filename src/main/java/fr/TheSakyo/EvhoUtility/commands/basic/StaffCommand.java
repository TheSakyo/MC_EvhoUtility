package fr.TheSakyo.EvhoUtility.commands.basic;

import net.minecraft.ChatFormatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.utils.sanctions.SanctionUtils;

public class StaffCommand implements CommandExecutor {

    /**************************************************/
	/* PARTIE COMMANDE POUR AFFICHER LE MENU DE STAFF */ 
    /**************************************************/
    
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {	
    	
    	if(sender instanceof Player p) {

			if(args.length == 0) {
    			
    			if(sender.hasPermission("evhoutility.staff")) p.openInventory(SanctionUtils.sanctionInv);
    			else { sender.sendMessage(SanctionUtils.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !"); }
    			
    		} else sender.sendMessage(SanctionUtils.prefix + ChatFormatting.RED + "Essayez /staff sans arguments");
    	} 
    	
    	if(sender instanceof ConsoleCommandSender) sender.sendMessage(SanctionUtils.prefix + ChatFormatting.RED + "Vous devez être en jeux pour utiliser le Menu !");
    	return false;
    }
	
	/**************************************************/
	/* PARTIE COMMANDE POUR AFFICHER LE MENU DE STAFF */ 
    /**************************************************/
}
