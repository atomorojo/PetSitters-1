package PetSitters.service;

import PetSitters.entity.User;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.RegisterSchema;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;


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

        User u = UserRep.findByUsername("rod98");

        assertEquals("Expected the firstName Rodrigo", registerSchema.getFirstName(), u.getFirstName());
        assertEquals("Expected the lastName Gomez", registerSchema.getLastName(), u.getLastName());
        assertEquals("Expected the username rod98", registerSchema.getUsername(), u.getUsername());
        assertEquals("Expected the password 123", registerSchema.getPassword(), u.getPassword());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date birthDate = format.parse(registerSchema.getBirthdate());
        assertEquals("Expected the birthdate 20-12-1998", birthDate, u.getBirthdate());
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
}