package PetSitters.serviceLocator;

import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTO;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;

public interface Service {
    DTO execute(DTO parameter) throws Exception;
}
