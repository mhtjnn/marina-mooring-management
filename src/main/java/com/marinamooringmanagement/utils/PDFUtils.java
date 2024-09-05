package com.marinamooringmanagement.utils;

import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Transactional
public class PDFUtils {

    public static byte[] isPdfFile(String encodedString) {
        // Decode the Base64 encoded string to get the byte array
        String base64String = encodedString.substring(encodedString.indexOf(",") + 1);
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        // Check if the byte array has the PDF magic number
        String pdfMagicNumber = "%PDF-";
        String fileHeader = new String(decodedBytes, 0, pdfMagicNumber.length());

        if(fileHeader.equals(pdfMagicNumber)) return decodedBytes;
        else throw new RuntimeException("Given form is not in pdf format.");
    }
}
