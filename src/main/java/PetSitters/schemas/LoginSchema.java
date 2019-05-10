package PetSitters.schemas;

import org.springframework.batch.item.validator.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

public class LoginSchema implements Serializable {
    @NotBlank
    String username;
    @NotBlank
    String password;


    public LoginSchema(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();     // Validation of the schema
        Set<ConstraintViolation<LoginSchema>> violations = validator.validate(this);
        if (!violations.isEmpty())
            throw new ValidationException("There are blank fields");
    }

}
