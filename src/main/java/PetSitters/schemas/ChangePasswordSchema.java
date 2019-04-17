package PetSitters.schemas;

import org.springframework.batch.item.validator.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

public class ChangePasswordSchema implements Serializable {
    @NotBlank
    String oldPassword;

    @NotBlank
    String newPassword;

    public ChangePasswordSchema() {
    }

    public ChangePasswordSchema(@NotBlank String oldPassword, @NotBlank String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();     // Validation of the schema
        Set<ConstraintViolation<ChangePasswordSchema>> violations = validator.validate(this);
        if (!violations.isEmpty())
            throw new ValidationException("There are blank fields");
    }
}