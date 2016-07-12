package info.doseamigos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import info.doseamigos.authusers.AccessTokenDeserializer;
import info.doseamigos.authusers.AuthUser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

/**
 * Due to how the API Gateway works with Amazon Lambdas, to pass in header and query param information from the client
 * to the server, we have to convert the request into a custom object that we can use instead.  This object will have
 * three fields: AuthUser -> created from the accessToken passed in from the HEADER, QueryParams -> a Mapping of String
 * to String representing the params passed in the url, and Body -> an object represented either from the url path param
 * for GET and DELETE calls, or an json object passed into the request body for POST and PUT commands.
 */
public class ClientRequestObject<T> {

    @JsonDeserialize(using = AccessTokenDeserializer.class)
    private final AuthUser sessionUser;

    private final Map<String, String> queryParams;

    private final T body;

    @JsonCreator
    public ClientRequestObject(
        @JsonProperty("accessToken") AuthUser sessionUser,
        @JsonProperty("queryParams") Map<String, String> queryParams,
        @JsonProperty("body") T body
    ) {
        this.sessionUser = sessionUser;
        this.queryParams = queryParams;
        this.body = body;
    }

    public AuthUser getSessionUser() {
        return sessionUser;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public T getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
