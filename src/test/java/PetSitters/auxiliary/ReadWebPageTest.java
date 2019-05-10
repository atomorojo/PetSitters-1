package PetSitters.auxiliary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ReadWebPageTest {

    ReadWebPage readWebPage;

    @Before
    public void setUp() throws Exception {
        readWebPage = new ReadWebPage();
    }

    @After
    public void tearDown() throws Exception {
        readWebPage = null;
    }

    @Test
    public void getProcessedTextNormal() throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("text", "TESTING...");
        String output = readWebPage.getProcessedText("/src/test/java/PetSitters/files/test.txt", data);
        assertEquals("The returned message should be 'Hello World!!! TESTING...'", output,"Hello World!!! TESTING...");
    }

    @Test(expected = AccessDeniedException.class)
    public void getProcessedTextNonExistingFile() throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("text", "TESTING...");
        readWebPage.getProcessedText("...", data);
    }

    @Test
    public void getTextNormal() throws IOException {
        String output = readWebPage.getText("/src/test/java/PetSitters/files/test.txt");
        assertEquals("The returned message should be 'Hello World!!! ${text}'", output,"Hello World!!! ${text}");
    }

    @Test(expected = AccessDeniedException.class)
    public void getTextNonExistingFile() throws IOException {
        readWebPage.getText("...");
    }
}