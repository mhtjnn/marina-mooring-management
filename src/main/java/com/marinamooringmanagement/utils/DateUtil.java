package com.marinamooringmanagement.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    // Define the input and output date formats
    private static final SimpleDateFormat INPUT_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    private static final String OUTPUT_FORMAT = "yyyy-MM-dd HH:mm:ss";;

    // Method to convert date from "Wed Jul 10 00:00:00 IST 2024" to "2024-11-07 00:00:00" format
    public static Date convertDateFormat(Date inputDate) {
        try {
            // Define the output date format
            SimpleDateFormat outputFormat = new SimpleDateFormat(OUTPUT_FORMAT);
            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Format the input date into the desired string format
            String formattedDateString = outputFormat.format(inputDate);

            // Parse the formatted date string back into a Date object
            return outputFormat.parse(formattedDateString);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to convert date string from input format to java.util.Date
    public static Date convertToUtilDate(String inputDateString) throws ParseException {
        // Parse the input date string to a java.util.Date object
        return INPUT_FORMAT.parse(inputDateString);
    }

    public static java.sql.Date convertToSqlDate(java.util.Date utilDate) {
        // Convert java.util.Date to java.sql.Date
        return new java.sql.Date(utilDate.getTime());
    }

    public static Date stringToDate(final String dateStr) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw e;
        }
    }

    public static String dateToString(final Date date) {
        try {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            return localDate.format(dateTimeFormatter);
        } catch (Exception e) {
            throw e;
        }
    }

    public static String dateToStringSQLFormat(final Date date) {
        try {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return localDate.format(dateTimeFormatter);
        } catch (Exception e) {
            throw e;
        }
    }

    public static LocalDate stringToLocalDate(final String dateStr) {
        final Date filterFromDate = stringToDate(dateStr);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(filterFromDate);
        return LocalDate.parse(formattedDate);
    }

    public static java.sql.Date toSqlDate(java.util.Date utilDate) {
        return new java.sql.Date(utilDate.getTime());
    }

    public static LocalDate dateToLocalDate(final Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
