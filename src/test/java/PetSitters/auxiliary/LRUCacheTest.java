package PetSitters.auxiliary;

import PetSitters.exception.ExceptionCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LRUCacheTest {

    LRUCache<Integer, String> LRUCache;

    @Before
    public void setUp() throws Exception {
        LRUCache = new LRUCache<>(2);
    }

    @After
    public void tearDown() throws Exception {
        LRUCache = null;
    }

    @Test
    public void testInsertOneElement() throws ExceptionCache {
        LRUCache.update(1, "A");
        assertEquals("Cache should have cached 'A'", LRUCache.getFromCache(1), "A");
    }

    @Test
    public void testInsertThreeElements() throws ExceptionCache {
        LRUCache.update(1, "A");
        LRUCache.update(2, "B");
        LRUCache.update(3, "C");

        assertEquals("Cache should have cached 'B'",LRUCache.getFromCache(2), "B");
        assertEquals("Cache should have cached 'C'",LRUCache.getFromCache(3), "C");
    }

    @Test(expected = ExceptionCache.class)
    public void testInsertThreeElementsRetireNonExisting() throws ExceptionCache {
        LRUCache.update(1, "A");
        LRUCache.update(2, "B");
        LRUCache.update(3, "C");

        LRUCache.getFromCache(1);
    }

    @Test(expected = ExceptionCache.class)
    public void testVoidRetireNonExisting() throws ExceptionCache {
        LRUCache.getFromCache(1);
    }

    @Test(expected = ExceptionCache.class)
    public void testLRU() throws ExceptionCache {
        LRUCache.update(1, "A");
        LRUCache.update(2, "B");
        LRUCache.update(3, "C");
        LRUCache.update(4, "D");

        LRUCache.getFromCache(2);
    }
}