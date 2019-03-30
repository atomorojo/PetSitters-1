package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class ChangePasswordSchemaTest {

    ChangePasswordSchema changePass;

    @Before
    public void setUp() {
        changePass = new ChangePasswordSchema();
    }

    @After
    public void tearDown() {
        changePass = null;
    }

    void fillChangePasswordSchema() {
        changePass = new ChangePasswordSchema("1234", "54321");
    }

    @Test
    public void validateAllIsCorrect() {
        fillChangePasswordSchema();
        changePass.validate();
        assertEquals("The old password should be '1234'", changePass.getOldPassword(), "1234");
        assertEquals("The new password should be '54321'", changePass.getNewPassword(), "54321");
    }


    @Test(expected = ValidationException.class)
    public void validateOldPasswordIsBlank() {
        fillChangePasswordSchema();
        changePass.setNewPassword("");
        changePass.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateOldPasswordIsNull() {
        fillChangePasswordSchema();
        changePass.setNewPassword(null);
        changePass.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateNewPasswordIsBlank() {
        fillChangePasswordSchema();
        changePass.setOldPassword("");
        changePass.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateNewPasswordIsEmpty() {
        fillChangePasswordSchema();
        changePass.setOldPassword(null);
        changePass.validate();
    }
}