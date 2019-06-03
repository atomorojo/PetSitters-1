package PetSitters.service;

import PetSitters.domain.Animal;
import PetSitters.domain.Coordinates;
import PetSitters.entity.*;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.repository.*;
import PetSitters.schemas.*;
import PetSitters.security.JwtTokenUtil;
import PetSitters.security.UserServiceImpl;
import PetSitters.security.VerificationToken;
import PetSitters.security.VerificationTokenService;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
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
import java.util.LinkedList;
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

    @Autowired
    ContractRepository ContractRepository;

    @Autowired
    MessageRepository MessageRep;

    @Autowired
    GridFS gridFs;

    @Autowired
    ValuationRepository ValuationRep;

    @After
    public void tearDown() {  // Add here all the repositories
        PSS = null;
        UserRep.deleteAll();
        ReportRep.deleteAll();
        ChatRep.deleteAll();
        MessageRep.deleteAll();
        ContractRepository.deleteAll();
        ValuationRep.deleteAll();
    }

    RegisterSchema getFilledSchemaRegistrationPersona1() {
        RegisterSchema registerSchema = new RegisterSchema("Rodrigo", "Gomez", "rod98", "123", "Barcelona", "a@b.com", "20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona2() {
        RegisterSchema registerSchema = new RegisterSchema("Juan", "del Castillo", "casjua92", "789", "Barcelona", "a@example.com", "20-7-1992");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona3() {
        RegisterSchema registerSchema = new RegisterSchema("Pedro", "Suarez", "pes44", "1542", "Barcelona", "a@bo.com", "20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona4() {
        RegisterSchema registerSchema = new RegisterSchema("Mario", "Gonzalo", "marGonz", "789", "Barcelona", "a@gre.com", "20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona5() {
        RegisterSchema registerSchema = new RegisterSchema("Gregorio", "Lopez", "gre647", "abc123", "Barcelona", "a@sop.com", "20-12-1998");
        return registerSchema;
    }

    DeleteAccountSchema getFilledSchemaDeletion(String password) {
        DeleteAccountSchema deleteAccount = new DeleteAccountSchema(password);
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

    ReportSchema getFilledReportSchema2() {
        ReportSchema reportSchema = new ReportSchema("rod98", "No description");
        return reportSchema;
    }
    GetCoordinatesSchema getFilledGetCoordinatesSchema() {
        GetCoordinatesSchema getCoordinatesSchema = new GetCoordinatesSchema("Los Angeles");
        return getCoordinatesSchema;
    }

    MessageSchema getMessageSchema(String content, String userWhoReceives, String isMultimedia) {
        MessageSchema messageSchema = new MessageSchema(userWhoReceives, isMultimedia, content);
        return messageSchema;
    }

    DeleteChatSchema getFilledDeleteChatSchema(String username) {
        DeleteChatSchema deleteChatSchema = new DeleteChatSchema(username);
        return deleteChatSchema;
    }

    ValuationSchema getFilledValuationSchema(String username) {
        ValuationSchema valuationSchema = new ValuationSchema(username, "Comment", 1);
        return valuationSchema;
    }

    TranslationSchema getFilledTranslationSchema() {
        String[] list = {"Hello", "Monday"};
        return new TranslationSchema(list, "es");
    }

    TranslationSchema getFilledTranslationSchema2() {
        String[] list = {"Hello", "Monday", "Blue"};
        return new TranslationSchema(list, "es");
    }

    @Test
    public void testRegisterNormal() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        UserPetSitters u = UserRep.findByUsername("rod98");
        assertEquals("Expected the firstName 'Rodrigo'", u.getFirstName(), registerSchema.getFirstName());
        assertEquals("Expected the lastName 'Gomez'", u.getLastName(), registerSchema.getLastName());
        assertEquals("Expected the username 'rod98'", u.getUsername(), registerSchema.getUsername());
        assertEquals("Expected the city 'Barcelona'", u.getCity(), registerSchema.getCity());
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
        DeleteAccountSchema deleteAccount = getFilledSchemaDeletion("123");
        PSS.deleteAccount(deleteAccount, "rod98");
        assertFalse("The user 'rod98' should not exist", UserRep.existsByUsername("rod98"));
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void testDeleteExistingAccountWithDifferentPassword() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        DeleteAccountSchema deleteAccount = getFilledSchemaDeletion("123");
        deleteAccount.setPassword("321");
        PSS.deleteAccount(deleteAccount, "rod98");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void testDeleteNonExistingAccount() throws ExceptionInvalidAccount {
        DeleteAccountSchema deleteAccount = getFilledSchemaDeletion("123");
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

    public void getCoordinatesFromService() throws Exception {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
        assertEquals("The latitude should be 34.0536909", c.getLatitude(), 34.0536909, 0.05);
        assertEquals("The longitude should be -118.2427666", c.getLongitude(), -118.2427666, 0.05);
    }

    public void getCoordinatesFromServiceWithCachedResult() throws Exception {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
        assertEquals("The latitude should be 34.0536909", c.getLatitude(), 34.0536909, 0.05);
        assertEquals("The longitude should be -118.2427666", c.getLongitude(), -118.2427666, 0.05);
        c = PSS.getCoordinates(getCoordinatesSchema);
        assertEquals("The latitude should be 34.0536909", c.getLatitude(), 34.0536909, 0.05);
        assertEquals("The longitude should be -118.2427666", c.getLongitude(), -118.2427666, 0.05);
    }

    @Test(expected = ExceptionServiceError.class)
    public void getCoordinatesFromServiceWithNonExistingCity() throws Exception {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        getCoordinatesSchema.setCity("Llefsdfida");
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
    }

    @Test(expected = ValidationException.class)
    public void getCoordinatesFromServiceEmptyCity() throws Exception {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        getCoordinatesSchema.setCity("");
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
    }

    @Test(expected = ValidationException.class)
    public void getCoordinatesFromServiceNullCity() throws Exception {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        getCoordinatesSchema.setCity(null);
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
    }

    @Test
    public void getAllUsersLight() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        Boolean good=false;
        List<LightUserSchema> users= PSS.getUsersLight("rod98");
        if (UserRep.findAll().size()-1==users.size()) {
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
    public void getUserValoration() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        myUser.setStars(2.0);
        UserRep.save(myUser);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        UserPetSitters myUser2=UserRep.findByUsername("casjua92");
        myUser2.setStars(1.0);
        UserRep.save(myUser2);
        Boolean good=false;
        List<LightUserSchema> users= PSS.getUsersValoration(3,0,"rod98");
        for (LightUserSchema user:users)  {
            System.out.println(user.getName()+" "+registerSchema2.getFirstName()+" "+registerSchema2.getLastName());
            if (user.getName().equals(registerSchema2.getFirstName()+" "+registerSchema2.getLastName())) good=true;

        }
        assertTrue("User with correct valoration not received",good);
    }

    @Test
    public void distanceCalc() throws Exception {
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
    public void distanceCalcError() throws Exception {
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

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        List<ChatPreviewSchema> list = PSS.getOpenedChats(registerSchema2.getUsername());
        ChatPreviewSchema chatPreviewSchema = list.get(0);
        assertEquals("Output should be 'Rodrigo Gomez'", chatPreviewSchema.getName(), "Rodrigo Gomez");
        assertEquals("Output should be '" + registerSchema1.getUsername() + "'", chatPreviewSchema.getUsername(), registerSchema1.getUsername());
        assertTrue("Output should be 'null'", chatPreviewSchema.getProfileImage() == null);
        assertEquals("Output should be 'Hello'", chatPreviewSchema.getLastMessage(), "Hello");
    }

    @Test
    public void getOpenedChatsReportedWithoutMessagesInOneSide() throws ParseException, ExceptionInvalidAccount, JSONException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        ReportSchema reportSchema = getFilledReportSchema();
        PSS.report(reportSchema, "rod98");

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        List<ChatPreviewSchema> list1 = PSS.getOpenedChats(registerSchema1.getUsername());
        List<ChatPreviewSchema> list2 = PSS.getOpenedChats(registerSchema2.getUsername());
        assertEquals("List1 should not be empty", list1.size(), 1);
        assertEquals("List2 should be empty", list2.size(), 0);
    }

    @Test
    public void getOpenedChatsEmpty() throws ParseException, ExceptionInvalidAccount, JSONException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);

        List<ChatPreviewSchema> list = PSS.getOpenedChats(registerSchema1.getUsername());
        assertEquals("Output should be empty", list.size(), 0);
    }

    @Test
    public void proposeContract() throws IOException, JSONException, ExceptionServiceError, ParseException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        ContractSchema contract=new ContractSchema();
        Animal dog=new Animal();
        dog.setName("Doggy");
        dog.setTipus("Dog");
        List<Animal> an=new ArrayList<Animal>();
        an.add(dog);
        contract.setAnimal(an);
        contract.setEnd("2019-01-01");
        contract.setStart("2018-01-01");
        contract.setFeedback(false);
        contract.setUsername(registerSchema2.getUsername());
        PSS.proposeContract(contract, myUser.getUsername());
        Contract c=ContractRepository.findByUsernameFromAndUsernameTo(myUser.getUsername(),registerSchema2.getUsername());
        assertTrue("Is not null",c!=null);
    }
    @Test
    public void acceptContract() throws IOException, JSONException, ExceptionServiceError, ParseException {
        proposeContract();
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.acceptContract(registerSchema1.getUsername(),registerSchema2.getUsername());
        Contract c=ContractRepository.findByUsernameFromAndUsernameTo(registerSchema1.getUsername(),registerSchema2.getUsername());
        System.out.println(c.getAccepted());
        assertTrue("Is true",c.getAccepted());
    }
    @Test
    public void rejectContract() throws IOException, JSONException, ExceptionServiceError, ParseException {
        proposeContract();
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.rejectContract(registerSchema1.getUsername(),registerSchema2.getUsername());
        Contract c=ContractRepository.findByUsernameFromAndUsernameTo(registerSchema1.getUsername(),registerSchema2.getUsername());
        assertTrue("Is null",c==null);
    }

    @Test
    public void getContractsTo() throws IOException, JSONException, ExceptionServiceError, ParseException {
        proposeContract();
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        List<Contract> c=PSS.contractListReceived(registerSchema2.getUsername());
        assertTrue("Is null",c.size()==1);
    }

    @Test
    public void getContractsFrom() throws IOException, JSONException, ExceptionServiceError, ParseException {
        proposeContract();
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        List<Contract> c=PSS.contractListProposed(registerSchema1.getUsername());
        assertTrue("Is null",c.size()==1);
    }

    @Test
    public void isContracted() throws IOException, JSONException, ExceptionServiceError, ParseException {
        proposeContract();
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        Contract c=PSS.isContracted(registerSchema1.getUsername(),registerSchema2.getUsername());
        assertTrue("Is null",c!=null);
    }

    @Test
    public void sendMessage() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        List<Message> listMessages = MessageRep.findAll();
        assertNotEquals("The size of the messages repository should be greater than 0", listMessages.size(), 0);
        Message m = listMessages.get(0);
        assertEquals("The content should be 'Hello'", m.getContent(), "Hello");
        String sender = registerSchema1.getUsername();
        String receptor = registerSchema2.getUsername();
        assertEquals("The userWhoReceives should be '" + receptor + "'", m.getUserWhoReceives(), receptor);
        assertEquals("The userWhoSends should be '" + sender + "'", m.getUserWhoSends(), sender);
        assertTrue("The message should be sent before now", m.getWhenSent().before(new Date()));
        assertTrue("The message should be visible", m.getVisible());
        assertFalse("The message should not be a multimedia file", m.getMultimedia());

        List<Chat> listChats = ChatRep.findAll();
        assertNotEquals("The size of the chats repository should be greater than 0", listChats.size(), 0);
        Chat c = listChats.get(0);
        assertEquals("The smallest lexicographically username should be 'casjua92'", c.getUsernameA(), "casjua92");
        assertEquals("The greatest lexicographically username should be 'rod98'", c.getUsernameB(), "rod98");
        assertEquals("The preview of the last message of the chat should be the content of the last chat sent", c.getLastMessage(), m.getContent());
        assertEquals("The last chat should be user when the last message was sent", c.getLastUse(), m.getWhenSent());
    }

    @Test
    public void sendMessageWithMultimedia() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "true");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        List<Message> listMessages = MessageRep.findAll();
        Message m = listMessages.get(0);
        assertTrue("The message should be a multimedia file", m.getMultimedia());

        List<Chat> listChats = ChatRep.findAll();
        Chat c = listChats.get(0);
        assertEquals("The preview of the last message of the chat should be the content of the last chat sent", c.getLastMessage(), "Multimedia file");
    }

    @Test
    public void sendMessageWithReportedUsers() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        ReportSchema reportSchema = getFilledReportSchema();
        PSS.report(reportSchema, "rod98");

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        List<Message> listMessages = MessageRep.findAll();
        Message m = listMessages.get(0);
        assertFalse("The message should not be visible", m.getVisible());

        List<Chat> listChats = ChatRep.findAll();
        assertNotEquals("The size of the chats repository should be greater than 0", listChats.size(), 0);
        Chat c = listChats.get(0);
        assertEquals("The smallest lexicographically username should be 'casjua92'", c.getUsernameA(), "casjua92");
        assertEquals("The greatest lexicographically username should be 'rod98'", c.getUsernameB(), "rod98");
        assertEquals("The preview of the last message of the chat should be the content of the last chat sent", c.getLastMessage(), m.getContent());
        assertEquals("The last chat should be user when the last message was sent", c.getLastUse(), m.getWhenSent());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void sendMessageToMyself() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema1.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void sendMessageSenderDoesNotExist() throws ParseException, ExceptionInvalidAccount {
        MessageSchema messageSchema = getMessageSchema("Hello", "ukud", "false");
        PSS.sendMessage(messageSchema,"uruko");
    }

    @Test
    public void getAllMessagesFromChatNoLimit() throws ExceptionInvalidAccount, ParseException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        LinkedList<Message> listMessages = PSS.getAllMessagesFromChat(null, registerSchema2.getUsername(), registerSchema1.getUsername());
        assertNotEquals("The size of the messages repository should be greater than 0", listMessages.size(), 0);
        Message m = listMessages.get(0);
        assertEquals("The content should be 'Hello'", m.getContent(), "Hello");
        String sender = registerSchema1.getUsername();
        String receptor = registerSchema2.getUsername();
        assertEquals("The userWhoReceives should be '" + receptor + "'", m.getUserWhoReceives(), receptor);
        assertEquals("The userWhoSends should be '" + sender + "'", m.getUserWhoSends(), sender);
        assertTrue("The message should be sent before now", m.getWhenSent().before(new Date()));
        assertTrue("The message should be visible", m.getVisible());
        assertFalse("The message should not be a multimedia file", m.getMultimedia());
    }

    @Test
    public void getAllMessagesFromChatLimitOf1() throws ExceptionInvalidAccount, ParseException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        LinkedList<Message> listMessages = PSS.getAllMessagesFromChat(1, registerSchema2.getUsername(), registerSchema1.getUsername());
        assertEquals("The size of the messages repository should be 1", listMessages.size(), 1);
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void getAllMessagesFromChatNonExistingSender() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        PSS.getAllMessagesFromChat(null, registerSchema2.getUsername(), "esr");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void getAllMessagesFromChatNonExistingReceiver() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        PSS.getAllMessagesFromChat(null, "esr", registerSchema1.getUsername());
    }

    @Test
    public void getAllMessagesFromChatEmptyChat() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        LinkedList<Message> listMessages = PSS.getAllMessagesFromChat(null, registerSchema2.getUsername(), registerSchema1.getUsername());
        assertEquals("The size of the messages repository should be 0", listMessages.size(), 0);
    }

    @Test
    public void getAllMessagesFromChatBlockedCommunication() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        ReportSchema reportSchema = getFilledReportSchema();
        PSS.report(reportSchema, "rod98");

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        LinkedList<Message> listMessages = PSS.getAllMessagesFromChat(null, registerSchema2.getUsername(), registerSchema1.getUsername());
        assertEquals("The size of the messages repository should be 2", listMessages.size(), 2);

        listMessages = PSS.getAllMessagesFromChat(null, registerSchema1.getUsername(), registerSchema2.getUsername());
        assertEquals("The size of the messages repository should be 0", listMessages.size(), 0);
    }

    @Test
    public void getAllMessagesFromChatBlockedCommunicationReversed() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        ReportSchema reportSchema = getFilledReportSchema2();
        PSS.report(reportSchema, "casjua92");

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        LinkedList<Message> listMessages = PSS.getAllMessagesFromChat(null, registerSchema2.getUsername(), registerSchema1.getUsername());
        assertEquals("The size of the messages repository should be 2", listMessages.size(), 2);

        listMessages = PSS.getAllMessagesFromChat(null, registerSchema1.getUsername(), registerSchema2.getUsername());
        assertEquals("The size of the messages repository should be 0", listMessages.size(), 0);
    }

    @Test
    public void deleteChatNormal() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        DeleteChatSchema deleteChatSchema = getFilledDeleteChatSchema(registerSchema2.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema1.getUsername());

        assertNotNull("The chat should exist", ChatRep.findByUsernameAAndUsernameB("casjua92", "rod98"));
        List<Message> listMessages = MessageRep.findAll();
        assertFalse("There should exist some messages", listMessages.isEmpty());

        deleteChatSchema = getFilledDeleteChatSchema(registerSchema1.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema2.getUsername());

        assertNull("The chat should not exist", ChatRep.findByUsernameAAndUsernameB("casjua92", "rod98"));
        listMessages = MessageRep.findAll();
        assertTrue("There should not exist any message", listMessages.isEmpty());
    }

    @Test
    public void deleteChatNormalWithMultiMedia() throws ParseException, ExceptionInvalidAccount, IOException { //INTEGRATION
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String filename = gridFs.saveFile(file, "dude");

        MessageSchema messageSchema = getMessageSchema(filename, registerSchema2.getUsername(), "true");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        DeleteChatSchema deleteChatSchema = getFilledDeleteChatSchema(registerSchema2.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema1.getUsername());

        assertNotNull("The chat should exist", ChatRep.findByUsernameAAndUsernameB("casjua92", "rod98"));
        List<Message> listMessages = MessageRep.findAll();
        assertFalse("There should exist some messages", listMessages.isEmpty());
        assertTrue("The file should exist", gridFs.getFile(filename).exists());

        deleteChatSchema = getFilledDeleteChatSchema(registerSchema1.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema2.getUsername());

        assertNull("The chat should not exist", ChatRep.findByUsernameAAndUsernameB("casjua92", "rod98"));
        listMessages = MessageRep.findAll();
        assertTrue("There should not exist any message", listMessages.isEmpty());

        assertFalse("The file should not exist", gridFs.getFile(filename).exists());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void deleteChatDoesNotExist() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        DeleteChatSchema deleteChatSchema = getFilledDeleteChatSchema(registerSchema2.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema1.getUsername());

        deleteChatSchema = getFilledDeleteChatSchema(registerSchema1.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema2.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema2.getUsername());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void deleteChatAlreadyDeleted() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        DeleteChatSchema deleteChatSchema = getFilledDeleteChatSchema(registerSchema1.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema2.getUsername());

        deleteChatSchema = getFilledDeleteChatSchema(registerSchema1.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema2.getUsername());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void deleteChatUsernameWhoDeletesDoesNotExist() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        DeleteChatSchema deleteChatSchema = getFilledDeleteChatSchema(registerSchema2.getUsername());
        PSS.deleteChat(deleteChatSchema, "erefre");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void deleteChatDoesNotExistWithNoPreviousChats() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        DeleteChatSchema deleteChatSchema = getFilledDeleteChatSchema(registerSchema1.getUsername());
        PSS.deleteChat(deleteChatSchema, registerSchema2.getUsername());
    }
	
	@Test
    public void getAllReportedUsers() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        ReportSchema reportSchema = getFilledReportSchema();
        PSS.report(reportSchema, "rod98");
        assertTrue("Was not found",PSS.getAllReportedUsers()!=null);
    }

    @Test
    public void deleteAccountAndChats() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), "false");
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        DeleteAccountSchema deleteAccount = getFilledSchemaDeletion(registerSchema1.getPassword());
        PSS.deleteAccount(deleteAccount, registerSchema1.getUsername());

        deleteAccount = getFilledSchemaDeletion(registerSchema2.getPassword());
        PSS.deleteAccount(deleteAccount, registerSchema2.getUsername());

        List<UserPetSitters> users = UserRep.findAll();
        assertTrue("UserRep should be empty", users.isEmpty());
        List<Message> messages = MessageRep.findAll();
        assertTrue("MessageRep should be empty", messages.isEmpty());
        List<Chat> chats = ChatRep.findAll();
        assertTrue("ChatRep should be empty", chats.isEmpty());
    }

    private void proposeContractAuxiliary() throws ParseException {
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        ContractSchema contract=new ContractSchema();
        Animal dog=new Animal();
        dog.setName("Doggy");
        dog.setTipus("Dog");
        List<Animal> an=new ArrayList<Animal>();
        an.add(dog);
        contract.setAnimal(an);
        contract.setEnd("2019-01-01");
        contract.setStart("2018-01-01");
        contract.setFeedback(false);
        contract.setUsername("casjua92");
        PSS.proposeContract(contract, myUser.getUsername());
        Contract c=ContractRepository.findByUsernameFromAndUsernameTo(myUser.getUsername(),"casjua92");
        assertTrue("Is not null",c!=null);
    }

    @Test
    public void saveValuationNormal() throws ParseException, ExceptionInvalidAccount, JSONException, IOException, ExceptionServiceError {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        proposeContractAuxiliary();

        ValuationSchema valuationSchema = getFilledValuationSchema(registerSchema2.getUsername());

        PSS.saveValuation(valuationSchema, registerSchema1.getUsername());

        List<Valuation> valuations = ValuationRep.findByUserWhoValuesAndValuedUser(registerSchema1.getUsername(), registerSchema2.getUsername());
        Valuation valuation = valuations.get(0);

        assertEquals("The user who values should be equal", valuation.getUserWhoValues(), registerSchema1.getUsername());
        assertEquals("The valued user should be equal", valuation.getValuedUser(), registerSchema2.getUsername());
        assertEquals("The number of stars should be equal", valuation.getstars(), valuationSchema.getStars());
        assertEquals("The commentary should be equal", valuation.getCommentary(), valuationSchema.getCommentary());
        assertTrue("The date should be before", valuation.getDate().before(new Date()));

        Contract contract = ContractRepository.findByUsernameFromAndUsernameTo(registerSchema1.getUsername(), registerSchema2.getUsername());

        assertEquals("The list of animals should be equal", valuation.getAnimals(), contract.getAnimal());

        UserPetSitters userPetSitters = UserRep.findByUsername(valuationSchema.getValuedUser());

        assertEquals("The number of stars should be 1", Math.round(userPetSitters.getStars()), 1);
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void saveValuationValuedUserDoesNotExists() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);

        proposeContractAuxiliary();

        ValuationSchema valuationSchema = getFilledValuationSchema("fjdklfds");

        PSS.saveValuation(valuationSchema, registerSchema1.getUsername());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void saveValuationUserDoesNotExists() throws ParseException, ExceptionInvalidAccount, JSONException, IOException, ExceptionServiceError {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);

        proposeContractAuxiliary();

        ValuationSchema valuationSchema = getFilledValuationSchema(registerSchema1.getUsername());

        PSS.saveValuation(valuationSchema, "djkls");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void saveValuationContractDoesNotExists() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        ValuationSchema valuationSchema = getFilledValuationSchema(registerSchema2.getUsername());

        PSS.saveValuation(valuationSchema, registerSchema1.getUsername());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void saveValuationValueItself() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);

        ValuationSchema valuationSchema = getFilledValuationSchema(registerSchema1.getUsername());

        PSS.saveValuation(valuationSchema, registerSchema1.getUsername());
    }

    @Test
    public void getValuationsNormal() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        PSS.setProfileImage(registerSchema1.getUsername(), "PROFILE_IMAGE1");
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        PSS.setProfileImage(registerSchema2.getUsername(), "PROFILE_IMAGE2");
        proposeContractAuxiliary();
        ValuationSchema valuationSchema = getFilledValuationSchema(registerSchema2.getUsername()); // 1 --> 2
        PSS.saveValuation(valuationSchema, registerSchema1.getUsername());
        valuationSchema.setCommentary(null);
        PSS.saveValuation(valuationSchema, registerSchema1.getUsername());
        List<Valuation> valuations = ValuationRep.findByUserWhoValuesAndValuedUser(registerSchema1.getUsername(), registerSchema2.getUsername());

        LinkedList<ValuationPreviewSchema> valuationPreviewSchemas = PSS.getValuations(registerSchema2.getUsername());

        ValuationPreviewSchema valuationPreviewSchema1 = valuationPreviewSchemas.get(0);
        ValuationPreviewSchema valuationPreviewSchema2 = valuationPreviewSchemas.get(1);

        Valuation valuation1 = valuations.get(0);
        Valuation valuation2 = valuations.get(1);

        assertEquals("The user who values should be equal", valuationPreviewSchema1.getUsernameWhoValues(), valuation1.getUserWhoValues());
        assertEquals("The comment should be equal", valuationPreviewSchema1.getComment(), valuation1.getCommentary());
        UserPetSitters userPetSitters = UserRep.findByUsername(registerSchema1.getUsername());
        assertEquals("The profile image should be equal", valuationPreviewSchema1.getProfileImage(), userPetSitters.getImage());
        assertEquals("The number of stars should be equal", valuationPreviewSchema1.getStars(), valuation1.getStars());
        assertEquals("The date should be equal", valuationPreviewSchema1.getWhenValued(), valuation1.getDate());

        assertEquals("The user who values should be equal", valuationPreviewSchema2.getUsernameWhoValues(), valuation2.getUserWhoValues());
        assertEquals("The comment should be equal", valuationPreviewSchema2.getComment(), valuation2.getCommentary());
        userPetSitters = UserRep.findByUsername(registerSchema1.getUsername());
        assertEquals("The profile image should be equal", valuationPreviewSchema2.getProfileImage(), userPetSitters.getImage());
        assertEquals("The number of stars should be equal", valuationPreviewSchema2.getStars(), valuation2.getStars());
        assertEquals("The date should be equal", valuationPreviewSchema2.getWhenValued(), valuation2.getDate());
    }

    @Test
    public void getValuationsEmpty() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        PSS.setProfileImage(registerSchema1.getUsername(), "PROFILE_IMAGE1");
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        PSS.setProfileImage(registerSchema2.getUsername(), "PROFILE_IMAGE2");
        proposeContractAuxiliary();

        List<Valuation> valuations = ValuationRep.findByUserWhoValuesAndValuedUser(registerSchema1.getUsername(), registerSchema2.getUsername());

        LinkedList<ValuationPreviewSchema> valuationPreviewSchemas = PSS.getValuations(registerSchema2.getUsername());

        assertTrue("The valuations from the database should be empty", valuations.isEmpty());
        assertTrue("The valuations from the service should be empty", valuationPreviewSchemas.isEmpty());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void getValuationsUserDoesNotExist() throws ParseException, ExceptionInvalidAccount {
        LinkedList<ValuationPreviewSchema> valuationPreviewSchemas = PSS.getValuations("nobody");
    }

    @Test
    public void executeNonCachedResult() throws Exception {
        TranslationSchema translationSchema = getFilledTranslationSchema();
        LinkedList<String> translated = PSS.translate(translationSchema);
        LinkedList<String> output = new LinkedList<>();
        output.addLast("Hola");
        output.addLast("lunes");
        assertEquals("The translated texts should be equal to output list",translated, output);
    }

    @Test
    public void executeCachedResult() throws Exception {
        TranslationSchema translationSchema = getFilledTranslationSchema();
        PSS.translate(translationSchema);
        LinkedList<String> translated = PSS.translate(translationSchema);
        LinkedList<String> output = new LinkedList<>();
        output.addLast("Hola");
        output.addLast("lunes");
        assertEquals("The translated texts should be equal to output list",translated, output);
    }

    @Test
    public void executeCachedResultEntangled() throws Exception {
        TranslationSchema translationSchema = getFilledTranslationSchema();
        PSS.translate(translationSchema);
        TranslationSchema translationSchema2 = getFilledTranslationSchema2();
        LinkedList<String> translated = PSS.translate(translationSchema2);
        LinkedList<String> output = new LinkedList<>();
        output.addLast("Hola");
        output.addLast("lunes");
        output.addLast("Azul");
        assertEquals("The translated texts should be equal to output list",translated, output);
    }
}
