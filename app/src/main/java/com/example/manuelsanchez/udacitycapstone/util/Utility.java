package com.example.manuelsanchez.udacitycapstone.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class Utility {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String[] months = new DateFormatSymbols().getMonths();
    private static String[] weekdays = new DateFormatSymbols().getWeekdays();

    public static String getFormattedDateString(String longDate, boolean withDayOfWeek) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date = simpleDateFormat.parse(longDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            int year = calendar.get(Calendar.YEAR);

            if (withDayOfWeek) {
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                return String.format("%s %s %d, %d",weekdays[dayOfWeek], months[month], day, year);
            } else {
                return String.format("%s %d, %d", months[month], day, year);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("location", "Dallas");
    }


}
