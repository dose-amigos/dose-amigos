package info.doseamigos.amigousers;

import info.doseamigos.authusers.AuthUser;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Default implementation of {@link AmigoUserService}.
 */
public class DefaultAmigoUserService implements AmigoUserService {

    private final AmigoUserDao amigoUserDao;

    @Inject
    public DefaultAmigoUserService(
        @Nonnull AmigoUserDao amigoUserDao
    ) {
        this.amigoUserDao = requireNonNull(amigoUserDao);
    }

    @Override
    public List<AmigoUser> getAllAmigosInSystem() {
        return amigoUserDao.getAllAmigosInSystem();
    }

    @Override
    public List<AmigoUser> getAmigosForAuthUser(AuthUser user) {
        return  amigoUserDao.getAmigosForAuthUser(user);
    }

    @Override
    public AmigoUser createNewAmigo(AuthUser user, AmigoUser newUser) {
        Long newId = null;
        try {
            newId = amigoUserDao.createNewAmigo(user, newUser);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return amigoUserDao.getById(newId);
    }

    @Override
    public AmigoUser updateAmigo(AuthUser user, AmigoUser updatedUser) {
        validateAmigoChange(user, updatedUser);
        try {
            amigoUserDao.updateAmigo(updatedUser);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return amigoUserDao.getById(updatedUser.getId());
    }

    @Override
    public AmigoUser deleteAmigo(AuthUser user, Long deletedUserId) {
        AmigoUser deletedUser = amigoUserDao.getById(deletedUserId);
        validateAmigoChange(user, deletedUser);
        try {
            amigoUserDao.deleteAmigo(user, deletedUserId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deletedUser;
    }

    @Override
    public void validateAmigoChange(AuthUser user, AmigoUser updatedUser) {
        List<AmigoUser> amigoUsers = getAmigosForAuthUser(user);
        AmigoUser originalUser = amigoUserDao.getById(updatedUser.getId());

        if (!amigoUsers.contains(originalUser) && !originalUser.getId().equals(updatedUser.getId())) {
            throw new RuntimeException("Amigo was not found.");
        }
    }
}
