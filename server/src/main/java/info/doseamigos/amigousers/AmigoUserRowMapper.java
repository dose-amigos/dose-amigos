package info.doseamigos.amigousers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Simple Row Mapper for result sets to return Amigo Users.
 */
public class AmigoUserRowMapper {

    public AmigoUser mapRow(ResultSet rs) throws SQLException {
        AmigoUser amigoUser = new AmigoUser(rs.getLong("amigoUserId"), rs.getString("amigoName"));
        Timestamp lastTimeDoseTaken = rs.getTimestamp("lastTimeDoseTaken");
        Timestamp nextTimeDoseScheduled = rs.getTimestamp("nextTimeDoseScheduled");
        amigoUser.setLastTimeDoseTaken(lastTimeDoseTaken);
        amigoUser.setNextTimeDoseScheduled(nextTimeDoseScheduled);
        return amigoUser;
    }
}
