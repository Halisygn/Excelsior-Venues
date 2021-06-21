package com.techelevator.model.jdbc;

import com.techelevator.DAOIntegrationTest;
import com.techelevator.model.Reservation;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class JDBCReservationDAOTest extends DAOIntegrationTest {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
    private JDBCReservationDAO reservationDAO = new JDBCReservationDAO(getDataSource());

    @Test
    public void makeReservation() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse("2021-10-10", dtf);
        LocalDate endDate = LocalDate.parse("2021-10-15", dtf);
        SqlRowSet nextReservationIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('reservation_reservation_id_seq')");

        int nextId = 0;
        if (nextReservationIdResult.next()) {
            nextId = nextReservationIdResult.getInt(1);
        }
        Reservation createdReservation = reservationDAO.makeReservation(1, 25, startDate, endDate, "Amy");

        assertEquals(nextId + 1, createdReservation.getId());
    }
}