package info.doseamigos.doseevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.meds.Med;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * POJO for holding events that took place.  For example, if "Bobby" took "Allegra" today at 3PM, this would
 * hold that AmigoUser, a date representing today at 3PM, the list of medicines that user took (just Allegra in this
 * case), and an event type of "Took Medicine".
 */
public class DoseEvent {

    private final long eventId;
    private final AmigoUser amigoUser;
    private final List<Med> meds;
    private final Date timeTaken;
    private final EventType eventType;

    /**
     * Default Constructor for creating a DoseEvent.
     * @param amigoUser The user who caused the event.
     * @param meds The list of meds involved with the event.
     * @param timeTaken The time at which the event took place.
     * @param eventType The type of event we're sending.
     * @param eventId
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoseEvent doseEvent = (DoseEvent) o;
        return Objects.equals(amigoUser, doseEvent.amigoUser) &&
            Objects.equals(meds, doseEvent.meds) &&
            Objects.equals(timeTaken, doseEvent.timeTaken) &&
            eventType == doseEvent.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amigoUser, meds, timeTaken, eventType);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DoseEvent{");
        sb.append("amigoUser=").append(amigoUser);
        sb.append(", meds=").append(meds);
        sb.append(", timeTaken=").append(timeTaken);
        sb.append(", eventType=").append(eventType);
        sb.append('}');
        return sb.toString();
    }
}
