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
import org.springframework.http.HttpHeaders;
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
    public void tearDown() {
        UserRep.deleteAll();
    }

    ResultActions register(String cont) throws Exception {
        return mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions deleteAccount(String cont) throws Exception {
        return mvc.perform(post("/petsitters/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    ResultActions login(String cont) throws Exception {
        return mvc.perform(post("/petsitters/login")
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
        register(cont).andExpect(status().isOk());

        UserPetSitters u = UserRep.findByUsername("andy.luc24");
        assertEquals("Expected the firstName 'andy'", u.getFirstName(), "andy");
        assertEquals("Expected the lastName 'lucas'", u.getLastName(), "lucas");
        assertEquals("Expected the username 'andy.luc24'", u.getUsername(), "andy.luc24");
        assertTrue("Expected the password '1234'", new BCryptPasswordEncoder().matches("1234",u.getPassword()));
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
        register(cont).andExpect(status().is5xxServerError());
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
        register(cont).andExpect(status().is5xxServerError());
    }

    @Test
    public void registerWrongFormatOfDate() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"email\":\"a@b.com\",\n" +
                "\t\"birthdate\":\"22/9/1982\"\n" +
                "}";
        register(cont).andExpect(status().is5xxServerError());
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

        ResultActions result = login(cont).andExpect(status().isOk());
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
        register(cont).andExpect(status().isOk());
        cont = "{\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\"\n" +
                "}";
        ResultActions result = login(cont).andExpect(status().isOk());
        ObjectMapper objectMapper = new ObjectMapper();
        String resJson = result.andReturn().getResponse().getContentAsString();
        ResultActionLoginSchema resultActionLoginSchema = objectMapper.readValue(resJson,ResultActionLoginSchema.class);
        String token = resultActionLoginSchema.getResult().getToken();
        cont = "{\n" +
                "\t\"password\":\"1234\"\n" +
                "}";

        mvc.perform(post("/petsitters/deleteAccount")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont))
                .andExpect(status().isOk());
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
        ResultActions result = login(cont).andExpect(status().isOk());
        ObjectMapper objectMapper = new ObjectMapper();
        String resJson = result.andReturn().getResponse().getContentAsString();
        ResultActionLoginSchema resultActionLoginSchema = objectMapper.readValue(resJson,ResultActionLoginSchema.class);
        String token = resultActionLoginSchema.getResult().getToken();
        cont = "{\n" +
                "\t\"password\":\"asdasd\"\n" +
                "}";

        mvc.perform(post("/petsitters/deleteAccount")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont))
                .andExpect(status().is5xxServerError());
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
        ResultActions result = login(cont).andExpect(status().isOk());
        ObjectMapper objectMapper = new ObjectMapper();
        String resJson = result.andReturn().getResponse().getContentAsString();
        ResultActionLoginSchema resultActionLoginSchema = objectMapper.readValue(resJson,ResultActionLoginSchema.class);
        String token = resultActionLoginSchema.getResult().getToken();
        cont = "{\n" +
                "}";

        mvc.perform(post("/petsitters/deleteAccount")
                .header(HttpHeaders.AUTHORIZATION, "Bearer: " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont))
                .andExpect(status().is5xxServerError());
    }
}
