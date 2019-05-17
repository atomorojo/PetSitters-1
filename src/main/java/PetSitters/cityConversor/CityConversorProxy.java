package PetSitters.cityConversor;

import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTO;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;

public class CityConversorProxy implements ICityConversor {
    private CacheCityConversor cacheCityConversor;
    private OpenStreetCityConversor cityConversor;

    public CityConversorProxy() {
        cacheCityConversor = new CacheCityConversor(5000);
        cityConversor = new OpenStreetCityConversor();
    }

    @Override
    public DTO execute(DTO parameter) throws IOException, JSONException, ExceptionServiceError {
        DTO result;
        try {
            result = cacheCityConversor.execute(parameter);
        } catch (ExceptionServiceError exceptionServiceError) {
            result = cityConversor.execute(parameter);
            cacheCityConversor.update(parameter, result);
        }
        return result;
    }
}
