package info.doseamigos.amigousers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple Row Mapper for result sets to return Amigo Users.
 */
public class AmigoUserRowMapper {

    public AmigoUser mapRow(ResultSet rs) throws SQLException {
        return new AmigoUser(rs.getLong("amigoUserId"), rs.getString("amigoName"));
    }
}
