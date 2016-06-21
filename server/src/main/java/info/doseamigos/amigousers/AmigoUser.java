package info.doseamigos.amigousers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
