package PetSitters.service;

import PetSitters.entity.Report;
import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.repository.ReportRepository;
import PetSitters.repository.VerificationTokenRepository;
import PetSitters.schemas.*;
import PetSitters.repository.UserRepository;
import PetSitters.security.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    ReportRepository ReportRep;

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

    ChangePasswordSchema getFilledSchemaChangePassword() {
        ChangePasswordSchema changePasswordSchema = new ChangePasswordSchema("123", "54321");
        return changePasswordSchema;
    }

    UserPetSitters createUser() throws ParseException {
        return UserRep.save(new UserPetSitters(getFilledSchemaRegistrationPersona1()));
    }
    ReportSchema getFilledReportSchema() {
        ReportSchema reportSchema = new ReportSchema("casjua92", "No description");
        return reportSchema;
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
    @Test
    public void testValidEmailVerify() throws ParseException {
        UserPetSitters user=createUser();
        VerificationToken aux=new VerificationToken();
        aux.setUsername(user.getUsername());
        aux.setEmail(user.getEmail());
        String token=aux.getToken();
        verToken.save(aux);
        assertEquals("Email token did not verify correctly",verificationTokenService.verifyEmail(token).getStatusCode(),HttpStatus.OK);
    }

    @Test
    public void testInvalidEmailVerify() {
        String token="random string";
        Boolean good=false;
        if (ResponseEntity.status(HttpStatus.OK).equals(verificationTokenService.verifyEmail(token).getStatusCode())){
            good=true;
        }
        assertFalse("Email wrongly verified correctly",good);
    }

    @Test
    public void testValidLogin() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        LoginSchema loginUser = new LoginSchema("rod98", "123");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        final UserPetSitters user = userService.findOne(loginUser.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
    }

    @Test
    public void testChangePasswordNormal() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        ChangePasswordSchema changePasswordSchema = getFilledSchemaChangePassword();
        PSS.changePassword(changePasswordSchema, "rod98");
        UserPetSitters u = UserRep.findByUsername("rod98");
        assertTrue("The new password should be '54321'", u.isTheSamePassword("54321"));
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void testChangePasswordWithWrongOldPassword() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        ChangePasswordSchema changePasswordSchema = getFilledSchemaChangePassword();
        changePasswordSchema.setOldPassword("321");
        PSS.changePassword(changePasswordSchema, "rod98");
    }

    @Test(expected = ValidationException.class)
    public void testChangePasswordWithBlankNewPassword() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        ChangePasswordSchema changePasswordSchema = getFilledSchemaChangePassword();
        changePasswordSchema.setOldPassword("");
        PSS.changePassword(changePasswordSchema, "rod98");
    }

    @Test(expected = ValidationException.class)
    public void testChangePasswordWithNullNewPassword() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        ChangePasswordSchema changePasswordSchema = getFilledSchemaChangePassword();
        changePasswordSchema.setOldPassword(null);
        PSS.changePassword(changePasswordSchema, "rod98");
    }

    @Test
    public void setDescription() throws ParseException, ExceptionInvalidAccount {
        ModifySchema mod=new ModifySchema("Dummy Text");
        String username=createUser().getUsername();
        PSS.modify("description","Dummy Text",username);
        String description=UserRep.findByUsername(username).getDescription();
        assertEquals("Description changed",description, "Dummy Text");
    }

    @Test
    public void setCity() throws ParseException, ExceptionInvalidAccount {
        ModifySchema mod=new ModifySchema("Dummy Text");
        String username=createUser().getUsername();
        PSS.modify("city","Dummy Text",username);
        String description=UserRep.findByUsername(username).getCity();
        assertEquals("City changed",description, "Dummy Text");
    }

    @Test
    public void setImage() throws ParseException, ExceptionInvalidAccount {
        ModifySchema mod=new ModifySchema("Dummy Text");
        String username=createUser().getUsername();
        PSS.modify("image","Dummy Text",username);
        String description=UserRep.findByUsername(username).getImage();
        assertEquals("City changed",description, "Dummy Text");
    }

    @Test
    public void setAvailability() throws ParseException, ExceptionInvalidAccount {
        ModifySchema mod=new ModifySchema("Dummy Text");
        String username=createUser().getUsername();
        PSS.modify("availability","Dummy Text",username);
        String description=UserRep.findByUsername(username).getAvailability().getWhatIsThis();
        assertEquals("City changed",description, "Dummy Text");
    }

    @Test
    public void setExpert() throws ParseException, ExceptionInvalidAccount {
        ModifySchema mod=new ModifySchema("Dummy Text");
        String username=createUser().getUsername();
        PSS.modify("expert","Dummy Text",username);
        List<String> description=UserRep.findByUsername(username).getExpert();
        List<String> tocheck= new ArrayList<String>();
        tocheck.add("Dummy");
        tocheck.add("Text");
        assertEquals("City changed",description,tocheck);
    }


    public void reportAUserNormal() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        assertTrue("The user 'casjua92' should exist", UserRep.existsByUsername("casjua92"));
        ReportSchema report = getFilledReportSchema();
        PSS.report(report, "rod98");
        List<Report> reports = ReportRep.findByReporter(registerSchema1.getEmail());
        Report rep = reports.get(0);
        assertEquals("The reported should be 'rod98'", rep.getReported(), UserRep.findByUsername(report.getReported()).getEmail());
        assertEquals("The reporter should be 'casjua92'", rep.getReporter(), UserRep.findByUsername("rod98").getEmail());
        assertEquals("The description should be 'No description'", rep.getDescription(), report.getDescription());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void reportAUserWithNonExistingReporter() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        assertFalse("The user 'rod98' should not exist", UserRep.existsByUsername("rod98"));
        assertTrue("The user 'casjua92' should exist", UserRep.existsByUsername("casjua92"));
        ReportSchema report = getFilledReportSchema();
        PSS.report(report, "rod98");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void reportAUserWithNonExistingReported() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        assertFalse("The user 'casjua92' should not exist", UserRep.existsByUsername("casjua92"));
        ReportSchema report = getFilledReportSchema();
        PSS.report(report, "rod98");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void reportAUserWithNonExistingUsersReporterAndReported() throws ParseException, ExceptionInvalidAccount {
        assertFalse("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        assertFalse("The user 'casjua92' should not exist", UserRep.existsByUsername("casjua92"));
        ReportSchema report = getFilledReportSchema();
        PSS.report(report, "rod98");
    }

    @Test
    public void getAllUsersLight() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        Boolean good=false;
        List<LightUserSchema> users= PSS.getUsersLight();
        if (UserRep.findAll().size()==users.size()) {
            good=true;
        }
        assertTrue("All users received",good);
    }

    @Test
    public void getUserFull() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        Boolean good=false;
        FullUserSchema user= PSS.getUserFull(registerSchema1.getUsername());
        assertTrue("All users received",user.getName().equals(registerSchema1.getFirstName()+" "+registerSchema1.getLastName()));
    }

}
