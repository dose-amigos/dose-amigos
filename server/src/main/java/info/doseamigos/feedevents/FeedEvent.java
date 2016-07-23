package info.doseamigos.feedevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.doseevents.EventType;
import info.doseamigos.meds.Med;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created by jking31cs on 7/23/16.
 */
public class FeedEvent {
    private Long id;
    private EventType action;
    private Date actionDateTime;
    private List<Med> meds;
    private AmigoUser user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getAction() {
        return action;
    }

    public void setAction(EventType action) {
        this.action = action;
    }

    public Date getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(Date actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public List<Med> getMeds() {
        return meds;
    }

    public void setMeds(List<Med> meds) {
        this.meds = meds;
    }

    public AmigoUser getUser() {
        return user;
    }

    public void setUser(AmigoUser user) {
        this.user = user;
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
