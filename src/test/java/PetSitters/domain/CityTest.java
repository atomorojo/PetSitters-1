package PetSitters.domain;

import PetSitters.cityConversor.CacheCityConversor;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTO;
import PetSitters.serviceDTO.DTOCity;
import PetSitters.serviceDTO.DTOCoordinates;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CityTest {

    City city;

    @Before
    public void setUp() throws Exception {
        city = new City("Barcelona");
    }

    @After
    public void tearDown() throws Exception {
        city = null;
    }

    @Test
    public void getCoordinatesFromExistingCity() throws Exception {
        city.setName("Los Angeles");
        Coordinates c = city.getCoordinates();
        // We compare the floating-point numbers with a tolerance of 5%
        assertEquals("The latitude should be 34.0536909", c.getLatitude(), 34.0536909, 0.05);
        assertEquals("The longitude should be -118.2427666", c.getLongitude(), -118.2427666, 0.05);
    }

    @Test(expected = ExceptionServiceError.class)
    public void getCoordinatesFromNonExistingCity() throws Exception {
        city.setName("Los Angedlgjdflles");
        Coordinates c = city.getCoordinates();
    }
}