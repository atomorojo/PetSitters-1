package PetSitters.controller;

import PetSitters.entity.Report;
import PetSitters.entity.UserPetSitters;
import PetSitters.repository.ReportRepository;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.RegisterSchema;
import PetSitters.schemas.ResultActionLoginSchemaTest;
import PetSitters.security.JwtTokenUtil;
import PetSitters.service.GridFS;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    GridFS gridFs;

    private MockMvc mvc;

    @Before
    public void setUp() {
        DefaultMockMvcBuilder dfmk = MockMvcBuilders.webAppContextSetup(context);
        mvc = dfmk.build();
    }

    @After
    public void tearDown() {
        UserRep.deleteAll();
    }

    ResultActions register(String cont) throws Exception {
        return mvc.perform(post("/petsitters/registerNoMail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions deleteAccount(String cont) throws Exception {
        return mvc.perform(post("/petsitters/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions deleteAccountWithHeader(String cont, String token) throws Exception {
        return mvc.perform(post("/petsitters/deleteAccount")
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

    String ActivateUserAndLoginOkAndGetToken(String cont, String username) throws Exception {
        UserPetSitters user=UserRep.findByUsername(username);
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
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());

        UserPetSitters u = UserRep.findByUsername("andy.luc24");
        assertEquals("Expected the firstName 'andy'", u.getFirstName(), "andy");
        assertEquals("Expected the lastName 'lucas'", u.getLastName(), "lucas");
        assertEquals("Expected the username 'andy.luc24'", u.getUsername(), "andy.luc24");
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
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"firstName\":\"redrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"876\",\n" +
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
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"firstName\":\"redrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rodgo\",\n" +
                "\t\"password\":\"876\",\n" +
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
        mvc.perform(MockMvcRequestBuilders.multipart("/petsitters/store").file(file).header(HttpHeaders.AUTHORIZATION,"Bearer: "+ validToken()))
                .andDo(print())
                .andExpect(status().isOk());
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
        String filename=gridFs.saveFile(file,"dude");
        mvc.perform(get("/petsitters/get/"+filename).header("Not a real header","lol"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test(expected=NestedServletException.class)
    public void GetFail() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String filename=gridFs.saveFile(file,"dude");
        mvc.perform(get("/petsitters/get/"+filename+"error").header("Not a real header","lol"));
    }

    private String validToken() throws ParseException {
        UserPetSitters guy;
        if (UserRep.findByUsername("guy")==null) {
            guy = new UserPetSitters(new RegisterSchema("Guy", "Guy2", "guy", "pass", "NotARealOne", "1-1-1111"));
            guy.setActive(true);
            UserRep.save(guy);
        }
        else guy=UserRep.findByUsername("guy");
        JwtTokenUtil util=new JwtTokenUtil();
        String token=util.generateToken(guy);
        return token;
    }

    @Test
    public void testChangePasswordNormal() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
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
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        cont = "{\n" +                                  //reporter
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
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
        reportUser(cont,token);
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
    public void reportAUserWithNonExistingReported() throws Exception {
        String cont = "{\n" +                                  //reporter
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
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
        reportUser(cont,token).andExpect(status().is4xxClientError());
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
    public void SetDescription() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/description").content("{ \"toModify\":\"Dummy Text\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+ token)).andExpect(status().is2xxSuccessful());
    }
    @Test
    public void SetImage() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/image").content("{ \"toModify\":\"Dummy Text\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+token)).andExpect(status().is2xxSuccessful());
    }
    @Test
    public void SetAvailability() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/availability").content("{ \"toModify\": \"Dummy Text\" }").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+ token)).andExpect(status().is2xxSuccessful());
    }
    @Test
    public void SetExpert() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/expert").content("{ \"toModify\":\"Dummy Text\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+ token)).andExpect(status().is2xxSuccessful());
    }
    @Test
    public void SetCity() throws Exception {
        String token = validToken();
        System.out.println(token);
        mvc.perform(post("/petsitters/modify/city").content("{ \"toModify\":\"Dummy Text\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+ token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getUsersLight() throws Exception {
        String token = validToken();
        mvc.perform(get("/petsitters/users").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+ token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getUsersExpert() throws Exception {
        String token = validToken();
        mvc.perform(get("/petsitters/user/filterExpert").content("{ \"animal\":\"cat\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+ token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getUsersName() throws Exception {
        String token = validToken();
        mvc.perform(get("/petsitters/user/filterName").content("{ \"name\":\"Rodrigo\"}").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+ token)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getFullUser() throws Exception {
        String token = validToken();
        String cont = "{\n" +                                  //reporter
                "\t\"firstName\":\"rodrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rod98\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"c@desco.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont).andExpect(status().isOk());
        mvc.perform(get("/petsitters/user/rod98").content("").contentType("application/json").header(HttpHeaders.AUTHORIZATION,"Bearer: "+ token)).andExpect(status().is2xxSuccessful());
    }


}
