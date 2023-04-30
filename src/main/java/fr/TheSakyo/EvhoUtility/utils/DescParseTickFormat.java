package fr.TheSakyo.EvhoUtility.utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class DescParseTickFormat {
	
	/************************************************************************/
	/* PARTIE VARIABLES POUR LA CONVERSION DE TICKS (Temps dans Minecraft) */ 
	/***********************************************************************/
	
    public static final Map<String, Integer> nameToTicks = new LinkedHashMap<>();
    public static final Set<String> resetAliases = new HashSet<>();
    public static final int ticksAtMidnight = 18000;
    public static final int ticksPerDay = 24000;
    public static final int ticksPerHour = 1000;
    public static final double ticksPerMinute = 1000d / 60d;
    public static final double ticksPerSecond = 1000d / 60d / 60d;
    
	/************************************************************************/
	/* PARTIE VARIABLES POUR LA CONVERSION DE TICKS (Temps dans Minecraft) */ 
	/***********************************************************************/
    
    

    /*******************************************************************/
	/* MÉTHODES ET FONCTIONS UTILES POUR CONVERTIR DES TICKS MINECRAFT */
	/* (Pour le fonctionnement du changement de temps dans le jeu)    */
	/*******************************************************************/
    static {

        nameToTicks.put("sunrise", 23000);
        nameToTicks.put("day", 1000);
        nameToTicks.put("noon", 6000);
        nameToTicks.put("sunset", 12000);
        nameToTicks.put("night", 13000);
        nameToTicks.put("midnight", 18000);
        resetAliases.add("reset");
    }
    
    
    public static long parse(String desc) throws NumberFormatException {

        desc = desc.toLowerCase(Locale.FRENCH).replaceAll("[^A-Za-z0-9:]", "");

        /***********************************/

        try { return parseTicks(desc); }
        catch (final NumberFormatException ignored) {}

        /*******************/

        try {  return parse24(desc); }
        catch(final NumberFormatException ignored) {}

        /*******************/

        try { return parse12(desc); }
        catch(final NumberFormatException ignored) {}

        /*******************/

        try { return parseAlias(desc); }
        catch(final NumberFormatException ignored) {}

        /***********************************/
        /***********************************/

        throw new NumberFormatException();
    }

    public static long parseTicks(String desc) throws NumberFormatException {

        if(!desc.matches("^[0-9]+ti?c?k?s?$")) throw new NumberFormatException();
        desc = desc.replaceAll("[^0-9]", "");

        /***********************************/

        return Long.parseLong(desc) % 24000;
    }

    public static long parse24(String desc) throws NumberFormatException {

        if(!desc.matches("^[0-9]{2}[^0-9]?[0-9]{2}$")) throw new NumberFormatException();

        /***********************************/

        desc = desc.toLowerCase(Locale.FRENCH).replaceAll("[^0-9]", "");
        if(desc.length() != 4) throw new NumberFormatException();

        /***********************************/

        final int hours = Integer.parseInt(desc.substring(0, 2));
        final int minutes = Integer.parseInt(desc.substring(2, 4));

        /***********************************/

        return hoursMinutesToTicks(hours, minutes);
    }

    public static long parse12(String desc) throws NumberFormatException {

        if(!desc.matches("^[0-9]{1,2}([^0-9]?[0-9]{2})?(pm|am)$")) throw new NumberFormatException();

        /***********************************/

        int hours = 0;
        int minutes = 0;

        /**********************/

        desc = desc.toLowerCase(Locale.FRENCH);
        final String parseTime = desc.replaceAll("[^0-9]", "");

        /***********************************/

        if(parseTime.length() > 4) throw new NumberFormatException();

        /***********************************/

        if(parseTime.length() == 4) {

            hours += Integer.parseInt(parseTime.substring(0, 2));
            minutes += Integer.parseInt(parseTime.substring(2, 4));

        } else if (parseTime.length() == 3) {

            hours += Integer.parseInt(parseTime.substring(0, 1));
            minutes += Integer.parseInt(parseTime.substring(1, 3));

        } else if (parseTime.length() == 2) hours += Integer.parseInt(parseTime.substring(0, 2));
        else if (parseTime.length() == 1)  hours += Integer.parseInt(parseTime.substring(0, 1));
        else throw new NumberFormatException();

        /***********************************/

        if(desc.endsWith("pm") && hours != 12) hours += 12;
        if(desc.endsWith("am") && hours == 12) hours -= 12;

        /***********************************/

        return hoursMinutesToTicks(hours, minutes);
    }

    public static long hoursMinutesToTicks(final int hours, final int minutes) {
        long ret = ticksAtMidnight;
        ret += (long)hours * ticksPerHour;

        ret += (long)((minutes / 60.0) * ticksPerHour);

        ret %= ticksPerDay;
        return ret;
    }

    public static long parseAlias(final String desc) throws NumberFormatException {
        final Integer ret = nameToTicks.get(desc);
        if (ret == null) {
            throw new NumberFormatException();
        }

        return ret;
    }

    /*******************************************************************/
	/* MÉTHODES ET FONCTIONS UTILES POUR CONVERTIR DES TICKS MINECRAFT */
	/* (Pour le fonctionnement du changement de temps dans le jeu)    */
	/*******************************************************************/

}
