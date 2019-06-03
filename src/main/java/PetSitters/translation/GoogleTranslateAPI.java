package PetSitters.translation;

import PetSitters.cityConversor.ICityConversor;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.serviceDTO.DTO;
import PetSitters.serviceDTO.DTOTranslationIncoming;
import PetSitters.serviceDTO.DTOTranslationOutgoing;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class GoogleTranslateAPI implements ICityConversor {

    private final String serviceURL = "https://translation.googleapis.com/language/translate/v2?key=AIzaSyCXd3M-Cb0KvyBMKTNS23nfaoiez6l51Go";

    public DTO execute(DTO parameter) throws ExceptionServiceError, IOException, JSONException {
        DTOTranslationIncoming dtoTranslationIncoming = (DTOTranslationIncoming) parameter;

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(serviceURL);

        post.addHeader("User-Agent", "Agent");
        post.addHeader("Origin", "https://developers.google.com");
        post.addHeader("Content-Type", "application/json");

        JSONObject jsonObject = new JSONObject();
        for (String input : dtoTranslationIncoming.getText()) {
            jsonObject.accumulate("q", input);
        }
        jsonObject.accumulate("target", dtoTranslationIncoming.getTargetLanguage());

        StringEntity requestEntity = new StringEntity(
                jsonObject.toString(),
                ContentType.APPLICATION_JSON);

        post.setEntity(requestEntity);

        HttpResponse response = client.execute(post);
        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode == 200) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            JSONObject element;
            DTOTranslationOutgoing dtoTranslationOutgoing = new DTOTranslationOutgoing();
            try {
                element = new JSONObject(result.toString());
                element = element.getJSONObject("data");
                JSONArray array = element.getJSONArray("translations");

                LinkedList<String> linkedList = new LinkedList<>();
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject jsonObject1 = (JSONObject) array.get(i);
                    linkedList.addLast(jsonObject1.getString("translatedText"));
                }
                dtoTranslationOutgoing.setText(linkedList);
            } catch (JSONException e) {
                throw new ExceptionServiceError("An error in the translation has occurred");
            }
            return dtoTranslationOutgoing;
        }
        throw new ExceptionServiceError("There is an error in the Service Provider, responseCode = " + responseCode);
    }
}
