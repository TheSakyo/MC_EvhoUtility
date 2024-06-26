package fr.TheSakyo.EvhoUtility.commands.others;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class ResetPlayerCommand implements CommandExecutor {

    /* Récupère la class "Main" */
    private final UtilityMain main;
    public ResetPlayerCommand(UtilityMain pluginMain) { this.main = pluginMain; }
    /* Récupère la class "Main" */


    /*************************************************/
    /* PARTIE COMMANDE POUR RÉINITIALISER UN JOUEUR */
    /***********************************************/

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        if(sender instanceof Player p) {

            if(p.hasPermission("evhoutility.resetplayer")) {

                if(args.length == 0) {

                    PlayerEntity.resetPlayerByName(p.getName());
                    p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous venez de vous réinitialiser entièrement !");

                } else {

                    if(args.length == 1) {

                        if(p.hasPermission("evhoutility.resetplayer.other")) {

                            if(Bukkit.getServer().getPlayer(args[0]) != null) {

                                Player target = Bukkit.getServer().getPlayer(args[0]);

                                /******************************/

                                resetPlayer(p, target);
                                p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez entièrement réinitialiser " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " !");

                            } else if(args[0].equalsIgnoreCase("all")) {

                                for(Player players : Bukkit.getServer().getOnlinePlayers()) { resetPlayer(sender, players); }
                                p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez entièrement réinitialiser " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + "tous" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " les joueurs du serveur !");

                            } else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

                        } else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /resetplayer sans arguments");

                    } else {

                        if(p.hasPermission("evhoutility.resetboard.other"))  p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
                        else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /resetboard sans arguments");
                    }
                }

            } else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");

        } else {

            if(args.length == 0) {

                sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous réinitialiser entièrement, ou essayez de mettre un joueur en premier argument !");

            } else if(args.length == 1) {

                if(Bukkit.getServer().getPlayer(args[0]) != null) {

                    Player target = Bukkit.getServer().getPlayer(args[0]);

                    /******************************/

                    resetPlayer(sender, target);
                    sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez entièrement réinitialiser " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " !");

                } else if(args[0].equalsIgnoreCase("all")) {

                    for(Player players : Bukkit.getServer().getOnlinePlayers()) { resetPlayer(sender, players); }
                    sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez entièrement réinitialiser " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + "tous" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " les joueurs du serveur !");

                } else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

            } else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
        }

        return false;
    }

    /*************************************************/
    /* PARTIE COMMANDE POUR RÉINITIALISER UN JOUEUR */
    /***********************************************/

    /*********************************************/

    /**
     *
     * Méthode raccourcie pour réinitialiser entièrement un joueur.
     *
     * @param sender L'{@link CommandSender Expéditeur} de la commande.
     * @param target Le {@link Player joueur} cible.
     */
    private void resetPlayer(@Nullable CommandSender sender, Player target) {

        String senderName = sender instanceof Player p ? p.getName() : "La Console";

        /******************************/

        try {

            PlayerEntity.resetPlayerByName(target.getName());
            target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous venez de vous faire entièrement réinitialiser par " + ChatFormatting.YELLOW.toString() + ChatFormatting.ITALIC.toString() + senderName + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " !");

        } catch(Exception e) {

            sender.sendMessage(main.prefix + ChatFormatting.RED + "Une erreur est survenue, le joueur n'a pu être entièrement réinitialisé !");
        }
    }
}
