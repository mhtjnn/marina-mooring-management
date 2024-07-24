package com.marinamooringmanagement.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    public static Date stringToDate(String dateStr) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw e;
        }
    }

    public static String dateToString(Date date) {
        try {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            return localDate.format(dateTimeFormatter);
        } catch (Exception e) {
            throw e;
        }
    }

    public static LocalDate stringToLocalDate(String dateStr) {
        final Date filterFromDate = stringToDate(dateStr);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(filterFromDate);
        return LocalDate.parse(formattedDate);
    }

}
