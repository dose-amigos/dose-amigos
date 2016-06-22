package info.doseamigos.doseevents;

import info.doseamigos.authusers.AuthUser;

import java.util.List;

/**
 * Service that gets events for a specific user.
 */
public interface DoseEventService {

    /**
     * Gets a list of DoseEvents for a given authUser. All events are only for
     * AmigoUsers that this auth user has access to.
     * @param authUser The Auth User to look up Dose Events for
     * @return a List of Dose Events to display ordered by Date from latest to earliest.
     */
    List<DoseEvent> getEventsForUser(AuthUser authUser);
}
