package com.techelevator.model.jdbc;

import com.techelevator.DAOIntegrationTest;
import com.techelevator.model.Space;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JDBCSpaceDAOTest extends DAOIntegrationTest {

    private JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
    private JDBCSpaceDAO spaceDAO = new JDBCSpaceDAO(getDataSource());

    @Test
    public void getSpaceByVenue() {
        String sql = "INSERT INTO space(venue_id, name, is_accessible, daily_rate, max_occupancy) " +
                "VALUES(1, 'Blue Lagoon', TRUE, '900', 50)";

        jdbcTemplate.update(sql);
        String spaceCountSql = "SELECT COUNT(*) FROM space WHERE venue_id = 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(spaceCountSql);
        int spaceCount = 0;

        if (results.next()) {
            spaceCount = Integer.parseInt(results.getString("COUNT"));
        }
        List<Space> spaceList = spaceDAO.getSpaceByVenue(1);

        assertNotNull(spaceList);
        assertEquals(spaceCount, spaceList.size());
    }

    @Test
    public void searchAvailableSpace() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse("2021-06-12", formatter);
        LocalDate endDate = LocalDate.parse("2021-06-20", formatter);

        String venueSql = "INSERT INTO venue(id,name,city_id,description) VALUES(DEFAULT,'xyz',1,'vhjnneu') RETURNING id";
        Long venueId = jdbcTemplate.queryForObject(venueSql,Long.class);

        String spaceSql = "INSERT INTO space (id, venue_id, name,is_accessible,open_from,open_to,daily_rate,max_occupancy)" +
                " VALUES(DEFAULT, ?, 'mySpace', TRUE, 4, 10, '200', 150) RETURNING id";
        Long spaceId = jdbcTemplate.queryForObject(spaceSql, Long.class,venueId);

        String spaceSql2 = "INSERT INTO space (id, venue_id, name,is_accessible,open_from,open_to,daily_rate,max_occupancy)" +
                " VALUES(DEFAULT, ?, 'mySpace2', TRUE, 4, 10, '250', 100) RETURNING id";
        Long spaceId2 = jdbcTemplate.queryForObject(spaceSql2, Long.class,venueId);

        String reservationSql1 = "INSERT INTO reservation(reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for)\n" +
                "VALUES(DEFAULT, ?, 4, '2021-07-15', '2021-07-19', 'Halis')";
        jdbcTemplate.update(reservationSql1 ,spaceId);

        String reservationSql2 = "INSERT INTO reservation(reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for)\n" +
                "VALUES(DEFAULT, ?, 10, '2021-06-15', '2021-06-19', 'Amy')";
        jdbcTemplate.update(reservationSql2 ,spaceId2);

        List<Space> availableSpacesList = spaceDAO.searchAvailableSpace(20,startDate,endDate,venueId);

        assertNotNull(availableSpacesList);
        assertEquals(1, availableSpacesList.size());
    }
}