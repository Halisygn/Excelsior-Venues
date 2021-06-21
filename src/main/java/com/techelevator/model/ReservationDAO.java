package com.techelevator.model;

import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {
    Reservation makeReservation(long spaceId, int numberOfPeople, LocalDate fromDate,
                                LocalDate toDate, String reservedFor);
}
