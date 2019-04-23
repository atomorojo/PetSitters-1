package PetSitters.schemas;

import org.springframework.batch.item.validator.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Set;

public class GetCoordinatesSchema {
    @NotBlank
    String city;

    public GetCoordinatesSchema(String city) {
        this.city = city;
    }

    public GetCoordinatesSchema() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();     // Validation of the schema
        Set<ConstraintViolation<GetCoordinatesSchema>> violations = validator.validate(this);
        if (!violations.isEmpty())
            throw new ValidationException("There are blank fields");
    }
}
