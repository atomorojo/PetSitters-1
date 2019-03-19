package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ValidationException;

import static org.junit.Assert.*;

public class DeleteAccountSchemaTest {

    DeleteAccountSchema deleteSchema;

    @Before
    public void setUp() throws Exception {
        deleteSchema = new DeleteAccountSchema();
    }

    @After
    public void tearDown() throws Exception {
        deleteSchema = null;
    }

    @Test
    public void validateAllIsCorrect() {
        deleteSchema.setUsername("User1234");
        deleteSchema.validate();
        assertEquals("The username should be 'User1234'", deleteSchema.getUsername(), "User1234");
    }

    @Test(expected = ValidationException.class)
    public void validateUsernameIsBlank() {
        deleteSchema.setUsername("");
        deleteSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateUsernameIsNull() {
        deleteSchema.validate();
    }
}