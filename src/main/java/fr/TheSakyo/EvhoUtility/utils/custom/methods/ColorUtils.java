package fr.TheSakyo.EvhoUtility.utils.custom.methods;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.awt.Color;
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
     * Remplace tous les caractères ayant un '&' par le caractère de code de couleur Minecraft '§' - {@link ChatFormatting}.
     *
     * @param msg Message à formater.
     * @return Le message formaté avec les codes couleurs Minecraft {@link ChatFormatting}.
     */
    public static String format(String msg) {

        Preconditions.checkArgument(msg != null, "Cannot translate null text");
        char[] b = msg.toCharArray();

        /********************************************/

        for(int i = 0; i < b.length - 1; ++i) {

            if(b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        /********************************************/

        return new String(b);
    }

    /*********************************************************/
    /* TOUTE PETITE MÉTHODE POUR TRADUIRE LES CODES COULEURS */
    /*********************************************************/

                          /* --------------------------------------------------------- */

    /****************************************************************************************************/
    /* PETITE MÉTHODE POUR RÉCUPÉRER LE DERNIER CODE COULEUR 'CHATFORMATTING' DEPUIS UNE CHAÎNE DE CARACTÈRE */
    /***************************************************************************************************/

    /**
     * Il renvoie le dernier code couleur de {@link ChatFormatting} depuis une chaîne de caractère
     *
     * @param input La chaîne dans laquelle rechercher le dernier code couleur.
     * @param forceDecoration force ou pas la vérification des codes couleurs de décoration.
     *
     * @return Le dernier code couleur de {@link ChatFormatting} de la chaîne de caractère.
     */
    public static ChatFormatting getLastChatFormattingByString(String input, boolean forceDecoration) {

        List<String> colorDecorations = List.of("§k", "§m", "§o", "§l", "§n"); // Listes des codes couleurs de décoration

        // On définit une variable 'lastColors' récupèrent la chaîne de caractère des dernières récupéré par 'ChatFormatting'.
        String lastColors = getLastColors(format(input));

                          /* --------------------------------------------------------- */

        // On boucle sur la liste des codes couleurs de décoration, pour les supprimer de la variable 'lastColors'
        for(String decoration : colorDecorations) lastColors = lastColors.replaceAll(decoration, "");

                          /* --------------------------------------------------------- */

        // Si 'forceDecoration' est vrai, on change la variable 'getLastColor', pour vérifier par la suite directement la variable 'lastColors'
        if(forceDecoration) lastColors = "";

        // ⬇️ On vérifie le dernier code couleur de 'getLastColor' ⬇️ //
        if(lastColors.endsWith("§0")) return ChatFormatting.BLACK;
        else if(lastColors.endsWith("§2")) return ChatFormatting.DARK_GREEN;
        else if(lastColors.endsWith("§4")) return ChatFormatting.DARK_RED;
        else if(lastColors.endsWith("§6")) return ChatFormatting.GOLD;
        else if(lastColors.endsWith("§8")) return ChatFormatting.DARK_GRAY;
        else if(lastColors.endsWith("§a")) return ChatFormatting.GREEN;
        else if(lastColors.endsWith("§c")) return ChatFormatting.RED;
        else if(lastColors.endsWith("§e")) return ChatFormatting.YELLOW;
        else if(lastColors.endsWith("§1")) return ChatFormatting.DARK_BLUE;
        else if(lastColors.endsWith("§3")) return ChatFormatting.DARK_AQUA;
        else if(lastColors.endsWith("§5")) return ChatFormatting.DARK_PURPLE;
        else if(lastColors.endsWith("§7")) return ChatFormatting.GRAY;
        else if(lastColors.endsWith("§9")) return ChatFormatting.BLUE;
        else if(lastColors.endsWith("§b")) return ChatFormatting.AQUA;
        else if(lastColors.endsWith("§d")) return ChatFormatting.LIGHT_PURPLE;
        else if(lastColors.endsWith("§f")) return ChatFormatting.WHITE;
        else if(lastColors.endsWith("§r")) return ChatFormatting.RESET;
        else {

            // ⬇️ Si on n'a rien trouvé, On vérifie le dernier code couleur de 'lastColors' qui eux contiennent les codes couleurs de décoration ⬇️ //
            if(lastColors.endsWith("§k")) return ChatFormatting.OBFUSCATED;
            else if(lastColors.endsWith("§m")) return ChatFormatting.STRIKETHROUGH;
            else if(lastColors.endsWith("§o")) return ChatFormatting.ITALIC;
            else if(lastColors.endsWith("§l")) return ChatFormatting.BOLD;
            else if(lastColors.endsWith("§n")) return ChatFormatting.UNDERLINE;
            else return ChatFormatting.RESET;
            // ⬆️ Si on n'a rien trouvé, On vérifie le dernier code couleur de 'lastColors' qui eux contiennent les codes couleurs de décoration ⬆️ //
        }
        // ⬆️ On vérifie le dernier code couleur de 'getLastColor' ⬆️ //
    }

    /****************************************************************************************************/
    /* PETITE MÉTHODE POUR RÉCUPÉRER LE DERNIER CODE COULEUR 'CHATFORMATTING' DEPUIS UNE CHAÎNE DE CARACTÈRE */
    /***************************************************************************************************/

                          /* --------------------------------------------------------- */

    /****************************************************************************************************/
    /* PETITE MÉTHODE POUR RÉCUPÉRER LE DERNIER CODE COULEUR 'CHATFORMATTING' DEPUIS UNE CHAÎNE DE CARACTÈRE */
    /***************************************************************************************************/

    /**
     * Il renvoie le dernier code couleur de {@link ChatFormatting} depuis une chaîne de caractère
     *
     * @param input La chaîne dans laquelle rechercher le dernier code couleur.
     *
     * @return Le dernier code couleur de {@link ChatFormatting} de la chaîne de caractère.
     */
    public static ChatFormatting getLastChatFormattingByString(String input) { return getLastChatFormattingByString(input, false); }

    /****************************************************************************************************/
    /* PETITE MÉTHODE POUR RÉCUPÉRER LE DERNIER CODE COULEUR 'CHATFORMATTING' DEPUIS UNE CHAÎNE DE CARACTÈRE */
    /***************************************************************************************************/

            /* ---------------------------------------------------------------------------------------------------- */
            /* ---------------------------------------------------------------------------------------------------- */

    /*************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'CHATFORMATTING' PAR LES COULEURS 'NamedTextColor' */
    /************************************************************************************************/

    /**
     * Convertie les couleurs de {@link ChatFormatting} par des couleurs {@link NamedTextColor}.
     *
     * @param color La {@link ChatFormatting couleur} à convertir.
     * @return Une énumération de {@link NamedTextColor}.
     */
    public static NamedTextColor convertNamedTextColor(ChatFormatting color) {

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

    /*************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'CHATFORMATTING' PAR LES COULEURS 'NamedTextColor' */
    /************************************************************************************************/

                          /* --------------------------------------------------------- */

    /*******************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES DÉCORATIONS 'CHATFORMATTING' PAR LES DÉCORATIONS 'TextDecoration' */
    /*******************************************************************************************************/

    /**
     * Convertie les couleurs de {@link ChatFormatting} par des décorations de {@link TextDecoration}.
     *
     * @param color La {@link ChatFormatting couleur} à convertir.
     * @return Une énumération de {@link TextDecoration}.
     */
    public static TextDecoration convertTextDecoration(ChatFormatting color) {

        return switch(color) {

            case OBFUSCATED -> TextDecoration.OBFUSCATED;
            case STRIKETHROUGH -> TextDecoration.STRIKETHROUGH;
            case UNDERLINE -> TextDecoration.UNDERLINED;
            case BOLD -> TextDecoration.BOLD;
            case ITALIC -> TextDecoration.ITALIC;
            default -> null;
        };
    }

    /*******************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES DÉCORATIONS 'CHATFORMATTING' PAR LES DÉCORATIONS 'TextDecoration' */
    /*******************************************************************************************************/

            /* ---------------------------------------------------------------------------------------------------- */
            /* ---------------------------------------------------------------------------------------------------- */

    /**************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'NamedTextColor' PAR LES COULEURS 'CHATFORMATTING' */
    /************************************************************************************************/

    /**
     * Convertie les couleurs de {@link NamedTextColor} par des couleurs {@link ChatFormatting}.
     *
     * @param color La {@link NamedTextColor couleur} à convertir.
     * @return Une énumération de {@link ChatFormatting}.
     */
    public static ChatFormatting NamedTextColorToChatFormatting(NamedTextColor color) {

        if(color == NamedTextColor.WHITE) return ChatFormatting.WHITE;
        else if(color == NamedTextColor.BLACK) return ChatFormatting.BLACK;
        else if(color == NamedTextColor.GRAY) return ChatFormatting.GRAY;
        else if(color == NamedTextColor.AQUA) return ChatFormatting.AQUA;
        else if(color == NamedTextColor.RED) return ChatFormatting.RED;
        else if(color == NamedTextColor.BLUE) return ChatFormatting.BLUE;
        else if(color == NamedTextColor.GREEN) return ChatFormatting.GREEN;
        else if(color == NamedTextColor.YELLOW) return ChatFormatting.YELLOW;
        else if(color == NamedTextColor.GOLD) return ChatFormatting.GOLD;
        else if(color == NamedTextColor.LIGHT_PURPLE) return ChatFormatting.LIGHT_PURPLE;
        else if(color == NamedTextColor.DARK_AQUA) return ChatFormatting.DARK_AQUA;
        else if(color == NamedTextColor.DARK_RED) return ChatFormatting.DARK_RED;
        else if(color == NamedTextColor.DARK_BLUE) return ChatFormatting.DARK_BLUE;
        else if(color == NamedTextColor.DARK_GRAY) return ChatFormatting.DARK_GRAY;
        else if(color == NamedTextColor.DARK_GREEN) return ChatFormatting.DARK_GREEN;
        else if(color == NamedTextColor.DARK_PURPLE) return ChatFormatting.DARK_PURPLE;
        else return ChatFormatting.RESET;
    }

    /**************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES COULEURS 'NamedTextColor' PAR LES COULEURS 'CHATFORMATTING' */
    /************************************************************************************************/

                          /* --------------------------------------------------------- */

    /********************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES DÉCORATIONS 'TextDecoration' PAR LES DÉCORATIONS 'CHATFORMATTING' */
    /******************************************************************************************************/

    /**
     * Convertie les décorations de {@link TextDecoration} par des couleurs {@link ChatFormatting}.
     *
     * @param color La {@link TextDecoration décoration} à convertir.
     * @return Une énumération de {@link ChatFormatting}.
     */
    public static ChatFormatting TextDecorationToChatFormatting(TextDecoration color) {

        return switch(color) {

            case OBFUSCATED -> ChatFormatting.OBFUSCATED;
            case STRIKETHROUGH -> ChatFormatting.STRIKETHROUGH;
            case UNDERLINED -> ChatFormatting.UNDERLINE;
            case BOLD -> ChatFormatting.BOLD;
            case ITALIC -> ChatFormatting.ITALIC;
        };
    }

    /********************************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR LES DÉCORATIONS 'TextDecoration' PAR LES DÉCORATIONS 'CHATFORMATTING' */
    /******************************************************************************************************/

                            /* --------------------------------------------------------- */

    /***********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR UNE CHAîNE DE CARACTÈRE EN COULEURS DE CHAT 'CHATFORMATTING' */
    /**********************************************************************************************/

    public static ChatFormatting convertChatFormatting(Color color) { return convertChatFormatting( "#" + String.format("%08x", color.getRGB()).substring( 2 )); }

    public static ChatFormatting convertChatFormatting(String string)  {

        Preconditions.checkArgument( string != null, "string cannot be null" );

        /********************************************/

        if(string.startsWith("#") && string.length() == 7) {

            int rgb;

            /******************************/

            try { rgb = Integer.parseInt(string.substring(1), 16); }
            catch(NumberFormatException ex) { throw new IllegalArgumentException( "Illegal hex string " + string ); }

            /******************************/

            StringBuilder magic = new StringBuilder(ChatFormatting.PREFIX_CODE + "x");
            for(char c : string.substring(1).toCharArray()) { magic.append(ChatFormatting.PREFIX_CODE).append(c); }

            /******************************/

            ChatFormatting chatFormattingByName = ChatFormatting.getByName(string);
            ChatFormatting chatFormattingByColor = ChatFormatting.getByHexValue(rgb);
            ChatFormatting chatFormattingByCode = ChatFormatting.getByCode(magic.toString().replace("§", "").charAt(0));

            if(chatFormattingByCode != null) return chatFormattingByCode;
            else if(chatFormattingByColor != null) return chatFormattingByColor;
            else if(chatFormattingByName != null) return chatFormattingByName;
        }

        /********************************************/

        ChatFormatting defined = ChatFormatting.getByName(string.toUpperCase(Locale.ROOT));
        if(defined != null) { return defined; }

        /********************************************/

        throw new IllegalArgumentException( "Could not parse ChatColor " + string );
    }
    /***********************************************************************************************/
    /* PETITE MÉTHODE POUR CONVERTIR UNE CHAîNE DE CARACTÈRE EN COULEURS DE CHAT 'CHATFORMATTING' */
    /**********************************************************************************************/

            /* ---------------------------------------------------------------------------------------------------- */
            /* ---------------------------------------------------------------------------------------------------- */

    /**
     * Obtient la couleur la plus courante à partir d'une {@link Map}.
     *
     * @param map La {@link Map} en question
     *
     * @return La couleur la plus courante
     */
    public static Color getMostCommonColour(Map<Integer, Integer> map) {

        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());
        if(list.isEmpty()) return null;

        /********************************************/

        list.sort(Map.Entry.comparingByValue());
        Map.Entry<Integer, Integer> me = list.get(list.size() - 1);

        /********************************************/

        int[] rgb = getRGB(me.getKey());
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

        /********************************************/

        Matcher match = pattern.matcher(msg);
        while(match.find()) {

            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, "" + convertChatFormatting(color));
            match = pattern.matcher(msg);
        }

        /********************************************/

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

        /********************************************/

        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        int tolerance = 10;
        return (rgDiff <= tolerance && rgDiff >= -tolerance) || (rbDiff <= tolerance && rbDiff >= -tolerance);
    }

                    /* ---------------------------------------------------------------------------------------------------- */
                    /* ---------------------------------------------------------------------------------------------------- */

    public static @NotNull String getLastColors(@NotNull String input) {

        Preconditions.checkArgument(true, "Cannot get last colors from null text");
        StringBuilder result = new StringBuilder();
        int length = input.length();

        /*******************************************/

        for(int index = length - 1; index > -1; --index) {

            char section = input.charAt(index);

            /**********************/

            if(section == 167 && index < length - 1) {

                String hexColor = getHexColor(input, index);
                if(hexColor != null) {

                    result.insert(0, hexColor);
                    break;
                }

                /*******************************/

                char c = input.charAt(index + 1);
                ChatFormatting color = ChatFormatting.getByCode(c);

                /*******************************/

                if(color != null) {

                    String var10000 = color.toString();
                    result.insert(0, var10000);
                    if(color.isColor() || color.equals(ChatFormatting.RESET)) break;
                }
            }
        }

        /*******************************************/

        return result.toString();
    }

    /* -------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------- */

    public static @Nullable String getHexColor(@NotNull String input, int index) {

        if(index < 12) return null;
        else if(input.charAt(index - 11) == 'x' && input.charAt(index - 12) == 167) {

            int i;
            for(i = index - 10; i <= index; i += 2) {

                if(input.charAt(i) != 167) { return null; }
            }

            /*******************************/

            for(i = index - 9; i <= index + 1; i += 2) {

                char toCheck = input.charAt(i);
                if(toCheck < '0' || toCheck > 'f') { return null; }
                if(toCheck > '9' && toCheck < 'A') { return null; }
                if(toCheck > 'F' && toCheck < 'a') { return null; }
            }

            /*******************************/

            return input.substring(index - 12, index + 2);

        } else return null;
    }


                    /* ---------------------------------------------------------------------------------------------------- */
                    /* ---------------------------------------------------------------------------------------------------- */
                    /* ---------------------------------------------------------------------------------------------------- */

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}"); // Variable récupérant un patronage de couleur
}
