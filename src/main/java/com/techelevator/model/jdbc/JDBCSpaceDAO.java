package com.techelevator.model.jdbc;

import com.techelevator.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCSpaceDAO implements SpaceDAO {
    private JdbcTemplate jdbcTemplate;

    public JDBCSpaceDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Space> getSpaceByVenue(long venue_id) {
        List<Space> spaceList = new ArrayList<>();
        String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, CAST(daily_rate as decimal(25,2)), max_occupancy \n" +
                "FROM space WHERE venue_id = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, venue_id);
        while (results.next()){
            Space space = mapRowToSpace(results);
            spaceList.add(space);
        }
        return spaceList;
    }

    @Override
    public List<Space> searchAvailableSpace(int numberOfAttendees, LocalDate startDate, LocalDate endDate, long venueId) {
        int startMonth = startDate.getMonthValue();
        int endMonth = endDate.getMonthValue();
        List<Space> spaceList = new ArrayList<>();
        String sql = "SELECT DISTINCT s.id, s.venue_id, s.name, s.is_accessible, s.open_from, s.open_to,CAST(daily_rate as decimal(25,2)), s.max_occupancy\n" +
                "FROM space s \n" +
                "WHERE s.venue_id = ? AND (? >= open_from OR open_from IS NULL) AND (? < open_to OR open_to IS NULL) \n" +
                "AND s.id NOT IN (SELECT space.id FROM space\n" +
                "JOIN reservation r ON r.space_id = space.id\n" +
                "WHERE space.max_occupancy < ?\n" +
                "OR (? <= r.end_date AND ? >= r.start_date))" +
                "ORDER BY daily_rate DESC\n" +
                "LIMIT 5;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, venueId, startMonth, endMonth, numberOfAttendees,startDate, endDate);
        while (results.next()){
            Space space = mapRowToSpace(results);
            spaceList.add(space);
        }
        return spaceList;
    }

    private Space mapRowToSpace(SqlRowSet results){
        Space theSpace = new Space();
        theSpace.setId(results.getLong("id"));
        theSpace.setVenue_id(results.getLong("venue_id"));
        theSpace.setName(results.getString("name"));
        theSpace.setIs_accessible(results.getBoolean("is_accessible"));
        theSpace.setOpen_from(results.getInt("open_from"));
        theSpace.setOpen_to(results.getInt("open_to"));
        theSpace.setDaily_rate(results.getBigDecimal("daily_rate"));
        theSpace.setMax_occupancy(results.getInt("max_occupancy"));

        return theSpace;
    }
}
