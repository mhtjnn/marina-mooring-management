package com.marinamooringmanagement.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ConversionUtils {

    public boolean canConvertToInt(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean canConvertToBigDecimal(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
