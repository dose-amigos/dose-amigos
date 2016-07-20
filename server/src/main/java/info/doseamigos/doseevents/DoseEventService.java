package info.doseamigos.doseevents;

import java.util.Date;
import java.util.List;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.doseseries.DoseSeries;
import info.doseamigos.meds.Med;

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
     * Generates a bunch of new events for a Dose Series.  Meant to be called when adding a new Dose Series for the
     * first time.
     * @param series Series to generate events for
     * @return The newly generated events.
     */
    List<DoseEvent> generateWeekEventsForSeries(DoseSeries series);

    /**
     * Cron job method that should mark all events that took place over an hour ago as Missed
     */
    void markMissedEvents();

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

    /**
     * Get DoseEvents to display for the phone.
     * @param authUser The auth user making the call
     * @param startDate The start date
     * @param dir Whether we want the first dose after date, or last dose before date.
     * @return The list of doses at the same time.
     */
    List<DoseEvent> getDosesForPhone(AuthUser authUser, Date startDate, String dir);


}
