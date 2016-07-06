package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

import static java.util.Objects.requireNonNull;

/**
 * POJO for a medicine.  This is what we store for each user who is taking medicine.
 * Note that rxcui comes from an external rest service when we do lookups.  If one cannot
 * be found, rxcui can be null.
 */
public class Med {
    private Long medId;
    private AmigoUser user;
    private Long rxcui;
    private String name;
    private int doseAmount;
    private String doseUnit;
    private int totalAmount;
    private String doseInstructions;
    private Date firstTaken;
    private Date lastTaken;
    private Date nextScheduled;
    private boolean active = true; //Default to active.

    public Med() {
    }

    public Med(
        Long medId,
        AmigoUser amigoUser,
        long rxcui,
        String name
    ) {
        this.setMedId(medId);
        this.setUser(amigoUser);
        this.setRxcui(rxcui);
        this.setName(name);
    }

    public Long getMedId() {
        return medId;
    }

    public void setMedId(Long medId) {
        this.medId = medId;
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

    public int getDoseAmount() {
        return doseAmount;
    }

    public void setDoseAmount(int doseAmount) {
        this.doseAmount = doseAmount;
    }

    public String getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit(String doseUnit) {
        this.doseUnit = doseUnit;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDoseInstructions() {
        return doseInstructions;
    }

    public void setDoseInstructions(String doseInstructions) {
        this.doseInstructions = doseInstructions;
    }

    public Date getFirstTaken() {
        return firstTaken;
    }

    public void setFirstTaken(Date firstTaken) {
        this.firstTaken = firstTaken;
    }

    public Date getLastTaken() {
        return lastTaken;
    }

    public void setLastTaken(Date lastTaken) {
        this.lastTaken = lastTaken;
    }

    public Date getNextScheduled() {
        return nextScheduled;
    }

    public void setNextScheduled(Date nextScheduled) {
        this.nextScheduled = nextScheduled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
