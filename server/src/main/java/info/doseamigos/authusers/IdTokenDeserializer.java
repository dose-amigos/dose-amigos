package info.doseamigos.authusers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Guice;
import com.google.inject.Injector;
import info.doseamigos.amigousers.AmigoUserGuiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jackson Deserializer for AccessToken to turn into AuthUsers.  If we pass just the access token in, then we call the
 * google endpoints to
 */
public class IdTokenDeserializer extends JsonDeserializer<AuthUser> {

    private final AuthUserService authUserService;
    private static final Logger logger = LoggerFactory.getLogger(IdTokenDeserializer.class);

    /**
     * Not using Guice to construct this to make life easier since it relies on Annotation stuff from Jackson.
     */
    public IdTokenDeserializer() {
        Injector injector = Guice.createInjector(
            new AuthUserGuiceModule(),
            new AmigoUserGuiceModule()
        );
        authUserService = injector.getInstance(AuthUserService.class);
    }

    /**
     * If the object passed in is just a string, we use the service to figure it out.  Otherwise, just deserialize
     * normally using a default ObjectMapper.  String is going to be more common as it'll be on every request.  The
     * Object deserialization will be for when a user wants to modify their email or something similar.
     * @param p JsonParser passed into the super method.
     * @param ctxt deserialization context passed into the super method.
     * @return An AuthUser with the given access token or a deserialized JSON representation of an auth user.
     * @throws IOException if we cannot read the node properly.
     */
    @Override
    public AuthUser deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node  = p.getCodec().readTree(p);
        if (node.getNodeType() == JsonNodeType.STRING) {
            String idToken = node.textValue();
            String accessTokenRegex = "^Bearer (.*)$";
            Pattern pattern = Pattern.compile(accessTokenRegex);
            Matcher matcher = pattern.matcher(idToken);
            if (matcher.matches()) {
                idToken = matcher.group(1);
            }
            String accessToken = authUserService.getAccessToken(idToken);
            if (accessToken == null) {
                logger.error("Somehow got a blank access token from Auth0: " + idToken);
            }
            return authUserService.getByToken(accessToken);
        }
        return new ObjectMapper().treeToValue(node, AuthUser.class);
    }
}
