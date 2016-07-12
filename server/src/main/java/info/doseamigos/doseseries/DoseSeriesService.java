package info.doseamigos.doseseries;

import info.doseamigos.authusers.AuthUser;

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


}
