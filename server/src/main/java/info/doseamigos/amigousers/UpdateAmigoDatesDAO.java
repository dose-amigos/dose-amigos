package info.doseamigos.amigousers;

import info.doseamigos.doseevents.EventType;

import java.sql.*;

/**
 * Simple class whose job is to update an amigo that has updated dose events or added medications.
 */
public class UpdateAmigoDatesDAO {

    public AmigoUser updateAmigo(Connection conn, AmigoUser amigoUser) throws SQLException {
        PreparedStatement getLastDoseDatestatement = conn.prepareStatement(
            "SELECT DOSEEVENTS.actionDateTime " +
                "FROM DOSEEVENTS " +
                "JOIN MEDS " +
                "ON DOSEEVENTS.medId = MEDS.medId " +
                "WHERE MEDS.amigouserid = ? " +
                "  AND action = ?" +
                "ORDER BY DOSEEVENTS.actionDateTime DESC "
        );
        getLastDoseDatestatement.setLong(1, amigoUser.getId());
        getLastDoseDatestatement.setString(2, EventType.TAKEN.name());
        ResultSet resultSet = getLastDoseDatestatement.executeQuery();
        java.util.Date lastActionDate = null;
        if (resultSet.next()) {
            lastActionDate = resultSet.getTimestamp("actionDateTime");
        }
        amigoUser.setLastTimeDoseTaken(lastActionDate);

        PreparedStatement getNextDoseStatement = conn.prepareStatement(
            "SELECT DOSEEVENTS.scheduledDoseTime " +
                "FROM DOSEEVENTS " +
                "JOIN MEDS " +
                "ON DOSEEVENTS.medId = MEDS.medId " +
                "WHERE MEDS.amigouserid = ? " +
                "  AND DOSEEVENTS.action IS NULL " +
                "  AND MEDS.active IS 'Y' " +
                "ORDER BY DOSEEVENTS.actionDateTime ASC "
        );
        getNextDoseStatement.setLong(1, amigoUser.getId());
        ResultSet resultSet2 = getNextDoseStatement.executeQuery();
        java.util.Date nextScheduledDate = null;
        if (resultSet2.next()) {
            nextScheduledDate = resultSet2.getTimestamp("scheduledDoseTime");
        }
        amigoUser.setNextTimeDoseScheduled(nextScheduledDate);

        PreparedStatement updateAmigoStatement = conn.prepareStatement(
            "UPDATE AMIGOUSERS SET lastTimeDoseTaken = ?, nextTimeDoseScheduled = ? " +
                "WHERE amigouserid = ?"
        );
        if (amigoUser.getLastTimeDoseTaken() != null) {
            updateAmigoStatement.setTimestamp(1, new Timestamp(amigoUser.getLastTimeDoseTaken().getTime()));
        } else {
            updateAmigoStatement.setNull(1, Types.TIMESTAMP);
        }
        if (amigoUser.getNextTimeDoseScheduled() != null) {
            updateAmigoStatement.setTimestamp(2, new Timestamp(amigoUser.getNextTimeDoseScheduled().getTime()));
        } else {
            updateAmigoStatement.setNull(2, Types.TIMESTAMP);
        }
        updateAmigoStatement.setLong(3, amigoUser.getId());

        updateAmigoStatement.executeUpdate();

        return amigoUser;
    }
}
