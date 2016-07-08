package info.doseamigos.amigousers;

import java.util.List;

/**
 * Created by jking31cs on 7/7/16.
 */
public interface AmigoUserService {

    /**
     * Gets all amigos in the system.  Never called by a user.
     * @return a list of all amigos in the system.
     */
    List<AmigoUser> getAllAmigosInSystem();
}
