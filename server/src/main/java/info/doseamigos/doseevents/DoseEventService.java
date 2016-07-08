package info.doseamigos.doseevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.meds.Med;

import java.util.Date;
import java.util.List;

/**
 * Service that gets events for a specific user.
 */
public interface DoseEventService {

    /**
     * Gets a list of upcoming DoseEvents for a given authUser. All events are only for
     * AmigoUsers that this auth user has access to.
     * @param authUser The Auth User to look up Dose Events for
     * @return a List of Dose Events to display ordered by Date from latest to earliest.
     */
    List<DoseEvent> getUpcomingEventsForUser(AuthUser authUser, AmigoUser amigoUser, Date startDate);

    /**
     * Gets a list of dose events for a specific med starting at the begin date.  This is used for ensuring that we
     * don't duplicate dose events that are already scheduled.
     * @param med The med to look up
     * @param beginDate The date to start looking at.
     * @return
     */
    List<DoseEvent> getAllDoseEventsForMedAfter(Med med, Date beginDate);

    /**
     * Generates DoseEvents for all Amigo users in the system.  This will never be called directly
     * from a user, instead only being called from a scheduled Lambda.
     */
    void generateDoseEventsForAllUsers();

    /**
     * Gets a list of events for the user that end at the end of the day.  This will include all events before today
     * with no action associated with it.
     * @param authUser The AuthUser making the request, for validation purposes.
     * @param amigoUser The AmigoUser to get events for.
     * @return A list of DoseEvents for the specific user.
     */
    List<DoseEvent> getEventsForUserToday(AuthUser authUser, AmigoUser amigoUser);

    /**
     * Updates a list of DoseEvents with the updated Action and actionDateTime.
     * @param authUser The AuthUser making the request, for validation purposes.
     * @param doseEvents The list of dose events to save.
     * @return The updated list of DoseEvents.
     */
    List<DoseEvent> updateDoseEvents(AuthUser authUser, List<DoseEvent> doseEvents);
}
