package info.doseamigos.doseevents;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.amigousers.UpdateAmigoDatesDAO;
import info.doseamigos.db.MySQLConnection;
import info.doseamigos.meds.Med;
import org.joda.time.DateTime;
import org.joda.time.Instant;

/**
 * MySQL implementation of {@link DoseEventDao}.
 */
public class MySQLDoseEventDao implements DoseEventDao {

    private UpdateAmigoDatesDAO updateAmigoDatesDAO = new UpdateAmigoDatesDAO();

    @Override
    public Long create(DoseEvent doseEvent) throws SQLException {
        Connection conn = null;
        Long toRet;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);
            Long feedEventId = createFeedEvent(conn, doseEvent.getMed().getUser().getId(),
                new Date(), doseEvent.getAction());
            toRet = createEvent(doseEvent, feedEventId, conn);

            conn.commit();

        } finally {
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        }
        return toRet;
    }

    private Long createEvent(DoseEvent doseEvent, Long feedEventId, Connection conn) throws SQLException {
        Long toRet;PreparedStatement insertStatement = conn.prepareStatement(
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

        updateAmigoDatesDAO.updateAmigo(conn, doseEvent.getMed().getUser());

        if (feedEventId != null) {
            createFeedRel(conn, feedEventId, toRet);
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
            statement.setTimestamp(1, new Timestamp(tomorrow.getMillis()));
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
    public List<DoseEvent> getEventsForUserWeekly(AmigoUser amigoUser) {
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
                    "  AND DOSEEVENTS.scheduledDoseTime > ? " +
                    "  AND AMIGOUSERS.amigouserid = ? " +
                    "  AND DOSEEVENTS.action IS NULL " +
                    "ORDER BY DOSEEVENTS.scheduledDoseTime ASC"
            );
            DateTime now = DateTime.now();
            DateTime weekFromToday = now.plusDays(7);
            statement.setTimestamp(1, new Timestamp(weekFromToday.getMillis()));
            statement.setTimestamp(2, new Timestamp(now.getMillis()));
            statement.setLong(3, amigoUser.getId());

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
            EventType action = doseEvents.get(0).getAction();
            if (action == null) {
                action = EventType.UNDONE;
            }
            Long feedEventId = createFeedEvent(conn, doseEvents.get(0).getMed().getUser().getId(),
                new Date(), action);
            for (DoseEvent event : doseEvents) {
                updateEventCall(conn, event);
                createFeedRel(conn, feedEventId, event.getDoseEventId());
                event.getMed().setUser(updateAmigoDatesDAO.updateAmigo(conn, doseEvents.get(0).getMed().getUser()));
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
        if (event.getAction() != null) {
            updateStatement.setString(1, event.getAction().name());
            updateStatement.setTimestamp(2, new Timestamp(event.getActionDateTime().getTime()));
        } else {
            updateStatement.setNull(1, Types.VARCHAR);
            updateStatement.setNull(2, Types.TIMESTAMP);
        }
        updateStatement.setLong(3, event.getDoseEventId());

        updateStatement.executeUpdate();

        updateAmigoDatesDAO.updateAmigo(conn, event.getMed().getUser());
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
                    "  AND DOSEEVENTS.action IS NULL " +
                    "ORDER BY AMIGOUSERS.amigouserid"
            );

            statement.setTimestamp(1, new Timestamp(DateTime.now().minusHours(1).toDate().getTime()));

            //While going through them, mark them as MISSED in the DB.
            ResultSet rs = statement.executeQuery();
            Long curUserId = null;
            Long feedEventId = null;
            while (rs.next()) {
                DoseEvent e = new DoseEventRowMapper().mapRow(rs);
                Date curTime = new Date();
                if (!e.getMed().getUser().getId().equals(curUserId)) {
                    curUserId = e.getMed().getUser().getId();
                    feedEventId = createFeedEvent(
                        conn,
                        curUserId,
                        curTime,
                        EventType.MISSED
                    );
                }
                e.setAction(EventType.MISSED);
                e.setActionDateTime(curTime);
                updateEventCall(conn, e);
                createFeedRel(conn, feedEventId, e.getDoseEventId());
                updateAmigoDatesDAO.updateAmigo(conn, e.getMed().getUser());
            }

            conn.commit();

        } finally {
            conn.rollback();
            conn.close();
        }
    }

    private void createFeedRel(Connection conn, Long feedEventId, Long doseEventId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
            "INSERT INTO FEED_DOSE_REL(feedEventId, doseEventId) " +
                "VALUES (?, ?)"
        );
        statement.setLong(1, feedEventId);
        statement.setLong(2, doseEventId);
        statement.executeUpdate();
    }

    private Long createFeedEvent(
        Connection conn,
        Long curUserId,
        Date curTime,
        EventType eventType
    ) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
            "INSERT INTO FEEDEVENTS (amigouserid, actionDateTime, action) " +
                "VALUES (?, ?, ?)"
        );
        statement.setLong(1, curUserId);
        statement.setTimestamp(2, new Timestamp(curTime.getTime()));
        statement.setString(3, eventType.name());
        statement.executeUpdate();

        PreparedStatement getId = conn.prepareStatement(
            "SELECT LAST_INSERT_ID() AS feedEventId"
        );
        ResultSet rs = getId.executeQuery();
        rs.next();
        return rs.getLong("feedEventId");
    }

    @Override
    public void createMultiple(List<DoseEvent> eventsToAdd, AmigoUser user) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);
            String event = null;
            Long feedEventId = null;
            for (DoseEvent doseEvent : eventsToAdd) {
                if (doseEvent.getAction() != null && !doseEvent.getAction().name().equals(event)) {
                    event = doseEvent.getAction().name();
                    feedEventId = createFeedEvent(
                        conn,
                        doseEvent.getMed().getUser().getId(),
                        new Date(),
                        doseEvent.getAction()
                    );
                }
                createEvent(doseEvent, feedEventId, conn);
            }
            conn.commit();
        } finally {
            conn.rollback();
            conn.close();
        }
    }
}
