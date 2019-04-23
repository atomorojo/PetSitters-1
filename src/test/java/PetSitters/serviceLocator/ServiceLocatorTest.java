package PetSitters.serviceLocator;

import PetSitters.cityConversor.CityConversorProxy;
import PetSitters.cityConversor.ICityConversor;
import PetSitters.exception.ExceptionServiceError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ServiceLocatorTest {

    ServiceLocator serviceLocator;

    @Before
    public void setUp() throws Exception {
        serviceLocator = ServiceLocator.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        serviceLocator = null;
    }

    @Test
    public void testFindGetCoordinatesService() throws ExceptionServiceError {
        Service getCoordinates = serviceLocator.find("GetCoordinates");
        // We ensure that the service is instance of class CityConversorProxy
        assertThat(getCoordinates, instanceOf(CityConversorProxy.class));
    }

    @Test(expected = ExceptionServiceError.class)
    public void testFindNonExistingService() throws ExceptionServiceError {
        serviceLocator.find("");
    }
}