package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.authusers.AuthUser;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Handles business logic for med logic.  This doesn't include DoseSeries or DoseEvents, just meds themselves.
 */
public class DefaultMedService implements MedService {
    private final MedDao medDao;

    @Inject
    public DefaultMedService(
        @Nonnull MedDao medDao
    ) {
        this.medDao = requireNonNull(medDao);
    }

    @Override
    public Med addByName(AuthUser authUser, String name) {
        //TODO figure out Alexa workflow.
        return null;
    }

    @Override
    public List<Med> medsForUser(AuthUser authUser, AmigoUser user) {
        //TODO add validation ensuring authUser can modify amigo's stuff.
        return medDao.getMedsForAmigo((user.getAmigoUserId()));
    }

    @Override
    public Med saveMed(AuthUser authUser, Med medInfo) {
        //TODO add validation ensuring user can add this med for the amigo included.
        try {
            Long newMedId = medDao.save(medInfo);
            return medDao.getById(newMedId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
