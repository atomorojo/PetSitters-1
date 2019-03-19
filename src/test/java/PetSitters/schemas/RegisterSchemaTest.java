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

    @Test
    public void validateAllIsCorrect() {
        R.setFirstName("Name");
        R.setLastName("Surname");
        R.setUsername("UserN");
        R.setPassword("Pass");
        R.setBirthdate("20-11-1111");
        R.validate();
        assertEquals ("The name should be 'Name'", R.getFirstName(), "Name");
        assertEquals ("The name should be 'Surname'", R.getLastName(), "Surname");
        assertEquals ("The name should be 'UserN'", R.getUsername(), "UserN");
        assertEquals ("The name should be 'Pass'", R.getPassword(), "Pass");
        assertEquals ("The name should be '20-11-1111'", R.getBirthdate(), "20-11-1111");

    }

    @Test(expected = ValidationException.class)
    public void validateFirstNameIsNull() {
        R.setLastName("Surname");
        R.setUsername("UserN");
        R.setPassword("Pass");
        R.setBirthdate("20-11-1111");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateFirstNameIsEmpty() {
        R.setFirstName("");
        R.setLastName("Surname");
        R.setUsername("UserN");
        R.setPassword("Pass");
        R.setBirthdate("20-11-1111");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateLastNameIsEmpty() {
        R.setFirstName("Surname");
        R.setLastName("");
        R.setUsername("UserN");
        R.setPassword("Pass");
        R.setBirthdate("20-11-1111");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateUsernameIsEmpty() {
        R.setFirstName("Surname");
        R.setLastName("jjij");
        R.setUsername("");
        R.setPassword("Pass");
        R.setBirthdate("20-11-1111");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validatePasswordIsEmpty() {
        R.setFirstName("Surname");
        R.setLastName("joio");
        R.setUsername("UserN");
        R.setPassword("");
        R.setBirthdate("20-11-1111");
        R.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateBirthdateIsEmpty() {
        R.setFirstName("Surname");
        R.setLastName("jioijo");
        R.setUsername("UserN");
        R.setPassword("Pass");
        R.setBirthdate("");
        R.validate();
    }

}