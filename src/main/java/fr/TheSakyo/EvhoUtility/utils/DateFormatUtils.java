/*
 * Sous licence de "l'Apache Software Foundation (ASF)" sous un ou plusieurs
 * accords de licence de contributeur. Voir le fichier NOTICE distribué avec
 * ce travail pour des informations supplémentaires concernant la propriété des droits d'auteur.
 * L'ASF vous concède ce fichier sous licence Apache License, Version 2.0 (la "Licence").
 * (la "Licence") ; vous ne pouvez utiliser ce fichier que conformément à la Licence.
 * La Licence.  Vous pouvez obtenir une copie de la licence à l'adresse suivante
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Sauf si la loi applicable l'exige ou si un accord écrit a été conclu, le logiciel
 * distribué sous la Licence est distribué sur une base "AS IS",
 * SANS GARANTIE NI CONDITION DE QUELQUE NATURE QUE CE SOIT, expresse ou implicite.
 * Voir la Licence pour les termes spécifiques régissant les permissions et les limitations de la Licence.
 * Limitations de la Licence.
 */
package fr.TheSakyo.EvhoUtility.utils;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.time.FastTimeZone;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>Les utilitaires et les constantes de formatage de la date et de l'heure.</p>
 *
 * <p>Le formatage est effectué en utilisant la classe thread-safe.
 * {@link org.apache.commons.lang3.time.FastDateFormat} class.</p> *.
 *
 * <p>Notez que le JDK a un bug dans lequel l'appel à Calendar.get(int) va
 * remplacer tout appel à Calendar.clear() précédemment appelé. Voir LANG-755.</p>
 *
 * @since 2.0
 */
public class DateFormatUtils {

    /**
     * Le fuseau horaire UTC (souvent appelé GMT).
     * Ceci est privé, car il est mutable.
     */
    private static final TimeZone UTC_TIME_ZONE = FastTimeZone.getGmtTimeZone();

    /**
     * Formatage ISO 8601 de la date et de l'heure sans fuseau horaire.
     *
     * <p>
     * Le format utilisé est {@code yyyy-MM-dd'T'HH:mm:ss}. Ce format utilise le
     * TimeZone par défaut en vigueur au moment du chargement de la classe DateFormatUtils.
     * </p>
     *
     * @since 3.5
     */
    public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_FORMAT
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Formatage ISO 8601 de la date et de l'heure avec le fuseau horaire.
     *
     * <p>
     * Le format utilisé est {@code yyyy-MM-dd'T'HH:mm:ssZZ}. Ce format utilise le
     * TimeZone par défaut en vigueur au moment du chargement de la classe DateFormatUtils.
     * </p>
     *
     * @since 3.5
     */
    public static final FastDateFormat ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");

    /**
     * Formateur ISO 8601 pour la date sans fuseau horaire.
     *
     * <p>
     * Le format utilisé est {@code yyyy-MM-dd}. Ce format utilise le
     * TimeZone par défaut en vigueur au moment du chargement de la classe DateFormatUtils.
     * </p>
     *
     * @since 3.5
     */
    public static final FastDateFormat ISO_8601_EXTENDED_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    /**
     * Formateur ISO 8601 pour l'heure sans fuseau horaire.
     *
     * <p>
     * Le format utilisé est {@code HH:mm:ss}. Ce format utilise la valeur par défaut
     * TimeZone en vigueur au moment du chargement de la classe DateFormatUtils.
     * </p>
     *
     * @since 3.5
     */
    public static final FastDateFormat ISO_8601_EXTENDED_TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss");

    /**
     * Formateur ISO 8601 pour l'heure avec le fuseau horaire.
     *
     * <p>
     * Le format utilisé est {@code HH:mm:ssZZ}. Ce format utilise la valeur par défaut
     * TimeZone en vigueur au moment du chargement de la classe DateFormatUtils.
     * </p>
     *
     * @since 3.5
     */
    public static final FastDateFormat ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("HH:mm:ssZZ");

    /**
     * En-têtes de date SMTP (et probablement d'autres).
     *
     * <p>
     * Le format utilisé est {@code EEE, dd MMM yyyy HH:mm:ss Z} en locale US.
     * Ce format utilise le TimeZone par défaut en vigueur au moment du chargement.
     * Classe DateFormatUtils.
     * </p>
     */
    public static final FastDateFormat SMTP_DATETIME_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    // ----------------------------------------------------------------------- //

    /**
     * <p>Les instances de DateFormatUtils ne doivent PAS être construites en programmation standard.</p>
     *
     * <p>Ce constructeur est public pour permettre aux outils qui nécessitent une instance de JavaBean.
     * pour fonctionner.</p>
     */
    public DateFormatUtils() {}

    /**
     * <p>Formate une date/heure en un modèle spécifique en utilisant le fuseau horaire UTC.</p>
     *
     * @param millis la date à formater exprimée en millisecondes.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @return la date formatée
     */
    public static String formatUTC(final long millis, final String pattern) { return format(new Date(millis), pattern, UTC_TIME_ZONE, null); }

    /**
     * <p>Formate une date/heure en un modèle spécifique en utilisant le fuseau horaire UTC.</p>
     *
     * @param date la date à formater, non nulle.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @return la date formatée
     */
    public static String formatUTC(final Date date, final String pattern) { return format(date, pattern, UTC_TIME_ZONE, null); }

    /**
     * <p>Formate une date/heure en un modèle spécifique en utilisant le fuseau horaire UTC.</p>
     *
     * @param millis la date à formater exprimée en millisecondes.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @param locale la locale à utiliser, peut être {@code null}
     * @return la date formatée
     */
    public static String formatUTC(final long millis, final String pattern, final Locale locale) { return format(new Date(millis), pattern, UTC_TIME_ZONE, locale); }

    /**
     * <p>Formate une date/heure en un modèle spécifique en utilisant le fuseau horaire UTC.</p>
     *
     * @param date la date à formater, non nulle.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @param locale la locale à utiliser, peut être {@code null}
     * @return la date formatée
     */
    public static String formatUTC(final Date date, final String pattern, final Locale locale) { return format(date, pattern, UTC_TIME_ZONE, locale);
    }

    /**
     * <p>Formate une date/heure en un motif spécifique.</p>
     *
     * @param millis la date à formater exprimée en millisecondes.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @return la date formatée
     */
    public static String format(final long millis, final String pattern) { return format(new Date(millis), pattern, null, null); }

    /**
     * <p>Formate une date/heure en un motif spécifique.</p>
     *
     * @param date la date à formater, non nulle.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @return la date formatée
     */
    public static String format(final Date date, final String pattern) { return format(date, pattern, null, null); }

    /**
     * <p>Formate un calendrier en un modèle spécifique.</p>
     *
     * @param calendar le calendrier à formater, non nul.
     * @param pattern le motif à utiliser pour formater le calendrier, non nul.
     * @return le calendrier formaté
     * @see FastDateFormat#format(Calendar)
     * @since 2.4
     */
    public static String format(final Calendar calendar, final String pattern) { return format(calendar, pattern, null, null); }

    /**
     * <p>Formate une date/heure en un modèle spécifique dans un fuseau horaire.</p>
     *
     * @param millis le temps exprimé en millisecondes.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @param timeZone le fuseau horaire à utiliser, peut être {@code null}.
     * @return la date formatée
     */
    public static String format(final long millis, final String pattern, final TimeZone timeZone) { return format(new Date(millis), pattern, timeZone, null); }

    /**
     * <p>Formate une date/heure en un modèle spécifique dans un fuseau horaire.</p>
     *
     * @param date la date à formater, non nulle.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @param timeZone le fuseau horaire à utiliser, peut être {@code null}.
     * @return la date formatée
     */
    public static String format(final Date date, final String pattern, final TimeZone timeZone) { return format(date, pattern, timeZone, null); }

    /**
     * <p>Formate un calendrier selon un modèle spécifique dans un fuseau horaire.</p>
     *
     * @param calendar le calendrier à formater, non nul.
     * @param pattern le motif à utiliser pour formater le calendrier, not null
     * @param timeZone le fuseau horaire à utiliser, peut être {@code null}.
     * @return le calendrier formaté
     * @see FastDateFormat#format(Calendar)
     * @since 2.4
     */
    public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone) { return format(calendar, pattern, timeZone, null); }

    /**
     * <p>Formate une date/heure en un motif spécifique dans une locale.</p>
     *
     * @param millis la date à formater exprimée en millisecondes.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @param locale la locale à utiliser, peut être {@code null}.
     * @return la date formatée
     */
    public static String format(final long millis, final String pattern, final Locale locale) { return format(new Date(millis), pattern, null, locale); }

    /**
     * <p>Formats a date/time into a specific pattern in a locale.</p>
     *
     * @param date  the date to format, not null
     * @param pattern  the pattern to use to format the date, not null
     * @param locale  the locale to use, may be {@code null}
     * @return la date formatée
     */
    public static String format(final Date date, final String pattern, final Locale locale) { return format(date, pattern, null, locale); }

    /**
     * <p>Formate un calendrier en un modèle spécifique dans une locale.</p>
     *
     * @param calendar le calendrier à formater, non nul.
     * @param pattern le motif à utiliser pour formater le calendrier, not null
     * @param locale la locale à utiliser, peut être {@code null}.
     * @return le calendrier formaté
     * @see FastDateFormat#format(Calendar)
     * @since 2.4
     */
    public static String format(final Calendar calendar, final String pattern, final Locale locale) { return format(calendar, pattern, null, locale); }

    /**
     * <p>Formate une date/heure en un modèle spécifique dans un fuseau horaire et une locale.</p>
     *
     * @param millis la date à formater exprimée en millisecondes.
     * @param pattern le motif à utiliser pour formater la date, non nul.
     * @param timeZone le fuseau horaire à utiliser, peut être {@code null}.
     * @param locale la locale à utiliser, peut être {@code null}.
     * @return la date formatée
     */
    public static String format(final long millis, final String pattern, final TimeZone timeZone, final Locale locale) { return format(new Date(millis), pattern, timeZone, locale); }

    /**
     * <p>Formate une date/heure en un modèle spécifique dans un fuseau horaire et une locale.</p>
     *
     * @param date la date à formater, non nulle.
     * @param pattern le motif à utiliser pour formater la date, not null, not null.
     * @param timeZone le fuseau horaire à utiliser, peut être {@code null}.
     * @param locale la locale à utiliser, peut être {@code null}.
     * @return la date formatée
     */
    public static String format(final Date date, final String pattern, final TimeZone timeZone, final Locale locale) {

        final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
        return df.format(date);
    }

    /**
     * <p>Formate un calendrier en un modèle spécifique dans un fuseau horaire et une locale.</p>.
     *
     * @param calendar le calendrier à formater, non nul.
     * @param pattern le motif à utiliser pour formater le calendrier, not null
     * @param timeZone le fuseau horaire à utiliser, peut être {@code null}.
     * @param locale la locale à utiliser, peut être {@code null}
     * @return le calendrier formaté
     * @see FastDateFormat#format(Calendar)
     * @since 2.4
     */
    public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone, final Locale locale) {

        final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
        return df.format(calendar);
    }

}