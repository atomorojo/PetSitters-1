package PetSitters.service;

import PetSitters.domain.Coordinates;
import PetSitters.entity.Chat;
import PetSitters.entity.Message;
import PetSitters.entity.Report;
import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.repository.ChatRepository;
import PetSitters.repository.MessageRepository;
import PetSitters.repository.ReportRepository;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.*;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PetSittersServiceTest {

    @Autowired
    PetSittersService PSS;

    @Autowired
    UserRepository UserRep;

    @Autowired
    ReportRepository ReportRep;

    @Autowired
    GridFS gridFs;

    @Autowired
    ChatRepository ChatRep;

    @Autowired
    MessageRepository MessageRep;

    @After
    public void tearDown() {  // Add here all the repositories
        PSS = null;
        UserRep.deleteAll();
        ReportRep.deleteAll();
        ChatRep.deleteAll();
        MessageRep.deleteAll();
    }

    RegisterSchema getFilledSchemaRegistrationPersona1() {
        RegisterSchema registerSchema = Mockito.mock(RegisterSchema.class);
        Mockito.when(registerSchema.getFirstName()).thenReturn("Rodrigo");
        Mockito.when(registerSchema.getLastName()).thenReturn("Gomez");
        Mockito.when(registerSchema.getUsername()).thenReturn("rod98");
        Mockito.when(registerSchema.getPassword()).thenReturn("123");
        Mockito.when(registerSchema.getCity()).thenReturn("Barcelona");
        Mockito.when(registerSchema.getEmail()).thenReturn("a@b.com");
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona2() {
        RegisterSchema registerSchema = Mockito.mock(RegisterSchema.class);
        Mockito.when(registerSchema.getFirstName()).thenReturn("Juan");
        Mockito.when(registerSchema.getLastName()).thenReturn("del Castillo");
        Mockito.when(registerSchema.getUsername()).thenReturn("casjua92");
        Mockito.when(registerSchema.getPassword()).thenReturn("789");
        Mockito.when(registerSchema.getCity()).thenReturn("Barcelona");
        Mockito.when(registerSchema.getEmail()).thenReturn("a@example.com");
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20-7-1992");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona3() {
        RegisterSchema registerSchema = Mockito.mock(RegisterSchema.class);
        Mockito.when(registerSchema.getFirstName()).thenReturn("Pedro");
        Mockito.when(registerSchema.getLastName()).thenReturn("Suarez");
        Mockito.when(registerSchema.getUsername()).thenReturn("pes44");
        Mockito.when(registerSchema.getPassword()).thenReturn("1542");
        Mockito.when(registerSchema.getCity()).thenReturn("Barcelona");
        Mockito.when(registerSchema.getEmail()).thenReturn("a@bo.com");
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20-12-1998");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona4() {
        RegisterSchema registerSchema = Mockito.mock(RegisterSchema.class);
        Mockito.when(registerSchema.getFirstName()).thenReturn("Mario");
        Mockito.when(registerSchema.getLastName()).thenReturn("Gonzalo");
        Mockito.when(registerSchema.getUsername()).thenReturn("marGonz");
        Mockito.when(registerSchema.getPassword()).thenReturn("789");
        Mockito.when(registerSchema.getCity()).thenReturn("Barcelona");
        Mockito.when(registerSchema.getEmail()).thenReturn("a@gre.com");
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20-7-1992");
        return registerSchema;
    }

    RegisterSchema getFilledSchemaRegistrationPersona5() {
        RegisterSchema registerSchema = Mockito.mock(RegisterSchema.class);
        Mockito.when(registerSchema.getFirstName()).thenReturn("Gregorio");
        Mockito.when(registerSchema.getLastName()).thenReturn("Lopez");
        Mockito.when(registerSchema.getUsername()).thenReturn("gre647");
        Mockito.when(registerSchema.getPassword()).thenReturn("abc123");
        Mockito.when(registerSchema.getCity()).thenReturn("Barcelona");
        Mockito.when(registerSchema.getEmail()).thenReturn("a@sop.com");
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20-12-1998");
        return registerSchema;
    }

    DeleteAccountSchema getFilledSchemaDeletion() {
        DeleteAccountSchema deleteAccount = Mockito.mock(DeleteAccountSchema.class);
        Mockito.when(deleteAccount.getPassword()).thenReturn("123");
        return deleteAccount;
    }

    ChangePasswordSchema getFilledSchemaChangePassword() {
        ChangePasswordSchema changePasswordSchema = Mockito.mock(ChangePasswordSchema.class);
        Mockito.when(changePasswordSchema.getOldPassword()).thenReturn("123");
        Mockito.when(changePasswordSchema.getNewPassword()).thenReturn("54321");
        return changePasswordSchema;
    }

    ReportSchema getFilledReportSchema() {
        ReportSchema reportSchema = Mockito.mock(ReportSchema.class);
        Mockito.when(reportSchema.getReported()).thenReturn("casjua92");
        Mockito.when(reportSchema.getDescription()).thenReturn("No description");
        return reportSchema;
    }

    ReportSchema getFilledReportSchema2() {
        ReportSchema reportSchema = Mockito.mock(ReportSchema.class);
        Mockito.when(reportSchema.getReported()).thenReturn("rod98");
        Mockito.when(reportSchema.getDescription()).thenReturn("No description");
        return reportSchema;
    }

    GetCoordinatesSchema getFilledGetCoordinatesSchema() {
        GetCoordinatesSchema getCoordinatesSchema = Mockito.mock(GetCoordinatesSchema.class);
        Mockito.when(getCoordinatesSchema.getCity()).thenReturn("Los Angeles");
        return getCoordinatesSchema;
    }

    MessageSchema getMessageSchema(String content, String userWhoReceives, Boolean isMultimedia) {
        MessageSchema messageSchema = Mockito.mock(MessageSchema.class);
        Mockito.when(messageSchema.getContent()).thenReturn(content);
        Mockito.when(messageSchema.getUserWhoReceives()).thenReturn(userWhoReceives);
        Mockito.when(messageSchema.getIsMultimedia()).thenReturn(isMultimedia);
        return messageSchema;
    }

    @Test
    public void testRegisterNormal() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        UserPetSitters u = UserRep.findByUsername("rod98");

        assertEquals("Expected the firstName 'Rodrigo'", u.getFirstName(), registerSchema.getFirstName());
        assertEquals("Expected the lastName 'Gomez'", u.getLastName(), registerSchema.getLastName());
        assertEquals("Expected the username 'rod98'", u.getUsername(), registerSchema.getUsername());
        assertEquals("Expected the city 'rod98'", u.getCity(), registerSchema.getCity());
        assertTrue("Expected the password '123'",new BCryptPasswordEncoder().matches("123",u.getPassword()));
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date birthDate = format.parse(registerSchema.getBirthdate());
        assertEquals("Expected the birthdate '20-12-1998'", u.getBirthdate(), birthDate);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testRegisterDuplicatedWithUsername() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        Mockito.when(registerSchema2.getUsername()).thenReturn("rod98");
        PSS.register(registerSchema2);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testRegisterDuplicatedWithEmail() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        Mockito.when(registerSchema2.getEmail()).thenReturn("a@b.com");
        PSS.register(registerSchema2);
    }

    @Test(expected = ParseException.class)
    public void testRegisterErrorInDateFormat() throws ParseException {
        RegisterSchema registerSchema = getFilledSchemaRegistrationPersona1();
        Mockito.when(registerSchema.getBirthdate()).thenReturn("20/12/1998");
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
        Mockito.when(deleteAccount.getPassword()).thenReturn("321");
        PSS.deleteAccount(deleteAccount, "rod98");
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void testDeleteNonExistingAccount() throws ExceptionInvalidAccount {
        DeleteAccountSchema deleteAccount = getFilledSchemaDeletion();
        assertFalse("The user 'rod98' should not exist", UserRep.existsByUsername("rod98"));
        PSS.deleteAccount(deleteAccount, "rod98");
    }

    @Test
    public void testValidStorage() throws IOException {
        MultipartFile result=loadFile();
        String filename=gridFs.saveFile(result, "test");
        GridFsResource res=gridFs.getFile(filename);
        assertTrue("Same files", IOUtils.contentEquals( result.getInputStream(), res.getInputStream()));
    }
    @Test
    public void testInvalidStorage() throws IOException {
        MultipartFile result=loadFile();
        gridFs.saveFile(result,"fail");
        GridFsResource res=gridFs.getFile("notFail");
    }

    private MultipartFile loadFile() {
        Path path = Paths.get("/PetSitters/files/image.jpg");
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile result = new MockMultipartFile(name,originalFileName, contentType, content);
        return result;
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
        Mockito.when(changePasswordSchema.getOldPassword()).thenReturn("321");
        PSS.changePassword(changePasswordSchema, "rod98");
    }

    @Test
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

    @Test
    public void getCoordinatesFromService() throws IOException, JSONException, ExceptionServiceError {
        GetCoordinatesSchema getCoordinatesSchema = getFilledGetCoordinatesSchema();
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
        assertEquals("The latitude should be 34.0536909", c.getLatitude(), 34.0536909, 0.05);
        assertEquals("The longitude should be -118.2427666", c.getLongitude(), -118.2427666, 0.05);
    }

    @Test
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
        Mockito.when(getCoordinatesSchema.getCity()).thenReturn("Llefsdfida");
        Coordinates c = PSS.getCoordinates(getCoordinatesSchema);
    }

    @Test
    public void addFavorites() throws Exception {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        PSS.addFavorites("casjua92","rod98");
        UserRep.save(myUser);
        UserPetSitters us=UserRep.findByUsername("rod98");
        assertEquals("Same favorites",us.getFavorites(),myUser.getFavorites());
    }
    @Test
    public void unsetFavorites() throws Exception {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        UserPetSitters myUser=UserRep.findByUsername("rod98");
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        myUser.addFavorites("casjua92");
        UserRep.save(myUser);
        PSS.unsetFavorites("casjua92","rod98");
        UserPetSitters us=UserRep.findByUsername("rod98");
        assertEquals("No favorites",us.getFavorites().size(),0);
    }

    @Test
    public void getOpenedChats() throws ParseException, ExceptionInvalidAccount, JSONException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), false);
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        List<ChatPreviewSchema> list = PSS.getOpenedChats(registerSchema2.getUsername());
        ChatPreviewSchema chatPreviewSchema = list.get(0);
        assertEquals("Output should be '" + registerSchema1.getUsername() + "'", chatPreviewSchema.getName(), "Rodrigo Gomez");
    }

    @Test
    public void getOpenedChatsEmpty() throws ParseException, ExceptionInvalidAccount, JSONException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);

        List<ChatPreviewSchema> list = PSS.getOpenedChats(registerSchema1.getUsername());
        assertEquals("Output should be empty", list.size(), 0);
    }

    @Test
    public void sendMessage() throws ParseException, ExceptionInvalidAccount {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);
        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), false);
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
        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), true);
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

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), false);
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
        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema1.getUsername(), false);
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
    }

    @Test(expected = ExceptionInvalidAccount.class)
    public void sendMessageSenderDoesNotExist() throws ExceptionInvalidAccount {
        MessageSchema messageSchema = getMessageSchema("Hello", "ukud", false);
        PSS.sendMessage(messageSchema,"uruko");
    }

    @Test
    public void getAllMessagesFromChatNoLimit() throws ExceptionInvalidAccount, ParseException {
        RegisterSchema registerSchema1 = getFilledSchemaRegistrationPersona1();
        PSS.register(registerSchema1);
        RegisterSchema registerSchema2 = getFilledSchemaRegistrationPersona2();
        PSS.register(registerSchema2);

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), false);
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

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), false);
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

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), false);
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

        MessageSchema messageSchema = getMessageSchema("Hello", registerSchema2.getUsername(), false);
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());
        PSS.sendMessage(messageSchema,registerSchema1.getUsername());

        LinkedList<Message> listMessages = PSS.getAllMessagesFromChat(null, registerSchema2.getUsername(), registerSchema1.getUsername());
        assertEquals("The size of the messages repository should be 2", listMessages.size(), 2);

        listMessages = PSS.getAllMessagesFromChat(null, registerSchema1.getUsername(), registerSchema2.getUsername());
        assertEquals("The size of the messages repository should be 0", listMessages.size(), 0);
    }
}
