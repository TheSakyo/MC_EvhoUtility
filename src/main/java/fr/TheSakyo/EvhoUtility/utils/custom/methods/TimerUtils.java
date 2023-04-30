package fr.TheSakyo.EvhoUtility.utils.custom.methods;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimerUtils {
  public static String DATE_FORMAT_LARGE = "dd/MM/yyyy HH:mm:ss";

  /* ~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String DATE_FORMAT_SHORT = "dd/MM/yyyy";

  /***************************************************************/
  /***************************************************************/

  public static String getCRCounter(int totalSecs) {

    int hours = totalSecs / 3600;
    int minutes = totalSecs % 3600 / 60;
    int seconds = totalSecs % 60;
    int days = 0;

    /******************************/

    if(hours >= 24) {

      days = hours / 24;
      hours %= 24;
    }

    /******************************/

    String toreTurn = "";
    if(days > 0) toreTurn = toreTurn + toreTurn + "d ";
    if(hours > 0) toreTurn = toreTurn + toreTurn + "h ";
    if(minutes > 0) toreTurn = toreTurn + toreTurn + "m ";

    /******************************/

    if(seconds > 0) toreTurn = toreTurn + toreTurn + "s ";
    else if(days == 0 && hours <= 0 && minutes <= 0) toreTurn = toreTurn + toreTurn + "s ";

    /******************************/

    toreTurn = toreTurn.substring(0, toreTurn.length() - 1);
    return toreTurn;
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String getCRCounterSimple(int totalSecs, boolean large) {

    int hours = totalSecs / 3600;
    int minutes = totalSecs % 3600 / 60;
    int seconds = totalSecs % 60;
    int days = 0;

    /******************************/

    if(hours >= 24) {

      days = hours / 24;
      hours %= 24;
    }

    /******************************/

    if(days > 0) return "" + days + days;
    if(hours > 0) return "" + hours + hours;
    if(minutes > 0) return "" + minutes + minutes;

    /******************************/

    return "" + seconds + seconds;
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static boolean isDateBetween2Dates(Date date, Date min, Date max) { return (date.after(min) && date.before(max)); }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static boolean isDateBetween2Dates(String date, String min, String max) {

    Date date1 = convertStringToDate(date, DATE_FORMAT_SHORT);
    Date min1 = convertStringToDate(min, DATE_FORMAT_SHORT);
    Date max1 = convertStringToDate(max, DATE_FORMAT_SHORT);

    /******************************/

    return (date1.after(min1) && date1.before(max1));
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static boolean isDateBetween2Dates(String min, String max) {

    Date date1 = new Date();
    Date min1 = convertStringToDate(min, DATE_FORMAT_SHORT);
    Date max1 = convertStringToDate(max, DATE_FORMAT_SHORT);

    /******************************/

    return (date1.after(min1) && date1.before(max1));
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static boolean isAfter(String date, String min) {

    Date date1 = convertStringToDate(date, DATE_FORMAT_SHORT);
    Date min1 = convertStringToDate(min, DATE_FORMAT_SHORT);

    /******************************/

    return (date1.after(min1) || date1.getTime() == min1.getTime());
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static boolean isBefore(String date, String max) {

    Date date1 = convertStringToDate(date, DATE_FORMAT_SHORT);
    Date max1 = convertStringToDate(max, DATE_FORMAT_SHORT);

    /******************************/

    return date1.before(max1);
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static Date convertStringToDate(String s, String format) {

    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.parse(s, new ParsePosition(0));
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String getCurrentDate(String format) {

    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.format(new Date());
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String getCurrentDate() {

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_LARGE);
    return dateFormat.format(new Date());
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String addTime(String date, Integer amount, TimeUnit timeUnit) { return addTime(date, DATE_FORMAT_LARGE, amount, timeUnit); }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String addTime(String date, String format, Integer amount, TimeUnit timeUnit) {

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat myFormat = new SimpleDateFormat(format);
    cal.setTime(myFormat.parse(date, new ParsePosition(0)));

    /******************************/

    if(timeUnit.equals(TimeUnit.SECONDS)) cal.add(Calendar.SECOND, amount);
    if(timeUnit.equals(TimeUnit.HOURS)) cal.add(Calendar.HOUR, amount);
    if(timeUnit.equals(TimeUnit.DAYS)) cal.add(Calendar.DATE, amount);

    /******************************/

    return myFormat.format(cal.getTime());
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static Integer getBetweenDatesString(String strDate1, String strDate2, String dateFormat, TimeUnit timeUnit) {

    SimpleDateFormat myFormat = new SimpleDateFormat(dateFormat);
    Date date1 = myFormat.parse(strDate1, new ParsePosition(0));
    Date date2 = myFormat.parse(strDate2, new ParsePosition(0));

    /******************************/

    return getBetweenDates(date1, date2, dateFormat, timeUnit);
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static Integer getBetweenDates(Date date1, Date date2, String dateFormat, TimeUnit timeUnit) {

    SimpleDateFormat myFormat = new SimpleDateFormat(dateFormat);

    /******************************/

    try {

      myFormat.format(date1);
      myFormat.format(date2);

      /*********************/

      long diff = date2.getTime() - date1.getTime();
      return (int)timeUnit.convert(diff, TimeUnit.MILLISECONDS);

    } catch(Exception e) {

      e.printStackTrace(System.err);
      return 0;
    }
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String addDays(String date, int days, String format) {

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat myFormat = new SimpleDateFormat(format);

    /******************************/

    cal.setTime(myFormat.parse(date, new ParsePosition(0)));
    cal.add(Calendar.DATE, days);

    /******************************/

    return myFormat.format(cal.getTime());
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String addDays(String date, int days) {

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat myFormat = new SimpleDateFormat(DATE_FORMAT_SHORT);

    /******************************/

    cal.setTime(myFormat.parse(date, new ParsePosition(0)));
    cal.add(Calendar.DATE, days);

    /******************************/

    return myFormat.format(cal.getTime());
  }

  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
  /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

  public static String getCurrentSimpleDate() { return getCurrentDate(DATE_FORMAT_SHORT); }
}
