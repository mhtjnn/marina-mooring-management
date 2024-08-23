package com.marinamooringmanagement.utils;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class MathUtils extends GlobalExceptionHandler {

    public static double PercentageCalculator(double obtained, double total) {
        if (total == 0) {
            throw new IllegalArgumentException("Total value cannot be zero.");
        }
        return (obtained / total) * 100;
    }

}
