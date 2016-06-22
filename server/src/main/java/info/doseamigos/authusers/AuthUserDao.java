package info.doseamigos.authusers;

import java.math.BigInteger;
import java.sql.SQLException;

/**
 * Data Access Object for grabbing AuthUsers' by their properties and saving the updated AuthUser.
 */
public interface AuthUserDao {

    /**
     * Gets an AuthUser by it's googleRef or null if none exists.
     * @param googleRef The reference from Google servers after validating the token.
     * @return The AuthUser or null if none with googleRef exists.
     */
    AuthUser getByGoogleRef(BigInteger googleRef);

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
}
