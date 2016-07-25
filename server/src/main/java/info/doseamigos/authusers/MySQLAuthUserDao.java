package info.doseamigos.authusers;

import java.sql.*;
import java.util.Date;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.db.MySQLConnection;

/**
 * MySQL implementation  of AuthUserDao.
 */
public class MySQLAuthUserDao implements AuthUserDao {
    @Override
    public AuthUser getByGoogleRef(String googleRef) {
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT AUTHUSERS.AUTHUSERID, " +
                    "AUTHUSERS.EMAIL, " +
                    "AUTHUSERS.GOOGLEREFID, " +
                    "AUTHUSERS.AMIGOUSERID," +
                    "AMIGOUSERS.NAME, " +
                    "AMIGOUSERS.picUrl, " +
                    "AMIGOUSERS.LASTTIMEDOSETAKEN, " +
                    "AMIGOUSERS.NEXTTIMEDOSESCHEDULED " +
                    "FROM AUTHUSERS " +
                    "JOIN AMIGOUSERS " +
                    "ON AUTHUSERS.AMIGOUSERID =AMIGOUSERS.AMIGOUSERID " +
                    "WHERE AUTHUSERS.GOOGLEREFID = ?;"
            );
            statement.setString(1, googleRef);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return populateAuthUserFromRS(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException("A DB exception occurred:", e);
        }
    }

    @Override
    public AuthUser getByEmail(String email) {
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT AUTHUSERS.AUTHUSERID, " +
                    "AUTHUSERS.EMAIL, " +
                    "AUTHUSERS.GOOGLEREFID, " +
                    "AUTHUSERS.AMIGOUSERID," +
                    "AMIGOUSERS.NAME, " +
                    "AMIGOUSERS.picUrl, " +
                    "AMIGOUSERS.LASTTIMEDOSETAKEN, " +
                    "AMIGOUSERS.NEXTTIMEDOSESCHEDULED " +
                    "FROM AUTHUSERS " +
                    "JOIN AMIGOUSERS " +
                    "ON AUTHUSERS.AMIGOUSERID =AMIGOUSERS.AMIGOUSERID " +
                    "WHERE AUTHUSERS.EMAIL = ?;"
            );
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return populateAuthUserFromRS(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException("A DB exception occurred:", e);
        }
    }

    @Override
    public AuthUser getById(long authUserId) {
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT AUTHUSERS.AUTHUSERID, " +
                    "AUTHUSERS.EMAIL, " +
                    "AUTHUSERS.GOOGLEREFID, " +
                    "AUTHUSERS.AMIGOUSERID," +
                    "AMIGOUSERS.NAME, " +
                    "AMIGOUSERS.picUrl, " +
                    "AMIGOUSERS.LASTTIMEDOSETAKEN, " +
                    "AMIGOUSERS.NEXTTIMEDOSESCHEDULED " +
                    "FROM AUTHUSERS " +
                    "JOIN AMIGOUSERS " +
                    "ON AUTHUSERS.AMIGOUSERID =AMIGOUSERS.AMIGOUSERID " +
                    "WHERE AUTHUSERS.AUTHUSERID = ?;"
            );
            statement.setLong(1, authUserId);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return populateAuthUserFromRS(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException("A DB exception occurred:", e);
        }
    }

    @Override
    public Long save(AuthUser user) throws SQLException {
        Connection conn = null;
        Long newAuthId = null;
        try  {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);
            PreparedStatement authUserStatement;
            if (user.getAuthUserId() != null) {
                authUserStatement = conn.prepareStatement(
                    "UPDATE AUTHUSERS" +
                        " SET email = ? WHERE AUTHUSERID=?"
                );
                authUserStatement.setString(1, user.getEmail());
                authUserStatement.setLong(2, user.getAuthUserId());
                authUserStatement.executeUpdate();
                newAuthId = user.getAuthUserId();

                PreparedStatement updateAmigo = conn.prepareStatement("" +
                    "UPDATE AMIGOUSERS " +
                        "SET name = ?, " +
                        "    picUrl = ? " +
                    " WHERE amigouserid IN ( " +
                    "   SELECT amigouserid " +
                    "     FROM AUTHUSERS " +
                    "    WHERE authUserId = ?" +
                    ")"
                );

                updateAmigo.setString(1, user.getAmigoUser().getName());
                updateAmigo.setString(2, user.getAmigoUser().getPicture());
                updateAmigo.setLong(3, user.getAuthUserId());
                updateAmigo.execute();
            } else {

                PreparedStatement amigoStatement = conn.prepareStatement(
                    "INSERT INTO AMIGOUSERS(NAME) VALUES (?);"
                );
                amigoStatement.setString(1, user.getAmigoUser().getName());
                amigoStatement.executeUpdate();
                PreparedStatement getAmigoId = conn.prepareStatement(
                    "SELECT LAST_INSERT_ID() AS amigoUserId"
                );
                ResultSet amigoResultSet = getAmigoId.executeQuery();
                amigoResultSet.next();
                long newAmigoId = amigoResultSet.getLong("amigoUserId");

                authUserStatement = conn.prepareStatement(
                    "INSERT INTO AUTHUSERS(email, googleRefId, amigoUserId) " +
                        "VALUES (?,?,?);"
                );
                authUserStatement.setString(1, user.getEmail());
                authUserStatement.setString(2, user.getGoogleRef());
                authUserStatement.setLong(3, newAmigoId);
                authUserStatement.executeUpdate();
                PreparedStatement getAuthId = conn.prepareStatement(
                    "SELECT LAST_INSERT_ID() AS authUserId"
                );
                ResultSet authResultSet = getAuthId.executeQuery();
                authResultSet.next();
                newAuthId = authResultSet.getLong("authUserId");

                PreparedStatement relStatement = conn.prepareStatement(
                    "INSERT INTO AMIGO_AUTH_REL(AMIGOUSERID, AUTHUSERID, acknowledged) " +
                        "VALUES (?,?,'Y')"
                );
                relStatement.setLong(1, newAmigoId);
                relStatement.setLong(2, newAuthId);
                relStatement.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("A DB exception occurred:", e);
        } finally {
            conn.rollback();
            conn.close();
        }
        return newAuthId;
    }

    @Override
    public AuthUser getByIdToken(String idToken) throws SQLException {
        try (Connection conn= MySQLConnection.create()) {
            PreparedStatement getInfo = conn.prepareStatement(
                "SELECT AUTHTOKENCACHE.idToken, " +
                    "   AUTHTOKENCACHE.dateAdded, " +
                    "   AUTHTOKENCACHE.duration, " +
                    "   AUTHUSERS.authUserId, " +
                    "   AUTHUSERS.EMAIL, " +
                    "   AUTHUSERS.GOOGLEREFID, " +
                    "   AUTHUSERS.AMIGOUSERID," +
                    "   AMIGOUSERS.NAME, " +
                    "   AMIGOUSERS.picUrl, " +
                    "   AMIGOUSERS.LASTTIMEDOSETAKEN, " +
                    "   AMIGOUSERS.NEXTTIMEDOSESCHEDULED " +
                    "FROM AUTHTOKENCACHE " +
                    "JOIN AUTHUSERS " +
                    "  ON AUTHTOKENCACHE.authUserId = AUTHUSERS.authUserId " +
                    "JOIN AMIGOUSERS " +
                    "  ON AUTHUSERS.AMIGOUSERID =AMIGOUSERS.AMIGOUSERID " +
                    "WHERE AUTHTOKENCACHE.idToken = ?;"
            );
            getInfo.setString(1, idToken);

            ResultSet rs =getInfo.executeQuery();
            if (!rs.next()) {
                return null;
            }
            long duration = rs.getLong("duration");
            Date dateAdded = rs.getTimestamp("dateAdded");
            if (new Date().getTime() - dateAdded.getTime() > (duration*1000)) {
                PreparedStatement deleteRow = conn.prepareStatement(
                    "DELETE FROM AUTHTOKENCACHE WHERE idToken = ?"
                );
                deleteRow.setString(1, idToken);
                deleteRow.executeUpdate();
                return null;
            }
            return populateAuthUserFromRS(rs);
        }
    }

    @Override
    public void storeInfo(AuthUser user, String token, Integer durationInSeconds) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);

            if (getByIdToken(token) != null) {
                return;
            }

            PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO AUTHTOKENCACHE(authUserId, idToken, dateAdded, duration) " +
                    "VALUES (?, ?, ?, ?)"
            );
            statement.setLong(1, user.getAuthUserId());
            statement.setString(2, token);
            statement.setTimestamp(3, new Timestamp(new Date().getTime()));
            statement.setInt(4, durationInSeconds);

            statement.executeUpdate();

            conn.commit();

        } finally {
            conn.rollback();
            conn.close();
        }

    }

    private AuthUser populateAuthUserFromRS(ResultSet resultSet) throws SQLException {
        AmigoUser amigoUser = new AmigoUser();
        amigoUser.setName(resultSet.getString("NAME"));
        amigoUser.setId(resultSet.getLong("AMIGOUSERID"));
        amigoUser.setPicture(resultSet.getString("picUrl"));
        AuthUser authUser = new AuthUser();
        authUser.setAmigoUser(amigoUser);
        authUser.setEmail(resultSet.getString("EMAIL"));
        authUser.setAuthUserId(resultSet.getLong("AUTHUSERID"));
        authUser.setGoogleRef(resultSet.getString("GOOGLEREFID"));
        if (resultSet.next()) {
            throw new RuntimeException("TOO MANY RESULTS FOUND");
        }
        return authUser;
    }
}
