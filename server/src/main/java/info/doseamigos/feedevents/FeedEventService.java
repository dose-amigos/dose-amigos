package info.doseamigos.feedevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AuthUser;

import java.util.List;
import java.util.Map;

/**
 * Service that gets Feed Events for the auth user.
 */
public interface FeedEventService {

    /**
     * Gets a list of Feed Events for the auth user logged in to see his/her amigos' latest actions.
     * @param user The user to get feed events for
     * @return The feed events
     */
    List<FeedEvent> getFeedEvents(AuthUser user);

    /**
     * Returns a mapping of User to their latest feed event.
     * @param user The user to get feed events for
     * @return A mapping of amigos to feed events.
     */
    Map<AmigoUser, FeedEvent> getLatestestEventsForEcho(AuthUser user);
}
