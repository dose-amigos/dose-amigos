package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUserRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates a Med object from a ResultSet.
 */
public class MedRowMapper {

    public Med mapRow(ResultSet rs) throws SQLException {
        Med med = new Med();

        med.setMedId(rs.getLong("medId"));
        med.setDoseAmount(rs.getInt("doseAmount"));
        med.setNextScheduled(rs.getDate("nextScheduledDose"));
        med.setUser(new AmigoUserRowMapper().mapRow(rs));
        med.setLastTaken(rs.getDate("lastDoseTaken"));
        med.setActive("Y".equalsIgnoreCase(rs.getString("active")));
        med.setDoseInstructions(rs.getString("doseInstructions"));
        med.setDoseUnit(rs.getString("doseUnit"));
        med.setFirstTaken(rs.getDate("firstTaken"));
        med.setName(rs.getString("medName"));
        med.setRxcui(rs.getLong("rxcui"));

        return med;

    }
}
