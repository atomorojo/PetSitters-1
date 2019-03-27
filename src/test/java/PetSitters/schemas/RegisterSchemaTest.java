package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.batch.item.validator.ValidationException;

import static junit.framework.TestCase.assertEquals;

public class RegisterSchemaTest {

    RegisterSchema R;

    @Before
    public void setUp() {
        R = new RegisterSchema();
    }

    @After
    public void tearDown() {
        R = null;
    }

    void fillRegisterSchema() {
        R = new RegisterSchema("Name", "Surname", "UserN", "Pass", "Name@Surname.com", "20-11-1111");
    }

    @Test
    public void validateAllIsCorrect() {
        fillRegisterSchema();
        R.validate();
        assertEquals ("The name should be 'Name'", R.getFirstName(), "Name");
        assertEquals ("The surname should be 'Surname'", R.getLastName(), "Surname");
        assertEquals ("The username should be 'UserN'", R.getUsername(), "UserN");
        assertEquals ("The password should be 'Pass'", R.getPassword(), "Pass");
        assertEquals ("The email should be 'Name@Surname.com'", R.getEmail(), "Name@Surname.com");
        assertEquals ("The birthdate should be '20-11-1111'", R.getBirthdate(), "20-11-1111");
    }

    @Test(expected = ValidationException.class)
    public void validateFirstNameIsNull() {
        fillRegisterSchema();
        R.setFirstName(null);
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateFirstNameIsEmpty() {
        fillRegisterSchema();
        R.setFirstName("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateLastNameIsNull() {
        fillRegisterSchema();
        R.setLastName(null);
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateLastNameIsEmpty() {
        fillRegisterSchema();
        R.setLastName("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateUsernameIsNull() {
        fillRegisterSchema();
        R.setUsername(null);
        R.validate();
    }


    @Test(expected = ValidationException.class)
    public void validateUsernameIsEmpty() {
        fillRegisterSchema();
        R.setUsername("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validatePasswordIsNull() {
        fillRegisterSchema();
        R.setPassword(null);
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validatePasswordIsEmpty() {
        fillRegisterSchema();
        R.setPassword("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateEmailIsNull() {
        fillRegisterSchema();
        R.setEmail(null);
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateEmailIsEmpty() {
        fillRegisterSchema();
        R.setEmail("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateBirthdateIsNull() {
        fillRegisterSchema();
        R.setBirthdate(null);
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateBirthdateIsEmpty() {
        fillRegisterSchema();
        R.setBirthdate("");
        R.validate();
    }

}
