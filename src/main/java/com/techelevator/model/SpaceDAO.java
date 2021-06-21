package com.techelevator.model;

import java.time.LocalDate;
import java.util.List;

public interface SpaceDAO {
    List<Space> getSpaceByVenue(long venue_id);
    List<Space> searchAvailableSpace(int numberOfAttendees, LocalDate startDate, LocalDate endDate,long venueId);
}
