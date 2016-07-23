package info.doseamigos.feedevents;

import info.doseamigos.authusers.AuthUser;

import java.util.List;

/**
 * Service that gets Feed Events for the auth user.
 */
public interface FeedEventService {

    List<FeedEvent> getFeedEvents(AuthUser user);
}
