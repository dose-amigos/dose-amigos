package info.doseamigos.sharerequests;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import info.doseamigos.authusers.AuthUser;

import static java.util.Objects.requireNonNull;

/**
 * Default Implementation of {@link ShareRequestService}.
 */
public class DefaultShareRequestService implements ShareRequestService {

    private final ShareRequestDao dao;

    @Inject
    public DefaultShareRequestService(
            @Nonnull ShareRequestDao dao
    ) {
        this.dao = requireNonNull(dao);
    }

    @Override
    public ShareRequest addNewShareRequest(AuthUser user, ShareRequest request) {
        if (request.getSharedAmigo() == null) {
            request.setSharedAmigo(user.getAmigoUser());
        }

        //TODO verify that user can see the amigo in the request.
        try {
            dao.addNewShareRequest(request);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    @Override
    public ShareRequest updateShareRequest(AuthUser user, ShareRequest request) {

        //TODO add same validation as above todo mentions.
        try {
            dao.updateShareRequest(request);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return request;
    }

    @Override
    public List<ShareRequest> getPendingShareRequests(AuthUser user) {
        try {
            return dao.getPendingShareRequests(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
