package com.marinamooringmanagement.utils;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class GPSUtil extends GlobalExceptionHandler {

    public String getGpsCoordinates(String gpsCoordinates) {
        try {
            boolean westernHemisphere = false;
            boolean southernHemisphere = false;

            int spaceInd = 0;
            while (gpsCoordinates.charAt(spaceInd) != ' ') spaceInd++;
            if (spaceInd == gpsCoordinates.length() - 1)
                throw new RuntimeException(String.format("Invalid GPS Coordinate: %1$s format", gpsCoordinates));

            String latitude = gpsCoordinates.substring(0, spaceInd);
            String longitude = gpsCoordinates.substring(spaceInd);

            double decimalLatitude = 0;
            double decimalLongitude = 0;

            int latDotCount = 0;
            for (char ch : latitude.toCharArray()) if (ch == '.') latDotCount++;
            if (latDotCount < 1 || latDotCount > 3)
                throw new RuntimeException(String.format("Latitude: %1$s is in wrong format", latitude));
            if (latDotCount == 2) {

                int zeroIndLat = 0;
                while (latitude.charAt(zeroIndLat) == '0' || latitude.charAt(zeroIndLat) == ' ') zeroIndLat++;
                latitude = latitude.substring(zeroIndLat);

                if(latitude.charAt(0) == '-') {
                    southernHemisphere = true;
                    latitude = latitude.substring(1);
                }

                decimalLatitude = convertToDecimalDegrees(latitude);

                if(southernHemisphere) decimalLatitude = -decimalLatitude;
            }
            else decimalLatitude = Double.parseDouble(latitude);

            int longDotCount = 0;
            for (char ch : longitude.toCharArray()) if (ch == '.') longDotCount++;
            if (longDotCount < 1 || longDotCount > 3)
                throw new RuntimeException(String.format("Longitude: %1$s is in wrong format", longitude));
            if (longDotCount == 2) {

                int zeroIndLong = 0;
                while (longitude.charAt(zeroIndLong) == '0' || longitude.charAt(zeroIndLong) == ' ') zeroIndLong++;
                longitude = longitude.substring(zeroIndLong);

                if(latitude.charAt(0) == '-') {
                    westernHemisphere = true;
                    longitude = longitude.substring(1);
                }

                decimalLongitude = convertToDecimalDegrees(longitude);

                if (westernHemisphere) decimalLongitude = -decimalLongitude;
            }
            else decimalLongitude = Double.parseDouble(longitude);

            if (!isValidLatitude(decimalLatitude))
                throw new RuntimeException(String.format("Latitude: %1$s is not valid", latitude));
            if (!isValidLongitude(decimalLongitude))
                throw new RuntimeException(String.format("Longitude: %1$s is not valid", longitude));

            String latitudeString = String.format("%.6f", decimalLatitude);
            String longitudeString = String.format("%.6f", decimalLongitude);

            return latitudeString + " " + longitudeString;
        } catch (Exception e) {
            throw new RuntimeException("Wrong GPS coordinate");
        }
    }

    public double convertToDecimalDegrees(String dmm) throws NumberFormatException {
        String[] parts = dmm.split("\\.");
        if (parts.length != 3) {
            throw new NumberFormatException("Invalid DMM format");
        }

        int degrees = Integer.parseInt(parts[0]);
        double minutes = Double.parseDouble(parts[1] + "." + parts[2]);

        // Check if the degrees part was negative and adjust accordingly
        if (degrees < 0) {
            return degrees - (minutes / 60);
        } else {
            return degrees + (minutes / 60);
        }
    }

    public boolean isValidLatitude(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    public boolean isValidLongitude(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }

}
