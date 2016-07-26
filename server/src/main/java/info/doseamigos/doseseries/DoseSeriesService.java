package info.doseamigos.doseseries;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.meds.Med;

import java.util.List;

/**
 * Business Service for handling DoseSeries logic..
 */
public interface DoseSeriesService {

    /**
     * Adds a new Series based on the series passed in.
     * @param authUser The AuthUser doing the action, used for validation purposes.
     * @param series The series information to save.
     * @return The newly saved DoseSeries information.
     */
    DoseSeries addSeries(AuthUser authUser, DoseSeries series);

    /**
     * Gets a DoseSeries by its Id.
     * @param id The id to look up.
     * @return The series associated with that id.
     */
    DoseSeries getById(Long id);

    /**
     * Gets a DoseSeries for a given med.
     * @param med The med to look up for.
     * @return The DoseSeries
     */
    DoseSeries getForMed(Med med);

    /**
     * Gets all dose series for a given user.
     * @param authUser The auth user to validate against.
     * @param user The user to get series for
     * @return The dose series for that user.
     */
    List<DoseSeries> getSeriesForUser(AuthUser authUser, AmigoUser user);
}
