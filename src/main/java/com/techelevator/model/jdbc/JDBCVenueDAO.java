package com.techelevator.model.jdbc;

import com.techelevator.model.Venue;
import com.techelevator.model.VenueDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JDBCVenueDAO implements VenueDAO {
    private JdbcTemplate jdbcTemplate;

    public JDBCVenueDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Venue> getVenueList() {
        List<Venue> venueList = new ArrayList<>();
        String sql = "SELECT v.id, v.name venue_name, v.description, c.name ||', ' || c.state_abbreviation city_name , string_agg(cat.name, ', ') as categories\n" +
                "FROM venue v\n" +
                "JOIN city c ON c.id = v.city_id\n" +
                "LEFT JOIN category_venue cv ON cv.venue_id = v.id\n" +
                "FULL OUTER JOIN category cat ON cv.category_id = cat.id\n" +
                "GROUP BY  venue_name, v.description, city_name, v.id\n" +
                "ORDER BY venue_name;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Venue venue = mapRowToVenue(results);
            venueList.add(venue);
        }

        return venueList;
    }

    private Venue mapRowToVenue(SqlRowSet results) {
        Venue theVenue = new Venue();
        theVenue.setId(results.getLong("id"));
        theVenue.setName(results.getString("venue_name"));
        theVenue.setCity(results.getString("city_name"));
        theVenue.setDescription(results.getString("description"));
        theVenue.setCategory(results.getString("categories"));

        return theVenue;
    }
}
