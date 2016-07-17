package info.doseamigos.sharerequests;

import java.sql.ResultSet;
import java.sql.SQLException;

import info.doseamigos.amigousers.AmigoUserRowMapper;

/**
 * Maps rows from ResultSets to ShareRequests.
 */
public class ShareRequestRowMapper {

    public ShareRequest mapRow(ResultSet resultSet) throws SQLException {
        ShareRequest request = new ShareRequest();
        request.setId(resultSet.getLong("shareRequestId"));
        request.setTargetUserEmail(resultSet.getString("targetUserEmail"));
        request.setSharedAmigo(new AmigoUserRowMapper().mapRow(resultSet));
        String approved = resultSet.getString("approved");
        if (approved != null) {
            request.setAccepted("Y".equals(approved));
        }
        return request;
    }
}
