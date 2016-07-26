package info.doseamigos.doseseries;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.amigousers.AmigoUserService;
import info.doseamigos.authusers.AuthUser;
import info.doseamigos.doseevents.DoseEventService;
import info.doseamigos.meds.Med;
import info.doseamigos.meds.MedService;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation for the {@link DoseSeriesService}.
 */
public class DefaultDoseSeriesService implements DoseSeriesService {

    private final DoseSeriesDao doseSeriesDao;
    private final AmigoUserService amigoUserService;
    private final MedService medService;
    private final DoseEventService doseEventService;

    @Inject
    public DefaultDoseSeriesService(
        @Nonnull DoseSeriesDao doseSeriesDao,
        @Nonnull MedService medService,
        @Nonnull AmigoUserService amigoUserService,
        @Nonnull DoseEventService doseEventService
    ) {
        this.doseSeriesDao = requireNonNull(doseSeriesDao);
        this.medService = requireNonNull(medService);
        this.amigoUserService = requireNonNull(amigoUserService);
        this.doseEventService = requireNonNull(doseEventService);
    }

    @Override
    public DoseSeries addSeries(AuthUser user, DoseSeries series) {
        AmigoUser amigoToCheck = series.getMed().getUser();
        //If the new med has no amigo associated with it, it's the auth user's amigo.
        if (amigoToCheck == null) {
            amigoToCheck = user.getAmigoUser();
        }
        //verify the user can add a med for this amigo user
        amigoUserService.validateAmigoChange(user, amigoToCheck);

        try {
            Med savedMed = medService.saveMed(user, series.getMed());
            series.setMed(savedMed);
            Long id = doseSeriesDao.save(series);
            DoseSeries newSeries = getById(id);

            //Add a weekly amount of events by default.
            doseEventService.addWeeklySeriesForDoseSeries(newSeries);

            return newSeries;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DoseSeries getById(Long id) {
        return doseSeriesDao.getById(id);
    }

    @Override
    public DoseSeries getForMed(Med med) {
        return doseSeriesDao.getForMed(med);
    }

    @Override
    public List<DoseSeries> getSeriesForUser(AuthUser authUser, AmigoUser user) {
        //verify the user can add a med for this amigo user
        amigoUserService.validateAmigoChange(authUser, user);
        return doseSeriesDao.getSeriesForUser(user);
    }
}
