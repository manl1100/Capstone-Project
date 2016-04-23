package com.example.manuelsanchez.udacitycapstone;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String[] months = new DateFormatSymbols().getMonths();

    public static String getFormattedDateString(String longDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date = simpleDateFormat.parse(longDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            int year = calendar.get(Calendar.YEAR);
            return String.format("%s %d, %d", months[month], day, year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFormattedTimeString(String longDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date = simpleDateFormat.parse(longDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            return String.format("%d:%02d", hour, min);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
