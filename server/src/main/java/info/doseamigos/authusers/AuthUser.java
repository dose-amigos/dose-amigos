package info.doseamigos.authusers;

import info.doseamigos.amigousers.AmigoUser;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * POJO for users that authenticate with Google.
 */
public class AuthUser {

    private long authUserId;
    private AmigoUser amigoUser;
    private String email;

    public long getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(long authUserId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthUser authUser = (AuthUser) o;
        return authUserId == authUser.authUserId &&
            Objects.equals(amigoUser, authUser.amigoUser) &&
            Objects.equals(email, authUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authUserId, amigoUser, email);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AuthUser{");
        sb.append("authUserId=").append(authUserId);
        sb.append(", amigoUser=").append(amigoUser);
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
