package info.doseamigos.amigousers;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * POJO that represents an Amigo User.  Amigo Users are bare minimum, just a name and id.
 */
public class AmigoUser {
    private long amigoUserId;
    private String name;

    public AmigoUser() {
    }

    public AmigoUser(
        long amigoUserId,
        String name
    ) {
        this.setAmigoUserId(amigoUserId);
        this.setName(name);
    }

    public long getAmigoUserId() {
        return amigoUserId;
    }

    public void setAmigoUserId(long amigoUserId) {
        this.amigoUserId = amigoUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireNonNull(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmigoUser amigoUser = (AmigoUser) o;
        return amigoUserId == amigoUser.amigoUserId &&
            Objects.equals(name, amigoUser.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amigoUserId, name);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AmigoUser{");
        sb.append("amigoUserId=").append(amigoUserId);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
