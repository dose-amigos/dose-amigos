package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;

import java.sql.SQLException;
import java.util.List;

/**
 * Methods that represent queries to run on the DB side.
 */
public interface MedDao {

    /**
     * Saves a med to the database either by updating the existing row with new values or adding a new row with the
     * given values.
     * @param med A med to save.
     * @return The already existing id or new id generated.
     */
    Long save(Med med) throws SQLException;

    /**
     * Gets a particular med by its id.
     * @param id The id of the med to get
     * @return The med information for that id.
     */
    Med getById(Long id);

    /**
     * Gets all meds for a particular amigo.
     * @param amigoId The amigo id to look on.
     * @return A list of Meds that amigo is taking.
     */
    List<Med> getMedsForAmigo(Long amigoId);

}
