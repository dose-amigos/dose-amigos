package info.doseamigos.authusers;

import java.io.IOException;

/**
 * Service for handling any logic related to AuthUsers.
 */
public interface AuthUserService {

    /**
     * Verifies that a accessToken is valid and grabs the AuthUser associated with
     * that user from the DB.
     * @param accessToken The access token passed in from the client.
     * @return An AuthUser associated with google user with the accessToken.
     * @throws IllegalArgumentException if access token causes an error from google.
     */
    AuthUser getByToken(String accessToken);

    /**
     * If an auth user wants to update their email.
     * @param updatedAuthUser The user in the form to save into the DB.
     * @return The saved Auth User.
     * @throws IllegalArgumentException if any fields on the user are in an invalid state.
     */
    AuthUser modifyAuthUser(AuthUser updatedAuthUser);

    /**
     * Gets a user from the cache by id token.  If it's not in the cache or is expired,
     * it'll call the service for verification and store it in the cache.
     * @param idToken The id token to look at.
     * @return The Auth user associated with it.
     */
    AuthUser getByIdToken(String idToken) throws IOException;
}
