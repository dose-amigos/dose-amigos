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

    private final long eventId;

    private final AmigoUser amigoUser;
    private final List<Med> meds;
    private final Date timeTaken;
    private final EventType eventType;
    /**
     * Default Constructor for creating a DoseEvent.
     * @param eventId The id for the event in the DB.
     * @param amigoUser The user who caused the event.
     * @param meds The list of meds involved with the event.
     * @param timeTaken The time at which the event took place.
     * @param eventType The type of event we're sending.
     */
    public DoseEvent(
        long eventId,
        AmigoUser amigoUser,
        List<Med> meds,
        Date timeTaken,
        EventType eventType
    ) {
        this.eventId = requireNonNull(eventId);
        this.amigoUser = requireNonNull(amigoUser);
        this.meds = requireNonNull(meds);
        this.timeTaken = requireNonNull(timeTaken);
        this.eventType = requireNonNull(eventType);
    }

    public long getEventId() {
        return eventId;
    }

    public AmigoUser getAmigoUser() {
        return amigoUser;
    }

    public List<Med> getMeds() {
        return meds;
    }

    public Date getTimeTaken() {
        return timeTaken;
    }

    public EventType getEventType() {
        return eventType;
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
