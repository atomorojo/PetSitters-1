package PetSitters.service;

import PetSitters.domain.Coordinates;
import PetSitters.entity.Chat;
import PetSitters.entity.Report;
import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.repository.ChatRepository;
import PetSitters.repository.ReportRepository;
import PetSitters.repository.VerificationTokenRepository;
import PetSitters.schemas.*;
import PetSitters.repository.UserRepository;
import PetSitters.security.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.validator.ValidationException;
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

import java.io.IOException;
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

    @Autowired
    ChatRepository ChatRep;

    @After
    public void tearDown() {  // Add here all the repositories
        PSS = null;
        UserRep.deleteAll();
        ReportRep.deleteAll();
        ChatRep.deleteAll();
    }

    RegisterSchema getFilledSchemaRegistrationPersona1() {
        RegisterSchema registerSchema = new RegisterSchema("Rodrigo", "Gomez", "rod98", "123", "a@b.com", "20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona2() {
        RegisterSchema registerSchema = new RegisterSchema("Juan", "del Castillo", "casjua92", "789", "a@example.com", "20-7-1992");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona3() {
        RegisterSchema registerSchema = new RegisterSchema("Pedro", "Suarez", "pes44", "1542", "a@bo.com", "20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona4() {
        RegisterSchema registerSchema = new RegisterSchema("Mario", "Gonzalo", "marGonz", "789", "a@gre.com", "20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona5() {
        RegisterSchema registerSchema = new RegisterSchema("Gregorio", "Lopez", "gre647", "abc123", "a@sop.com", "20-12-1998");
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

    GetCoordinatesSchema getFilledGetCoordinatesSchema() {
        GetCoordinatesSchema getCoordinatesSchema = new GetCoordinatesSchema("Los Angeles");
        return getCoordinatesSchema;
    }

    StartChatSchema getFilledStartChatSchema() {
        StartChatSchema startChatSchema = new StartChatSchema("rod98");
        return startChatSchema;
    }

    StartChatSchema getFilledStartChatSchema(String name) {
        StartChatSchema startChatSchema = new StartChatSchema(name);
        return startChatSchema;
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
    public void testValidEmailVerify() throws ParseException, IOException {
        UserPetSitters user=createUser();
        VerificationToken aux=new VerificationToken();
        aux.setUsername(user.getUsername());
        aux.setEmail(user.getEmail());
        String token=aux.getToken();
        verToken.save(aux);
        assertEquals("Email token did not verify correctly",verificationTokenService.verifyEmail(token).getStatusCode(),HttpStatus.OK);
    }

    @Test
    public void testInvalidEmailVerify() throws IOException {
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
        ModifySchema mod=new ModifySchema("Dummy''Text");
        String username=createUser().getUsername();
        PSS.modify("expert","Dummy''Text",username);
        List<String> description=UserRep.findByUsername(username).getExpert();
        List<String> tocheck= new ArrayList<String>();
        tocheck.add("Dummy");
        tocheck.add("Text");
        assertEquals("Expert is different",description,tocheck);
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
    public void reportTheReporterUser() throws Exception {
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
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

    public void getCoordinatesFromService() throws IOException, JSONException, ExceptionServiceError {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
        assertEquals("The latitude should be 34.0536909", c.getLatitude(), 34.0536909, 0.05);
        assertEquals("The longitude should be -118.2427666", c.getLongitude(), -118.2427666, 0.05);
    }

    public void getCoordinatesFromServiceWithCachedResult() throws IOException, JSONException, ExceptionServiceError {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
        assertEquals("The latitude should be 34.0536909", c.getLatitude(), 34.0536909, 0.05);
        assertEquals("The longitude should be -118.2427666", c.getLongitude(), -118.2427666, 0.05);
        c = PSS.getCoordinates(getCoordinatesSchema);
        assertEquals("The latitude should be 34.0536909", c.getLatitude(), 34.0536909, 0.05);
        assertEquals("The longitude should be -118.2427666", c.getLongitude(), -118.2427666, 0.05);
    }

    @Test(expected = ExceptionServiceError.class)
    public void getCoordinatesFromServiceWithNonExistingCity() throws IOException, JSONException, ExceptionServiceError {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        getCoordinatesSchema.setCity("Llefsdfida");
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
    }

    @Test(expected = ValidationException.class)
    public void getCoordinatesFromServiceEmptyCity() throws IOException, JSONException, ExceptionServiceError {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        getCoordinatesSchema.setCity("");
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
    }

    @Test(expected = ValidationException.class)
    public void getCoordinatesFromServiceNullCity() throws IOException, JSONException, ExceptionServiceError {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        getCoordinatesSchema.setCity(null);
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
    }

    @Test
    public void getAllUsersLight() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        Boolean good=false;
        List<LightUserSchema> users= PSS.getUsersLight("rod98");
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

    @Test
    public void getUserName() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        UserRep.save(myUser);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        UserPetSitters myUser2=UserRep.findByUsername("casjua92");
        UserRep.save(myUser2);
        Boolean good=false;
        List<LightUserSchema> users= PSS.getUsersName("Juan","rod98");
        for (LightUserSchema user:users)  {
            if (user.getName().equals(registerSchema2.getFirstName()+" "+registerSchema2.getLastName())) good=true;
        }
        assertTrue("User with name Juan received",good);
    }
    @Test
    public void getUserExpert() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        List<String> why=new ArrayList<String>();
        why.add("cat");
        myUser.setExpert(why);
        UserRep.save(myUser);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        UserPetSitters myUser2=UserRep.findByUsername("casjua92");
        myUser2.setExpert(why);
        UserRep.save(myUser2);
        Boolean good=false;
        List<LightUserSchema> users= PSS.getUsersExpert("cat","rod98");
        for (LightUserSchema user:users)  {
            if (user.getName().equals(registerSchema2.getFirstName()+" "+registerSchema2.getLastName())) good=true;

        }
        assertTrue("User with cat received",good);
    }

    @Test
    public void startNewChatWithAnotherUser() throws ExceptionInvalidAccount, ParseException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        StartChatSchema startChatSchema = getFilledStartChatSchema();
        PSS.startChat(startChatSchema, "casjua92");
        Chat c = ChatRep.findByUsernameAAndUsernameB("casjua92","rod98");
        assertEquals("UsernameA should be 'casjua92'", c.getUsernameA(), "casjua92");
        assertEquals("UsernameA should be 'rod98'", c.getUsernameB(), "rod98");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void startNewChatWithHimself() throws Exception {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);

        StartChatSchema startChatSchema = getFilledStartChatSchema();
        PSS.startChat(startChatSchema, "rod98");
        ChatRep.findByUsernameAAndUsernameB("rod98","casjua92");
    }

    @Test(expected = DuplicateKeyException.class)
    public void startNewChatWithAnotherUserDuplicated() throws Exception {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        StartChatSchema startChatSchema = getFilledStartChatSchema();
        PSS.startChat(startChatSchema, "casjua92");
        Chat c = ChatRep.findByUsernameAAndUsernameB("casjua92","rod98");
        assertEquals("UsernameA should be 'casjua92'", c.getUsernameA(), "casjua92");
        assertEquals("UsernameA should be 'rod98'", c.getUsernameB(), "rod98");
        PSS.startChat(startChatSchema, "casjua92");
    }

    @Test(expected = DuplicateKeyException.class)
    public void startNewChatWithDuplicateReversed() throws Exception {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        StartChatSchema startChatSchema = getFilledStartChatSchema();

        startChatSchema.setOtherUsername("casjua92");

        PSS.startChat(startChatSchema, "rod98");
        Chat c = ChatRep.findByUsernameAAndUsernameB("casjua92","rod98");
        assertEquals("UsernameA should be 'casjua92'", c.getUsernameA(), "casjua92");
        assertEquals("UsernameA should be 'rod98'", c.getUsernameB(), "rod98");

        startChatSchema = getFilledStartChatSchema();
        PSS.startChat(startChatSchema, "casjua92");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void startNewChatWithNonExistingUser() throws Exception {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);

        StartChatSchema startChatSchema = getFilledStartChatSchema();
        PSS.startChat(startChatSchema, "casjua92");
        Chat c = ChatRep.findByUsernameAAndUsernameB("casjua92","rod98");
        assertEquals("UsernameA should be 'casjua92'", c.getUsernameA(), "casjua92");
        assertEquals("UsernameA should be 'rod98'", c.getUsernameB(), "rod98");
    }
    @Test
    public void distanceCalc() throws IOException, JSONException, ExceptionServiceError, ParseException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        myUser.setCity("Barcelona");
        UserRep.save(myUser);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        UserPetSitters myUser2=UserRep.findByUsername("casjua92");
        myUser2.setCity("Sant Boi de Llobregat");
        UserRep.save(myUser2);
        List<LightUserSchema> people=PSS.getUsersDistance(100,"rod98");
        assertTrue("They are within 100km",people.size()>0);
    }
    @Test
    public void distanceCalcError() throws IOException, JSONException, ExceptionServiceError, ParseException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        myUser.setCity("Barcelona");
        UserRep.save(myUser);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        UserPetSitters myUser2=UserRep.findByUsername("casjua92");
        myUser2.setCity("Los Angeles");
        UserRep.save(myUser2);
        List<LightUserSchema> people=PSS.getUsersDistance(100,"rod98");
        assertTrue("They are not within 100km",people.size()==0);
    }
    @Test
    public void addFavorites() throws IOException, JSONException, ExceptionServiceError, ParseException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        myUser.addFavorites("casjua92");
        UserRep.save(myUser);
        List<LightUserSchema> people=PSS.getFavorites("rod98");
        assertTrue("There are no favorites",people.size()==1);
    }

    @Test
    public void getOpenedChats() throws ParseException, ExceptionInvalidAccount, JSONException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        RegisterSchema registerSchema3 = getFilledSchemaRegistrationPersona3();
        PSS.register(registerSchema3);
        RegisterSchema registerSchema4 = getFilledSchemaRegistrationPersona4();
        PSS.register(registerSchema4);
        RegisterSchema registerSchema5 = getFilledSchemaRegistrationPersona5();
        PSS.register(registerSchema5);

        StartChatSchema startChatSchema1 = getFilledStartChatSchema("marGonz");
        StartChatSchema startChatSchema2 = getFilledStartChatSchema("casjua92");
        StartChatSchema startChatSchema3 = getFilledStartChatSchema("rod98");
        StartChatSchema startChatSchema4 = getFilledStartChatSchema("pes44");

        PSS.startChat(startChatSchema1, "gre647");
        PSS.startChat(startChatSchema2, "gre647");
        PSS.startChat(startChatSchema3, "gre647");
        PSS.startChat(startChatSchema4, "gre647");

        JSONArray array = PSS.getOpenedChats("gre647");
        assertEquals("Output should be 'pes44'", array.get(0), "pes44");
        assertEquals("Output should be 'rod98'", array.get(1), "rod98");
        assertEquals("Output should be 'casjua92'", array.get(2), "casjua92");
        assertEquals("Output should be 'marGonz'", array.get(3), "marGonz");
    }

    @Test
    public void getOpenedChatsEmpty() throws ParseException, ExceptionInvalidAccount, JSONException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);

        JSONArray array = PSS.getOpenedChats("rod98");
        assertEquals("Output should be empty", array.length(), 0);
    }

}
