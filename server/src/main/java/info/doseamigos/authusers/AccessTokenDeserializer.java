package info.doseamigos.authusers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jackson Deserializer for AccessToken to turn into AuthUsers.  If we pass just the access token in, then we call the
 * google endpoints to
 */
public class AccessTokenDeserializer extends JsonDeserializer<AuthUser> {

    private final AuthUserService authUserService;

    /**
     * Not using Guice to construct this to make life easier since it relies on Annotation stuff from Jackson.
     */
    public AccessTokenDeserializer() {
        authUserService = new DefaultAuthUserService(new MySQLAuthUserDao());
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
            String accessToken = node.textValue();
            String accessTokenRegex = "^Bearer (.*)$";
            Pattern pattern = Pattern.compile(accessTokenRegex);
            Matcher matcher = pattern.matcher(accessToken);
            if (matcher.matches()) {
                accessToken = matcher.group(1);
            }
            return authUserService.getByToken(accessToken);
        }
        return new ObjectMapper().treeToValue(node, AuthUser.class);
    }
}
