package info.doseamigos.amigousers;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static java.util.Objects.requireNonNull;

/**
 * POJO that represents an Amigo User.  Amigo Users are bare minimum, just a name and id.
 */
public class AmigoUser {
    private Long id;
    private String name;

    private Date lastTimeDoseTaken;
    private Date nextTimeDoseScheduled;

    private String picture;

    public AmigoUser() {
    }

    public AmigoUser(
        Long id,
        String name
    ) {
        this.setId(id);
        this.setName(name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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
