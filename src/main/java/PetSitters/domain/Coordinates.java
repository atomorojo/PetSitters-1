package PetSitters.domain;

import PetSitters.serviceDTO.DTOCoordinates;

public class Coordinates {
    double longitude;
    double latitude;

    public Coordinates(DTOCoordinates dtoCoordenades) {
        this.longitude = Double.valueOf(dtoCoordenades.getLongitude());
        this.latitude = Double.valueOf(dtoCoordenades.getLatitude());
    }

    public Coordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
