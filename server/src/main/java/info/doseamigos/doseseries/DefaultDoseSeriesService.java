package info.doseamigos.doseseries;

import info.doseamigos.authusers.AuthUser;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation for the {@link DoseSeriesService}.
 */
public class DefaultDoseSeriesService implements DoseSeriesService {

    private final DoseSeriesDao doseSeriesDao;

    @Inject
    public DefaultDoseSeriesService(
        @Nonnull DoseSeriesDao doseSeriesDao
    ) {
        this.doseSeriesDao = requireNonNull(doseSeriesDao);
    }

    @Override
    public DoseSeries addSeries(AuthUser user, DoseSeries series) {
        //TODO verify that the med associated with the series is defined and
        //TODO allowed to be modified by the current Auth User.
        try {
            Long id = doseSeriesDao.save(series);
            return getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DoseSeries getById(Long id) {
        return doseSeriesDao.getById(id);
    }
}
