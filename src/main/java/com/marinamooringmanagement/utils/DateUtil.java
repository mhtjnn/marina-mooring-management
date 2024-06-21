package com.marinamooringmanagement.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateUtil {

    public Date stringToDate(String dateStr) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw e;
        }
    }

    public String dateToString(Date date) {
        try {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return localDate.format(dateTimeFormatter);
        } catch (Exception e) {
            throw e;
        }
    }

}
