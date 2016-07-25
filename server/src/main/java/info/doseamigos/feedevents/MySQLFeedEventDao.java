package info.doseamigos.feedevents;

import info.doseamigos.authusers.AuthUser;
import info.doseamigos.db.MySQLConnection;
import info.doseamigos.meds.Med;
import info.doseamigos.meds.MedRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of {@link FeedEventDao}.
 */
public class MySQLFeedEventDao implements FeedEventDao {

    @Override
    public List<FeedEvent> getEventsForUser(AuthUser user) throws SQLException {
        List<FeedEvent> toRet = new ArrayList<>();
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT FEEDEVENTS.feedEventId, " +
                    "   FEEDEVENTS.action, " +
                    "   FEEDEVENTS.actionDateTime, " +
                    "   AMIGOUSERS.amigouserid, " +
                    "   AMIGOUSERS.name AS amigoName, " +
                    "   AMIGOUSERS.picUrl, " +
                    "   AMIGOUSERS.lastTimeDoseTaken, " +
                    "   AMIGOUSERS.nextTimeDoseScheduled " +
                    "FROM FEEDEVENTS " +
                    "JOIN AMIGOUSERS " +
                    " ON FEEDEVENTS.amigouserid = AMIGOUSERS.amigouserid " +
                    "JOIN SHAREREQUESTS " +
                    " ON AMIGOUSERS.amigouserid = SHAREREQUESTS.amigouserid " +
                    "AND SHAREREQUESTS.authUserId = ? " +
                    "AND SHAREREQUESTS.approved = 'Y' " +
                    "ORDER BY FEEDEVENTS.actionDateTime DESC"
            );
            statement.setLong(1, user.getAuthUserId());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                FeedEvent feedEvent = new FeedEventRowMapper().mapRow(rs);
                feedEvent.setMeds(getMeds(conn, feedEvent.getId()));
                toRet.add(feedEvent);
            }
        }
        return toRet;
    }

    private List<Med> getMeds(Connection conn, Long feedEventId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
            "   SELECT MEDS.medId," +
                "      MEDS.rxcui," +
                "      MEDS.name AS medName," +
                "      MEDS.doseamount," +
                "      MEDS.doseUnit," +
                "      MEDS.totalAmount," +
                "      MEDS.doseInstructions," +
                "      MEDS.firstTaken," +
                "      MEDS.lastDoseTaken," +
                "      MEDS.nextScheduledDose," +
                "      MEDS.active," +
                "      AMIGOUSERS.amigouserid," +
                "      AMIGOUSERS.picUrl, " +
                "      AMIGOUSERS.lastTimeDoseTaken," +
                "      AMIGOUSERS.nextTimeDoseScheduled," +
                "      AMIGOUSERS.name AS amigoName" +
                " FROM FEED_DOSE_REL " +
                " JOIN DOSEEVENTS " +
                "   ON FEED_DOSE_REL.doseEventId = DOSEEVENTS.doseEventId " +
                " JOIN MEDS " +
                "   ON DOSEEVENTS.medId = MEDS.medId" +
                " JOIN AMIGOUSERS" +
                "   ON MEDS.amigoUserId = AMIGOUSERS.amigoUserId " +
                "WHERE FEED_DOSE_REL.feedEventId = ? "
        );
        statement.setLong(1, feedEventId);

        ResultSet rs = statement.executeQuery();
        List<Med> results = new ArrayList<>();
        while (rs.next()) {
            results.add(new MedRowMapper().mapRow(rs));
        }
        return results;
    }
}
