package info.doseamigos.amigousers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

import static java.util.Objects.requireNonNull;

/**
 * POJO that represents an Amigo User.  Amigo Users are bare minimum, just a name and id.
 */
public class AmigoUser {
    private long amigoUserId;
    private String name;

    private Date lastTimeDoseTaken;
    private Date nextTimeDoseScheduled;

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

    public Date getLastTimeDoseTaken() {
        return lastTimeDoseTaken;
    }

    public void setLastTimeDoseTaken(Date lastTimeDoseTaken) {
        this.lastTimeDoseTaken = lastTimeDoseTaken;
    }

    public Date getNextTimeDoseScheduled() {
        return nextTimeDoseScheduled;
    }

    public void setNextTimeDoseScheduled(Date nextTimeDoseScheduled) {
        this.nextTimeDoseScheduled = nextTimeDoseScheduled;
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
