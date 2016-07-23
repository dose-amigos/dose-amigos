package info.doseamigos.feedevents;

import info.doseamigos.authusers.AuthUser;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO for grabbing FeedEvents out of the DB.
 */
public interface FeedEventDao {

    /**
     * Gets the feed events for all amigos associated with user.
     * @param user The AuthUser to look at.
     * @return The list of FeedEvents.
     */
    List<FeedEvent> getEventsForUser(AuthUser user) throws SQLException;
}
