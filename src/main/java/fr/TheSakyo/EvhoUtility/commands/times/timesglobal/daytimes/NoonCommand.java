package fr.TheSakyo.EvhoUtility.commands.times.timesglobal.daytimes;

import net.minecraft.ChatFormatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class NoonCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public NoonCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */

	
	
	/**************************************************/
	/* PARTIE COMMANDE POUR METTRE MIDI DANS LE MONDE */ 
	/**************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.time")) {
				
				if(args.length == 0) {
					
					p.getWorld().setTime(6000);
					p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit le temps étant le midi dans votre monde");
					
				} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /noon sans arguments");

			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux !");
		
		return false;
	}
	
	/**************************************************/
	/* PARTIE COMMANDE POUR METTRE MIDI DANS LE MONDE */ 
	/**************************************************/
}
