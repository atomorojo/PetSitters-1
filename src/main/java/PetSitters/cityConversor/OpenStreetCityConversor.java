package PetSitters.cityConversor;

import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTO;
import PetSitters.serviceDTO.DTOCity;
import PetSitters.serviceDTO.DTOCoordinates;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenStreetCityConversor implements ICityConversor {

    private final String serviceURL = "https://nominatim.openstreetmap.org/";

    @Override
    public DTO execute(DTO parameter) throws IOException, JSONException, ExceptionServiceError {
        DTOCity dtoCity = (DTOCity) parameter;
        String parameterCity = dtoCity.getCity();
        parameterCity = parameterCity.replaceAll(" ", "%20");
        String GETParameters = "search?city=" + parameterCity + "&limit=1&format=json";

        URL obj = new URL(serviceURL + GETParameters);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            StringBuffer response = new StringBuffer();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject element;
            try {
                JSONArray returnedObject = new JSONArray(response.toString());
                element = returnedObject.getJSONObject(0);
            } catch (JSONException e) {
                throw new ExceptionServiceError("The requested city does not exist");
            }
            String longitude = element.getString("lon");
            String latitude = element.getString("lat");
            return new DTOCoordinates(longitude, latitude);
        } else {
            throw new ExceptionServiceError("There is an error in the Service Provider");
        }
    }
}
