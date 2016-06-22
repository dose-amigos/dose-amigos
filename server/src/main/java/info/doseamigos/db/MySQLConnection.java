package info.doseamigos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for creating a connection to use to connect to the DB.
 */
public class MySQLConnection {

    /**
     * Creates a new MySQL DB connection to the DoseAmigos DB>
     * @return The connection to the DB.
     */
    public static Connection create() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://doseamigos2.cdjwskv4xmf8.us-east-1.rds.amazonaws.com:3306/doseamigos2?&useLegacyDatetimeCode=false&serverTimezone=UTC",
            "amigoadmin",
            "Newuser_22"
        );
    }
}
