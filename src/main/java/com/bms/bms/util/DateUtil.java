package com.bms.bms.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {
    public static Date dateFormat(String date) {
        Date date1 = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date1 = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public static String dateToString(Date date) {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return format.format(date);
    }

    public static String dateToString(Date date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);

        return format.format(date);
    }

    public static String dateToJoinedString(Date date) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");

        return format.format(date);
    }

    public static Date dateTimeFullFormat(String date) {
        Date date1 = null;
        String dateTime = date + " 00:00:00";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date1 = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {

        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date todayDate() {
        Date todayDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = simpleDateFormat.format(todayDate);
        return dateTimeFullFormat(today);
    }

    public static LocalDateTime getDateAgoFor(Date date, int daysAgo) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return localDateTime.minusDays(daysAgo);
    }

    public static Date atStartOfDay(LocalDateTime localDateTime) {
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }
}
