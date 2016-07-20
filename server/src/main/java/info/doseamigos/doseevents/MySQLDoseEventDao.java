package info.doseamigos.doseevents;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.db.MySQLConnection;
import info.doseamigos.meds.Med;
import org.joda.time.DateTime;

/**
 * MySQL implementation of {@link DoseEventDao}.
 */
public class MySQLDoseEventDao implements DoseEventDao {
    @Override
    public Long create(DoseEvent doseEvent) throws SQLException {
        Connection conn = null;
        Long toRet;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);

            PreparedStatement insertStatement = conn.prepareStatement(
                "INSERT INTO DOSEEVENTS(scheduledDoseTime, medId) " +
                    "VALUES (?, ?)"
            );

            insertStatement.setTimestamp(1, new Timestamp(doseEvent.getScheduledDateTime().getTime()));
            insertStatement.setLong(2, doseEvent.getMed().getMedId());

            insertStatement.executeUpdate();

            PreparedStatement getIdStatement = conn.prepareStatement(
                "SELECT LAST_INSERT_ID() AS doseEventId"
            );

            ResultSet rs = getIdStatement.executeQuery();
            rs.next();
            toRet = rs.getLong("doseEventId");

            conn.commit();

        } finally {
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        }
        return toRet;
    }

    @Override
    public List<DoseEvent> getDoseEventForMedAfter(Med med, Date beginDate) {
        List<DoseEvent> toRet = new ArrayList<>();

        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT DOSEEVENTS.doseEventId, " +
                    "   DOSEEVENTS.scheduledDoseTime, " +
                    "   DOSEEVENTS.actionDateTime, " +
                    "   DOSEEVENTS.action, " +
                    "   MEDS.medId," +
                    "   MEDS.rxcui," +
                    "   MEDS.name AS medName," +
                    "   MEDS.doseamount," +
                    "   MEDS.doseUnit," +
                    "   MEDS.totalAmount," +
                    "   MEDS.doseInstructions," +
                    "   MEDS.firstTaken," +
                    "   MEDS.lastDoseTaken," +
                    "   MEDS.nextScheduledDose," +
                    "   MEDS.active," +
                    "   AMIGOUSERS.amigouserid," +
                    "   AMIGOUSERS.picUrl, " +
                    "   AMIGOUSERS.lastTimeDoseTaken," +
                    "   AMIGOUSERS.nextTimeDoseScheduled," +
                    "   AMIGOUSERS.name AS amigoName " +
                    "FROM DOSEEVENTS " +
                    "JOIN MEDS " +
                    "  ON DOSEEVENTS.medId = MEDS.medId " +
                    "JOIN AMIGOUSERS " +
                    "  ON MEDS.amigouserid = AMIGOUSERS.amigouserid " +
                    "WHERE DOSEEVENTS.scheduledDoseTime > ? " +
                    "  AND MEDS.medId = ? " +
                    "ORDER BY DOSEEVENTS.scheduledDoseTime ASC"
            );

            statement.setTimestamp(1, new Timestamp(beginDate.getTime()));
            statement.setLong(2, med.getMedId());

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                toRet.add(new DoseEventRowMapper().mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return toRet;
    }

    @Override
    public List<DoseEvent> getEventsForUserToday(AmigoUser amigoUser) {
        List<DoseEvent> toRet = new ArrayList<>();

        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT DOSEEVENTS.doseEventId, " +
                    "   DOSEEVENTS.scheduledDoseTime, " +
                    "   DOSEEVENTS.actionDateTime, " +
                    "   DOSEEVENTS.action, " +
                    "   MEDS.medId," +
                    "   MEDS.rxcui," +
                    "   MEDS.name AS medName," +
                    "   MEDS.doseamount," +
                    "   MEDS.doseUnit," +
                    "   MEDS.totalAmount," +
                    "   MEDS.doseInstructions," +
                    "   MEDS.firstTaken," +
                    "   MEDS.lastDoseTaken," +
                    "   MEDS.nextScheduledDose," +
                    "   MEDS.active," +
                    "   AMIGOUSERS.amigouserid," +
                    "   AMIGOUSERS.picUrl, " +
                    "   AMIGOUSERS.lastTimeDoseTaken," +
                    "   AMIGOUSERS.nextTimeDoseScheduled," +
                    "   AMIGOUSERS.name AS amigoName " +
                    "FROM DOSEEVENTS " +
                    "JOIN MEDS " +
                    "  ON DOSEEVENTS.medId = MEDS.medId " +
                    "JOIN AMIGOUSERS " +
                    "  ON MEDS.amigouserid = AMIGOUSERS.amigouserid " +
                    "WHERE DOSEEVENTS.scheduledDoseTime < ? " +
                    "  AND AMIGOUSERS.amigouserid = ? " +
                    "  AND DOSEEVENTS.action IS NULL " +
                    "ORDER BY DOSEEVENTS.scheduledDoseTime ASC"
            );
            DateTime tomorrow = DateTime.now().plusDays(1);
            statement.setTimestamp(1, new Timestamp(tomorrow.toDate().getTime()));
            statement.setLong(2, amigoUser.getId());

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                toRet.add(new DoseEventRowMapper().mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return toRet;
    }

    @Override
    public void updateDoseEvents(List<DoseEvent> doseEvents) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);
            for (DoseEvent event : doseEvents) {
                updateEventCall(conn, event);
            }
            conn.commit();

        } finally {
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        }
    }

    private void updateEventCall(Connection conn, DoseEvent event) throws SQLException {
        PreparedStatement updateStatement = conn.prepareStatement(
            "UPDATE DOSEEVENTS " +
                "  SET action=?, actionDateTime=? " +
                "WHERE doseEventId = ?"
        );
        updateStatement.setString(1, event.getAction().name());
        updateStatement.setTimestamp(2, new Timestamp(event.getActionDateTime().getTime()));
        updateStatement.setLong(3, event.getDoseEventId());

        updateStatement.executeUpdate();
    }

    @Override
    public List<DoseEvent> getEventsForUser(AmigoUser amigoUser, Date startDate, String dir) {
        List<DoseEvent> toRet = new ArrayList<>();
        boolean next = dir.equalsIgnoreCase("next");
        String sqlCompareAction = next ? ">" : "<";
        String sqlDir = next ? "ASC" : "DESC";
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT DOSEEVENTS.doseEventId, " +
                            "   DOSEEVENTS.scheduledDoseTime, " +
                            "   DOSEEVENTS.actionDateTime, " +
                            "   DOSEEVENTS.action, " +
                            "   MEDS.medId," +
                            "   MEDS.rxcui," +
                            "   MEDS.name AS medName," +
                            "   MEDS.doseamount," +
                            "   MEDS.doseUnit," +
                            "   MEDS.totalAmount," +
                            "   MEDS.doseInstructions," +
                            "   MEDS.firstTaken," +
                            "   MEDS.lastDoseTaken," +
                            "   MEDS.nextScheduledDose," +
                            "   MEDS.active," +
                            "   AMIGOUSERS.amigouserid," +
                            "   AMIGOUSERS.picUrl, " +
                            "   AMIGOUSERS.lastTimeDoseTaken," +
                            "   AMIGOUSERS.nextTimeDoseScheduled," +
                            "   AMIGOUSERS.name AS amigoName " +
                            "FROM DOSEEVENTS " +
                            "JOIN MEDS " +
                            "  ON DOSEEVENTS.medId = MEDS.medId " +
                            "JOIN AMIGOUSERS " +
                            "  ON MEDS.amigouserid = AMIGOUSERS.amigouserid " +
                            "WHERE DOSEEVENTS.scheduledDoseTime " + sqlCompareAction + " ? " +
                            "  AND AMIGOUSERS.amigouserid = ? " +
                            "ORDER BY DOSEEVENTS.scheduledDoseTime " + sqlDir
            );
            statement.setTimestamp(1, new Timestamp(startDate.getTime()));
            statement.setLong(2, amigoUser.getId());

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                toRet.add(new DoseEventRowMapper().mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return toRet;
    }

    @Override
    public void markMissedEvents() throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);

            //First, get all events in the system whose scheduled time is over an hour ago and missing an action.
            PreparedStatement statement = conn.prepareStatement(
                "SELECT DOSEEVENTS.doseEventId, " +
                    "   DOSEEVENTS.scheduledDoseTime, " +
                    "   DOSEEVENTS.actionDateTime, " +
                    "   DOSEEVENTS.action, " +
                    "   MEDS.medId," +
                    "   MEDS.rxcui," +
                    "   MEDS.name AS medName," +
                    "   MEDS.doseamount," +
                    "   MEDS.doseUnit," +
                    "   MEDS.totalAmount," +
                    "   MEDS.doseInstructions," +
                    "   MEDS.firstTaken," +
                    "   MEDS.lastDoseTaken," +
                    "   MEDS.nextScheduledDose," +
                    "   MEDS.active," +
                    "   AMIGOUSERS.amigouserid," +
                    "   AMIGOUSERS.picUrl, " +
                    "   AMIGOUSERS.lastTimeDoseTaken," +
                    "   AMIGOUSERS.nextTimeDoseScheduled," +
                    "   AMIGOUSERS.name AS amigoName " +
                    "FROM DOSEEVENTS " +
                    "JOIN MEDS " +
                    "  ON DOSEEVENTS.medId = MEDS.medId " +
                    "JOIN AMIGOUSERS " +
                    "  ON MEDS.amigouserid = AMIGOUSERS.amigouserid " +
                    "WHERE DOSEEVENTS.scheduledDoseTime <= ? " +
                    "  AND DOSEEVENTS.action IS NULL "
            );

            statement.setTimestamp(1, new Timestamp(DateTime.now().minusHours(1).toDate().getTime()));

            //While going through them, mark them as MISSED in the DB.
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                DoseEvent e = new DoseEventRowMapper().mapRow(rs);
                e.setAction(EventType.MISSED);
                e.setActionDateTime(new Date());
                updateEventCall(conn, e);
            }

            conn.commit();

        } finally {
            conn.rollback();
            conn.close();
        }
    }
}
