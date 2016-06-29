package info.doseamigos.authusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.doseamigos.amigousers.AmigoUser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of AuthUserService using Google OAuth URLs.
 */
public class DefaultAuthUserService implements AuthUserService {

    private final AuthUserDao authUserDao;

    @Inject
    public DefaultAuthUserService(@Nonnull  AuthUserDao authUserDao) {
        this.authUserDao = requireNonNull(authUserDao);
    }


    @Override
    public AuthUser getByToken(String accessToken) {
        Map<String, Object> infoFromGoogle;
        try {
            infoFromGoogle = getUserInfo(accessToken);
            if (infoFromGoogle.containsKey("error")) {
                throw new IllegalArgumentException("There were errors retrieving user info from Google.");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AuthUser toRet = authUserDao.getByGoogleRef((String) infoFromGoogle.get("id"));
        if (toRet == null) {
            AmigoUser amigoUser= new AmigoUser();
            amigoUser.setName((String) infoFromGoogle.get("name"));
            AuthUser authUser = new AuthUser();
            authUser.setAmigoUser(amigoUser);
            authUser.setEmail((String) infoFromGoogle.get("email"));
            authUser.setGoogleRef((String) infoFromGoogle.get("id"));
            try {
                Long newId = authUserDao.save(authUser);
                toRet = authUserDao.getById(newId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return toRet;
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
        return null; //TODO do this.
    }
}
