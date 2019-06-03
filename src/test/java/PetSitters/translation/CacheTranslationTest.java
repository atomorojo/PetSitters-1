package PetSitters.translation;

import PetSitters.exception.ExceptionCache;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTOTranslationIncoming;
import PetSitters.serviceDTO.DTOTranslationOutgoing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class CacheTranslationTest {

    CacheTranslation cacheTranslation;

    @Before
    public void setUp() throws Exception {
        cacheTranslation = new CacheTranslation(2);
    }

    @After
    public void tearDown() throws Exception {
        cacheTranslation = null;
    }

    @Test
    public void testInsertOneElement() throws ExceptionServiceError {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!");
        LinkedList<String> listOutput = new LinkedList<>();
        listOutput.addLast("¡Hola mundo!");
        cacheTranslation.update(new DTOTranslationIncoming(list, "es"), new DTOTranslationOutgoing(listOutput, true));
        DTOTranslationOutgoing output = (DTOTranslationOutgoing)cacheTranslation.execute(new DTOTranslationIncoming(list, "es"));
        LinkedList<String> listOutputCheck = new LinkedList<>();
        listOutputCheck.addLast("¡Hola mundo!");
        assertEquals("Cache should have cached '¡Hola mundo!'", output.getText(), listOutputCheck);
    }

    @Test
    public void testInsertThreeElements() throws ExceptionServiceError {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!1");
        list.addLast("Hello World!2");
        list.addLast("Hello World!3");
        LinkedList<String> listOutput = new LinkedList<>();
        listOutput.addLast("¡Hola mundo!1");
        listOutput.addLast("¡Hola mundo!2");
        listOutput.addLast("¡Hola mundo!3");
        cacheTranslation.update(new DTOTranslationIncoming(list, "es"), new DTOTranslationOutgoing(listOutput, true));
        list.pollFirst();
        DTOTranslationOutgoing output = (DTOTranslationOutgoing)cacheTranslation.execute(new DTOTranslationIncoming(list, "es"));
        LinkedList<String> listOutputCheck = new LinkedList<>();
        listOutputCheck.addLast("¡Hola mundo!2");
        listOutputCheck.addLast("¡Hola mundo!3");
        assertEquals("Cache should have cached the last two elements", output.getText(), listOutputCheck);
    }

    @Test
    public void testInsertThreeElementsRetireNonExisting() throws ExceptionServiceError {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!1");
        list.addLast("Hello World!2");
        list.addLast("Hello World!3");
        LinkedList<String> listOutput = new LinkedList<>();
        listOutput.addLast("¡Hola mundo!1");
        listOutput.addLast("¡Hola mundo!2");
        listOutput.addLast("¡Hola mundo!3");
        cacheTranslation.update(new DTOTranslationIncoming(list, "es"), new DTOTranslationOutgoing(listOutput, true));
        list.pollLast();
        list.pollLast();
        DTOTranslationOutgoing dtoTranslationOutgoing = (DTOTranslationOutgoing)cacheTranslation.execute(new DTOTranslationIncoming(list, "es"));
        assertFalse("The query shouldn't be fully resolved", dtoTranslationOutgoing.isFullyResolved());
    }

    @Test
    public void testVoidRetireNonExisting() throws ExceptionServiceError {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!1");
        DTOTranslationOutgoing dtoTranslationOutgoing = (DTOTranslationOutgoing)cacheTranslation.execute(new DTOTranslationIncoming(list, "es"));
        assertFalse("The query shouldn't be fully resolved", dtoTranslationOutgoing.isFullyResolved());
    }

    @Test
    public void testLRU() throws ExceptionServiceError {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!1");
        list.addLast("Hello World!2");
        list.addLast("Hello World!3");
        list.addLast("Hello World!4");
        LinkedList<String> listOutput = new LinkedList<>();
        listOutput.addLast("¡Hola mundo!1");
        listOutput.addLast("¡Hola mundo!2");
        listOutput.addLast("¡Hola mundo!3");
        listOutput.addLast("¡Hola mundo!4");
        cacheTranslation.update(new DTOTranslationIncoming(list, "es"), new DTOTranslationOutgoing(listOutput, true));
        list = new LinkedList<>();
        list.addLast("¡Hola mundo!2");
        DTOTranslationOutgoing dtoTranslationOutgoing = (DTOTranslationOutgoing)cacheTranslation.execute(new DTOTranslationIncoming(list, "es"));
        assertFalse("The query shouldn't be fully resolved", dtoTranslationOutgoing.isFullyResolved());
    }
}