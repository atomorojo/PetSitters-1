package PetSitters.translation;

import PetSitters.serviceDTO.DTOTranslationIncoming;
import PetSitters.serviceDTO.DTOTranslationOutgoing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class TranslationProxyTest {

    TranslationProxy translationProxy;

    @Before
    public void setUp() {
        translationProxy = new TranslationProxy();
    }

    @After
    public void tearDown() {
        translationProxy = null;
    }

    @Test
    public void executeNonCachedResult() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!");
        DTOTranslationOutgoing output = (DTOTranslationOutgoing)translationProxy.execute(new DTOTranslationIncoming(list, "es"));
        LinkedList<String> listOutputCheck = new LinkedList<>();
        listOutputCheck.addLast("Hola Mundo!");
        assertEquals("The result in Spanish should be: 'Hola Mundo!'", output.getText(), listOutputCheck);
    }

    @Test
    public void executeCachedResult() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!");
        DTOTranslationOutgoing output = (DTOTranslationOutgoing)translationProxy.execute(new DTOTranslationIncoming(list, "es"));
        LinkedList<String> listOutputCheck = new LinkedList<>();
        listOutputCheck.addLast("Hola Mundo!");
        output = (DTOTranslationOutgoing)translationProxy.execute(new DTOTranslationIncoming(list, "es"));
        assertEquals("The result in Spanish should be: 'Hola Mundo!'", output.getText(), listOutputCheck);
    }

    @Test
    public void executeCachedResultEntangled() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello World!");
        list.addLast("Hello");
        translationProxy.execute(new DTOTranslationIncoming(list, "es"));
        list.addLast("Blue");
        DTOTranslationOutgoing output = (DTOTranslationOutgoing)translationProxy.execute(new DTOTranslationIncoming(list, "es"));
        LinkedList<String> listOutputCheck = new LinkedList<>();
        listOutputCheck.addLast("Hola Mundo!");
        listOutputCheck.addLast("Hola");
        listOutputCheck.addLast("Azul");
        assertEquals("The result in Spanish should be equal to listOutputCheck", output.getText(), listOutputCheck);
    }
}