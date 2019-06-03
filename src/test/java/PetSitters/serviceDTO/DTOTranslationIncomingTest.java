package PetSitters.serviceDTO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class DTOTranslationIncomingTest {

    DTOTranslationIncoming dtoTranslationIncoming;

    @Before
    public void setUp() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast("Hello2");
        list.addLast("Hello3");
        dtoTranslationIncoming = new DTOTranslationIncoming(list, "ro");
    }

    @After
    public void tearDown() throws Exception {
        dtoTranslationIncoming = null;
    }

    @Test
    public void splitNormal() {
        LinkedList<DTOTranslationIncoming> list = dtoTranslationIncoming.split();
        DTOTranslationIncoming dtoTranslationIncoming1 = list.get(0);
        DTOTranslationIncoming dtoTranslationIncoming2 = list.get(1);
        DTOTranslationIncoming dtoTranslationIncoming3 = list.get(2);
        LinkedList<String> elem1 = new LinkedList<>();
        elem1.addLast("Hello1");
        LinkedList<String> elem2 = new LinkedList<>();
        elem2.addLast("Hello2");
        LinkedList<String> elem3 = new LinkedList<>();
        elem3.addLast("Hello3");
        assertEquals("The element should be 'Hello1", dtoTranslationIncoming1.getText(), elem1);
        assertEquals("The element should be 'Hello2", dtoTranslationIncoming2.getText(), elem2);
        assertEquals("The element should be 'Hello3", dtoTranslationIncoming3.getText(), elem3);
        assertEquals("The language should be the same", dtoTranslationIncoming1.getTargetLanguage(), dtoTranslationIncoming.getTargetLanguage());
        assertEquals("The language should be the same", dtoTranslationIncoming2.getTargetLanguage(), dtoTranslationIncoming.getTargetLanguage());
        assertEquals("The language should be the same", dtoTranslationIncoming3.getTargetLanguage(), dtoTranslationIncoming.getTargetLanguage());
    }

    @Test
    public void splitEmpty() {
        dtoTranslationIncoming = new DTOTranslationIncoming();
        LinkedList<DTOTranslationIncoming> list = dtoTranslationIncoming.split();
        assertTrue("The list should be empty", list.isEmpty());
    }

    @Test
    public void joinNormal() {
        LinkedList<String> elem1 = new LinkedList<>();
        elem1.addLast("Hello1");
        LinkedList<String> elem2 = new LinkedList<>();
        elem2.addLast("Hello2");
        LinkedList<String> elem3 = new LinkedList<>();
        elem3.addLast("Hello3");
        DTOTranslationIncoming dtoTranslationIncoming1 = new DTOTranslationIncoming(elem1, "ro");
        DTOTranslationIncoming dtoTranslationIncoming2 = new DTOTranslationIncoming(elem2, "ro");
        DTOTranslationIncoming dtoTranslationIncoming3 = new DTOTranslationIncoming(elem3, "ro");
        LinkedList<DTOTranslationIncoming> list = new LinkedList<>();
        list.addLast(dtoTranslationIncoming1);
        list.addLast(dtoTranslationIncoming2);
        list.addLast(dtoTranslationIncoming3);
        dtoTranslationIncoming.join(list);
        LinkedList<String> output = dtoTranslationIncoming.getText();

        LinkedList<String> array = elem1;
        array.addAll(elem2);
        array.addAll(elem3);

        assertEquals("The lists should be equal", dtoTranslationIncoming.getText(), array);
        assertEquals("The language should be the same", dtoTranslationIncoming.getTargetLanguage(), "ro");
    }

    @Test
    public void joinEmpty() {
        LinkedList<DTOTranslationIncoming> list = new LinkedList<>();
        dtoTranslationIncoming.join(list);
        LinkedList<String> listCheck = dtoTranslationIncoming.getText();
        assertTrue("The list should be empty", listCheck.isEmpty());
    }

    @Test
    public void equalsWithEqual() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast("Hello2");
        list.addLast("Hello3");
        assertTrue("Should be equal", dtoTranslationIncoming.equals(new DTOTranslationIncoming(list, "ro")));
    }

    @Test
    public void equalsWithDifferentText() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast("Hello2");
        assertFalse("Shouldn't be equal", dtoTranslationIncoming.equals(new DTOTranslationIncoming(list, "ro")));
    }

    @Test
    public void equalsWithDifferentTargetLanguage() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast("Hello2");
        list.addLast("Hello3");
        assertFalse("Shouldn't be equal", dtoTranslationIncoming.equals(new DTOTranslationIncoming(list, "es")));
    }
}