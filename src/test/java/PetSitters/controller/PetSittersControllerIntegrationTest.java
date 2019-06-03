package PetSitters.controller;

import PetSitters.entity.*;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.repository.*;
import PetSitters.schemas.RegisterSchema;
import PetSitters.schemas.ResultActionLoginSchemaTest;
import PetSitters.security.JwtTokenUtil;
import PetSitters.service.GridFS;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PetSittersControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    UserRepository UserRep;

    @Autowired
    ReportRepository ReportRep;

    @Autowired
    ChatRepository ChatRep;

    @Autowired
    MessageRepository MessageRep;

    @Autowired
    ContractRepository ContractRepository;

    @Autowired
    GridFS gridFs;

    private MockMvc mvc;

    @Before
    public void setUp() {
        DefaultMockMvcBuilder dfmk = MockMvcBuilders.webAppContextSetup(context);
        mvc = dfmk.build();
    }

    @After
    public void tearDown() { // Add here all the repositories
        UserRep.deleteAll();
        ReportRep.deleteAll();
        ChatRep.deleteAll();
    }

    ResultActions register(String cont) throws Exception {
        return mvc.perform(post("/petsitters/registerNoMail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions deleteAccount(String cont) throws Exception {
        return mvc.perform(delete("/petsitters/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions deleteAccountWithHeader(String cont, String token) throws Exception {
        return mvc.perform(delete("/petsitters/deleteAccount")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions login(String cont) throws Exception {
        return mvc.perform(post("/petsitters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions changePassword(String cont, String token) throws Exception {
        return mvc.perform(post("/petsitters/changePassword")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions reportUser(String cont, String token) throws Exception {
        return mvc.perform(post("/petsitters/report")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions getCoordinates(String cont, String token) throws Exception {
        return mvc.perform(post("/petsitters/getCoordinates")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions getOpenedChats(String token) throws Exception {
        return mvc.perform(get("/petsitters/getOpenedChats")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token));
    }

    ResultActions sendMessage(String token, String cont) throws Exception {
        return mvc.perform(post("/petsitters/sendMessage")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions deleteChat(String token, String cont) throws Exception {
        return mvc.perform(delete("/petsitters/deleteChat")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions getAllMessagesFromChatLimit(String token, String limit, String userWhoReceives) throws Exception {
        return mvc.perform(get("/petsitters/getMessagesFromChat")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .param("limit", limit)
                .param("userWhoReceives", userWhoReceives));
    }

    ResultActions getAllMessagesFromChat(String token, String userWhoReceives) throws Exception {
        return mvc.perform(get("/petsitters/getMessagesFromChat")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .param("userWhoReceives", userWhoReceives));
    }

    ResultActions valueUser(String cont, String token) throws Exception {
        return mvc.perform(post("/petsitters/saveValuation")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions getValuations(String token) throws Exception {
        return mvc.perform(get("/petsitters/getValuations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token));
    }

    ResultActions translate(String cont, String token) throws Exception {
        return mvc.perform(post("/petsitters/translate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    String ActivateUserAndLoginOkAndGetToken(String cont, String username) throws Exception {
        UserPetSitters user = UserRep.findByUsername(username);
        user.setActive(true);
        UserRep.save(user);
        ResultActions result = login(cont).andExpect(status().isOk());
        ObjectMapper objectMapper = new ObjectMapper();
        String resJson = result.andReturn().getResponse().getContentAsString();
        ResultActionLoginSchemaTest resultActionLoginSchema = objectMapper.readValue(resJson, ResultActionLoginSchemaTest.class);
        return resultActionLoginSchema.getResult().getToken();
    }

    @Test
    public void registerNormalRegister() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        UserPetSitters u = UserRep.findByUsername("andy.luc24");
        assertEquals("Expected the firstName 'andy'", u.getFirstName(), "andy");
        assertEquals("Expected the lastName 'lucas'", u.getLastName(), "lucas");
        assertEquals("Expected the username 'andy.luc24'", u.getUsername(), "andy.luc24");
        assertEquals("Expected the city 'Barcelona'", u.getCity(), "Barcelona");
        assertTrue("Expected the password '1234'", new BCryptPasswordEncoder().matches("1234", u.getPassword()));
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date birthDate = format.parse("22-9-1982");
        assertEquals("Expected the birthdate '22-9-1982'", u.getBirthdate(), birthDate);
    }

    @Test
    public void testRegisterDuplicatedWithUsername() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"firstName\":\"redrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"876\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"c@d.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().is4xxClientError());
    }

    @Test
    public void testRegisterDuplicatedWithEmail() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"firstName\":\"redrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rodgo\",\n" +
                "\t\"password\":\"876\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().is4xxClientError());
    }

    @Test(expected = NestedServletException.class)
    public void registerWrongFormatOfDate() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22/9/1982\"\n" +
                "}";
        register(cont);
    }

    @Test
    public void registerFirstNameFieldIsNull() throws Exception {
        String cont = "{\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().is5xxServerError());
    }

    @Test
    public void registerFirstNameFieldIsEmpty() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().is5xxServerError());
    }

    @Test
    public void deleteAnExistingAccountWithoutLogin() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        deleteAccount(cont).andExpect(status().is4xxClientError());
        assertTrue("The User 'andy.luc24' should not exist", UserRep.existsByUsername("andy.luc24"));
    }

    @Test
    public void deleteAnExistingAccountWithWrongPasswordWithoutLogin() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"password\":\"asdas\"\n" +
                "}";
        deleteAccount(cont).andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteAnExistingAccountWithoutPasswordWithoutLogin() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "}";
        deleteAccount(cont).andExpect(status().is4xxClientError());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
    }

    @Test
    public void deleteAnNonExistingAccount() throws Exception {
        String cont = "{\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        deleteAccount(cont).andExpect(status().is4xxClientError());
    }

    @Test
    public void loginCorrect() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"birthdate\":\"22-9-1982\",\n" +
                "\t\"email\":\"dummyemail\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        login(cont).andExpect(status().isOk());
    }

    @Test
    public void loginIncorrect() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"birthdate\":\"22-9-1982\",\n" +
                "\t\"email\":\"dummyemail\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        login(cont).andExpect(status().is5xxServerError());
    }

    @Test
    public void loginJWTtoUser() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"birthdate\":\"22-9-1982\",\n" +
                "\t\"email\":\"dummyemail\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "andy.luc24");
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        assertEquals("Expected user is andy.luc24", jwtTokenUtil.getUsernameFromToken(token), "andy.luc24");
    }

    @Test
    public void loginAndDeleteAccount() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"birthdate\":\"22-9-1982\",\n" +
                "\t\"email\":\"dummyemail\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "andy.luc24");
        cont = "{\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        deleteAccountWithHeader(cont, token).andExpect(status().isOk());
    }

    @Test
    public void deleteAnExistingAccountWithWrongPasswordWithLogin() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "andy.luc24");
        cont = "{\n" +
                "\t\"password\":\"asdasd\"\n" +
                "}";
        deleteAccountWithHeader(cont, token).andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteAnExistingAccountWithoutPasswordWithLogin() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "andy.luc24");
        cont = "{\n" +
                "}";
        deleteAccountWithHeader(cont, token).andExpect(status().is5xxServerError());
    }

    @Test
    public void Store() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String help=mvc.perform(MockMvcRequestBuilders.multipart("/petsitters/store").file(file).header(HttpHeaders.AUTHORIZATION, "Bearer: " + validToken()))
                .andDo(print())
                .andExpect(status().isOk()).toString();
        System.out.println(help);
    }

    @Test(expected=NestedServletException.class)
    public void Delete() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String filename = gridFs.saveFile(file, "dude");
        String token=validToken();
        mvc.perform(delete("/petsitters/delete/"+filename).content("{}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
        mvc.perform(get("/petsitters/get/" + filename).header("Not a real header", "lol"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void StoreNoAuth() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        mvc.perform(MockMvcRequestBuilders.multipart("/petsitters/store").file(file).header("Not a real header", "lol"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void Get() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String filename = gridFs.saveFile(file, "dude");
        mvc.perform(get("/petsitters/get/" + filename).header("Not a real header", "lol"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test(expected = NestedServletException.class)
    public void GetFail() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String filename = gridFs.saveFile(file, "dude");
        mvc.perform(get("/petsitters/get/" + filename + "error").header("Not a real header", "lol"));
    }

    private String validToken() throws ParseException {
        UserPetSitters guy;
        if (UserRep.findByUsername("guy") == null) {
            guy = new UserPetSitters(new RegisterSchema("Guy", "Guy2", "guy", "pass", "Barcelona", "NotARealOne", "1-1-1111"));
            guy.setActive(true);
            UserRep.save(guy);
        } else guy = UserRep.findByUsername("guy");
        JwtTokenUtil util = new JwtTokenUtil();
        String token = util.generateToken(guy);
        return token;
    }

    @Test
    public void testChangePasswordNormal() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "andy.luc24");
        assertTrue("The user 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"oldPassword\":\"1234\",\n" +
                "\t\"newPassword\":\"54321\"\n" +
                "}";
        changePassword(cont, token).andExpect(status().isOk());

        UserPetSitters u = UserRep.findByUsername("andy.luc24");
        assertTrue("The new password should be '54321'", u.isTheSamePassword("54321"));
    }

    @Test
    public void testChangePasswordWithWrongOldPassword() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "andy.luc24");
        assertTrue("The user 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"oldPassword\":\"12\",\n" +
                "\t\"newPassword\":\"54321\"\n" +
                "}";
        changePassword(cont, token).andExpect(status().is4xxClientError());
    }

    @Test
    public void testChangePasswordWithBlankNewPassword() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "andy.luc24");
        assertTrue("The user 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"oldPassword\":\"1234\",\n" +
                "\t\"newPassword\":\"\"\n" +
                "}";
        changePassword(cont, token).andExpect(status().is5xxServerError());
    }

    @Test
    public void testChangePasswordWithNullNewPassword() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "andy.luc24");
        assertTrue("The user 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"oldPassword\":\"1234\"\n" +
                "}";
        changePassword(cont, token).andExpect(status().is5xxServerError());
    }

    @Test
    public void reportAUserNormal() throws Exception {
        String cont = "{\n" +                           //reported
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"casjua92\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +                                  //reporter
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        assertTrue("The user 'casjua92' should exist", UserRep.existsByUsername("casjua92"));
        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");
        cont = "{\n" +
                "\t\"reported\":\"casjua92\",\n" +
                "\t\"description\":\"No description\"\n" +
                "}";
        reportUser(cont, token).andExpect(status().isOk());
        List<Report> reports = ReportRep.findByReporter(UserRep.findByUsername("rod98").getEmail());
        Report rep = reports.get(0);
        System.out.println(String.valueOf(rep != null));
        assertEquals("The reported should be 'casjua92'", rep.getReported(), UserRep.findByUsername("casjua92").getEmail());
        assertEquals("The reporter should be 'rod98'", rep.getReporter(), UserRep.findByUsername("rod98").getEmail());
        assertEquals("The description should be 'No description'", rep.getDescription(), rep.getDescription());
    }

    @Test
    public void reportAUserWithNonExistingReporter() throws Exception {
        String cont = "{\n" +                           //reported
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"casjua92\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertFalse("The user 'rod98' should not exist", UserRep.existsByUsername("rod98"));
        assertTrue("The user 'casjua92' should exist", UserRep.existsByUsername("casjua92"));
        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        login(cont).andExpect(status().is5xxServerError());
    }

    @Test
    public void reportTheReporterUser() throws Exception {
        String cont = "{\n" +                           //reporter and reported
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"casjua92\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'casjua92' should exist", UserRep.existsByUsername("casjua92"));
        cont = "{\n" +
                "\t\"username\":\"casjua92\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "casjua92");
        cont = "{\n" +
                "\t\"reported\":\"casjua92\",\n" +
                "\t\"description\":\"No description\"\n" +
                "}";
        reportUser(cont, token).andExpect(status().is4xxClientError());
    }

    @Test
    public void reportAUserWithNonExistingReported() throws Exception {
        String cont = "{\n" +                                  //reporter
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        assertFalse("The user 'casjua92' should not exist", UserRep.existsByUsername("casjua92"));
        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");
        cont = "{\n" +
                "\t\"reported\":\"casjua92\",\n" +
                "\t\"description\":\"No description\"\n" +
                "}";
        reportUser(cont, token).andExpect(status().is4xxClientError());
    }

    @Test
    public void reportAUserWithNonExistingUsersReporterAndReported() throws Exception {
        assertFalse("The user 'rod98' should not exist", UserRep.existsByUsername("rod98"));
        assertFalse("The user 'casjua92' should not exist", UserRep.existsByUsername("casjua92"));
        String cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        login(cont).andExpect(status().is5xxServerError());
    }

    @Test
    public void getCoordinatesFromService() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");
        cont = "{\n" +
                "\t\"city\":\"Lleida\"\n" +
                "}";
        getCoordinates(cont, token).andExpect(status().isOk());
    }

    @Test
    public void getCoordinatesFromServiceWithCachedResult() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");
        cont = "{\n" +
                "\t\"city\":\"Lleida\"\n" +
                "}";
        getCoordinates(cont, token).andExpect(status().isOk());
        getCoordinates(cont, token).andExpect(status().isOk());
    }

    @Test
    public void getCoordinatesFromServiceWithNonExistingCity() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");
        cont = "{\n" +
                "\t\"city\":\"Llefsdfida\"\n" +
                "}";
        getCoordinates(cont, token).andExpect(status().is5xxServerError());
    }

    @Test
    public void getCoordinatesFromServiceEmptyCity() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");
        cont = "{\n" +
                "\t\"city\":\"\"\n" +
                "}";
        getCoordinates(cont, token).andExpect(status().is5xxServerError());
    }

    @Test
    public void getCoordinatesFromServiceNullCity() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");
        cont = "{\n" +
                "}";
        getCoordinates(cont, token).andExpect(status().is5xxServerError());
    }

    @Test
    public void SetDescription() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/description").content("{ \"toModify\":\"Dummy Text\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void SetImage() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/image").content("{ \"toModify\":\"Dummy Text\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void SetAvailability() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/availability").content("{ \"toModify\": \"Dummy Text\" }").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void SetExpert() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/expert").content("{ \"toModify\":\"Dummy Text\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void SetCity() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/city").content("{ \"toModify\":\"Dummy Text\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getUsersLight() throws Exception {
        String token = validToken();
        mvc.perform(get("/petsitters/users").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getUsersExpert() throws Exception {
        String token = validToken();
        mvc.perform(get("/petsitters/user/filterExpert?animal=cat").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getUsersValidation() throws Exception {
        String token = validToken();
        mvc.perform(get("/petsitters/user/filterValoration?upperBound=3&lowerBound=0").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getUsersName() throws Exception {
        String token = validToken();
        mvc.perform(get("/petsitters/user/filterName?name=Rodr").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getFullUser() throws Exception {
        String token = validToken();
        String cont = "{\n" +                                  //reporter
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        mvc.perform(get("/petsitters/user/rod98").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getUsersDistance() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        UserPetSitters user1 = UserRep.findByUsername("rod98");
        user1.setCity("Barcelona");
        UserRep.save(user1);
        cont = "{\n" +
                "\t\"firstName\":\"amie\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"stt1\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desca.es\",\n" +
                "\t\"birthdate\":\"2-11-1442\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'stt1' should exist", UserRep.existsByUsername("stt1"));
        UserPetSitters user2 = UserRep.findByUsername("stt1");
        user2.setCity("Sant Boi de Llobregat");
        UserRep.save(user2);
        String token = validToken();
        mvc.perform(get("/petsitters/user/filterDistance?rad=100").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());


    }

    @Test
    public void getFavorites() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        UserPetSitters user1 = UserRep.findByUsername("rod98");
        user1.addFavorites("stt1");
        UserRep.save(user1);
        cont = "{\n" +
                "\t\"firstName\":\"amie\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"stt1\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desca.es\",\n" +
                "\t\"birthdate\":\"2-11-1442\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'stt1' should exist", UserRep.existsByUsername("stt1"));
        String token = validToken();
        mvc.perform(get("/petsitters/getFavorites").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void addFavorites() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'rod98' should exist", UserRep.existsByUsername("rod98"));
        UserPetSitters user1 = UserRep.findByUsername("rod98");
        UserRep.save(user1);
        cont = "{\n" +
                "\t\"firstName\":\"amie\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"stt1\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desca.es\",\n" +
                "\t\"birthdate\":\"2-11-1442\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'stt1' should exist", UserRep.existsByUsername("stt1"));
        String token = validToken();
        mvc.perform(post("/petsitters/addFavorites?userList=stt1").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
        UserPetSitters end=UserRep.findByUsername("guy");
        assertTrue("Favorite is not set",end.getFavorites().get(0).equals("stt1"));
    }

    @Test
    public void unsetFavorites() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"amie\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"stt1\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desca.es\",\n" +
                "\t\"birthdate\":\"2-11-1442\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        assertTrue("The user 'stt1' should exist", UserRep.existsByUsername("stt1"));
        String token = validToken();
        UserPetSitters guy=UserRep.findByUsername("guy");
        guy.addFavorites("stt1");
        UserRep.save(guy);
        mvc.perform(post("/petsitters/unsetFavorites?userList=stt1").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
        UserPetSitters end=UserRep.findByUsername("guy");
        assertTrue("Favorite is not set",end.getFavorites().size()==0);
    }

    @Test
    public void getOpenedChats() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@boq.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"aare\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"userWhoReceives\": \"aare\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        getOpenedChats(token).andExpect(status().isOk());
    }
    @Test
    public void getOpenedChatsReportedWithoutMessagesInOneSide() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "\t\"reported\":\"casjua92\",\n" +
                "\t\"description\":\"No description\"\n" +
                "}";
        reportUser(cont, token).andExpect(status().isOk());

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        getOpenedChats(token).andExpect(status().isOk());
    }

    @Test
    public void getOpenedChatsEmptyChat() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@boq.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"aare\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        getOpenedChats(token).andExpect(status().isOk());
    }

    @Test
    public void proposeContract() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        String token = validToken();
        mvc.perform(post("/petsitters/proposeContract").content("{\n" +
                "   \"username\":\"rod98\",\n" +
                "   \"end\":\"2019-01-01\",\n" +
                "   \"start\":\"2018-01-01\",\n" +
                "   \"animal\":[\n" +
                "      {\n" +
                "         \"name\":\"doggy\",\n" +
                "         \"tipus\":\"Dog\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"feedback\":\"false\"\n" +
                "}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
        Contract c=ContractRepository.findByUsernameFromAndUsernameTo("guy","rod98");
        assertTrue("Exists",c!=null);
    }

    @Test
    public void acceptContract() throws Exception {
        proposeContract();
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";

        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");
        mvc.perform(post("/petsitters/acceptContract?contract=guy").content("{}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
        Contract c=ContractRepository.findByUsernameFromAndUsernameTo("guy","rod98");
        assertTrue("Accepted",c.getAccepted());
    }
    @Test
    public void rejectContract() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        String token = validToken();
        mvc.perform(delete("/petsitters/rejectContract?contract=rod98").content("{}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
        Contract c=ContractRepository.findByUsernameFromAndUsernameTo("guy","rod98");
        assertTrue("Not exists",c==null);
    }

    @Test
    public void getProposedContracts() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        String token = validToken();
        mvc.perform(get("/petsitters/getProposedContracts").content("{}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getReceivedContracts() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        String token = validToken();
        mvc.perform(get("/petsitters/getReceivedContracts").content("{}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void isContracted() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"password\": \"123\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        String token = validToken();
        ResultActions res=mvc.perform(get("/petsitters/isContracted?contract=rod98").content("{}").contentType("application/json").header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)).andExpect(status().is2xxSuccessful());
    }
    @Test
    public void sendMessage() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());
    }

    @Test
    public void sendMessageWithReportedUsers() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "\t\"reported\":\"casjua92\",\n" +
                "\t\"description\":\"No description\"\n" +
                "}";
        reportUser(cont, token).andExpect(status().isOk());

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());
    }

    @Test
    public void sendMessageToMyself() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().is4xxClientError());
    }

    @Test
    public void sendMessageReceiverDoesNotExist() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"asdsad\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllMessagesFromChatNoLimit() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        getAllMessagesFromChat(token, "casjua92").andExpect(status().isOk());
    }

    @Test
    public void getAllMessagesFromChatLimit1() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        getAllMessagesFromChatLimit(token, "1", "casjua92").andExpect(status().isOk());
    }

    @Test
    public void getAllMessagesFromChatNonExistingReceiver() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        getAllMessagesFromChat(token, "efssdfds").andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllMessagesFromChatBlockedCommunication() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "\t\"reported\":\"casjua92\",\n" +
                "\t\"description\":\"No description\"\n" +
                "}";
        reportUser(cont, token).andExpect(status().isOk());

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        getAllMessagesFromChat(token, "casjua92").andExpect(status().isOk());
    }

    @Test
    public void getAllMessagesFromChatBlockedCommunicationReversed() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "\t\"username\":\"casjua92\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token2 = ActivateUserAndLoginOkAndGetToken(cont, "casjua92");

        cont = "{\n" +
                "\t\"reported\":\"rod98\",\n" +
                "\t\"description\":\"No description\"\n" +
                "}";
        reportUser(cont, token2).andExpect(status().isOk());

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        getAllMessagesFromChat(token, "casjua92").andExpect(status().isOk());
    }
    @Test
    public void deleteUserAdmin() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        ResultActions res=mvc.perform(post("/petsitters/deleteUserAccount?adminToken=111122223333444455556666&toDelete=rod98").content("{}").contentType("application/json")).andExpect(status().is2xxSuccessful());
        assertTrue("Account not deleted",UserRep.findByUsername("rod98")==null);
    }

    @Test
	    public void getReports() throws Exception {
        reportAUserNormal();
        ResultActions res=mvc.perform(get("/petsitters/getUserReports?adminToken=111122223333444455556666&reported=casjua92").content("{}").contentType("application/json")).andExpect(status().is2xxSuccessful());
        assertTrue("Account not deleted",res.andReturn().getResponse().getContentAsString()!=null);
    }
	
	public void deleteChatNormal() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"otherUsername\":\"casjua92\"\n" +
                "}";

        deleteChat(token, cont).andExpect(status().isOk());
    }
	
    @Test
    public void getAllReports() throws Exception {
		        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
				"\t\"reported\":\"casjua92\",\n" +
                "\t\"description\":\"No description\"\n" +
                "}";
        reportUser(cont, token).andExpect(status().isOk());
        ResultActions res=mvc.perform(get("/petsitters/getAllReportedUsers?adminToken=111122223333444455556666").content("{}").contentType("application/json")).andExpect(status().is2xxSuccessful());
        assertTrue("Account not deleted",res.andReturn().getResponse().getContentAsString()!=null);

    }		

    @Test
    public void deleteChatAlreadyDeleted() throws Exception, ExceptionInvalidAccount {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
		                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"otherUsername\":\"casjua92\"\n" +
                "}";

        deleteChat(token, cont).andExpect(status().isOk());
        deleteChat(token, cont).andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteChatDoesNotExistWithNoPreviousChats() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"otherUsername\":\"casjua92\"\n" +
                "}";

        deleteChat(token, cont).andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteAccountAndChats() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());
        sendMessage(token, cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        deleteAccountWithHeader(cont, token).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"casjua92\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";

        token = ActivateUserAndLoginOkAndGetToken(cont, "casjua92");

        cont = "{\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        deleteAccountWithHeader(cont, token).andExpect(status().isOk());

        List<UserPetSitters> users = UserRep.findAll();
        assertTrue("UserRep should be empty", users.isEmpty());
        List<Message> messages = MessageRep.findAll();
        assertTrue("MessageRep should be empty", messages.isEmpty());
        List<Chat> chats = ChatRep.findAll();
        assertTrue("ChatRep should be empty", chats.isEmpty());
    }

    @Test
    public void deleteUserAdminWithChats() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"userWhoReceives\": \"casjua92\",\n" +
                "  \"content\":\"Hello\",\n" +
                "  \"isMultimedia\":\"false\"\n" +
                "}";

        sendMessage(token, cont).andExpect(status().isOk());
        sendMessage(token, cont).andExpect(status().isOk());

        ResultActions res=mvc.perform(post("/petsitters/deleteUserAccount?adminToken=111122223333444455556666&toDelete=rod98").content("{}").contentType("application/json")).andExpect(status().is2xxSuccessful());
        assertTrue("Account not deleted",UserRep.findByUsername("rod98")==null);
        res=mvc.perform(post("/petsitters/deleteUserAccount?adminToken=111122223333444455556666&toDelete=casjua92").content("{}").contentType("application/json")).andExpect(status().is2xxSuccessful());
        assertTrue("Account not deleted",UserRep.findByUsername("casjua92")==null);

        List<UserPetSitters> users = UserRep.findAll();
        assertTrue("UserRep should be empty", users.isEmpty());
        List<Message> messages = MessageRep.findAll();
        assertTrue("MessageRep should be empty", messages.isEmpty());
        List<Chat> chats = ChatRep.findAll();
        assertTrue("ChatRep should be empty", chats.isEmpty());
    }

    @Test
    public void saveValuationNormal() throws Exception {
        proposeContract();

       String cont = "{\n" +
                "\t\"username\":\"guy\",\n" +
                "\t\"password\":\"pass\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "guy");

        cont = "{\n" +
                "   \"valuedUser\":\"rod98\",\n" +
                "   \"commentary\":\"Hello\",\n" +
                "   \"stars\":1\n" +
                "}";

        valueUser(cont, token).andExpect(status().isOk());
    }

    @Test
    public void saveValuationValuedUserDoesNotExists() throws Exception {
        proposeContract();

        String cont = "{\n" +
                "\t\"username\":\"guy\",\n" +
                "\t\"password\":\"pass\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "guy");

        cont = "{\n" +
                "  \"valuedUser\": \"sdjkf\",\n" +
                "  \"commentary\":\"Hello\",\n" +
                "  \"stars\":1\n" +
                "}";

        valueUser(cont, token).andExpect(status().is4xxClientError());
    }

    @Test
    public void saveValuationValueItself() throws Exception {
        proposeContract();

        String cont = "{\n" +
                "\t\"username\":\"guy\",\n" +
                "\t\"password\":\"pass\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "guy");

        cont = "{\n" +
                "  \"valuedUser\": \"guy\",\n" +
                "  \"commentary\":\"Hello\",\n" +
                "  \"stars\":\"1\"\n" +
                "}";

        valueUser(cont, token).andExpect(status().is4xxClientError());
    }

    @Test
    public void saveValuationContractDoesNotExists() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@bo.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"casjua92\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "  \"valuedUser\": \"casjua92\",\n" +
                "  \"commentary\":\"Hello\",\n" +
                "  \"stars\":1\n" +
                "}";

        valueUser(cont, token).andExpect(status().is4xxClientError());
    }

    @Test
    public void getValuationsNormal() throws Exception {
        proposeContract();
        UserPetSitters userPetSitters = UserRep.findByUsername("guy");
        userPetSitters.setImage("IMAGE");
        String cont = "{\n" +
                "  \"valuedUser\": \"rod98\",\n" +
                //"  \"commentary\":\"Hello\",\n" +
                "  \"stars\":1\n" +
                "}";

        valueUser(cont, validToken()).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        getValuations(token).andExpect(status().isOk());
    }

    @Test
    public void getValuationsEmpty() throws Exception {
        proposeContract();
        UserPetSitters userPetSitters = UserRep.findByUsername("guy");
        userPetSitters.setImage("IMAGE");

        String cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        getValuations(token).andExpect(status().isOk());
    }

    @Test
    public void executeNonCachedResult() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "\t\"inputInEnglish\":[\"Hello\"],\n" +
                "\t\"outputLanguage\":\"es\"\n" +
                "}";

        translate(cont, token).andExpect(status().isOk());
    }

    @Test
    public void executeCachedResult() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "\t\"inputInEnglish\":[\"Hello\"],\n" +
                "\t\"outputLanguage\":\"es\"\n" +
                "}";

        translate(cont, token).andExpect(status().isOk());
        translate(cont, token).andExpect(status().isOk());
    }

    @Test
    public void executeCachedResultEntangled() throws Exception {
        String cont = "{\n" +
                "  \"birthdate\": \"20-11-1987\",\n" +
                "  \"email\": \"a@b.com\",\n" +
                "  \"firstName\": \"stri1ng\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"password\": \"123\",\n" +
                "\t\"city\":\"Barcelona\",\n" +
                "  \"username\": \"rod98\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";
        String token = ActivateUserAndLoginOkAndGetToken(cont, "rod98");

        cont = "{\n" +
                "\t\"inputInEnglish\":[\"Hello\"],\n" +
                "\t\"outputLanguage\":\"es\"\n" +
                "}";

        translate(cont, token).andExpect(status().isOk());

        cont = "{\n" +
                "\t\"inputInEnglish\":[\"Hello\", \"World\"],\n" +
                "\t\"outputLanguage\":\"es\"\n" +
                "}";
        translate(cont, token).andExpect(status().isOk());
    }
}
