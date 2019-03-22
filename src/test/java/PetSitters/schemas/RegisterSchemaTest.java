package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ValidationException;

import static junit.framework.TestCase.assertEquals;

public class RegisterSchemaTest {

    RegisterSchema R;

    @Before
    public void setUp() throws Exception {
        R = new RegisterSchema();
    }

    @After
    public void tearDown() throws Exception {
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
        assertEquals ("The name should be 'Surname'", R.getLastName(), "Surname");
        assertEquals ("The name should be 'UserN'", R.getUsername(), "UserN");
        assertEquals ("The name should be 'Pass'", R.getPassword(), "Pass");
        assertEquals ("The name should be 'Name@Surname.com'", R.getEmail(), "Name@Surname.com");
        assertEquals ("The name should be '20-11-1111'", R.getBirthdate(), "20-11-1111");

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
    public void validateLastNameIsEmpty() {
        fillRegisterSchema();
        R.setLastName("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateUsernameIsEmpty() {
        fillRegisterSchema();
        R.setUsername("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validatePasswordIsEmpty() {
        fillRegisterSchema();
        R.setPassword("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateEmailIsEmpty() {
        fillRegisterSchema();
        R.setEmail("");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateBirthdateIsEmpty() {
        fillRegisterSchema();
        R.setBirthdate("");
        R.validate();
    }

}
