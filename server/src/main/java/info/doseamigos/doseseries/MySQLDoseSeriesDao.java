package info.doseamigos.doseseries;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.doseamigos.amigousers.AmigoUser;
import info.doseamigos.amigousers.UpdateAmigoDatesDAO;
import info.doseamigos.db.MySQLConnection;
import info.doseamigos.meds.Med;
import info.doseamigos.meds.MedRowMapper;

/**
 * MySQL implementation for {@link DoseSeriesDao}.
 */
public class MySQLDoseSeriesDao implements DoseSeriesDao {

    private UpdateAmigoDatesDAO updateAmigoDatesDAO = new UpdateAmigoDatesDAO();

    @Override
    public Long save(DoseSeries series) throws SQLException {

        Connection conn = null;
        Long toRet = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);

            //First, create the series row if now seriesId exists
            Long seriesId = series.getSeriesId();
            if (seriesId == null) {
                PreparedStatement seriesRowStatement = conn.prepareStatement(
                    "INSERT INTO DOSESERIES(medId) VALUES (?)"
                );
                seriesRowStatement.setLong(1, series.getMed().getMedId());
                seriesRowStatement.executeUpdate();
                PreparedStatement getSeriesId = conn.prepareStatement(
                    "SELECT LAST_INSERT_ID() AS seriesId"
                );
                ResultSet seriesIdRS = getSeriesId.executeQuery();
                seriesIdRS.next();
                seriesId = seriesIdRS.getLong("seriesId");

            }
            deleteDoseSeriesItems(conn, seriesId);


            //Now add each item.
            for (Integer day : series.getDaysOfWeek()) {
                for (Date time : series.getTimesOfDay()) {
                    PreparedStatement addItem = conn.prepareStatement(
                        "INSERT INTO DOSESERIESITEM(seriesId, seriesDay, seriesTime) " +
                            "VALUES (?,?,?)"
                    );
                    addItem.setLong(1, seriesId);
                    addItem.setInt(2, day);
                    addItem.setTimestamp(3, new java.sql.Timestamp(time.getTime()));

                    addItem.executeUpdate();
                }
            }
            deleteFutureDoseEvents(conn, seriesId);
            updateAmigoDatesDAO.updateAmigo(conn, series.getMed().getUser());
            conn.commit();
            toRet = seriesId;

        } catch (SQLException e) {
            throw e;
        } finally {
            conn.rollback();
            conn.close();
        }

        return toRet;
    }

    public void deleteDoseSeriesItems(Connection conn, Long seriesId) throws SQLException {
        //Now delete all existing items with the series id
        PreparedStatement deleteAllExistingItems = conn.prepareStatement(
            "DELETE FROM DOSESERIESITEM WHERE seriesId = ?"
        );
        deleteAllExistingItems.setLong(1, seriesId);
        deleteAllExistingItems.executeUpdate();
    }

    private void deleteFutureDoseEvents(Connection conn, Long seriesId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
            "DELETE FROM DOSEEVENTS WHERE medId in ( " +
                "SELECT medId FROM DOSESERIES WHERE seriesId = ?" +
                ") " +
                " AND DOSEEVENTS.action IS NULL " +
                " AND DOSEEVENTS.scheduledDoseTime > ?"
        );

        statement.setLong(1, seriesId);
        statement.setTimestamp(2, new Timestamp(Instant.now().toEpochMilli()));
        statement.executeUpdate();
    }


    @Override
    public void delete(DoseSeries series) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLConnection.create();
            conn.setAutoCommit(false);

            //First, delete all future dose events
            deleteFutureDoseEvents(conn, series.getSeriesId());

            //Second, delete all DoseSeriesItems
            deleteDoseSeriesItems(conn, series.getSeriesId());

            //Lastly, mark the med as inactive
            PreparedStatement statement = conn.prepareStatement(
                "UPDATE MEDS SET active = 'N' WHERE medId = ?"
            );
            statement.setLong(1, series.getMed().getMedId());
            statement.executeUpdate();

            updateAmigoDatesDAO.updateAmigo(conn, series.getMed().getUser());
            conn.commit();
        } finally {
            conn.rollback();
            conn.close();
        }
    }

    @Override
    public DoseSeries getById(Long id) {
        DoseSeries series = new DoseSeries();
        series.setSeriesId(id);
        series.setDaysOfWeek(new ArrayList<Integer>());
        series.setTimesOfDay(new ArrayList<Date>());
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT DOSESERIESITEM.seriesId, " +
                    "   DOSESERIESITEM.seriesDay, " +
                    "   DOSESERIESITEM.seriesTime, " +
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
                    "   AMIGOUSERS.name AS amigoName" +
                    " FROM DOSESERIESITEM " +
                    " JOIN DOSESERIES " +
                    "   ON DOSESERIESITEM.seriesId = DOSESERIES.seriesId" +
                    " JOIN MEDS " +
                    "   ON DOSESERIES.medId = MEDS.medId " +
                    " JOIN AMIGOUSERS " +
                    "   ON MEDS.amigouserid = AMIGOUSERS.amigouserid " +
                    "WHERE DOSESERIES.seriesId = ?"
            );
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            Med med = null;
            while (rs.next()) {
                if (med == null) {
                    med = new MedRowMapper().mapRow(rs);
                }
                Integer seriesDay = rs.getInt("seriesDay");
                Date seriesTime = new Date(rs.getTimestamp("seriesTime").getTime());
                if (!series.getDaysOfWeek().contains(seriesDay)) {
                    series.getDaysOfWeek().add(seriesDay);
                }
                if (!series.getTimesOfDay().contains(seriesTime)) {
                    series.getTimesOfDay().add(seriesTime);
                }
            }
            series.setMed(med);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return series;
    }

    @Override
    public DoseSeries getForMed(Med lookupMed) {
        DoseSeries series = new DoseSeries();
        series.setDaysOfWeek(new ArrayList<Integer>());
        series.setTimesOfDay(new ArrayList<Date>());
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT DOSESERIESITEM.seriesId, " +
                    "   DOSESERIESITEM.seriesDay, " +
                    "   DOSESERIESITEM.seriesTime, " +
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
                    "   AMIGOUSERS.name AS amigoName" +
                    " FROM DOSESERIESITEM " +
                    " JOIN DOSESERIES " +
                    "   ON DOSESERIESITEM.seriesId = DOSESERIES.seriesId" +
                    " JOIN MEDS " +
                    "   ON DOSESERIES.medId = MEDS.medId " +
                    " JOIN AMIGOUSERS " +
                    "   ON MEDS.amigouserid = AMIGOUSERS.amigouserid " +
                    "WHERE DOSESERIES.medId = ?"
            );
            statement.setLong(1, lookupMed.getMedId());
            ResultSet rs = statement.executeQuery();
            Med med = null;
            Long seriesId = null;
            while (rs.next()) {
                if (seriesId == null) {
                    seriesId = rs.getLong("seriesId");
                }
                if (med == null) {
                    med = new MedRowMapper().mapRow(rs);
                }
                Integer seriesDay = rs.getInt("seriesDay");
                Date seriesTime = new Date(rs.getTimestamp("seriesTime").getTime());
                if (!series.getDaysOfWeek().contains(seriesDay)) {
                    series.getDaysOfWeek().add(seriesDay);
                }
                if (!series.getTimesOfDay().contains(seriesTime)) {
                    series.getTimesOfDay().add(seriesTime);
                }
            }
            series.setSeriesId(seriesId);
            series.setMed(med);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return series;
    }

    @Override
    public List<DoseSeries> getSeriesForUser(AmigoUser amigoUser) {
        List<DoseSeries> allSeries = new ArrayList<>();
        try (Connection conn = MySQLConnection.create()) {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT DOSESERIES.seriesId, " +
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
                    "   AMIGOUSERS.name AS amigoName" +
                    " FROM DOSESERIES " +
                    " JOIN MEDS " +
                    "   ON DOSESERIES.medId = MEDS.medId " +
                    " JOIN AMIGOUSERS " +
                    "   ON MEDS.amigouserid = AMIGOUSERS.amigouserid " +
                    "WHERE AMIGOUSERS.amigouserid = ? " +
                    "  AND MEDS.active = 'Y'" +
                    "ORDER BY DOSESERIES.seriesId ASC"
            );
            statement.setLong(1, amigoUser.getId());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                DoseSeries series = new DoseSeries();
                series.setSeriesId(rs.getLong("seriesId"));
                series.setMed(new MedRowMapper().mapRow(rs));
                updateDaysAndTimes(conn, series);
                allSeries.add(series);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return allSeries;
    }

    private void updateDaysAndTimes(Connection conn, DoseSeries series) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
            "SELECT DOSESERIESITEM.seriesId," +
                "   DOSESERIESITEM.seriesDay, " +
                "   DOSESERIESITEM.seriesTime " +
                "FROM DOSESERIESITEM " +
                "WHERE seriesId = ?"
        );

        statement.setLong(1, series.getSeriesId());

        ResultSet rs = statement.executeQuery();
        series.setDaysOfWeek(new ArrayList<Integer>());
        series.setTimesOfDay(new ArrayList<Date>());
        while (rs.next()) {
            Integer day = rs.getInt("seriesDay");
            if (!series.getDaysOfWeek().contains(day)) {
                series.getDaysOfWeek().add(day);
            }
            Date time = rs.getTimestamp("seriesTime");
            if (!series.getTimesOfDay().contains(time)) {
                series.getTimesOfDay().add(time);
            }
        }
    }
}
