package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class MessageSchemaTest {

    MessageSchema M;

    @Before
    public void setUp() throws Exception {
        M = new MessageSchema("a","true","g");
    }

    @After
    public void tearDown() throws Exception {
        M = null;
    }

    @Test
    public void validateAllIsCorrect() {
        M.validate();
        assertEquals ("The message should be 'g'", M.getContent(), "g");
        assertEquals ("The user who receives should be 'a'", M.getUserWhoReceives(), "a");
        assertTrue ("The message should be a multimedia file", M.getIsMultimedia());
    }

    @Test(expected = ValidationException.class)
    public void validateContentIsNull() {
        M.setContent(null);
        M.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateContentIsEmpty() {
        M.setContent("");
        M.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateUserWhoReceivesIsNull() {
        M.setUserWhoReceives(null);
        M.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateUserWhoReceivesIsEmpty() {
        M.setUserWhoReceives("");
        M.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateIsMultimediaIsNull() {
        M = new MessageSchema("a",null,"g");
        M.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateIsMultimediaIsEmpty() {
        M = new MessageSchema("a","","g");
        M.validate();
    }

}
