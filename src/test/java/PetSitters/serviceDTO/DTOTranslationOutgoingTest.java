package PetSitters.serviceDTO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.ExecutableUpdateOperationExtensionsKt;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class DTOTranslationOutgoingTest {

    DTOTranslationOutgoing dtoTranslationOutgoing;

    @Before
    public void setUp() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast("Hello2");
        list.addLast("Hello3");
        dtoTranslationOutgoing = new DTOTranslationOutgoing(list, true);
    }

    @After
    public void tearDown() throws Exception {
        dtoTranslationOutgoing = null;
    }

    @Test
    public void splitNormal() {
        LinkedList<DTOTranslationOutgoing> list = dtoTranslationOutgoing.split();
        DTOTranslationOutgoing dtoTranslationOutgoing1 = list.get(0);
        DTOTranslationOutgoing dtoTranslationOutgoing2 = list.get(1);
        DTOTranslationOutgoing dtoTranslationOutgoing3 = list.get(2);
        LinkedList<String> elem1 = new LinkedList<>();
        elem1.addLast("Hello1");
        LinkedList<String> elem2 = new LinkedList<>();
        elem2.addLast("Hello2");
        LinkedList<String> elem3 = new LinkedList<>();
        elem3.addLast("Hello3");
        assertEquals("The element should be 'Hello1", dtoTranslationOutgoing1.getText(), elem1);
        assertEquals("The element should be 'Hello2", dtoTranslationOutgoing2.getText(), elem2);
        assertEquals("The element should be 'Hello3", dtoTranslationOutgoing3.getText(), elem3);
        assertEquals("The translation should be fully resolved", dtoTranslationOutgoing1.isFullyResolved(), dtoTranslationOutgoing.isFullyResolved());
        assertEquals("The translation should be fully resolved", dtoTranslationOutgoing2.isFullyResolved(), dtoTranslationOutgoing.isFullyResolved());
        assertEquals("The translation should be fully resolved", dtoTranslationOutgoing3.isFullyResolved(), dtoTranslationOutgoing.isFullyResolved());
    }

    @Test
    public void splitEmpty() {
        dtoTranslationOutgoing = new DTOTranslationOutgoing();
        LinkedList<DTOTranslationOutgoing> list = dtoTranslationOutgoing.split();
        assertTrue("The list should be empty", list.isEmpty());
    }

    @Test
    public void joinNormal() {
        LinkedList<String> elem1 = new LinkedList<>();
        elem1.addLast("Hello1");
        LinkedList<String> elem2 = new LinkedList<>();
        elem2.addLast("Hello2");
        LinkedList<String> elem3 = new LinkedList<>();
        elem3.addLast(null);
        DTOTranslationOutgoing dtoTranslationOutgoing1 = new DTOTranslationOutgoing(elem1, true);
        DTOTranslationOutgoing dtoTranslationOutgoing2 = new DTOTranslationOutgoing(elem2, true);
        DTOTranslationOutgoing dtoTranslationOutgoing3 = new DTOTranslationOutgoing(elem3, false);
        LinkedList<DTOTranslationOutgoing> list = new LinkedList<>();
        list.addLast(dtoTranslationOutgoing1);
        list.addLast(dtoTranslationOutgoing2);
        list.addLast(dtoTranslationOutgoing3);
        dtoTranslationOutgoing.join(list);

        LinkedList<String> array = elem1;
        array.addAll(elem2);
        array.addAll(elem3);

        assertEquals("The lists should be equal", dtoTranslationOutgoing.getText(), array);
        assertFalse("The translation should not be fully resolved", dtoTranslationOutgoing.isFullyResolved());
    }

    @Test
    public void joinAllTrue() {
        LinkedList<String> elem1 = new LinkedList<>();
        elem1.addLast("Hello1");
        LinkedList<String> elem2 = new LinkedList<>();
        elem2.addLast("Hello2");
        LinkedList<String> elem3 = new LinkedList<>();
        elem3.addLast("Hello3");
        DTOTranslationOutgoing dtoTranslationOutgoing1 = new DTOTranslationOutgoing(elem1, true);
        DTOTranslationOutgoing dtoTranslationOutgoing2 = new DTOTranslationOutgoing(elem2, true);
        DTOTranslationOutgoing dtoTranslationOutgoing3 = new DTOTranslationOutgoing(elem3, true);
        LinkedList<DTOTranslationOutgoing> list = new LinkedList<>();
        list.addLast(dtoTranslationOutgoing1);
        list.addLast(dtoTranslationOutgoing2);
        list.addLast(dtoTranslationOutgoing3);
        dtoTranslationOutgoing.join(list);

        LinkedList<String> array = elem1;
        array.addAll(elem2);
        array.addAll(elem3);

        assertEquals("The lists should be equal", dtoTranslationOutgoing.getText(), array);
        assertTrue("The translation should be fully resolved", dtoTranslationOutgoing.isFullyResolved());
    }

    @Test
    public void joinEmpty() {
        LinkedList<DTOTranslationOutgoing> list = new LinkedList<>();
        dtoTranslationOutgoing.join(list);
        LinkedList<String> listCheck = dtoTranslationOutgoing.getText();
        assertTrue("The list should be empty", listCheck.isEmpty());
    }

    @Test
    public void mergeNormal() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast("Hello2");
        list.addLast(null);
        dtoTranslationOutgoing = new DTOTranslationOutgoing(list, false);

        LinkedList<String> listServ = new LinkedList<>();
        listServ.addLast("Hello3");
        DTOTranslationOutgoing dtoTranslationOutgoingResultService = new DTOTranslationOutgoing(listServ, true);

        dtoTranslationOutgoing.merge(dtoTranslationOutgoingResultService);

        LinkedList<String> array = new LinkedList<>();
        array.addLast("Hello1");
        array.addLast("Hello2");
        array.addLast("Hello3");

        assertEquals("The lists should be equal", dtoTranslationOutgoing.getText(), array);
        assertTrue("The translation should be fully resolved", dtoTranslationOutgoing.isFullyResolved());
    }

    @Test
    public void mergeNothingToMerge() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast("Hello2");
        list.addLast("Hello3");
        dtoTranslationOutgoing = new DTOTranslationOutgoing(list, true);

        LinkedList<String> listServ = new LinkedList<>();
        DTOTranslationOutgoing dtoTranslationOutgoingResultService = new DTOTranslationOutgoing(listServ, true);

        dtoTranslationOutgoing.merge(dtoTranslationOutgoingResultService);

        LinkedList<String> array = new LinkedList<>();
        array.addLast("Hello1");
        array.addLast("Hello2");
        array.addLast("Hello3");

        assertEquals("The lists should be equal", dtoTranslationOutgoing.getText(), array);
        assertTrue("The translation should be fully resolved", dtoTranslationOutgoing.isFullyResolved());
    }

    @Test(expected = Exception.class)
    public void mergeExcessInServicePart() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast("Hello2");
        list.addLast(null);
        dtoTranslationOutgoing = new DTOTranslationOutgoing(list, false);

        LinkedList<String> listServ = new LinkedList<>();
        listServ.addLast("Hello3");
        listServ.addLast("Hello3");
        DTOTranslationOutgoing dtoTranslationOutgoingResultService = new DTOTranslationOutgoing(listServ, true);

        dtoTranslationOutgoing.merge(dtoTranslationOutgoingResultService);
    }

    @Test(expected = Exception.class)
    public void mergeExcessInServicePartInMiddle() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast(null);
        list.addLast("Hello3");
        dtoTranslationOutgoing = new DTOTranslationOutgoing(list, false);

        LinkedList<String> listServ = new LinkedList<>();
        listServ.addLast("Hello2");
        listServ.addLast("Hello4");
        DTOTranslationOutgoing dtoTranslationOutgoingResultService = new DTOTranslationOutgoing(listServ, true);

        dtoTranslationOutgoing.merge(dtoTranslationOutgoingResultService);
    }

    @Test(expected = Exception.class)
    public void mergeNotEnoughOarameters() throws Exception {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("Hello1");
        list.addLast(null);
        list.addLast("Hello3");
        dtoTranslationOutgoing = new DTOTranslationOutgoing(list, false);

        LinkedList<String> listServ = new LinkedList<>();
        DTOTranslationOutgoing dtoTranslationOutgoingResultService = new DTOTranslationOutgoing(listServ, true);

        dtoTranslationOutgoing.merge(dtoTranslationOutgoingResultService);
    }
}