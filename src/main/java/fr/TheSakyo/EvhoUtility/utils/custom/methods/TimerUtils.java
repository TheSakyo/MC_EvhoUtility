package fr.TheSakyo.EvhoUtility.utils.custom.methods;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimerUtils {
  public static String DATE_FORMAT_LARGE = "dd/MM/yyyy HH:mm:ss";

  public static String DATE_FORMAT_SHORT = "dd/MM/yyyy";

  public static String getCRCounter(int totalSecs) {
    int hours = totalSecs / 3600;
    int minutes = totalSecs % 3600 / 60;
    int seconds = totalSecs % 60;
    int days = 0;
    if (hours >= 24) {
      days = hours / 24;
      hours %= 24;
    }
    String toreturn = "";
    if (days > 0)
      toreturn = toreturn + toreturn + "d ";
    if (hours > 0)
      toreturn = toreturn + toreturn + "h ";
    if (minutes > 0)
      toreturn = toreturn + toreturn + "m ";
    if (seconds > 0) {
      toreturn = toreturn + toreturn + "s ";
    } else if (days <= 0 && hours <= 0 && minutes <= 0) {
      toreturn = toreturn + toreturn + "s ";
    }
    if (toreturn.endsWith(" "))
      toreturn = toreturn.substring(0, toreturn.length() - 1);
    return toreturn;
  }

  public static String getCRCounterSimple(int totalSecs, boolean large) {
    int hours = totalSecs / 3600;
    int minutes = totalSecs % 3600 / 60;
    int seconds = totalSecs % 60;
    int days = 0;
    if (hours >= 24) {
      days = hours / 24;
      hours %= 24;
    }
    if (days > 0)
      return "" + days + days;
    if (hours > 0)
      return "" + hours + hours;
    if (minutes > 0)
      return "" + minutes + minutes;
    return "" + seconds + seconds;
  }

  public static boolean isDateBetween2Dates(Date date, Date min, Date max) {
    return (date.after(min) && date.before(max));
  }

  public static boolean isDateBetween2Dates(String date, String min, String max) {
    Date date1 = convertStringToDate(date, DATE_FORMAT_SHORT);
    Date min1 = convertStringToDate(min, DATE_FORMAT_SHORT);
    Date max1 = convertStringToDate(max, DATE_FORMAT_SHORT);
    return (date1.after(min1) && date1.before(max1));
  }

  public static boolean isAfter(String date, String min) {
    Date date1 = convertStringToDate(date, DATE_FORMAT_SHORT);
    Date min1 = convertStringToDate(min, DATE_FORMAT_SHORT);
    return (date1.after(min1) || date1.getTime() == min1.getTime());
  }

  public static boolean isBefore(String date, String max) {
    Date date1 = convertStringToDate(date, DATE_FORMAT_SHORT);
    Date max1 = convertStringToDate(max, DATE_FORMAT_SHORT);
    return date1.before(max1);
  }

  public static boolean isDateBetween2Dates(String min, String max) {
    Date date1 = new Date();
    Date min1 = convertStringToDate(min, DATE_FORMAT_SHORT);
    Date max1 = convertStringToDate(max, DATE_FORMAT_SHORT);
    return (date1.after(min1) && date1.before(max1));
  }

  public static Date convertStringToDate(String s, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    String dateInString = s;
    Date date = sdf.parse(dateInString, new ParsePosition(0));
    return date;
  }

  public static String getCurrentDate(String format) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    Date date = new Date();
    return dateFormat.format(date);
  }

  public static String getCurrentDate() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_LARGE);
    Date date = new Date();
    return dateFormat.format(date);
  }

  public static String addTime(String date, Integer amount, TimeUnit timeUnit) {
    return addTime(date, DATE_FORMAT_LARGE, amount, timeUnit);
  }

  public static String addTime(String date, String format, Integer amount, TimeUnit timeUnit) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat myFormat = new SimpleDateFormat(format);
    cal.setTime(myFormat.parse(date, new ParsePosition(0)));
    if (timeUnit.equals(TimeUnit.SECONDS))
      cal.add(13, amount.intValue());
    if (timeUnit.equals(TimeUnit.HOURS))
      cal.add(10, amount.intValue());
    if (timeUnit.equals(TimeUnit.DAYS))
      cal.add(5, amount.intValue());
    return myFormat.format(cal.getTime());
  }

  public static Integer getBetweenDatesString(String sdate1, String sdate2, String dateFormat, TimeUnit timeUnit) {
    SimpleDateFormat myFormat = new SimpleDateFormat(dateFormat);
    Date date1 = myFormat.parse(sdate1, new ParsePosition(0));
    Date date2 = myFormat.parse(sdate2, new ParsePosition(0));
    return getBetweenDates(date1, date2, dateFormat, timeUnit);
  }

  public static Integer getBetweenDates(Date date1, Date date2, String dateFormat, TimeUnit timeUnit) {
    SimpleDateFormat myFormat = new SimpleDateFormat(dateFormat);
    try {
      myFormat.format(date1);
      myFormat.format(date2);
      long diff = date2.getTime() - date1.getTime();
      return Integer.valueOf((int)timeUnit.convert(diff, TimeUnit.MILLISECONDS));
    } catch (Exception e) {
      e.printStackTrace();
      return Integer.valueOf(0);
    }
  }

  public static String addDays(String date, int days, String format) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat myFormat = new SimpleDateFormat(format);
    cal.setTime(myFormat.parse(date, new ParsePosition(0)));
    cal.add(5, days);
    return myFormat.format(cal.getTime());
  }

  public static String addDays(String date, int days) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat myFormat = new SimpleDateFormat(DATE_FORMAT_SHORT);
    cal.setTime(myFormat.parse(date, new ParsePosition(0)));
    cal.add(5, days);
    return myFormat.format(cal.getTime());
  }

  public static String getCurrentSimpleDate() {
    return getCurrentDate(DATE_FORMAT_SHORT);
  }
}
