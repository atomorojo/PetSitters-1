package PetSitters.service;

import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.schemas.RegisterSchema;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PetSittersServiceTest {

    @Autowired
    PetSittersService PSS;

    @Autowired
    UserRepository UserRep;

    @After
    public void tearDown() throws Exception {
        PSS = null;
        UserRep.deleteAll();
    }

    @Test
    public void testRegisterNormal() throws ParseException {
        RegisterSchema registerSchema = Mockito.mock(RegisterSchema.class);
        Mockito.when(registerSchema.getFirstName()).thenReturn("Rodrigo");
        Mockito.when(registerSchema.getLastName()).thenReturn("Gomez");
        Mockito.when(registerSchema.getUsername()).thenReturn("rod98");
        Mockito.when(registerSchema.getPassword()).thenReturn("123");
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20-12-1998");

        PSS.register(registerSchema);

        UserPetSitters u = UserRep.findByUsername("rod98");

        assertEquals("Expected the firstName 'Rodrigo'", u.getFirstName(), registerSchema.getFirstName());
        assertEquals("Expected the lastName 'Gomez'", u.getLastName(), registerSchema.getLastName());
        assertEquals("Expected the username 'rod98'", u.getUsername(), registerSchema.getUsername());
        assertTrue("Expected the password '123'",new BCryptPasswordEncoder().matches("123",u.getPassword()));
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date birthDate = format.parse(registerSchema.getBirthdate());
        assertEquals("Expected the birthdate '20-12-1998'", u.getBirthdate(), birthDate);
    }

    @Test(expected = ParseException.class)
    public void testRegisterErrorInDateFormat() throws ParseException {
        RegisterSchema registerSchema = Mockito.mock(RegisterSchema.class);
        Mockito.when(registerSchema.getFirstName()).thenReturn("Rodrigo");
        Mockito.when(registerSchema.getLastName()).thenReturn("Gomez");
        Mockito.when(registerSchema.getUsername()).thenReturn("rod98");
        Mockito.when(registerSchema.getPassword()).thenReturn("123");
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20/12/1998");

        PSS.register(registerSchema);
    }

    @Test
    public void testDeleteExistingAccount() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema = Mockito.mock(RegisterSchema.class);
        Mockito.when(registerSchema.getFirstName()).thenReturn("Rodrigo");
        Mockito.when(registerSchema.getLastName()).thenReturn("Gomez");
        Mockito.when(registerSchema.getUsername()).thenReturn("rod98");
        Mockito.when(registerSchema.getPassword()).thenReturn("123");
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20-12-1998");

        PSS.register(registerSchema);

        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));

        DeleteAccountSchema deleteAccount = new DeleteAccountSchema();
        deleteAccount.setUsername("rod98");

        PSS.deleteAccount(deleteAccount);

        assertFalse("The user 'rod98' should not exist", UserRep.existsByUsername("rod98"));
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void testDeleteNonExistingAccount() throws ExceptionInvalidAccount {
        DeleteAccountSchema deleteAccount = Mockito.mock(DeleteAccountSchema.class);
        Mockito.when(deleteAccount.getUsername()).thenReturn("rod981");

        assertFalse("The user 'rod981' should not exist", UserRep.existsByUsername("rod981"));

        PSS.deleteAccount(deleteAccount);
    }
}