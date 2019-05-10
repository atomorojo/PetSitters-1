package PetSitters.domain;

import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTOCity;
import PetSitters.serviceDTO.DTOCoordinates;
import PetSitters.serviceLocator.Service;
import PetSitters.serviceLocator.ServiceLocator;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;

public class City {
    String name;

    public City(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() throws ExceptionServiceError, IOException, JSONException {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        Service service = serviceLocator.find("GetCoordinates");
        DTOCity dtoCity = new DTOCity(name);
        DTOCoordinates result = (DTOCoordinates) service.execute(dtoCity);
        return new Coordinates(result);
    }
}
