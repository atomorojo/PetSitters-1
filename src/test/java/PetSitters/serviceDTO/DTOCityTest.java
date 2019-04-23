package PetSitters.serviceDTO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DTOCityTest {

    DTOCity dtoCity;

    @Before
    public void setUp() throws Exception {
        dtoCity = new DTOCity();
    }

    @After
    public void tearDown() throws Exception {
        dtoCity = null;
    }

    @Test
    public void testHashCodeSameCity() {
        dtoCity.setCity("Barcelona");
        DTOCity dtoCity2 = new DTOCity("Barcelona");
        assertEquals("The hashCode of both DTOs should be equal",dtoCity.hashCode(),dtoCity2.hashCode());
    }

    @Test
    public void testHashCodeDifferentCity() {
        dtoCity.setCity("Barcelona");
        DTOCity dtoCity2 = new DTOCity("Lleida");
        assertNotEquals("The hashCode of both DTOs should be equal",dtoCity.hashCode(),dtoCity2.hashCode());
    }

    @Test
    public void testEqualsTwoEqualCities() {
        dtoCity.setCity("Barcelona");
        DTOCity dtoCity2 = new DTOCity("Barcelona");
        assertTrue("Both DTOs should have the city 'Barcelona'",dtoCity.equals(dtoCity2));
    }

    @Test
    public void testEqualsTwoDifferentCities() {
        dtoCity.setCity("Barcelona");
        DTOCity dtoCity2 = new DTOCity("Lleida");
        assertFalse("Both DTOs should have different cities",dtoCity.equals(dtoCity2));
    }
}