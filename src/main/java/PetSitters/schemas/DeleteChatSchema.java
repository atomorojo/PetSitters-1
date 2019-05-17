package PetSitters.schemas;

import org.springframework.batch.item.validator.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Set;

public class DeleteChatSchema {
    @NotBlank
    String otherUsername;

    public DeleteChatSchema() {
    }

    public DeleteChatSchema(@NotBlank String usernameReceiver) {
        this.otherUsername = usernameReceiver;
    }

    public String getOtherUsername() {
        return otherUsername;
    }

    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();     // Validation of the schema
        Set<ConstraintViolation<DeleteChatSchema>> violations = validator.validate(this);
        if (!violations.isEmpty())
            throw new ValidationException("There are blank fields");
    }
}
