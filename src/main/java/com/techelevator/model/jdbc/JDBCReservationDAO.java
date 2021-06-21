package com.techelevator.model.jdbc;

import com.techelevator.model.Reservation;
import com.techelevator.model.ReservationDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;

public class JDBCReservationDAO implements ReservationDAO {
    private JdbcTemplate jdbcTemplate;

    public JDBCReservationDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Reservation makeReservation(long spaceId, int numberOfPeople, LocalDate fromDate, LocalDate toDate, String reservedFor) {
      String sql = "INSERT INTO reservation(space_id, number_of_attendees, start_date, end_date, reserved_for)\n" +
              "VALUES(?, ?, ?, ?, ?) RETURNING reservation_id";

      SqlRowSet result = jdbcTemplate.queryForRowSet(sql, spaceId, numberOfPeople, fromDate, toDate, reservedFor);

      Reservation reservation = new Reservation();

      if(result.next()){
            long reservation_id = result.getLong("reservation_id");
            reservation.setId(reservation_id);
            reservation.setNumber_of_attendees(numberOfPeople);
            reservation.setStart_date(fromDate);
            reservation.setEnd_date(toDate);
            reservation.setReserved_for(reservedFor);
        }
           return reservation;
    }


}
