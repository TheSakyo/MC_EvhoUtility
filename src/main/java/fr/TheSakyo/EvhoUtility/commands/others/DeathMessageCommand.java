package fr.TheSakyo.EvhoUtility.commands.others;

import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class DeathMessageCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public DeathMessageCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	// Préfix Annonce //
	String AnnonceTitle = ChatFormatting.WHITE + "[" + ChatFormatting.GOLD + "Annonce" + ChatFormatting.WHITE + "]" + ChatFormatting.RESET + " ";
	// Préfix Annonce //
	
	
	
	/**************************************************************/
	/* PARTIE COMMANDE POUR ACTIVER/DÉSACTIVER LE MESSAGE DE MORT */
	/**************************************************************/	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(!p.hasPermission("evhoutility.deathmessage")) {
    			
	    		 p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
	    		 return true;
			}
			
		}

		/****************************************************************/
		/****************************************************************/

		if(args.length == 1) {

			if(args[0].equalsIgnoreCase("on")) {

				if(ConfigFile.getString(main.DeathOrAchievementConfig, "DeathMessage").equalsIgnoreCase("on")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Les message de mort de Minecraft sont déja activer");
					return true;
				}

				/*********************************************/

				ConfigFile.set(main.DeathOrAchievementConfig, "DeathMessage", "on");
				ConfigFile.saveConfig(main.DeathOrAchievementConfig);

				/*********************************************/

				if(sender instanceof Player p) {

					Bukkit.getServer().broadcast(CustomMethod.StringToComponent(AnnonceTitle + ChatFormatting.YELLOW + p.getName() + ChatFormatting.GREEN + " a activer les messages de mort de Minecraft !"));
					return true;
				}

				/*********************************************/

				Bukkit.getServer().broadcast(CustomMethod.StringToComponent(AnnonceTitle + ChatFormatting.YELLOW + "La Console" + ChatFormatting.GREEN + " a activer les messages de mort de Minecraft !"));

			} else if(args[0].equalsIgnoreCase("off")) {

				if(ConfigFile.getString(main.DeathOrAchievementConfig, "DeathMessage").equalsIgnoreCase("off")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Les message de mort de Minecraft sont déja désactiver");
					return true;
				}

				/*********************************************/

				ConfigFile.set(main.DeathOrAchievementConfig, "DeathMessage", "off");
				ConfigFile.saveConfig(main.DeathOrAchievementConfig);

				/*********************************************/

				if(sender instanceof Player p) {

					Bukkit.getServer().broadcast(CustomMethod.StringToComponent(AnnonceTitle + ChatFormatting.YELLOW + p.getName() + ChatFormatting.RED + " a désactiver les messages de mort de Minecraft !"));

					return true;
				}

				/*********************************************/

				Bukkit.getServer().broadcast(CustomMethod.StringToComponent(AnnonceTitle + ChatFormatting.YELLOW + "La Console" + ChatFormatting.RED + " a désactiver les messages de mort de Minecraft !"));

			} else Bukkit.getServer().dispatchCommand(sender, "deathmessage");

		} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /deathmessage <on/off> ou /dm <on/off>");
		
		return false;
	}
   /**************************************************************/
   /* PARTIE COMMANDE POUR ACTIVER/DÉSACTIVER LE MESSAGE DE MORT */
   /**************************************************************/

}
