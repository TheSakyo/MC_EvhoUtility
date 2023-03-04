package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.logging.Level;

public class Messenger {

    public static final String HORIZONTAL_BAR = ChatColor.STRIKETHROUGH + "----------------------------------------------------";
    static boolean DEBUG_ENABLED = false;
    private static UtilityMain plugin;

    public static void initialise(UtilityMain plugin) {
        Messenger.plugin = plugin;
    }

    public static void info(String message, Object... args) {
        plugin.getLogger().info(ChatColor.stripColor(String.format(message, args)));
    }

    public static void warn(Throwable e, String message, Object... args) {
        plugin.getLogger().log(Level.WARNING, ChatColor.stripColor(String.format(message, args)), e);
    }

    public static void warn(String message, Object... args) {
        plugin.getLogger().log(Level.WARNING, ChatColor.stripColor(String.format(message, args)));
    }

    /**
     * Ceci formatera tous les codes de couleur de l'esperluette (&),
     * formatera les arguments qui lui sont passés en utilisant {@link String#format(String, Object...)},
     * puis envoie le message à la {@link CommandSender} spécifiée.
     *
     * @param sender Le {@link CommandSender} auquel envoyer le message.
     * @param message Le message à envoyer.
     * @param args Les arguments avec lesquels formater le message.
     */
    public static void send(CommandSender sender, String message, Object... args) {

        Objects.requireNonNull(sender, "sender cannot be null!");
        Objects.requireNonNull(message, "message cannot be null!");

        sender.sendMessage(ColorUtils.format(String.format(message, args)));
    }

    /**
     * This will add the prefix to the message, format any ampersand (&) color codes,
     * format any args passed to it using {@link String#format(String, Object...)},
     * and then send the message to the specified {@link CommandSender}.
     *
     * @param sender  The {@link CommandSender} to send the message to.
     * @param message The message to send.
     * @param prefix  The prefix to the message
     * @param args    The args to format the message with.
     */
    private static void sendWithPrefix(CommandSender sender, String message, String prefix, Object... args) {
        send(sender, prefix + " " + message, args);
    }

    public static void sendNormalMessage(CommandSender sender, String message, Object... args){
        sendWithPrefix(sender, message, "&6[OCM]&r", args);
    }

    private static void sendDebugMessage(CommandSender sender, String message, Object... args){
        sendWithPrefix(sender, message, "&1[Debug]&r", args);
    }

    public static void debug(String message, Throwable throwable) {
        if(DEBUG_ENABLED) plugin.getLogger().log(Level.INFO, message, throwable);
    }

    public static void debug(String message, Object... args) {
        if(DEBUG_ENABLED) info("[DEBUG] " + message, args);
    }

    public static void debug(CommandSender sender, String message, Object... args) {
        if(DEBUG_ENABLED) sendDebugMessage(sender, message, args);
    }
}
