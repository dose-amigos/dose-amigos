package info.doseamigos.feedevents;

import info.doseamigos.amigousers.AmigoUserRowMapper;
import info.doseamigos.doseevents.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for FeedEvents
 */
public class FeedEventRowMapper {

    public FeedEvent mapRow(ResultSet rs) throws SQLException {

        FeedEvent feedEvent = new FeedEvent();
        feedEvent.setId(rs.getLong("feedEventId"));
        feedEvent.setActionDateTime(rs.getTimestamp("actionDateTime"));
        feedEvent.setUser(new AmigoUserRowMapper().mapRow(rs));
        feedEvent.setAction(EventType.getByString(rs.getString("action")));

        return feedEvent;
    }
}
