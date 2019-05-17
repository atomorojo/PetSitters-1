package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class DeleteChatSchemaTest {

    DeleteChatSchema deleteChatSchema;

    @Before
    public void setUp() {
        deleteChatSchema = new DeleteChatSchema();
    }

    @After
    public void tearDown() {
        deleteChatSchema = null;
    }

    void fillDeleteSchema() {
        deleteChatSchema = new DeleteChatSchema("1234");
    }

    @Test
    public void validateAllIsCorrect() {
        fillDeleteSchema();
        deleteChatSchema.validate();
        assertEquals("The username should be '1234'", deleteChatSchema.getOtherUsername(), "1234");
    }


    @Test(expected = ValidationException.class)
    public void validateUsernameReceiverIsBlank() {
        fillDeleteSchema();
        deleteChatSchema.setOtherUsername("");
        deleteChatSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateUsernameReceiverIsNull() {
        fillDeleteSchema();
        deleteChatSchema.setOtherUsername(null);
        deleteChatSchema.validate();
    }
}