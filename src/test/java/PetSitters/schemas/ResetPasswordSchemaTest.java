package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class ResetPasswordSchemaTest {

    ResetPasswordSchema resetPasswordSchema;

    @Before
    public void setUp() {
        resetPasswordSchema = new ResetPasswordSchema();
    }

    @After
    public void tearDown() {
        resetPasswordSchema = null;
    }

    void fillResetPasswordSchema() {
        resetPasswordSchema = new ResetPasswordSchema("a@b.com");
    }

    @Test
    public void validateAllIsCorrect() {
        fillResetPasswordSchema();
        resetPasswordSchema.validate();
        assertEquals("The email should be 'a@b.com'", resetPasswordSchema.getEmail(), "a@b.com");
    }


    @Test(expected = ValidationException.class)
    public void validateEmailIsBlank() {
        fillResetPasswordSchema();
        resetPasswordSchema.setEmail("");
        resetPasswordSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateEmailIsNull() {
        fillResetPasswordSchema();
        resetPasswordSchema.setEmail(null);
        resetPasswordSchema.validate();
    }
}