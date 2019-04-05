package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class SetAnotherPasswordSchemaTest {

    SetAnotherPasswordSchema setAnotherPasswordSchema;

    @Before
    public void setUp() {
        setAnotherPasswordSchema = new SetAnotherPasswordSchema();
    }

    @After
    public void tearDown() {
        setAnotherPasswordSchema = null;
    }

    void fillSetAnotherPasswordSchema() {
        setAnotherPasswordSchema = new SetAnotherPasswordSchema("pass1234");
    }

    @Test
    public void validateAllIsCorrect() {
        fillSetAnotherPasswordSchema();
        setAnotherPasswordSchema.validate();
        assertEquals("The new password should be 'pass1234'", setAnotherPasswordSchema.getNewPassword(), "pass1234");
    }


    @Test(expected = ValidationException.class)
    public void validateNewPasswordIsBlank() {
        fillSetAnotherPasswordSchema();
        setAnotherPasswordSchema.setNewPassword("");
        setAnotherPasswordSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateNewPasswordIsNull() {
        fillSetAnotherPasswordSchema();
        setAnotherPasswordSchema.setNewPassword(null);
        setAnotherPasswordSchema.validate();
    }
}