package PetSitters.cityConversor;

import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTOCity;
import PetSitters.serviceDTO.DTOCoordinates;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class OpenStreetCityConversorIntegrationTest {

    OpenStreetCityConversor openStreetCityConversor;

    @Before
    public void setUp() throws Exception {
        openStreetCityConversor = new OpenStreetCityConversor();
    }

    @After
    public void tearDown() throws Exception {
        openStreetCityConversor = null;
    }

    @Test
    public void testExecuteWithExistingCity() throws JSONException, IOException, ExceptionServiceError {
        DTOCoordinates dtoCoordinates = (DTOCoordinates) openStreetCityConversor.execute(new DTOCity("Barcelona"));
        assertEquals("The latitude should be 41.3828939", dtoCoordinates.getLatitude(), "41.3828939");
        assertEquals("The longitude should be 2.1774322", dtoCoordinates.getLongitude(), "2.1774322");
    }

    @Test
    public void testExecuteWithExistingCityWithSpacesInName() throws JSONException, IOException, ExceptionServiceError {
        DTOCoordinates dtoCoordinates = (DTOCoordinates) openStreetCityConversor.execute(new DTOCity("Los Angeles"));
        assertEquals("The latitude should be 34.0536909", dtoCoordinates.getLatitude(), "34.0536909");
        assertEquals("The longitude should be -118.2427666", dtoCoordinates.getLongitude(), "-118.2427666");
    }

    @Test(expected = ExceptionServiceError.class)
    public void testExecuteWithNonExistingCity() throws JSONException, IOException, ExceptionServiceError {
        openStreetCityConversor.execute(new DTOCity("ekwejfdsk"));
    }
}