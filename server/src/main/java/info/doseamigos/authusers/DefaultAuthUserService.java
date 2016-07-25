package info.doseamigos.authusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.amigousers.AmigoUserService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of AuthUserService using Google OAuth URLs.
 */
public class DefaultAuthUserService implements AuthUserService {

    private final AuthUserDao authUserDao;
    private final AmigoUserService amigoUserService;

    @Inject
    public DefaultAuthUserService(
        @Nonnull AuthUserDao authUserDao,
        @Nonnull AmigoUserService amigoUserService
    ) {
        this.authUserDao = requireNonNull(authUserDao);
        this.amigoUserService = requireNonNull(amigoUserService);
    }


    @Override
    public AuthUser getByToken(String accessToken) {
        Map<String, Object> infoFromGoogle;
        try {
            if (!validToken(accessToken)) {
                throw new RuntimeException("Access Token is not valid.");
            }
            infoFromGoogle = getUserInfo(accessToken);
            if (infoFromGoogle.containsKey("error")) {
                throw new IllegalArgumentException("There were errors retrieving user info from Google.");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getOrCreateAuthUser(infoFromGoogle, (String) infoFromGoogle.get("id"));
    }

    public AuthUser getOrCreateAuthUser(Map<String, Object> infoFromGoogle, String id) {
        AuthUser toRet = authUserDao.getByGoogleRef(id);
        if (toRet == null) {
            AmigoUser amigoUser= new AmigoUser();
            amigoUser.setName((String) infoFromGoogle.get("name"));
            amigoUser.setPicture((String) infoFromGoogle.get("picture"));
            AuthUser authUser = new AuthUser();
            authUser.setAmigoUser(amigoUser);
            authUser.setEmail((String) infoFromGoogle.get("email"));
            authUser.setGoogleRef(id);

            try {
                Long newId = authUserDao.save(authUser);
                toRet = authUserDao.getById(newId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {

            toRet.getAmigoUser().setName((String) infoFromGoogle.get("name"));
            toRet.getAmigoUser().setPicture((String) infoFromGoogle.get("picture"));
            amigoUserService.updateAmigo(toRet, toRet.getAmigoUser());

        }
        return toRet;
    }

    /**
     * Grabs User Information from the google apis.
     * @param accessToken The access token to get information for.
     * @return A map of String to Object with user information we would want.
     * @throws IOException IF there was a http error.
     */
    Map<String, Object> getUserInfo(String accessToken) throws IOException {

        HttpGet httpGet = new HttpGet("https://www.googleapis.com/userinfo/v2/me?access_token=" + accessToken);
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(httpGet);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getEntity().getContent(), objectMapper.getTypeFactory()
            .constructMapType(HashMap.class, String.class, Object.class));

    }

    /**
     * Calls google's validateToken REST endpoint to validate the token.
     * @param accessToken The token to validate.
     * @return true if the clientId matches our clientId and expires_in is above 0. false otherwise.
     * @throws IOException
     */
    boolean validToken(String accessToken) throws IOException {

        HttpGet httpGet = new HttpGet("https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken);
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(httpGet);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> validationResults =
            objectMapper.readValue(response.getEntity().getContent(), objectMapper.getTypeFactory()
                .constructMapType(HashMap.class, String.class, String.class));

        //This is the clientId for our app being validated.
        boolean correctAudience = validationResults.get("aud").equals(
            "732216027898-68qmvd4i3n8uqrg4kp1kcvc20qpjqn8p.apps.googleusercontent.com");

        boolean notExpired = Integer.parseInt(validationResults.get("expires_in")) > 0;

        return correctAudience && notExpired;
    }

    @Override
    public AuthUser modifyAuthUser(AuthUser updatedAuthUser) {
        return null; //TODO do this.
    }

    @Override
    public AuthUser getByIdToken(String idToken) throws IOException {

        try {
            AuthUser fromCache = authUserDao.getByIdToken(idToken);
            if (fromCache != null) {
                return fromCache;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HttpGet httpGet = new HttpGet("https://mckoon.auth0.com/tokeninfo?id_token=" + idToken);
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(httpGet);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> idTokenResults = objectMapper.readValue(response.getEntity().getContent(), objectMapper.getTypeFactory()
            .constructMapType(HashMap.class, String.class, Object.class));

        String id = null;
        Integer expiresTime = null;
        for (Map<String, Object> identity : (List<Map<String, Object>>) idTokenResults.get("identities")) {
            if (identity.get("provider").equals("google-oauth2")) {
                id = (String) identity.get("user_id");
                expiresTime = (Integer) identity.get("expires_in");
            }
        }

        AuthUser authUser = getOrCreateAuthUser(idTokenResults, id);
        try {
            authUserDao.storeInfo(authUser, idToken, expiresTime);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authUser;
    }

    @Override
    public List<AuthUser> lookupByName(String name) {
        try {
            return authUserDao.lookupByName(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
