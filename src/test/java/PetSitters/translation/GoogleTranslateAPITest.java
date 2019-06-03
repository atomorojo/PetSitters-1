package PetSitters.translation;

import PetSitters.serviceDTO.DTOTranslationIncoming;
import PetSitters.serviceDTO.DTOTranslationOutgoing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class GoogleTranslateAPITest {

    GoogleTranslateAPI googleTranslateAPI;

    @Before
    public void setUp() {
        googleTranslateAPI = new GoogleTranslateAPI();
    }

    @After
    public void tearDown() {
        googleTranslateAPI = null;
    }

    @Test
    public void execute() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!");
        DTOTranslationOutgoing dtoTranslationOutgoing = (DTOTranslationOutgoing) googleTranslateAPI.execute(new DTOTranslationIncoming(list, "es"));
        LinkedList<String> linkedList = dtoTranslationOutgoing.getText();
        LinkedList<String> listOutput = new LinkedList<>();
        listOutput.addLast("Hola Mundo!");
        assertEquals("The lists should be equal", linkedList, listOutput);
    }
}