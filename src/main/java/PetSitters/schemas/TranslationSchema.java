package PetSitters.schemas;

import org.springframework.batch.item.validator.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Set;

public class TranslationSchema {

    String[] inputInEnglish;

    @NotBlank
    String outputLanguage;

    public TranslationSchema() {
    }

    public TranslationSchema(@NotBlank String[] inputInEnglish, @NotBlank String outputLanguage) {
        this.inputInEnglish = inputInEnglish;
        this.outputLanguage = outputLanguage;
    }

    public String[] getInputInEnglish() {
        return inputInEnglish;
    }

    public void setInputInEnglish(String[] inputInEnglish) {
        this.inputInEnglish = inputInEnglish;
    }

    public String getOutputLanguage() {
        return outputLanguage;
    }

    public void setOutputLanguage(String outputLanguage) {
        this.outputLanguage = outputLanguage;
    }

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();     // Validation of the schema
        Set<ConstraintViolation<TranslationSchema>> violations = validator.validate(this);
        if (!violations.isEmpty())
            throw new ValidationException("There are blank fields");
        if (inputInEnglish == null) {
            throw new ValidationException("The field inputInEnglish cannot be null");
        }
        if (inputInEnglish.length == 0) {
            throw new ValidationException("Length of the field inputInEnglish cannot be 0");
        }
        for (String s: inputInEnglish) {
            if (s == null || s.equals("")) throw new ValidationException("The field inputInEnglish contains blank fields");
        }
    }
}
