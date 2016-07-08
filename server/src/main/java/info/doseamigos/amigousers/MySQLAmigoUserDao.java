package info.doseamigos.amigousers;

import info.doseamigos.db.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jking31cs on 7/7/16.
 */
public class MySQLAmigoUserDao implements AmigoUserDao {
    @Override
    public List<AmigoUser> getAllAmigosInSystem() {
        List<AmigoUser> amigos = new ArrayList<>();
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT AMIGOUSERS.amigouserid, " +
                    "   AMIGOUSERS.name AS amigoName, " +
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
}
