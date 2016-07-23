package info.doseamigos.authusers;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object for grabbing AuthUsers' by their properties and saving the updated AuthUser.
 */
public interface AuthUserDao {

    /**
     * Gets an AuthUser by it's googleRef or null if none exists.
     * @param googleRef The reference from Google servers after validating the token.
     * @return The AuthUser or null if none with googleRef exists.
     */
    AuthUser getByGoogleRef(String googleRef);

    /**
     * Gets an AuthUser by it's email or null if none exists.
     * @param email The email address for the user to look up.
     * @return The AuthUser or null if none with email exists.
     */
    AuthUser getByEmail(String email);

    /**
     * Gets an AuthUser by it's authUserId or null if none exists.
     * @param authUserId The authUserId we have stored internally.
     * @return The AuthUser or null if none with authUserId exists.
     */
    AuthUser getById(long authUserId);

    /**
     * Saves the user to the DB in it's current form.
     * @param user The user to save.
     * @return The saved user.
     */
    Long save(AuthUser user) throws SQLException;

    /**
     * Gets an auth user by token.
     * @param idToken The id token to look up the cached user for.
     * @return The cached user or null if the value is expired.
     */
    AuthUser getByIdToken(String idToken) throws SQLException;

    /**
     * Stores caching info to the DB.
     * @param user The user to store
     * @param token The token for that user
     * @param durationInSeconds The length of time in seconds it lasts.
     */
    void storeInfo(AuthUser user, String token, Integer durationInSeconds) throws SQLException;

    /**
     * Lookup AuthUsers by name.
     * @param name The name to look up
     * @return The auth users with that name.
     */
    List<AuthUser> lookupByName(String name) throws SQLException;
}
