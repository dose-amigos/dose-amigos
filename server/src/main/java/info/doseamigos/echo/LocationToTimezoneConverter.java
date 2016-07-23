package info.doseamigos.echo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calls into external service to get timezone information
 */
public class LocationToTimezoneConverter {

    public Map<String, Object> getTimezone(String location) throws IOException {
        String locationUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        location = location.replace(" ", "+");
        locationUrl = locationUrl + location;

        HttpGet httpGet = new HttpGet(locationUrl);
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(httpGet);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> results = objectMapper.readValue(response.getEntity().getContent(), objectMapper.getTypeFactory().constructMapType(
            HashMap.class, String.class, Object.class
        ));



        Map<String, Double> latLong =
            (Map<String, Double>) ((Map<String, Object>)((Map<String, Object>) ((List<Object>) results.get("results")).get(0)).get("geometry")).get("location");

        String timezoneUrl = "https://maps.googleapis.com/maps/api/timezone/json?location=";
        timezoneUrl = timezoneUrl + Joiner.on(",").join(latLong.values());
        timezoneUrl = timezoneUrl + "&timestamp=" + Instant.now().getEpochSecond();

        HttpResponse response2 = client.execute(new HttpGet(timezoneUrl));

        Map<String, Object> timezoneResults = objectMapper.readValue(response2.getEntity().getContent(), objectMapper.getTypeFactory().constructMapType(
            HashMap.class, String.class, Object.class
        ));

        return timezoneResults;
    }
}
