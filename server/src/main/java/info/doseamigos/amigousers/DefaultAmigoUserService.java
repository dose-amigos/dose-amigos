package info.doseamigos.amigousers;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by jking31cs on 7/7/16.
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
}
