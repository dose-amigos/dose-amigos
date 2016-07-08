package info.doseamigos.doseevents;

import info.doseamigos.meds.MedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jking31cs on 7/8/16.
 */
public class DoseEventRowMapper {

    public DoseEvent mapRow(ResultSet rs) throws SQLException {
        DoseEvent doseEvent = new DoseEvent();
        doseEvent.setDoseEventId(rs.getLong("doseEventId"));
        doseEvent.setScheduledDateTime(rs.getTimestamp("scheduledDoseTime"));
        doseEvent.setActionDateTime(rs.getTimestamp("actionDateTime"));
        doseEvent.setAction(EventType.getByString(rs.getString("action")));
        doseEvent.setMed(new MedRowMapper().mapRow(rs));

        return doseEvent;
    }
}
