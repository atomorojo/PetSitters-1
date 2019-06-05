package PetSitters.entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserPetSittersTest {

    UserPetSitters U;

    @Before
    public void setUp() throws Exception {
        U = new UserPetSitters();
    }

    @After
    public void tearDown() throws Exception {
        U = null;
    }

    @Test
    public void testIsTheSamePasswordWithTheSamePassword() {
        U.setPassword("Pass");
        assertTrue("The password should be 'Pass'", U.isTheSamePassword("Pass"));
    }

    @Test
    public void testIsTheSamePasswordWithDifferentPassword() {
        U.setPassword("PassDifferent");
        assertFalse("The password should be 'Pass'", U.isTheSamePassword("Pass"));
    }

    @Test
    public void compareToLower() {
        U.setUsername("A");
        UserPetSitters UU = new UserPetSitters();
        UU.setUsername("B");
        assertEquals("U should be lower than UU", U.compareTo(UU), -1);
    }

    @Test
    public void compareToEqual() {
        U.setUsername("B");
        UserPetSitters UU = new UserPetSitters();
        UU.setUsername("B");
        assertEquals("U should be equal to UU", U.compareTo(UU), 0);
    }

    @Test
    public void compareToGreater() {
        U.setUsername("B");
        UserPetSitters UU = new UserPetSitters();
        UU.setUsername("A");
        assertEquals("U should be greater than UU", U.compareTo(UU), 1);
    }
}