package info.doseamigos.authusers;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.db.MySQLConnection;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySQL implementation  of AuthUserDao.
 */
public class MySQLAuthUserDao implements AuthUserDao {
    @Override
    public AuthUser getByGoogleRef(BigInteger googleRef) {
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT AUTHUSERS.AUTHUSERID, " +
                    "AUTHUSERS.EMAIL, " +
                    "AUTHUSERS.GOOGLEREFID, " +
                    "AUTHUSERS.AMIGOUSERID," +
                    "AMIGOUSERS.NAME, " +
                    "AMIGOUSERS.LASTTIMEDOSETAKEN, " +
                    "AMIGOUSERS.NEXTTIMEDOSESCHEDULED " +
                    "FROM AUTHUSERS " +
                    "JOIN AMIGOUSERS " +
                    "ON AUTHUSERS.AMIGOUSERID =AMIGOUSERS.AMIGOUSERID " +
                    "WHERE AUTHUSERS.GOOGLEREFID = ?;"
            );
            statement.setString(1, googleRef.toString());
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
                    "AMIGOUSERS.LASTTIMEDOSETAKEN, " +
                    "AMIGOUSERS.NEXTTIMEDOSESCHEDULED " +
                    "FROM AUTHUSERS " +
                    "JOIN AMIGOUSERS " +
                    "ON AUTHUSERS.AMIGOUSERID =AMIGOUSERS.AMIGOUSERID " +
                    "WHERE AUTHUSERS.EMAIL = ?;"
            );
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
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
                    "AMIGOUSERS.LASTTIMEDOSETAKEN, " +
                    "AMIGOUSERS.NEXTTIMEDOSESCHEDULED " +
                    "FROM AUTHUSERS " +
                    "JOIN AMIGOUSERS " +
                    "ON AUTHUSERS.AMIGOUSERID =AMIGOUSERS.AMIGOUSERID " +
                    "WHERE AUTHUSERS.AUTHUSERID = ?;"
            );
            statement.setLong(1, authUserId);
            ResultSet resultSet = statement.executeQuery();
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
                    "UPDATE AUTHUSERS SET email = ? WHERE AUTHUSERID=?"
                );
                authUserStatement.setString(1, user.getEmail());
                authUserStatement.setLong(2, user.getAuthUserId());
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
                authUserStatement.setString(2, user.getGoogleRef().toString());
                authUserStatement.setLong(3, newAmigoId);
                authUserStatement.executeUpdate();
                PreparedStatement getAuthId = conn.prepareStatement(
                    "SELECT LAST_INSERT_ID() AS authUserId"
                );
                ResultSet authResultSet = getAuthId.executeQuery();
                authResultSet.next();
                newAuthId = authResultSet.getLong("authUserId");

                PreparedStatement relStatement = conn.prepareStatement(
                    "INSERT INTO AUTH_AMIGO_REL(AMIGOUSERID, AUTHUSERID, acknowledged) " +
                        "VALUES (?,?,'Y')"
                );
                relStatement.setLong(1, newAmigoId);
                relStatement.setLong(2, newAuthId);
                relStatement.executeUpdate();

                conn.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("A DB exception occurred:", e);
        } finally {
            conn.rollback();
            conn.close();
        }
        return newAuthId;
    }

    private AuthUser populateAuthUserFromRS(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        AmigoUser amigoUser = new AmigoUser();
        amigoUser.setName(resultSet.getString("NAME"));
        amigoUser.setAmigoUserId(resultSet.getLong("AMIGOUSERID"));
        AuthUser authUser = new AuthUser();
        authUser.setAmigoUser(amigoUser);
        authUser.setEmail(resultSet.getString("EMAIL"));
        authUser.setAuthUserId(resultSet.getLong("AUTHUSERID"));
        authUser.setGoogleRef(new BigInteger(resultSet.getString("GOOGLEREFID")));
        if (resultSet.next()) {
            throw new RuntimeException("TOO MANY RESULTS FOUND");
        }
        return authUser;
    }
}
