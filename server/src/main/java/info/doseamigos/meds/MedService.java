package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;

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
    Med addByName(String name);

    /**
     * Gets a list of all meds for a given user, ordered by nextScheduled Time.
     * @param user The amigo user to get meds for.
     * @return The List of Medications to take.
     */
    List<Med> medsForUser(AmigoUser user);

    /**
     * Creates or updates a med based on the information passed in.  It creates if medId is null, updates
     * if it's defined.
     * @param medInfo The med info object we're saving.
     * @return The new MedInfo object.
     */
    Med updateMed(Med medInfo);
}
