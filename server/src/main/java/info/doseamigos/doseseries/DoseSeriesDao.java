package info.doseamigos.doseseries;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.meds.Med;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO for handling saving and grabbing DoseSeries for Meds.
 */
public interface DoseSeriesDao {

    /**
     * Saves a doseseries to the DB.
     * @param series The series to save.  If id is null, it creates a new series.
     * @return The id of the series saved.
     */
    Long save(DoseSeries series) throws SQLException;

    /**
     * Deletes the dose series from the DB.
     * @param series
     * @throws SQLException
     */
    void delete(DoseSeries series) throws SQLException;

    /**
     * Gets a series by its id.
     * @param id The id to grab the series for.
     * @return The series with the given id.
     */
    DoseSeries getById(Long id);

    /**
     * Gets a series by its med.
     * @param med The med to look up
     * @return The series with the given id.
     */
    DoseSeries getForMed(Med med);

    /**
     * Gets a list of dose series for a specific user.
     * @param amigoUser The user to get dose series for.
     * @return The Dose Series listing
     */
    List<DoseSeries> getSeriesForUser(AmigoUser amigoUser);
}
