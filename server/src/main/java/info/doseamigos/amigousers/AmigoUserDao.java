package info.doseamigos.amigousers;

import info.doseamigos.authusers.AuthUser;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO for the AmigoUsers.
 */
public interface AmigoUserDao {

    List<AmigoUser> getAllAmigosInSystem();

    /**
     * Gets all amigos that the user can see.
     * @param user The user to get amigos for.
     * @return A list of Amigo Users for the auth user.
     */
    List<AmigoUser> getAmigosForAuthUser(AuthUser user);

    /**
     * Gets an amigo user from the DB by its id.
     * @param id The id to look up.
     * @return The amigo with the given id.
     */
    AmigoUser getById(Long id);

    /**
     * Creates a new amigo in the DB.
     * @param user The auth user creating the amigo, added to ShareRequests.
     * @param newUser The new amigo information
     * @return the newly created amigo's id.
     */
    Long createNewAmigo(AuthUser user, AmigoUser newUser) throws SQLException;

    /**
     * Updates the amigo with the info in the updated user.
     * @param updatedUser The updated information.
     */
    void updateAmigo(AmigoUser updatedUser) throws SQLException;

    /**
     * Deletes the amigo from the auth user by deleting the row in the SHAREREQUEST table.
     * @param user The auth user doing the delete.
     * @param deletedUser The id of the amigo getting deleted.
     */
    void deleteAmigo(AuthUser user, Long deletedUser) throws SQLException;
}
