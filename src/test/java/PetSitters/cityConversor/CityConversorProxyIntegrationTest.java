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

public class CityConversorProxyIntegrationTest {

    CityConversorProxy cityConversorProxy;

    @Before
    public void setUp() throws Exception {
        cityConversorProxy = new CityConversorProxy();
    }

    @After
    public void tearDown() throws Exception {
        cityConversorProxy = null;
    }

    @Test
    public void testExecuteNotCachedAndInSecondaryService() throws JSONException, IOException, ExceptionServiceError {
        DTOCoordinates dtoCoordinates = (DTOCoordinates) cityConversorProxy.execute(new DTOCity("Barcelona"));
        assertEquals("The latitude should be 41.3828939", dtoCoordinates.getLatitude(), "41.3828939");
        assertEquals("The longitude should be 2.1774322", dtoCoordinates.getLongitude(), "2.1774322");
    }

    @Test
    public void testExecuteCachedAndInSecondaryService() throws JSONException, IOException, ExceptionServiceError {
        DTOCoordinates dtoCoordinates = (DTOCoordinates) cityConversorProxy.execute(new DTOCity("Barcelona"));
        assertEquals("The latitude should be 41.3828939", dtoCoordinates.getLatitude(), "41.3828939");
        assertEquals("The longitude should be 2.1774322", dtoCoordinates.getLongitude(), "2.1774322");

        dtoCoordinates = (DTOCoordinates) cityConversorProxy.execute(new DTOCity("Barcelona"));
        assertEquals("The latitude should be 41.3828939", dtoCoordinates.getLatitude(), "41.3828939");
        assertEquals("The longitude should be 2.1774322", dtoCoordinates.getLongitude(), "2.1774322");
    }

    @Test(expected = ExceptionServiceError.class)
    public void testExecuteNotCachedAndInNotSecondaryService() throws JSONException, IOException, ExceptionServiceError {
        DTOCoordinates dtoCoordinates = (DTOCoordinates) cityConversorProxy.execute(new DTOCity("asdfAAAAF"));
    }
}