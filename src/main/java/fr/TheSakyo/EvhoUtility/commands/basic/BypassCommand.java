package fr.TheSakyo.EvhoUtility.commands.basic;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class BypassCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public BypassCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */


	/********************************************/
	/* PARTIE COMMANDE POUR RECHARGER LE PLUGIN */
    /********************************************/

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

		if(sender instanceof Player p) {

            if(!p.hasPermission("evhoutility.bypass")) { p.sendMessage(ChatFormatting.RED + "Vous n'avez pas la permission requise !"); }

            else {

                if(args.length >= 1) {

                    if(args.length == 1) {

                        Player target = Bukkit.getServer().getPlayer(args[0]);

                        if(target != null) {

                            if(CustomMethod.hasByPassPerm(target)) {

                                p.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass désactivé pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GREEN + " !");
                                target.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass désactivé par " + ChatFormatting.YELLOW + p.getName() + ChatFormatting.GREEN + " !");

                                target.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", false);

                            } else {

                                p.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass activé pour " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GREEN + " !");
                                target.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass activé par " + ChatFormatting.YELLOW + p.getName() + ChatFormatting.GREEN + " !");

                                target.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", true);
                            }

                        } else { p.sendMessage(main.prefix + ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "Erreur : " + ChatFormatting.RED + "Le joueur est introuvable !"); }

                    } else { p.sendMessage(main.prefix + ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "Erreur : " + ChatFormatting.RED + "Essayez /bypass" + ChatFormatting.GREEN + " <player>"); }

                } else {

                    if(CustomMethod.hasByPassPerm(p)) {

                        p.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass désactivé");
                        p.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", false);

                    } else {

                        p.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass activé");
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

                        sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass désactivé pour " + ChatFormatting.GOLD + target.getName());
                        target.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass désactivé par " + ChatFormatting.YELLOW + "La Console" + ChatFormatting.GREEN + " !");

                        target.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", false);

                    } else {

                        sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass activé pour " + ChatFormatting.GOLD + target.getName());
                        target.sendMessage(main.prefix + ChatFormatting.GREEN + "Mode Bypass activé par " + ChatFormatting.YELLOW + "La Console" + ChatFormatting.GREEN + " !");

                        target.addAttachment(UtilityMain.pm.getPlugin("EvhoUtility")).setPermission("utility.bypass", true);
                    }

                } else { sender.sendMessage(main.prefix + ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "Erreur : " + ChatFormatting.RED + "Le joueur est introuvable !"); }

            } else { sender.sendMessage(main.prefix + ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "Erreur : " + ChatFormatting.RED + "Essayez /bypass" + ChatFormatting.GREEN + " <player>"); } }

		return false;
	}

	/********************************************/
	/* PARTIE COMMANDE POUR RECHARGER LE PLUGIN */
    /********************************************/

}
