package PetSitters.controller;

import PetSitters.entity.UserPetSitters;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.ResultActionLoginSchema;
import PetSitters.security.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PetSittersControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    UserRepository UserRep;

    private MockMvc mvc;

    @Before
    public void setUp () {
        DefaultMockMvcBuilder dfmk = MockMvcBuilders.webAppContextSetup(context);
        mvc = dfmk.build();
    }

    @After
    public void tearDown() throws Exception {
        UserRep.deleteAll();
    }

    void register(String cont) throws Exception {
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    void deleteAccount(String cont) throws Exception {
        mvc.perform(post("/petsitters/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
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
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));

        UserPetSitters u = UserRep.findByUsername("andy.luc24");
        assertEquals("Expected the firstName 'andy'", u.getFirstName(), "andy");
        assertEquals("Expected the lastName 'lucas'", u.getLastName(), "lucas");
        assertEquals("Expected the username 'andy.luc24'", u.getUsername(), "andy.luc24");
        assertTrue("Expected the password '1234'", new BCryptPasswordEncoder().matches("1234",u.getPassword()));
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date birthDate = format.parse("22-9-1982");
        assertEquals("Expected the birthdate '22-9-1982'", u.getBirthdate(), birthDate);
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by org.springframework.dao.DuplicateKeyException
    public void testRegisterDuplicatedWithUsername() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont);
        cont = "{\n" +
                "\t\"firstName\":\"redrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"876\",\n" +
                "\t\"email\":\"c@d.es\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont);
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by org.springframework.dao.DuplicateKeyException
    public void testRegisterDuplicatedWithEmail() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont);
        cont = "{\n" +
                "\t\"firstName\":\"redrigo\",\n" +
                "\t\"lastName\":\"gomez\",\n" +
                "\t\"username\":\"rodgo\",\n" +
                "\t\"password\":\"876\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"2-11-1842\"\n" +
                "}";
        register(cont);
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by java.text.ParseException: Unparseable date: "22/9/1982"
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

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by javax.validation.ValidationException: There are blank fields
    public void registerFirstNameFieldIsNull() throws Exception {
        String cont = "{\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont);
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by javax.validation.ValidationException: There are blank fields
    public void registerFirstNameFieldIsEmpty() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont);
    }

    @Test
    public void deleteAnExistingAccount() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont);
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        deleteAccount(cont);
        assertFalse("The User 'andy.luc24' should not exist", UserRep.existsByUsername("andy.luc24"));
    }

    @Test
    public void deleteAnExistingAccountWithWrongPassword() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont);
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\"\n" +
                "\t\"password\":\"asdas\"\n" +
                "}";
        deleteAccount(cont);
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by ValidationException: There are blank fields
    public void deleteAnExistingAccountWithoutPassword() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        register(cont);
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\"\n" +
                "}";
        deleteAccount(cont);
        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by ExceptionInvalidAccount.class: The account does not exist
    public void deleteAnNonExistingAccount() throws Exception {
        String cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        deleteAccount(cont);
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
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));

        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";

        ResultActions result= mvc.perform(post("/petsitters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
        result.andExpect(status().isOk());
    }

    @Test (expected = org.springframework.web.util.NestedServletException.class)
    public void loginIncorrect() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));

        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"123\"\n" +
                "}";

        ResultActions result= mvc.perform(post("/petsitters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
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
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));

        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";

        ResultActions result = mvc.perform(post("/petsitters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
        ObjectMapper objectMapper = new ObjectMapper();
        String resJson = result.andReturn().getResponse().getContentAsString();
        ResultActionLoginSchema resultActionLoginSchema = objectMapper.readValue(resJson,ResultActionLoginSchema.class);
        String token = resultActionLoginSchema.getResult().getToken();
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        assertEquals("Expected user is andy.luc24", jwtTokenUtil.getUsernameFromToken(token),"andy.luc24");
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
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));

        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";

        mvc.perform(post("/petsitters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));

        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        ResultActions result = mvc.perform(post("/petsitters/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
        result.andExpect(status().isOk());
    }
}
