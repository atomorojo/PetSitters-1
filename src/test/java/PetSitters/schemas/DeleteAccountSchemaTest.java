package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ValidationException;

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

    void fillDeleteSchemaAndValidate() {
        deleteSchema = new DeleteAccountSchema("User1234", "123");
    }

    @Test
    public void validateAllIsCorrect() {
        fillDeleteSchemaAndValidate();
        deleteSchema.validate();
        assertEquals("The username should be 'User1234'", deleteSchema.getUsername(), "User1234");
    }

    @Test(expected = ValidationException.class)
    public void validateUsernameIsBlank() {
        fillDeleteSchemaAndValidate();
        deleteSchema.setUsername("");
        deleteSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validatePasswordIsBlank() {
        fillDeleteSchemaAndValidate();
        deleteSchema.setPassword("");
        deleteSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateUsernameIsNull() {
        fillDeleteSchemaAndValidate();
        deleteSchema.setUsername(null);
        deleteSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validatePasswordIsNull() {
        fillDeleteSchemaAndValidate();
        deleteSchema.setPassword(null);
        deleteSchema.validate();
    }
}