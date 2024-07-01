package com.marinamooringmanagement.utils;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberUtil {

    public String validateAndConvertToStandardFormatPhoneNumber(final String phoneNumber) {
        String givenPhoneNumber = phoneNumber;
        int hyphenCount = 0;
        for(char ch: givenPhoneNumber.toCharArray()) {
            if(ch != '-' && !Character.isDigit(ch)) throw new RuntimeException(String.format("Given phone number: %1$s is in wrong format", givenPhoneNumber));
            if(ch == '-') {
                hyphenCount++;
            }
        }

        if(hyphenCount == 0) {
            if(givenPhoneNumber.length() == 12) throw new RuntimeException(String.format("Given phone number: %1$s contains 12 digits (should be 10 digits)", givenPhoneNumber));

            givenPhoneNumber = givenPhoneNumber.substring(0, 3)
                    + "-" +
                    givenPhoneNumber.substring(3, 6)
                    + "-" +
                    givenPhoneNumber.substring(6, 10);
        } else if (hyphenCount == 2) {
            if(givenPhoneNumber.length() != 12 || givenPhoneNumber.charAt(3) != '-' || givenPhoneNumber.charAt(7) != '-') throw new RuntimeException(String.format("Given phone number: %1$s is in wrong format", givenPhoneNumber));
        } else {
            throw new RuntimeException(String.format("Given phone number: %1$s is in wrong format", givenPhoneNumber));
        }

        return givenPhoneNumber;
    }

}
