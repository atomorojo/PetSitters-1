package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class DeleteAccountSchemaTest {

    DeleteAccountSchema deleteSchema;

    @Before
    public void setUp() {
        deleteSchema = new DeleteAccountSchema();
    }

    @After
    public void tearDown() {
        deleteSchema = null;
    }

    void fillDeleteSchema() {
        deleteSchema = new DeleteAccountSchema("1234");
    }

    @Test
    public void validateAllIsCorrect() {
        fillDeleteSchema();
        deleteSchema.validate();
        assertEquals("The password should be '1234'", deleteSchema.getPassword(), "1234");
    }


    @Test(expected = ValidationException.class)
    public void validatePasswordIsBlank() {
        fillDeleteSchema();
        deleteSchema.setPassword("");
        deleteSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validatePasswordIsNull() {
        fillDeleteSchema();
        deleteSchema.setPassword(null);
        deleteSchema.validate();
    }
}