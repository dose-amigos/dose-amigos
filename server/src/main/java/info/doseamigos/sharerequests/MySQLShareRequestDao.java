package info.doseamigos.sharerequests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import info.doseamigos.authusers.AuthUser;
import info.doseamigos.db.MySQLConnection;

/**
 * MySQL implementation of {@link ShareRequestDao}.
 */
public class MySQLShareRequestDao implements ShareRequestDao {
    @Override
    public void addNewShareRequest(ShareRequest request) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);


            PreparedStatement addShareRequestStatement = conn.prepareStatement(
                    "INSERT INTO SHAREREQUESTS(amigouserid, authUserId) " +
                    "VALUES (?, ?);"
            );
            addShareRequestStatement.setLong(1, request.getSharedAmigo().getAmigoUserId());
            addShareRequestStatement.setLong(2, getAuthUserId(conn, request.getTargetUserEmail()));

            addShareRequestStatement.executeUpdate();
            conn.commit();
        } finally {
            conn.rollback();
            conn.close();
        }
    }

    @Override
    public ShareRequest updateShareRequest(ShareRequest request) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);
            PreparedStatement approveRequestStatement = conn.prepareStatement(
                    "UPDATE SHAREREQUESTS " +
                        "SET approved = ? " +
                        "WHERE shareRequestId = ?"
            );
            approveRequestStatement.setString(1, request.isApproved() ? "Y" : "N");
            approveRequestStatement.setLong(2, request.getId());

            approveRequestStatement.executeUpdate();

            conn.commit();
        } finally {
            conn.rollback();
            conn.close();
        }
        return request;
    }

    @Override
    public List<ShareRequest> getPendingShareRequests(AuthUser user) throws SQLException {
        List<ShareRequest> toRet = new ArrayList<>();
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement getRequestsStatement = conn.prepareStatement(
                    "SELECT AMIGOUSERS.amigouserid, " +
                            "   AMIGOUSERS.name AS amigoName, " +
                            "   AMIGOUSERS.lastTimeDoseTaken, " +
                            "   AMIGOUSERS.nextTimeDoseScheduled, " +
                            "   AUTHUSERS.email AS targetUserEmail, +" +
                            "   SHAREREQUESTS.shareRequestId, " +
                            "   SHAREREQUESTS.approved " +
                            "FROM SHAREREQUESTS " +
                            "JOIN AMIGOUSERS " +
                            "  ON SHAREREQUESTS.amigouserid = AMIGOUSERS.amigouserid " +
                            "JOIN AUTHUSERS " +
                            "  ON SHAREREQUESTS.authUserId = AUTHUSERS.authUserId " +
                            "WHERE AUTHUSERS.authUserId = ? " +
                            "  AND SHAREREQUESTS.approved IS NULL"
            );
            getRequestsStatement.setLong(1, user.getAuthUserId());
            ResultSet rs = getRequestsStatement.executeQuery();
            ShareRequestRowMapper shareRequestRowMapper = new ShareRequestRowMapper();
            while (rs.next()) {
                toRet.add(shareRequestRowMapper.mapRow(rs));
            }
        }
        return toRet;
    }

    private long getAuthUserId(Connection conn, String email) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT authUserId FROM AUTHUSERS WHERE email = ?");
        statement.setString(1, email);
        ResultSet rs = statement.executeQuery();
        if (!rs.next()) {
            throw new RuntimeException("No user with that email was found.");
        }
        return rs.getLong("authUserId");
    }
}
