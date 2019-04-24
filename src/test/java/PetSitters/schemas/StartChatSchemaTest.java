package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class StartChatSchemaTest {

    StartChatSchema startChatSchema;

    @Before
    public void setUp() throws Exception {
        startChatSchema = new StartChatSchema();
    }

    @After
    public void tearDown() throws Exception {
        startChatSchema = null;
    }

    void fillStartChatSchema() {
        startChatSchema = new StartChatSchema("user123");
    }

    @Test
    public void validateAllIsCorrect() {
        fillStartChatSchema();
        startChatSchema.validate();
        assertEquals("Username should be 'user123'", startChatSchema.getOtherUsername(), "user123");
    }


    @Test(expected = ValidationException.class)
    public void validateNewPasswordIsBlank() {
        fillStartChatSchema();
        startChatSchema.setOtherUsername("");
        startChatSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateNewPasswordIsNull() {
        fillStartChatSchema();
        startChatSchema.setOtherUsername(null);
        startChatSchema.validate();
    }
}