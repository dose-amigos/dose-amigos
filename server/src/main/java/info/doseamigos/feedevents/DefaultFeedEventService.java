package info.doseamigos.feedevents;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AuthUser;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Default FeedEventService implementation.
 */
public class DefaultFeedEventService implements FeedEventService {

    private final FeedEventDao dao;

    @Inject
    public DefaultFeedEventService(
        @Nonnull FeedEventDao dao
    ) {
        this.dao = requireNonNull(dao);
    }

    @Override
    public List<FeedEvent> getFeedEvents(AuthUser user) {
        try {
            return dao.getEventsForUser(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<AmigoUser, FeedEvent> getLatestestEventsForEcho(AuthUser user) {
        List<FeedEvent> allEvents = getFeedEvents(user);
        Map<AmigoUser, FeedEvent> eventMap = new HashMap<>();
        for (FeedEvent event : allEvents) {
            //Since latest events are first, we can go through feed event list to populate it.
            if (!eventMap.containsKey(event.getUser())) {
                eventMap.put(event.getUser(), event);
            }
        }
        return eventMap;
    }
}
