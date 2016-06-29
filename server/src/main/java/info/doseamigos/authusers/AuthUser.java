package info.doseamigos.authusers;

import info.doseamigos.amigousers.AmigoUser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static java.util.Objects.requireNonNull;

/**
 * POJO for users that authenticate with Google.
 */
public class AuthUser {

    private Long authUserId;
    private AmigoUser amigoUser;
    private String email;
    private String googleRef;

    public Long getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Long authUserId) {
        this.authUserId = requireNonNull(authUserId);
    }

    public AmigoUser getAmigoUser() {
        return amigoUser;
    }

    public void setAmigoUser(AmigoUser amigoUser) {
        this.amigoUser = requireNonNull(amigoUser);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = requireNonNull(email);
    }

    public String getGoogleRef() {
        return googleRef;
    }

    public void setGoogleRef(String googleRef) {
        this.googleRef = googleRef;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
