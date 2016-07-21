package info.doseamigos.amigousers;

import info.doseamigos.authusers.AuthUser;

import java.util.List;

/**
 * Amigo logic service interface.
 */
public interface AmigoUserService {

    /**
     * Gets all amigos in the system.  Never called by a user.
     * @return a list of all amigos in the system.
     */
    List<AmigoUser> getAllAmigosInSystem();

    /**
     * Gets all amigos that the user can see.
     * @param user The user to get amigos for.
     * @return A list of Amigo Users for the auth user.
     */
    List<AmigoUser> getAmigosForAuthUser(AuthUser user);

    /**
     * Creates a new amigo.
     * @param user The auth user creating the amigo
     * @param newUser The new amigo information
     * @return the newly created amigo
     */
    AmigoUser createNewAmigo(AuthUser user, AmigoUser newUser);

    /**
     * Updates the amigo with the info in the updated user.
     * @param user The user doing the update
     * @param updatedUser The updated information.
     * @return The updated user.
     */
    AmigoUser updateAmigo(AuthUser user, AmigoUser updatedUser);

    void validateAmigoChange(AuthUser user, AmigoUser updatedUser);

    /**
     * Deletes the amigo from the system.
     * @param user The user doing the delete.
     * @param deletedUser The amigo getting deleted.
     * @return The deleted user so the client can re-add quickly.
     */
    AmigoUser deleteAmigo(AuthUser user, Long deletedUser);


}
