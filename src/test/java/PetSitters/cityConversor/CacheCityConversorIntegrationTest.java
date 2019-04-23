package PetSitters.cityConversor;

import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTOCity;
import PetSitters.serviceDTO.DTOCoordinates;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class CacheCityConversorIntegrationTest {

    CacheCityConversor Cache;

    @Before
    public void setUp() throws Exception {
        Cache = new CacheCityConversor(2);
    }

    @After
    public void tearDown() throws Exception {
        Cache = null;
    }

    private DTOCoordinates insertCityCoordinates(String cityname, String longitude, String latitude) {
        DTOCity city = new DTOCity(cityname);
        DTOCoordinates coordinates = new DTOCoordinates(longitude, latitude);
        Cache.update(city,coordinates);
        return coordinates;
    }

    @Test
    public void testInsertOneElement() throws ExceptionServiceError {
        DTOCoordinates dtoCoordinates = insertCityCoordinates("Barcelona", "1", "2");

        DTOCity cityRetrieved = new DTOCity("Barcelona");
        assertEquals("Cache should have cached the city 'Barcelona'", Cache.execute(cityRetrieved), dtoCoordinates);
    }

    @Test
    public void testInsertThreeElements() throws ExceptionServiceError {
        DTOCoordinates dtoCoordinates1 = insertCityCoordinates("Barcelona", "1", "2");
        DTOCoordinates dtoCoordinates2 = insertCityCoordinates("Lleida", "1", "2");
        DTOCoordinates dtoCoordinates3 = insertCityCoordinates("Los Angeles", "1", "2");

        DTOCity cityRetrieved = new DTOCity("Lleida");
        assertEquals("Cache should have cached the city 'Lleida'",Cache.execute(cityRetrieved), dtoCoordinates2);

        cityRetrieved = new DTOCity("Los Angeles");
        assertEquals("Cache should have cached the city 'Los Angeles'",Cache.execute(cityRetrieved), dtoCoordinates3);
    }

    @Test(expected = ExceptionServiceError.class)
    public void testInsertThreeElementsRetireNonExisting() throws ExceptionServiceError {
        DTOCoordinates dtoCoordinates1 = insertCityCoordinates("Barcelona", "1", "2");
        insertCityCoordinates("Lleida", "1", "2");
        insertCityCoordinates("Los Angeles", "1", "2");

        DTOCity cityRetrieved = new DTOCity("Barcelona");
        Cache.execute(cityRetrieved);
    }

    @Test(expected = ExceptionServiceError.class)
    public void testVoidRetireNonExisting() throws ExceptionServiceError {
        DTOCity cityRetrieved = new DTOCity("Barcelona");
        Cache.execute(cityRetrieved);
    }

    @Test(expected = ExceptionServiceError.class)
    public void testLRU() throws ExceptionServiceError {
        DTOCoordinates dtoCoordinates1 = insertCityCoordinates("Barcelona","1", "2");
        DTOCoordinates dtoCoordinates2 = insertCityCoordinates("Lleida","1", "2");
        DTOCoordinates dtoCoordinates3 = insertCityCoordinates("Barcelona","1", "2");
        DTOCoordinates dtoCoordinates4 = insertCityCoordinates("Los Angeles", "1", "2");

        DTOCity cityRetrieved = new DTOCity("Lleida");
        Cache.execute(cityRetrieved);
    }
}