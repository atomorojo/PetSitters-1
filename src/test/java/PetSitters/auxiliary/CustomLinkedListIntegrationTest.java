package PetSitters.auxiliary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.springframework.test.util.AssertionErrors.assertEquals;

public class CustomLinkedListIntegrationTest {

    CustomLinkedList<Integer> C;

    @Before
    public void setUp() {
        C = new CustomLinkedList<Integer>();
    }

    @After
    public void tearDown() {
        C = null;
    }

    @Test
    public void testAddLastOneItem() {
        C.addLast(1);
        Node<Integer> N = C.getLastReference();
        assertEquals("The element should be 1", N.getElem(), 1);
    }

    @Test
    public void testAddLastThreeItems() {
        C.addLast(1);
        Node<Integer> N = C.getLastReference();
        assertEquals("The element should be 1", N.getElem(), 1);
        C.addLast(2);
        N = C.getLastReference();
        assertEquals("The element should be 2", N.getElem(), 2);
        C.addLast(3);
        N = C.getLastReference();
        assertEquals("The element should be 3", N.getElem(), 3);
    }

    @Test
    public void testPopFirst() {
        C.addLast(1);
        C.addLast(2);
        C.addLast(3);
        Node<Integer> N = C.getFirstReference();
        assertEquals("The element should be 1", N.getElem(), 1);
        C.popFirst();
        N = C.getFirstReference();
        assertEquals("The element should be 2", N.getElem(), 2);
    }

    @Test
    public void testRemoveByReference() {
        C.addLast(1);
        C.addLast(2);
        Node<Integer> N = C.getLastReference();
        C.addLast(3);
        assertEquals("The element should be 2", N.getElem(), 2);
        C.removeByReference(N);

        N = C.getFirstReference();
        assertEquals("The element should be 1", N.getElem(), 1);
        C.popFirst();
        N = C.getFirstReference();
        assertEquals("The element should be 3", N.getElem(), 3);
    }

    @Test
    public void testRemoveByReferenceOneElementFirst() {
        C.addLast(1);
        Node<Integer> N = C.getFirstReference();
        assertEquals("The element should be 1", N.getElem(), 1);
        C.removeByReference(N);
        Node<Integer> NS = C.getFirstReference();
        Node<Integer> NE = C.getLastReference();
        assertEquals("The pointer NS should be null", NS, null);
        assertEquals("The pointer NE should be null", NE, null);
    }

    @Test
    public void testRemoveByReferenceOneElementLast() {
        C.addLast(1);
        Node<Integer> N = C.getLastReference();
        assertEquals("The element should be 1", N.getElem(), 1);
        C.removeByReference(N);
        Node<Integer> NS = C.getFirstReference();
        Node<Integer> NE = C.getLastReference();
        assertEquals("The pointer NS should be null", NS, null);
        assertEquals("The pointer NE should be null", NE, null);
    }


    @Test
    public void testRemoveByReferenceThreeElements() {
        C.addLast(1);
        C.addLast(2);
        C.addLast(3);
        Node<Integer> N = C.getLastReference();
        assertEquals("The element should be 3", N.getElem(), 3);
        C.removeByReference(N);
        Node<Integer> NE = C.getLastReference();
        assertEquals("The pointer NE should be point to 2", NE.getElem(), 2);
    }
}