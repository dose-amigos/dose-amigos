package info.doseamigos.doseevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.meds.Med;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Dao for creating, getting, and updating DoseEvents.
 */
public interface DoseEventDao {

    /**
     * Creates a dose event in the DataBase
     * @param doseEvent The DoseEvent to save to the DB.
     * @return The newly
     */
    Long create(DoseEvent doseEvent) throws SQLException;

    /**
     * Gets a list of dose events scheduled for after the begin date.  This is used to prevent duplicate dose events.
     * @param med The Med to get dose events for.
     * @param beginDate The earliest date to look at, all dose events are scheduled after this.
     * @return The list of dose events.
     */
    List<DoseEvent> getDoseEventForMedAfter(Med med, Date beginDate);

    /**
     * Gets a list of dose events scheduled where the last one is today at midnight.
     * @param amigoUser The user to look it up for.
     * @return The list of dose events.
     */
    List<DoseEvent> getEventsForUserToday(AmigoUser amigoUser);

    /**
     * Updates the list of dose events passed in with the action/actionDateTime.
     * @param doseEvents The events to update.
     */
    void updateDoseEvents(List<DoseEvent> doseEvents) throws SQLException;
}
