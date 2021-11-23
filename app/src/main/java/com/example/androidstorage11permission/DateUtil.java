package com.example.androidstorage11permission;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {


    public static String geCurrendDate() {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String month1 = null;
        String day1 = null;
        if (month < 9) {
            month1 = "0" + (month + 1);
        } else {
            month1 = month + 1 + "";
        }
        if (day <= 9) {
            day1 = "0" + day;
        } else {
            day1 = day + "";
        }

        return day1 + "/" + month1 + "/" + year;

    }


    public static String getNextDate() {

        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1); //minus number would decrement the days
        int year = c.get(Calendar.YEAR);
        int month = 0;
        int day = 0;
        int daycheck = c.get(Calendar.DAY_OF_MONTH);
        String monthchk = new SimpleDateFormat("MMM").format(c.getTime()).toUpperCase();
        //String monthchk2 = c.getActualMaximum(Calendar.DAY_OF_MONTH);
       /* if (daycheck == 31) {
            day = 01;
            month = c.get(Calendar.MONTH) + 1;
        } else if (daycheck == 28 && monthchk.equals("FEB")) {
            day = 01;
            month = c.get(Calendar.MONTH) + 1;
        } else if (daycheck == 29 && monthchk.equals("FEB")) {
            day = 01;
            month = c.get(Calendar.MONTH) + 1;
        } else {
            day = c.get(Calendar.DAY_OF_MONTH) + 1;
            month = c.get(Calendar.MONTH);
        }*/

        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);


        String month1 = null;
        String day1 = null;
        if (month < 9) {
            month1 = "0" + (month + 1);
        } else {
            month1 = month + 1 + "";
        }
        if (day <= 9) {
            day1 = "0" + day;
        } else {
            day1 = day + "";
        }

        return day1 + "/" + month1 + "/" + year ;

    }

    public static void dateRang() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i - 7);
        Date start = c.getTime();

        c.add(Calendar.DATE, 6);
        Date end = c.getTime();
        Log.e("date", "date-->" + start + "--" + end);

        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        String startDate = DateFor.format(start);

        String endDate = DateFor.format(end);
        Log.e("startDate-->", startDate);
        Log.e("endDate-->", endDate);


    }

    public static String getCommission_date(int minusday) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        Date today = new Date();
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -minusday);
        Date date = cal.getTime();
        return formatter.format(date).toUpperCase();
    }

    public static String getVisaCurDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static Date NextDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1); //minus number would decrement the days
        return cal.getTime();
    }







}
