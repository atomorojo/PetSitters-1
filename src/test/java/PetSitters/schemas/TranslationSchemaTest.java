package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class TranslationSchemaTest {

    TranslationSchema translationSchema;

    @Before
    public void setUp() throws Exception {
        translationSchema = new TranslationSchema();
    }

    @After
    public void tearDown() throws Exception {
        translationSchema = null;
    }

    void fillTranslationSchema() {
        String[] array = {"A", "B", "C"};
        translationSchema = new TranslationSchema(array, "be");
    }

    @Test
    public void validateAllIsCorrect() {
        fillTranslationSchema();
        translationSchema.validate();
        String[] arrayCheck = {"A", "B", "C"};
        assertEquals("Array should be equal", translationSchema.getInputInEnglish(), arrayCheck);
        assertEquals("Output language should be equal", translationSchema.getOutputLanguage(), "be");
    }

    @Test(expected = ValidationException.class)
    public void validateInputInEnglishIsNull() {
        fillTranslationSchema();
        translationSchema.setInputInEnglish(null);
        translationSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateInputInEnglishHasZeroLength() {
        fillTranslationSchema();
        String[] array = {};
        translationSchema.setInputInEnglish(array);
        translationSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateInputInEnglishHasNullElement() {
        fillTranslationSchema();
        String[] array = {"A", null};
        translationSchema.setInputInEnglish(array);
        translationSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateInputInEnglishHasBlankElement() {
        fillTranslationSchema();
        String[] array = {"A", ""};
        translationSchema.setInputInEnglish(array);
        translationSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateOutputLanguageIsNull() {
        fillTranslationSchema();
        translationSchema.setOutputLanguage(null);
        translationSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateOutputLanguageIsBlank() {
        fillTranslationSchema();
        translationSchema.setOutputLanguage("");
        translationSchema.validate();
    }
}