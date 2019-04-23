package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class GetCoordinatesSchemaTest {

    GetCoordinatesSchema getCoordinatesSchema;

    @Before
    public void setUp() throws Exception {
        getCoordinatesSchema = new GetCoordinatesSchema();
    }

    @After
    public void tearDown() throws Exception {
        getCoordinatesSchema = null;
    }

    void fillGetCoordinatesSchema() {
        getCoordinatesSchema = new GetCoordinatesSchema("Barcelona");
    }

    @Test
    public void testValidateAllOk() {
        fillGetCoordinatesSchema();
        getCoordinatesSchema.validate();
        assertEquals ("The city should be 'Barcelona'", getCoordinatesSchema.getCity(), "Barcelona");
    }

    @Test(expected = ValidationException.class)
    public void testValidateWithEmpty() {
        fillGetCoordinatesSchema();
        getCoordinatesSchema.setCity("");
        getCoordinatesSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void testValidateWithBlank() {
        fillGetCoordinatesSchema();
        getCoordinatesSchema.setCity(null);
        getCoordinatesSchema.validate();
    }
}