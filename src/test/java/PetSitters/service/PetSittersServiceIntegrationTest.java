package PetSitters.service;

import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.repository.VerificationTokenRepository;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.LoginSchema;
import PetSitters.schemas.RegisterSchema;
import PetSitters.security.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PetSittersServiceIntegrationTest {

    @Autowired
    PetSittersService PSS;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private VerificationTokenRepository verToken;


    @Autowired
    private UserServiceImpl userService;

    @Autowired
    VerificationTokenService verificationTokenService;

    @Autowired
    UserRepository UserRep;

    @Autowired
    AuthenticationManager authenticationManager;

    @After
    public void tearDown() {
        PSS = null;
        UserRep.deleteAll();
    }

    RegisterSchema getFilledSchemaRegistrationPersona1() {
        RegisterSchema registerSchema = new RegisterSchema("Rodrigo", "Gomez", "rod98", "123", "a@b.com", "20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona2() {
        RegisterSchema registerSchema = new RegisterSchema("Juan", "del Castillo", "casjua92", "789", "a@example.com", "20-7-1992");
        return registerSchema;
    }

    DeleteAccountSchema getFilledSchemaDeletion() {
        DeleteAccountSchema deleteAccount = new DeleteAccountSchema("123");
        return deleteAccount;
    }

    @Test
    public void testRegisterNormal() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        UserPetSitters u = UserRep.findByUsername("rod98");
        assertEquals("Expected the firstName 'Rodrigo'", u.getFirstName(), registerSchema.getFirstName());
        assertEquals("Expected the lastName 'Gomez'", u.getLastName(), registerSchema.getLastName());
        assertEquals("Expected the username 'rod98'", u.getUsername(), registerSchema.getUsername());
        assertTrue("Expected the password '123'", new BCryptPasswordEncoder().matches("123", u.getPassword()));
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date birthDate = format.parse(registerSchema.getBirthdate());
        assertEquals("Expected the birthdate '20-12-1998'", u.getBirthdate(), birthDate);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testRegisterDuplicatedWithUsername() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        registerSchema2.setUsername(registerSchema.getUsername());
        PSS.register(registerSchema2);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testRegisterDuplicatedWithEmail() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        registerSchema2.setEmail(registerSchema.getEmail());
        PSS.register(registerSchema2);
    }

    @Test(expected = ParseException.class)
    public void testRegisterErrorInDateFormat() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        registerSchema.setBirthdate("20/12/1998");
        PSS.register(registerSchema);
    }

    @Test
    public void testDeleteExistingAccount() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        DeleteAccountSchema deleteAccount = getFilledSchemaDeletion();
        PSS.deleteAccount(deleteAccount, "rod98");
        assertFalse("The user 'rod98' should not exist", UserRep.existsByUsername("rod98"));
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void testDeleteExistingAccountWithDifferentPassword() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        DeleteAccountSchema deleteAccount = getFilledSchemaDeletion();
        deleteAccount.setPassword("321");
        PSS.deleteAccount(deleteAccount, "rod98");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void testDeleteNonExistingAccount() throws ExceptionInvalidAccount {
        DeleteAccountSchema deleteAccount = getFilledSchemaDeletion();
        assertFalse("The user 'rod98' should not exist", UserRep.existsByUsername("rod98"));
        PSS.deleteAccount(deleteAccount, "rod98");
    }

    @Test(expected = AuthenticationException.class)
    public void testInvalidLogin() throws AuthenticationException {
        LoginSchema loginUser = new LoginSchema("fail", "fail");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        final UserPetSitters user = userService.findOne(loginUser.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
    }

    public void testValidEmailVerify() {
        String token=new VerificationToken().getToken();
        Boolean good=false;
        if (ResponseEntity.status(HttpStatus.OK).equals(verificationTokenService.verifyEmail(token).getStatusCode())){
            good=true;
        }
        assertTrue("Email token did not verify correctly",good);
    }
    public void testInvalidEmailVerify() {
        String token="random string";
        Boolean good=false;
        if (ResponseEntity.status(HttpStatus.OK).equals(verificationTokenService.verifyEmail(token).getStatusCode())){
            good=true;
        }
        assertFalse("Email wrongly verified correctly",good);
    }


    public void testValidLogin() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        LoginSchema loginUser = new LoginSchema("rod98", "123");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        final UserPetSitters user = userService.findOne(loginUser.getUsername());
        final String token = jwtTokenUtil.generateToken(user);

    }
}
