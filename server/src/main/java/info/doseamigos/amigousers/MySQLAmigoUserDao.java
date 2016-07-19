package info.doseamigos.amigousers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import info.doseamigos.authusers.AuthUser;
import info.doseamigos.db.MySQLConnection;

/**
 * MySQL implementation of {@link AmigoUserDao}.
 */
public class MySQLAmigoUserDao implements AmigoUserDao {
    @Override
    public List<AmigoUser> getAllAmigosInSystem() {
        List<AmigoUser> amigos = new ArrayList<>();
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT AMIGOUSERS.amigouserid, " +
                    "   AMIGOUSERS.name AS amigoName, " +
                    "   AMIGOUSERS.picUrl, " +
                    "   AMIGOUSERS.lastTimeDoseTaken, " +
                    "   AMIGOUSERS.nextTimeDoseScheduled " +
                    "FROM AMIGOUSERS"
            );

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                amigos.add(new AmigoUserRowMapper().mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return amigos;
    }

    @Override
    public List<AmigoUser> getAmigosForAuthUser(AuthUser user) {
        List<AmigoUser> amigos = new ArrayList<>();
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "   SELECT AMIGOUSERS.amigouserid, " +
                    "      AMIGOUSERS.name AS amigoName, " +
                    "      AMIGOUSERS.picUrl, " +
                    "      AMIGOUSERS.lastTimeDoseTaken, " +
                    "      AMIGOUSERS.nextTimeDoseScheduled " +
                    " FROM AMIGOUSERS " +
                    " JOIN SHAREREQUESTS " +
                    "   ON AMIGOUSERS.amigouserid = SHAREREQUESTS.amigouserid " +
                    "WHERE SHAREREQUESTS.authUserId = ? " +
                    "  AND SHAREREQUESTS.amigouserid <> ?"

            );
            statement.setLong(1, user.getAuthUserId());
            statement.setLong(2, user.getAmigoUser().getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                amigos.add(new AmigoUserRowMapper().mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return amigos;
    }

    @Override
    public AmigoUser getById(Long id) {
        AmigoUser amigo = null;
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "   SELECT AMIGOUSERS.amigouserid, " +
                    "      AMIGOUSERS.name AS amigoName, " +
                    "      AMIGOUSERS.picUrl, " +
                    "      AMIGOUSERS.lastTimeDoseTaken, " +
                    "      AMIGOUSERS.nextTimeDoseScheduled " +
                    " FROM AMIGOUSERS " +
                    "WHERE AMIGOUSERS.amigouserid = ? "

            );
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("No amigos found with id:" + id);
            }
            amigo = new AmigoUserRowMapper().mapRow(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return amigo;
    }

    @Override
    public Long createNewAmigo(AuthUser user, AmigoUser newUser) throws SQLException {
        Connection conn = null;
        Long toRet = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);

            PreparedStatement createAmigoStatement = conn.prepareStatement(
                "INSERT INTO AMIGOUSERS(name, picUrl) VALUES (?, ?)"
            );
            createAmigoStatement.setString(1, newUser.getName());
            createAmigoStatement.setString(2, newUser.getPicture());
            createAmigoStatement.executeUpdate();

            PreparedStatement getNewAmigoID = conn.prepareStatement(
                "SELECT LAST_INSERT_ID() AS amigoId"
            );
            ResultSet rs = getNewAmigoID.executeQuery();
            rs.next();
            toRet = rs.getLong("amigoId");

            PreparedStatement addShareRequest = conn.prepareStatement(
                "INSERT INTO SHAREREQUESTS(amigouserid, authUserId, approved) " +
                    "VALUES (?, ?, 'Y')"
            );
            addShareRequest.setLong(1, toRet);
            addShareRequest.setLong(2, user.getAuthUserId());
            addShareRequest.executeUpdate();

            conn.commit();

        } finally {
            conn.rollback();
            conn.close();
        }
        return toRet;
    }

    @Override
    public void updateAmigo(AmigoUser updatedUser) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);

            PreparedStatement updateStatement = conn.prepareStatement(
                "UPDATE AMIGOUSERS " +
                    "SET lastTimeDoseTaken = ?, " +
                    "    nextTimeDoseScheduled = ?, " +
                    "    picUrl = ?" +
                    "WHERE amigouserid = ?"
            );

            updateStatement.setTimestamp(1, updatedUser.getLastTimeDoseTaken() != null
                ? new Timestamp(updatedUser.getLastTimeDoseTaken().getTime())
                : null);

            updateStatement.setTimestamp(2, updatedUser.getNextTimeDoseScheduled() != null
                ? new Timestamp(updatedUser.getNextTimeDoseScheduled().getTime())
                : null);

            updateStatement.setString(3, updatedUser.getPicture());

            updateStatement.setLong(4, updatedUser.getId());

            updateStatement.executeUpdate();
            conn.commit();
        } finally {
            conn.rollback();
            conn.close();
        }
    }

    @Override
    public void deleteAmigo(AuthUser user, Long deletedUserId) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);

            PreparedStatement deleteStatement = conn.prepareStatement(
                "DELETE FROM SHAREREQUESTS " +
                    "WHERE amigouserid = ?" +
                    "  AND authUserId = ?"
            );

            deleteStatement.setLong(1, deletedUserId);
            deleteStatement.setLong(2, user.getAuthUserId());

            deleteStatement.executeUpdate();
            conn.commit();

        } finally {
            conn.rollback();
            conn.close();
        }
    }
}
