package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AuthUser;

import java.util.List;

/**
 * Service for handling all business logic associated with Meds.
 */
public interface MedService {

    /**
     * Adds a new medication to the user's list by looking it up by name in the external api, then populating the
     * AmigoUser and rxcui afterward.
     * @param name The Name of the med to look up.
     * @return A new Med object for the user with the name.
     */
    Med addByName(AuthUser authUser, String name);

    /**
     * Gets a list of all meds for a given user, ordered by nextScheduled Time.
     * @param user The amigo user to get meds for.
     * @return The List of Medications to take.
     */
    List<Med> medsForUser(AuthUser authUser, AmigoUser user);

    /**
     * Creates or updates a med based on the information passed in.  It creates if medId is null, updates
     * if it's defined.
     * @param medInfo The med info object we're saving.
     * @return The new MedInfo object.
     */
    Med saveMed(AuthUser authUser, Med medInfo);

    /**
     * Copy of medsForUser to be used by the system, no validation required.
     * @param user The user to get meds for
     * @return the Meds for the user.
     */
    List<Med> medsForUserSystemCommand(AmigoUser user);
}
