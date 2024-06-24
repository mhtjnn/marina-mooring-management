package com.marinamooringmanagement.utils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@Component
public class ImageUtils {

    public List<byte[]> uploadImages(final List<String> encodedImageList) {
        try {
            List<byte[]> byteArrayList = encodedImageList
                    .stream()
                    .map(encodedImage -> {
                        try {
                            return validateEncodedString(encodedImage);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            return byteArrayList;
        } catch (Exception e) {
            throw e;
        }
    }

    public byte[] validateEncodedString(final String encodedString) throws IOException {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

            boolean isValidFile = isValidFile(decodedBytes);

            if (isValidFile) return decodedBytes;
            else throw new RuntimeException("Invalid image file");
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean isValidFile(byte[] decodedBytes) throws IOException {

        try (InputStream inputStream = new ByteArrayInputStream(decodedBytes)){
            return isImage(inputStream);
        } catch (Exception e) {
            throw e;
        }

    }

    private boolean isImage(InputStream inputStream) throws IOException {
        if(ImageIO.read(inputStream) == null) return false;
        return true;
    }

}
