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

public class AchievementsMessageCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public AchievementsMessageCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	// Préfix Annonce //
	String AnnonceTitle = ChatFormatting.WHITE + "[" + ChatFormatting.GOLD + "Annonce" + ChatFormatting.WHITE + "]" + ChatFormatting.RESET + " ";
	// Préfix Annonce //

	
	
	/************************************************************/
	/* PARTIE COMMANDE POUR ACTIVER/DÉSACTIVER LES ACHIEVEMENTS */
	/************************************************************/
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(!p.hasPermission("evhoutility.achievementsmessage")) {
    			
	    		 p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
	    		 return true;
			}
		}

		/****************************************************************/
		/****************************************************************/

		if(args.length == 1) {

			if(args[0].equalsIgnoreCase("on")) {

				if(ConfigFile.getString(main.DeathOrAchievementConfig, "AchievementMessage").equalsIgnoreCase("on")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Les messages 'achievements/advancements' de Minecraft sont déja activer");
					return true;
				}

				/*********************************************/

				ConfigFile.set(main.DeathOrAchievementConfig, "AchievementMessage", "on");
				ConfigFile.saveConfig(main.DeathOrAchievementConfig);

				if(sender instanceof Player p) {

					Bukkit.getServer().broadcast(CustomMethod.StringToComponent(AnnonceTitle + ChatFormatting.YELLOW + p.getName() + ChatFormatting.GREEN + " a activer les messages 'achievements/advancements' de Minecraft !"));
					return true;
				}

				/*********************************************/

				Bukkit.getServer().broadcast(CustomMethod.StringToComponent(AnnonceTitle + ChatFormatting.YELLOW + "La Console" + ChatFormatting.GREEN + " a activer les messages 'achievements/advancements' de Minecraft !"));

			} else if(args[0].equalsIgnoreCase("off")) {

				if(ConfigFile.getString(main.DeathOrAchievementConfig, "AchievementMessage").equalsIgnoreCase("off")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Les messages 'achievements/advancements' de Minecraft sont déja désactiver");
					return true;
				}

				/*********************************************/

				ConfigFile.set(main.DeathOrAchievementConfig, "AchievementMessage", "off");
				ConfigFile.saveConfig(main.DeathOrAchievementConfig);

				/*********************************************/

				if(sender instanceof Player p) {

					Bukkit.getServer().broadcast(CustomMethod.StringToComponent(AnnonceTitle + ChatFormatting.YELLOW + p.getName() + ChatFormatting.RED + " a désactiver les messages 'achievements/advancements' de Minecraft !"));
					return true;
				}

				/*********************************************/

				Bukkit.getServer().broadcast(CustomMethod.StringToComponent(AnnonceTitle + ChatFormatting.YELLOW + "La Console" + ChatFormatting.RED + " a désactiver les messages 'achievements/advancements' de Minecraft !"));

			} else Bukkit.getServer().dispatchCommand(sender, "achievements");

		} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /achievements <on/off>, /advancements <on/off> ou /am <on/off>");

		return false;
	}
   /************************************************************/
   /* PARTIE COMMANDE POUR ACTIVER/DÉSACTIVER LES ACHIEVEMENTS */
   /************************************************************/
}