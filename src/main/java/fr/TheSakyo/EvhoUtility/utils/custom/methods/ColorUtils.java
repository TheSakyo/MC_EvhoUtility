package fr.TheSakyo.EvhoUtility.utils.custom.methods;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.ChatFormatting;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Pattern;
import java.awt.Color;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * The type Color utils.
 */
public class ColorUtils {

                    /* ---------------------------------------------------------------------------------------------------- */

    /*********************************************************/
    /* TOUTE PETITE MÉTHODE POUR TRADUIRE LES CODES COULEURS */
    /*********************************************************/

    /**
     * Remplace tous les caractères ayant un '&' par le caractère de code de couleur Minecraft '§' - {@link ChatColor}.
     *
     * @param msg Message à formater.
     * @return Le message formaté avec les codes couleurs Minecraft {@link ChatColor}.
     */
    public static String format(String msg) { return ChatColor.translateAlternateColorCodes('&', msg); }

    /*********************************************************/
    /* TOUTE PETITE MÉTHODE POUR TRADUIRE LES CODES COULEURS */
    /*********************************************************/

                          /* --------------------------------------------------------- */

    /****************************************************************************************************/
    /* PETITE MÉTHODE POUR RÉCUPÉRER LE DERNIER CODE COULEUR 'CHATCOLOR' DEPUIS UNE CHAÎNE DE CARACTÈRE */
    /***************************************************************************************************/

    /**
     * Il renvoie le dernier code couleur de {@link ChatColor} depuis une chaîne de caractère
     *
     * @param input La chaîne dans laquelle rechercher le dernier code couleur.
     * @param forcedecoration force ou pas la vérification des codes couleurs de décoration.
     *
     * @return Le dernier code couleur de {@link ChatColor} de la chaîne de caractère.
     */
    public static ChatColor getLastChatColorByString(String input, boolean forcedecoration) {

        List<String> colorDecorations = List.of("§k", "§m", "§o", "§l", "§n"); // Listes des codes couleurs de décoration

        // On définit une variable 'lastColors' récupèrant la chaîne de caractère des dernières récupéré par 'ChatColor'.
        String lastColors = ChatColor.getLastColors(format(input));

        // On définit une variable 'getlastColor' qui permettra de récupérer la dernière couleur 'ChatColor' à partir de la chaîne de caractère récupéré 'lastColors'.
        String getlastColor = lastColors;

                          /* --------------------------------------------------------- */

        // On boucle sur la liste des codes couleus de décoration, pour les supprimer de la variable 'getlastColor'
        for(String decoration : colorDecorations) getlastColor = getlastColor.replaceAll(decoration, "");

                          /* --------------------------------------------------------- */

        // Si 'forcedecoration' est vrai, on change la variable 'getLastColor', pour vérifier par la suite directement la variable 'lastColors'
        if(forcedecoration) getlastColor = "";

        // ⬇️ On vérifie le dernier code couleur de 'getLastColor' ⬇️ //
        if(getlastColor.endsWith("§0")) return ChatColor.BLACK;
        else if(getlastColor.endsWith("§2")) return ChatColor.DARK_GREEN;
        else if(getlastColor.endsWith("§4")) return ChatColor.DARK_RED;
        else if(getlastColor.endsWith("§6")) return ChatColor.GOLD;
        else if(getlastColor.endsWith("§8")) return ChatColor.DARK_GRAY;
        else if(getlastColor.endsWith("§a")) return ChatColor.GREEN;
        else if(getlastColor.endsWith("§c")) return ChatColor.RED;
        else if(getlastColor.endsWith("§e")) return ChatColor.YELLOW;
        else if(getlastColor.endsWith("§1")) return ChatColor.DARK_BLUE;
        else if(getlastColor.endsWith("§3")) return ChatColor.DARK_AQUA;
        else if(getlastColor.endsWith("§5")) return ChatColor.DARK_PURPLE;
        else if(getlastColor.endsWith("§7")) return ChatColor.GRAY;
        else if(getlastColor.endsWith("§9")) return ChatColor.BLUE;
        else if(getlastColor.endsWith("§b")) return ChatColor.AQUA;
        else if(getlastColor.endsWith("§d")) return ChatColor.LIGHT_PURPLE;
        else if(getlastColor.endsWith("§f")) return ChatColor.WHITE;
        else if(getlastColor.endsWith("§r")) return ChatColor.RESET;
        else {

            // ⬇️ Si on n'a rien trouvé, On vérifie le dernier code couleur de 'lastColors' qui eux contiennent les codes couleurs de décoration ⬇️ //
            if(lastColors.endsWith("§k")) return ChatColor.MAGIC;
            else if(lastColors.endsWith("§m")) return ChatColor.STRIKETHROUGH;
            else if(lastColors.endsWith("§o")) return ChatColor.ITALIC;
            else if(lastColors.endsWith("§l")) return ChatColor.BOLD;
            else if(lastColors.endsWith("§n")) return ChatColor.UNDERLINE;
            else return ChatColor.RESET;
            // ⬆️ Si on n'a rien trouvé, On vérifie le dernier code couleur de 'lastColors' qui eux contiennent les codes couleurs de décoration ⬆️ //
        }
        // ⬆️ On vérifie le dernier code couleur de 'getLastColor' ⬆️ //
    }

    /****************************************************************************************************/
    /* PETITE MÉTHODE POUR RÉCUPÉRER LE DERNIER CODE COULEUR 'CHATCOLOR' DEPUIS UNE CHAÎNE DE CARACTÈRE */
    /***************************************************************************************************/

                          /* --------------------------------------------------------- */

    /****************************************************************************************************/
    /* PETITE MÉTHODE POUR RÉCUPÉRER LE DERNIER CODE COULEUR 'CHATCOLOR' DEPUIS UNE CHAÎNE DE CARACTÈRE */
    /***************************************************************************************************/

    /**
     * Il renvoie le dernier code couleur de {@link ChatColor} depuis une chaîne de caractère
     *
     * @param input La chaîne dans laquelle rechercher le dernier code couleur.
     *
     * @return Le dernier code couleur de {@link ChatColor} de la chaîne de caractère.
     */
    public static ChatColor getLastChatColorByString(String input) { return getLastChatColorByString(input, false); }

    /****************************************************************************************************/
    /* PETITE MÉTHODE POUR RÉCUPÉRER LE DERNIER CODE COULEUR 'CHATCOLOR' DEPUIS UNE CHAÎNE DE CARACTÈRE */
    /***************************************************************************************************/

            /* ---------------------------------------------------------------------------------------------------- */
            /* ---------------------------------------------------------------------------------------------------- */

    /********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'CHATCOLOR' PAR LES COULEURS 'NamedTextColor' */
    /********************************************************************************************/

    /**
     * Convertie les couleurs de {@link ChatColor} par des couleurs {@link NamedTextColor}.
     *
     * @param color La {@link ChatColor couleur} à convertir.
     * @return Une énumération de {@link NamedTextColor}.
     */
    public static NamedTextColor convertNamedTextColor(ChatColor color) {

        return switch(color) {

            case WHITE -> NamedTextColor.WHITE;
            case BLACK -> NamedTextColor.BLACK;
            case GRAY -> NamedTextColor.GRAY;
            case AQUA -> NamedTextColor.AQUA;
            case RED -> NamedTextColor.RED;
            case BLUE -> NamedTextColor.BLUE;
            case GREEN -> NamedTextColor.GREEN;
            case YELLOW -> NamedTextColor.YELLOW;
            case GOLD -> NamedTextColor.GOLD;
            case LIGHT_PURPLE -> NamedTextColor.LIGHT_PURPLE;
            case DARK_AQUA -> NamedTextColor.DARK_AQUA;
            case DARK_RED -> NamedTextColor.DARK_RED;
            case DARK_BLUE -> NamedTextColor.DARK_BLUE;
            case DARK_GRAY -> NamedTextColor.DARK_GRAY;
            case DARK_GREEN -> NamedTextColor.DARK_GREEN;
            case DARK_PURPLE -> NamedTextColor.DARK_PURPLE;
            default -> null;
        };
    }

    /********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'CHATCOLOR' PAR LES COULEURS 'NamedTextColor' */
    /********************************************************************************************/

                          /* --------------------------------------------------------- */

    /********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'CHATCOLOR' PAR LES COULEURS 'ChatFormatting' */
    /********************************************************************************************/

    /**
     * Convertie les couleurs de {@link ChatColor} par des couleurs {@link ChatFormatting}.
     *
     * @param color La {@link ChatColor couleur} à convertir.
     * @return Une énumération de {@link ChatFormatting}.
     */
    public static ChatFormatting convertChatFormattingColor(ChatColor color) {

        return switch(color) {

            case WHITE -> ChatFormatting.WHITE;
            case BLACK -> ChatFormatting.BLACK;
            case GRAY -> ChatFormatting.GRAY;
            case AQUA -> ChatFormatting.AQUA;
            case RED -> ChatFormatting.RED;
            case BLUE -> ChatFormatting.BLUE;
            case GREEN -> ChatFormatting.GREEN;
            case YELLOW -> ChatFormatting.YELLOW;
            case GOLD -> ChatFormatting.GOLD;
            case LIGHT_PURPLE -> ChatFormatting.LIGHT_PURPLE;
            case DARK_AQUA -> ChatFormatting.DARK_AQUA;
            case DARK_RED -> ChatFormatting.DARK_RED;
            case DARK_BLUE -> ChatFormatting.DARK_BLUE;
            case DARK_GRAY -> ChatFormatting.DARK_GRAY;
            case DARK_GREEN -> ChatFormatting.DARK_GREEN;
            case DARK_PURPLE -> ChatFormatting.DARK_PURPLE;
            case MAGIC -> ChatFormatting.OBFUSCATED;
            case STRIKETHROUGH -> ChatFormatting.STRIKETHROUGH;
            case UNDERLINE -> ChatFormatting.UNDERLINE;
            case BOLD -> ChatFormatting.BOLD;
            case ITALIC -> ChatFormatting.ITALIC;
            default -> ChatFormatting.RESET;
        };
    }

    /********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'CHATCOLOR' PAR LES COULEURS 'ChatFormatting' */
    /********************************************************************************************/

                          /* --------------------------------------------------------- */

    /**************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES DÉCORATIONS 'CHATCOLOR' PAR LES DÉCORATIONS 'TextDecoration' */
    /**************************************************************************************************/

    /**
     * Convertie les couleurs de {@link ChatColor} par des décorations de {@link TextDecoration}.
     *
     * @param color La {@link ChatColor couleur} à convertir.
     * @return Une énumération de {@link TextDecoration}.
     */
    public static TextDecoration convertTextDecoration(ChatColor color) {

        return switch(color) {

            case MAGIC -> TextDecoration.OBFUSCATED;
            case STRIKETHROUGH -> TextDecoration.STRIKETHROUGH;
            case UNDERLINE -> TextDecoration.UNDERLINED;
            case BOLD -> TextDecoration.BOLD;
            case ITALIC -> TextDecoration.ITALIC;
            default -> null;
        };
    }

    /**************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES DÉCORATIONS 'CHATCOLOR' PAR LES DÉCORATIONS 'TextDecoration' */
    /**************************************************************************************************/

            /* ---------------------------------------------------------------------------------------------------- */
            /* ---------------------------------------------------------------------------------------------------- */

    /********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'NamedTextColor' PAR LES COULEURS 'CHATCOLOR' */
    /********************************************************************************************/

    /**
     * Convertie les couleurs de {@link NamedTextColor} par des couleurs {@link ChatColor}.
     *
     * @param color La {@link NamedTextColor couleur} à convertir.
     * @return Une énumération de {@link ChatColor}.
     */
    public static ChatColor NamedTextColorToChatColor(NamedTextColor color) {

        if(color == NamedTextColor.WHITE) return ChatColor.WHITE;
        else if(color == NamedTextColor.BLACK) return ChatColor.BLACK;
        else if(color == NamedTextColor.GRAY) return ChatColor.GRAY;
        else if(color == NamedTextColor.AQUA) return ChatColor.AQUA;
        else if(color == NamedTextColor.RED) return ChatColor.RED;
        else if(color == NamedTextColor.BLUE) return ChatColor.BLUE;
        else if(color == NamedTextColor.GREEN) return ChatColor.GREEN;
        else if(color == NamedTextColor.YELLOW) return ChatColor.YELLOW;
        else if(color == NamedTextColor.GOLD) return ChatColor.GOLD;
        else if(color == NamedTextColor.LIGHT_PURPLE) return ChatColor.LIGHT_PURPLE;
        else if(color == NamedTextColor.DARK_AQUA) return ChatColor.DARK_AQUA;
        else if(color == NamedTextColor.DARK_RED) return ChatColor.DARK_RED;
        else if(color == NamedTextColor.DARK_BLUE) return ChatColor.DARK_BLUE;
        else if(color == NamedTextColor.DARK_GRAY) return ChatColor.DARK_GRAY;
        else if(color == NamedTextColor.DARK_GREEN) return ChatColor.DARK_GREEN;
        else if(color == NamedTextColor.DARK_PURPLE) return ChatColor.DARK_PURPLE;
        else return ChatColor.RESET;
    }

    /********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'NamedTextColor' PAR LES COULEURS 'CHATCOLOR' */
    /********************************************************************************************/

                          /* --------------------------------------------------------- */

    /********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'ChatFormatting' PAR LES COULEURS 'CHATCOLOR' */
    /********************************************************************************************/

    /**
     * Convertie les couleurs de {@link ChatFormatting} par des couleurs {@link ChatColor}.
     *
     * @param color La {@link ChatFormatting couleur} à convertir.
     * @return Une énumération de {@link ChatColor}.
     */
    public static ChatColor ChatFormattingColorToChatColor(ChatFormatting color) {

        if(color == ChatFormatting.WHITE) return ChatColor.WHITE;
        else if(color == ChatFormatting.BLACK) return ChatColor.BLACK;
        else if(color == ChatFormatting.GRAY) return ChatColor.GRAY;
        else if(color == ChatFormatting.AQUA) return ChatColor.AQUA;
        else if(color == ChatFormatting.RED) return ChatColor.RED;
        else if(color == ChatFormatting.BLUE) return ChatColor.BLUE;
        else if(color == ChatFormatting.GREEN) return ChatColor.GREEN;
        else if(color == ChatFormatting.YELLOW) return ChatColor.YELLOW;
        else if(color == ChatFormatting.GOLD) return ChatColor.GOLD;
        else if(color == ChatFormatting.LIGHT_PURPLE) return ChatColor.LIGHT_PURPLE;
        else if(color == ChatFormatting.DARK_AQUA) return ChatColor.DARK_AQUA;
        else if(color == ChatFormatting.DARK_RED) return ChatColor.DARK_RED;
        else if(color == ChatFormatting.DARK_BLUE) return ChatColor.DARK_BLUE;
        else if(color == ChatFormatting.DARK_GRAY) return ChatColor.DARK_GRAY;
        else if(color == ChatFormatting.DARK_GREEN) return ChatColor.DARK_GREEN;
        else if(color == ChatFormatting.DARK_PURPLE) return ChatColor.DARK_PURPLE;
        else if(color == ChatFormatting.OBFUSCATED) return ChatColor.MAGIC;
        else if(color == ChatFormatting.STRIKETHROUGH) return ChatColor.STRIKETHROUGH;
        else if(color == ChatFormatting.UNDERLINE) return ChatColor.UNDERLINE;
        else if(color == ChatFormatting.BOLD) return ChatColor.BOLD;
        else if(color == ChatFormatting.ITALIC) return ChatColor.ITALIC;
        else return ChatColor.RESET;
    }

    /********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'ChatFormatting' PAR LES COULEURS 'CHATCOLOR' */
    /********************************************************************************************/

                          /* --------------------------------------------------------- */

    /**************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES DÉCORATIONS 'TextDecoration' PAR LES DÉCORATIONS 'CHATCOLOR' */
    /**************************************************************************************************/

    /**
     * Convertie les décorations de {@link TextDecoration} par des couleurs {@link ChatColor}.
     *
     * @param color La {@link TextDecoration décoration} à convertir.
     * @return Une énumération de {@link ChatColor}.
     */
    public static ChatColor TextDecorationToChatColor(TextDecoration color) {

        return switch(color) {

            case OBFUSCATED -> ChatColor.MAGIC;
            case STRIKETHROUGH -> ChatColor.STRIKETHROUGH;
            case UNDERLINED -> ChatColor.UNDERLINE;
            case BOLD -> ChatColor.BOLD;
            case ITALIC -> ChatColor.ITALIC;
        };
    }

    /**************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES DÉCORATIONS 'TextDecoration' PAR LES DÉCORATIONS 'CHATCOLOR' */
    /**************************************************************************************************/

            /* ---------------------------------------------------------------------------------------------------- */
            /* ---------------------------------------------------------------------------------------------------- */

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}"); // Variable récupérant un patronage de couleur

    /**
     * Obtient la couleur la plus courante à partir d'une {@link Map}.
     *
     * @param map La {@link Map} en question
     *
     * @return La couleur la plus courante
     */
    public static Color getMostCommonColour(Map map) {

        List<?> list = new LinkedList(map.entrySet());
        if(list.size() == 0) return null;

        list.sort(new Comparator() { public int compare(Object o1, Object o2) { return ((Comparable)((Map.Entry)o1).getValue()).compareTo(((Map.Entry)o2).getValue()); } });
        Map.Entry me = (Map.Entry)list.get(list.size() - 1);

        int[] rgb = getRGB(((Integer)me.getKey()).intValue());
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Obtient rgb le RGB à partir d'un pixel.
     *
     * @param pixel le pixel en question
     *
     * @return le RGB obtenu (en int[])
     */
    public static int[] getRGB(int pixel) {

        int alpha = pixel >> 24 & 0xFF;
        int red = pixel >> 16 & 0xFF;
        int green = pixel >> 8 & 0xFF;
        int blue = pixel & 0xFF;
        return new int[] { red, green, blue };
    }

    /**
     * Obtient la couleur à partir d'un RGB.
     *
     * @param rgb Le RGB (en int[])
     *
     * @return La couleur correspondante
     */
    public static Color getColorFromRGB(int[] rgb) { return new Color(rgb[0], rgb[1], rgb[2]); }

    /**
     * Obtient le format de la chaîne de couleur hexagonale à partir d'un message.
     *
     * @param msg Le message en question
     *
     * @return le format de la chaîne de couleur hexagonale à partir du message
     */
    public static String formatHexColor(String msg) {

        if(msg == null) return null;

        Matcher match = pattern.matcher(msg);
        while(match.find()) {

            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, "" + net.md_5.bungee.api.ChatColor.of(color));
            match = pattern.matcher(msg);
        }

        return msg;
    }

    /**
     * Vérifie si le RGB en question correspond à une couleur grise.
     *
     * @param rgbArr Le RGB (en int[])
     *
     * @return Une valeur Booléenne
     */
    public static boolean isGray(int[] rgbArr) {

        if(rgbArr[0] < 150 && rgbArr[1] < 150 && rgbArr[2] < 150) return true;
        if(rgbArr[0] > 220 && rgbArr[1] > 200) return true;

        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        int tolerance = 10;
        if((rgDiff > tolerance || rgDiff < -tolerance) && (rbDiff > tolerance || rbDiff < -tolerance)) return false;

        return true;
    }

                    /* ---------------------------------------------------------------------------------------------------- */
}
