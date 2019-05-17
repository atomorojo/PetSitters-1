package PetSitters.auxiliary;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ReadWebPage {
    public String getProcessedText(String pathToFile, Map<String, String> template) throws IOException {
        String path = new File(".").getCanonicalPath();
        String htmlPage = new String(Files.readAllBytes(Paths.get(path + pathToFile)));
        return StrSubstitutor.replace(htmlPage, template);
    }

    public String getText(String pathToFile) throws IOException {
        String path = new File(".").getCanonicalPath();
        return new String(Files.readAllBytes(Paths.get(path + pathToFile)));
    }
}
