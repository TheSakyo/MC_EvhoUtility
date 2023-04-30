package fr.TheSakyo.EvhoUtility.commands.weathers.weathersglobal;

import net.minecraft.ChatFormatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ThunderCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public ThunderCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	/**************************************************/
	/* PARTIE COMMANDE POUR LE TEMPS ORAGEUX DU MONDE */ 
	/**************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.weather")) {
				
				if(args.length == 0) {
					
					p.getWorld().setStorm(true);
					p.getWorld().setThundering(true);
					p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez définit la météo étant un temps pluvieux et orageux dans votre monde");
					
				} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /thunder sans arguments");
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux !");
		
		return false;
	}
	
	/**************************************************/
	/* PARTIE COMMANDE POUR LE TEMPS ORAGEUX DU MONDE */ 
	/**************************************************/
	
}
