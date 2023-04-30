package fr.TheSakyo.EvhoUtility.commands.basic;

import net.minecraft.ChatFormatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ReloadCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public ReloadCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */

	
	/********************************************/
	/* PARTIE COMMANDE POUR RECHARGER LE PLUGIN */ 
    /********************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.reload")) {
				
				if(args.length == 0) {

					main.reloadPlugin(); //Recharge le plugin
					p.sendMessage(main.prefix + ChatFormatting.GREEN + "Le plugin a été rechargé !"); //Message au Joueur
				
				} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /utilityreload ou /utilityrl sans arguments");

			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
			return true;
		}

		if(sender instanceof CommandSender) {
	
			if(args.length == 0) {

				main.reloadPlugin(); //Recharge le plugin
				sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Le plugin a été rechargé !"); //Message au Serveur
			
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /utilityreload ou /utilityrl sans arguments");
			return true;
		}
		return false;
	}
	
	/********************************************/
	/* PARTIE COMMANDE POUR RECHARGER LE PLUGIN */ 
    /********************************************/
	
}
