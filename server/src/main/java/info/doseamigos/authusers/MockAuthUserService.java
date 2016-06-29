package info.doseamigos.authusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.doseamigos.amigousers.AmigoUser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock an auth user based on google stuff.
 */
public class MockAuthUserService implements AuthUserService {

    private static final Logger log = LoggerFactory.getLogger(MockAuthUserService.class);

    @Override
    public AuthUser getByToken(String accessToken) {
        log.info("Calling external Google URL");
        Map<String, Object> googleUser;
        try {
            googleUser = getUserInfo(accessToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Retrieved info from google.");
        AmigoUser amigoUser = new AmigoUser(1, (String) googleUser.get("name"));
        AuthUser authUser = new AuthUser();
        authUser.setAuthUserId(1L);
        authUser.setAmigoUser(amigoUser);
        authUser.setEmail((String) googleUser.get("email"));
        authUser.setGoogleRef((String) googleUser.get("id"));
        return authUser;
    }

    Map<String, Object> getUserInfo(String accessToken) throws IOException {

        HttpGet httpGet = new HttpGet("https://www.googleapis.com/userinfo/v2/me?access_token=" + accessToken);
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(httpGet);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getEntity().getContent(), objectMapper.getTypeFactory()
                .constructMapType(HashMap.class, String.class, Object.class));

    }

    @Override
    public AuthUser modifyAuthUser(AuthUser updatedAuthUser) {
        return null;
    }
}
