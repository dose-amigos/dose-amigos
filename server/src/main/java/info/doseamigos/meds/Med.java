package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * POJO for a medicine.  This is what we store for each user who is taking medicine.
 * Note that rxcui comes from an external rest service when we do lookups.  If one cannot
 * be found, rxcui can be null.
 */
public class Med {
    private long medId;
    private AmigoUser user;
    private long rxcui;
    private String name;

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

    public long getRxcui() {
        return rxcui;
    }

    public void setRxcui(long rxcui) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Med med = (Med) o;
        return medId == med.medId &&
            rxcui == med.rxcui &&
            Objects.equals(user, med.user) &&
            Objects.equals(name, med.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medId, user, rxcui, name);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Med{");
        sb.append("medId=").append(medId);
        sb.append(", user=").append(user);
        sb.append(", rxcui=").append(rxcui);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
