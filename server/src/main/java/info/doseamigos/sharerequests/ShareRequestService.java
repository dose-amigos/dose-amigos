package info.doseamigos.sharerequests;

import java.util.List;

import info.doseamigos.authusers.AuthUser;

/**
 * Service that handles sharing amigos with other auth users.
 */
public interface ShareRequestService {

    /**
     * Adds a new ShareRequest to the system.  Also handles validation.
     * @param user The user making the request.
     * @param request The information needed to save.  If amigo is empty, it gets populated in this method.
     * @return The newly saved ShareRequest
     */
    ShareRequest addNewShareRequest(AuthUser user, ShareRequest request);

    /**
     * Updates a sharerequest in the system.
     * @param user The user doing the updating.
     * @param request The information to be updated.  All information must be populated
     * @return the newly updated ShareRequest.
     */
    ShareRequest updateShareRequest(AuthUser user, ShareRequest request);

    /**
     * Gets all pending share requests for a given user.
     * @param user The user to get share requests for.
     * @return The list of share requests for that user.
     */
    List<ShareRequest> getPendingShareRequests(AuthUser user);
}
