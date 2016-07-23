package info.doseamigos.feedevents;

import info.doseamigos.authusers.AuthUser;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

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
}
