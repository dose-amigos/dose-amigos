package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static java.util.Objects.requireNonNull;

/**
 * POJO for a medicine.  This is what we store for each user who is taking medicine.
 * Note that rxcui comes from an external rest service when we do lookups.  If one cannot
 * be found, rxcui can be null.
 */
public class Med {
    private long medId;
    private AmigoUser user;
    private Long rxcui;
    private String name;

    public Med() {
    }

    public Med(
        long medId,
        AmigoUser amigoUser,
        long rxcui,
        String name
    ) {
        this.setMedId(medId);
        this.setUser(amigoUser);
        this.setRxcui(rxcui);
        this.setName(name);
    }

    public long getMedId() {
        return medId;
    }

    public void setMedId(long medId) {
        this.medId = requireNonNull(medId);
    }

    public AmigoUser getUser() {
        return user;
    }

    public void setUser(AmigoUser user) {
        this.user = requireNonNull(user);
    }

    public Long getRxcui() {
        return rxcui;
    }

    public void setRxcui(Long rxcui) {
        this.rxcui = rxcui;
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
