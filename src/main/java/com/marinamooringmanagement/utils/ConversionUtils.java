package com.marinamooringmanagement.utils;

import org.springframework.stereotype.Component;

@Component
public class ConversionUtils {

    public boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
