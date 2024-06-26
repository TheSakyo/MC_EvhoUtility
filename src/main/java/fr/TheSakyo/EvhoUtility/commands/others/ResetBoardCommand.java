package fr.TheSakyo.EvhoUtility.commands.others;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.managers.ScoreboardManager;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetBoardCommand implements CommandExecutor {

    /* Récupère la class "Main" */
    private final UtilityMain main;
    public ResetBoardCommand(UtilityMain pluginMain) { this.main = pluginMain; }
    /* Récupère la class "Main" */



    /******************************************************************/
    /* PARTIE COMMANDE POUR REMETTRE À ZÉRO LE SCOREBOARD D'UN JOUEUR */
    /******************************************************************/

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        if(sender instanceof Player p) {

            if(p.hasPermission("evhoutility.resetboard")) {

                if(args.length == 0) {

                    ScoreboardManager.makeScoreboard(p, true);
                    p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre 'Scoreboard' a été remis à zéro !");

                } else {

                    if(args.length == 1) {

                        if(p.hasPermission("evhoutility.resetboard.other")) {

                            if(Bukkit.getServer().getPlayer(args[0]) != null) {

                                Player target = Bukkit.getServer().getPlayer(args[0]);

                                /******************************/

                                ScoreboardManager.makeScoreboard(target, true);
                                target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre 'Scoreboard' a été remis à zéro par " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " !");

                            } else if(args[0].equalsIgnoreCase("all")) {

                                for(Player players : Bukkit.getServer().getOnlinePlayers()) {

                                    ScoreboardManager.makeScoreboard(players, true);
                                    players.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre 'Scoreboard' a été remis à zéro par " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + p.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " !");
                                }

                            } else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

                        } else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /resetboard sans arguments");

                    } else {

                        if(p.hasPermission("evhoutility.resetboard.other"))  p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
                        else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /resetboard sans arguments");
                    }
                }

            } else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");

        } else {

            if(args.length == 0) {

                sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour remettre à zéro votre 'Scoreboard', ou essayez de mettre un joueur en premier argument !");

            } else if(args.length == 1) {

                if(Bukkit.getServer().getPlayer(args[0]) != null) {

                    Player target = Bukkit.getServer().getPlayer(args[0]);

                    /******************************/

                    ScoreboardManager.makeScoreboard(target, true);
                    target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre 'Scoreboard' a été remis à zéro par " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " !");

                } else if(args[0].equalsIgnoreCase("all")) {

                    for(Player players : Bukkit.getServer().getOnlinePlayers()) {

                        ScoreboardManager.makeScoreboard(players, true);
                        players.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre 'Scoreboard' a été remis à zéro par " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + "La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " !");
                    }

                } else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

            } else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
        }

        return false;
    }
    /******************************************************************/
    /* PARTIE COMMANDE POUR REMETTRE À ZÉRO LE SCOREBOARD D'UN JOUEUR */
    /******************************************************************/
}
