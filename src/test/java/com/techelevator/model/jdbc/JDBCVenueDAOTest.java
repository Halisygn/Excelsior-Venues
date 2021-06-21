package com.techelevator.model.jdbc;

import com.techelevator.DAOIntegrationTest;
import com.techelevator.model.Venue;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JDBCVenueDAOTest extends DAOIntegrationTest {

    private JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
    private JDBCVenueDAO venueDAO = new JDBCVenueDAO(getDataSource());

    @Test
    public void getVenueList() {
        String venueSql = "SELECT COUNT(*) FROM venue";
        SqlRowSet results = jdbcTemplate.queryForRowSet(venueSql);
        int venueListCount = 0;

        if (results.next()) {
            venueListCount = Integer.parseInt(results.getString("COUNT"));
        }
        List<Venue> venueList = venueDAO.getVenueList();
        assertNotNull(venueList);
        assertEquals(venueListCount, venueList.size());
    }
}