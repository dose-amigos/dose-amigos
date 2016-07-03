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
            "jdbc:mysql://104.196.57.95:3306/doseamigos?&useLegacyDatetimeCode=false&serverTimezone=UTC",
            "amigoDBUser",
            "Newuser_22"
        );
    }
}
