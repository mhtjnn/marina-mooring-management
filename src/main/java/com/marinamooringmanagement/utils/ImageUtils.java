package com.marinamooringmanagement.utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

public class ImageUtils {

    public static byte[] validateEncodedString(final String encodedString) throws IOException {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

            boolean isValidFile = isValidFile(decodedBytes);

            if (isValidFile) return decodedBytes;
            else throw new RuntimeException("Invalid image file");
        } catch (Exception e) {
            throw e;
        }
    }

    private static boolean isValidFile(byte[] decodedBytes) throws IOException {

        try (InputStream inputStream = new ByteArrayInputStream(decodedBytes)){
            return isImage(inputStream);
        } catch (Exception e) {
            throw e;
        }

    }

    private static boolean isImage(InputStream inputStream) throws IOException {
        if(ImageIO.read(inputStream) == null) return false;
        return true;
    }

}
