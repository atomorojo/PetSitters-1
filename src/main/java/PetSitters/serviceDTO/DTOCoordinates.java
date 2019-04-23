package PetSitters.serviceDTO;

import javax.swing.text.Document;

public class DTOCoordinates implements DTO {
    String longitude;
    String latitude;

    public DTOCoordinates(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
