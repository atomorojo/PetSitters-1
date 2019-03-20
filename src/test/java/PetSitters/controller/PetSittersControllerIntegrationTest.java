package PetSitters.controller;

import PetSitters.entity.User;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

    @Test
    public void registerNormalRegister() throws Exception {
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

        User u = UserRep.findByUsername("andy.luc24");

        assertEquals("Expected the firstName 'andy'", u.getFirstName(), "andy");
        assertEquals("Expected the lastName 'lucas'", u.getLastName(), "lucas");
        assertEquals("Expected the username 'andy.luc24'", u.getUsername(), "andy.luc24");
        assertEquals("Expected the password '1234'", u.getPassword(), "1234");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date birthDate = format.parse("22-9-1982");
        assertEquals("Expected the birthdate '22-9-1982'", u.getBirthdate(), birthDate);
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by java.text.ParseException: Unparseable date: "22/9/1982"
    public void registerWrongFormatOfDate() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"andy\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"birthdate\":\"22/9/1982\"\n" +
                "}";
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by javax.validation.ValidationException: There are blank fields
    public void registerFirstNameFieldIsNull() throws Exception {
        String cont = "{\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by javax.validation.ValidationException: There are blank fields
    public void registerFirstNameFieldIsEmpty() throws Exception {
        String cont = "{\n" +
                "\t\"firstName\":\"\",\n" +
                "\t\"lastName\":\"lucas\",\n" +
                "\t\"username\":\"andy.luc24\",\n" +
                "\t\"password\":\"1234\",\n" +
                "\t\"birthdate\":\"22-9-1982\"\n" +
                "}";
        mvc.perform(post("/petsitters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }

    @Test
    public void deleteAnExistingAccount() throws Exception {
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


        assertTrue("The User 'andy.luc24' should exist", UserRep.existsByUsername("andy.luc24"));

        cont = "{\n" +
                "\t\"username\":\"andy.luc24\"\n" +
                "}";
        mvc.perform(post("/petsitters/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));

        assertFalse("The User 'andy.luc24' should not exist", UserRep.existsByUsername("andy.luc24"));
    }

    @Test(expected = org.springframework.web.util.NestedServletException.class)     // Provoked by ExceptionInvalidAccount.class: The account does not exist
    public void deleteAnNonExistingAccount() throws Exception {
        String cont = "{\n" +
                "\t\"username\":\"andy.luc24\"\n" +
                "}";
        mvc.perform(post("/petsitters/deleteAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cont));
    }
}