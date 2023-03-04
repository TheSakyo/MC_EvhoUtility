package fr.TheSakyo.EvhoUtility.commands.basic;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;

public class BypassCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private UtilityMain main;
	public BypassCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */


	/********************************************/
	/* PARTIE COMMANDE POUR RECHARGER LE PLUGIN */
    /********************************************/

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

		if(sender instanceof Player p) {

            if(!p.hasPermission("evhoutility.bypass")) { p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission requise !"); }

            else {

                if(args.length >= 1) {

                    if(args.length == 1) {

                        Player target = Bukkit.getServer().getPlayer(args[0]);

                        if(target != null) {

                            if(CustomMethod.hasByPassPerm(target)) {

                                p.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass désactivé pour " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + " !");
                                target.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass désactivé par " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + " !");

                                target.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", false);

                            } else {

                                p.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass activé pour " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + " !");
                                target.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass activé par " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + " !");

                                target.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", true);
                            }

                        } else { p.sendMessage(main.prefix + ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Erreur : " + ChatColor.RED + "Le joueur est introuvable !"); }

                    } else { p.sendMessage(main.prefix + ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Erreur : " + ChatColor.RED + "Essayez /bypass" + ChatColor.GREEN + " <player>"); }

                } else {

                    if(CustomMethod.hasByPassPerm(p)) {

                        p.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass désactivé");
                        p.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", false);

                    } else {

                        p.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass activé");
                        p.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", true);
                    }
                }
            }
        }
        if(sender instanceof ConsoleCommandSender) {

            if(args.length == 1) {

                Player target = Bukkit.getServer().getPlayer(args[0]);

                if(target != null) {

                    if(CustomMethod.hasByPassPerm(target)) {

                        sender.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass désactivé pour " + ChatColor.GOLD + target.getName());
                        target.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass désactivé par " + ChatColor.YELLOW + "La Console" + ChatColor.GREEN + " !");

                        target.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", false);

                    } else {

                        sender.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass activé pour " + ChatColor.GOLD + target.getName());
                        target.sendMessage(main.prefix + ChatColor.GREEN + "Mode Bypass activé par " + ChatColor.YELLOW + "La Console" + ChatColor.GREEN + " !");

                        target.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", true);
                    }

                } else { sender.sendMessage(main.prefix + ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Erreur : " + ChatColor.RED + "Le joueur est introuvable !"); }

            } else { sender.sendMessage(main.prefix + ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Erreur : " + ChatColor.RED + "Essayez /bypass" + ChatColor.GREEN + " <player>"); } }

		return false;
	}

	/********************************************/
	/* PARTIE COMMANDE POUR RECHARGER LE PLUGIN */
    /********************************************/

}
