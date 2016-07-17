package info.doseamigos.sharerequests;

import java.sql.SQLException;
import java.util.List;

import info.doseamigos.authusers.AuthUser;

/**
 * DAO interface with different methods for our share request.
 */
public interface ShareRequestDao {

    /**
     * Adds a new ShareRequest to the DB.
     * @param request The information needed to save.  If amigo is empty, it gets populated in this method.
     * @return The newly saved ShareRequest
     */
    void addNewShareRequest(ShareRequest request) throws SQLException;

    /**
     * Updates a sharerequest in the DB and fetches it.
     * @param request The information to be updated.  All information must be populated
     * @return the newly updated ShareRequest.
     */
    ShareRequest updateShareRequest(ShareRequest request) throws SQLException;

    /**
     * Gets all pending share requests for a given user.
     * @param user The user to get share requests for.
     * @return The list of share requests for that user.
     */
    List<ShareRequest> getPendingShareRequests(AuthUser user) throws SQLException;
}
