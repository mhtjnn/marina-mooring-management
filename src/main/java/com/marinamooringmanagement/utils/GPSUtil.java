package com.marinamooringmanagement.utils;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

@Component
public class GPSUtil extends GlobalExceptionHandler {

    public String getGpsCoordinates(final String gpsCoordinates) {
        try {
            boolean eastHemisphere = false;

            if (gpsCoordinates.charAt(gpsCoordinates.length() - 1) == 'E') eastHemisphere = true;

            int spaceInd = 0;
            while (gpsCoordinates.charAt(spaceInd) != ' ') spaceInd++;
            if (spaceInd == gpsCoordinates.length() - 1)
                throw new RuntimeException(String.format("Invalid GPS Coordinate: %1$s format", gpsCoordinates));

            String latitude = gpsCoordinates.substring(0, spaceInd);
            String longitude = gpsCoordinates.substring(spaceInd);

            int zeroInd = 0;
            while (latitude.charAt(zeroInd) == '0' || latitude.charAt(zeroInd) == ' ') zeroInd++;
            latitude = latitude.substring(zeroInd);

            zeroInd = 0;
            while (longitude.charAt(zeroInd) == '0' || longitude.charAt(zeroInd) == ' ') zeroInd++;
            longitude = longitude.substring(zeroInd);


            int dotCount = 0;
            double decimalLatitude = 0;
            double decimalLongitude = 0;

            for (char ch : latitude.toCharArray()) if (ch == '.') dotCount++;
            if (dotCount < 1 || dotCount > 3)
                throw new RuntimeException(String.format("Latitude: %1$s is in wrong format", latitude));
            if (dotCount == 2) decimalLatitude = convertToDecimalDegrees(latitude);
            else decimalLatitude = Double.parseDouble(latitude);

            dotCount = 0;
            for (char ch : longitude.toCharArray()) if (ch == '.') dotCount++;
            if (dotCount < 1 || dotCount > 3)
                throw new RuntimeException(String.format("Longitude: %1$s is in wrong format", longitude));
            if (dotCount == 2) decimalLongitude = convertToDecimalDegrees(longitude);
            else decimalLongitude = Double.parseDouble(longitude);

            if (!eastHemisphere) decimalLongitude = -decimalLongitude;

            if (!isValidLatitude(decimalLatitude))
                throw new RuntimeException(String.format("Latitude: %1$s is not valid", latitude));
            if (!isValidLongitude(decimalLongitude))
                throw new RuntimeException(String.format("Longitude: %1$s is not valid", longitude));

            String latitudeString = String.format("%.6f", decimalLatitude);
            String longitudeString = String.format("%.6f", decimalLongitude);

            return latitudeString + " " + longitudeString;
        } catch (Exception e) {
            throw e;
        }
    }

    public double convertToDecimalDegrees(String dmm) throws NumberFormatException {
        String[] parts = dmm.split("\\.");
        int degrees = Integer.parseInt(parts[0]);
        double minutes = Double.parseDouble(parts[1] + "." + parts[2]);
        return degrees + (minutes / 60);
    }

    public boolean isValidLatitude(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    public boolean isValidLongitude(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }

}
