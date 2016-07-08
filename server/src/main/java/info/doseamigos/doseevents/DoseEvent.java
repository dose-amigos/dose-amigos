package info.doseamigos.doseevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.meds.Med;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * POJO for holding dose-events that took place by various amigos for a given auth user.
 */
public class DoseEvent {

    private Long doseEventId;
    private Med med;
    private Date scheduledDateTime;
    private Date actionDateTime;
    private EventType action;

    public Long getDoseEventId() {
        return doseEventId;
    }

    public void setDoseEventId(Long doseEventId) {
        this.doseEventId = doseEventId;
    }

    public Med getMed() {
        return med;
    }

    public void setMed(Med med) {
        this.med = med;
    }

    public Date getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(Date scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public Date getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(Date actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public EventType getAction() {
        return action;
    }

    public void setAction(EventType action) {
        this.action = action;
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
