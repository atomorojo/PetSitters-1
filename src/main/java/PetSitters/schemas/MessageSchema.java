package PetSitters.schemas;

import org.springframework.batch.item.validator.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

public class MessageSchema implements Serializable {

    @NotBlank
    String userWhoReceives;

    @NotBlank
    String isMultimedia;

    @NotBlank
    String content;

    public MessageSchema() {
    }

    public MessageSchema(@NotBlank String userWhoReceives, @NotBlank String isMultimedia, @NotBlank String content) {
        this.userWhoReceives = userWhoReceives;
        this.isMultimedia = isMultimedia;
        this.content = content;
    }

    public String getUserWhoReceives() {
        return userWhoReceives;
    }

    public void setUserWhoReceives(String userWhoReceives) {
        this.userWhoReceives = userWhoReceives;
    }

    public Boolean getIsMultimedia() {
        return isMultimedia.equals("true");
    }

    public void setIsMultimedia(Boolean multimedia) {
        if (multimedia) {
            isMultimedia = "true";
        } else {
            isMultimedia = "false";
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();     // Validation of the schema
        Set<ConstraintViolation<MessageSchema>> violations = validator.validate(this);
        if (!violations.isEmpty())
            throw new ValidationException("There are blank fields");
    }
}