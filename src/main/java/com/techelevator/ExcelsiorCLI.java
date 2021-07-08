package com.techelevator;

import com.techelevator.model.*;
import com.techelevator.model.jdbc.JDBCReservationDAO;
import com.techelevator.model.jdbc.JDBCSpaceDAO;
import com.techelevator.model.jdbc.JDBCVenueDAO;
import com.techelevator.view.Menu;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class ExcelsiorCLI {
    private static final String VIEW_VENUES = "1";
    private static final String VIEW_SPACES = "1";
    private static final String RESERVE_A_SPACE = "1";
    private static final String QUIT = "Q";
    private static final String RETURN_TO_PREVIOUS_SCREEN = "R";
    private boolean venueSpaceListRunning;
    private boolean detailsIsRunning;
    private boolean venueListRun;
    private Menu menu;
    private ReservationDAO reservationDAO;
    private SpaceDAO spaceDAO;
    private VenueDAO venueDAO;

    public ExcelsiorCLI(DataSource datasource) {
        menu = new Menu();
        reservationDAO = new JDBCReservationDAO(datasource);
        spaceDAO = new JDBCSpaceDAO(datasource);
        venueDAO = new JDBCVenueDAO(datasource);
    }

    public static void main(String[] args) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/excelsior_venues");
        dataSource.setUsername("");
        dataSource.setPassword("");

        ExcelsiorCLI application = new ExcelsiorCLI(dataSource);
        application.run();
    }

    public void run() {
        while (true) {
            menu.printMainMenu();
            String choice = menu.getUserRespond();
            if (choice.equals(VIEW_VENUES)) {
                handleVenues();
            }
            else if (choice.equals(QUIT)) {
                System.exit(0);
            }
            else {
                menu.printErrorMessage("Please select a valid choice!");
            }
        }
    }

    private void handleVenues() {
        int venueListSize = venueDAO.getVenueList().size();
        venueListRun = true;
        while (venueListRun) {
            menu.printViewVenues(venueDAO.getVenueList());
            String venueChoice = menu.getUserRespond();
            try {
                if (venueChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_SCREEN)) {
                    venueListRun = false;
                }
                else if (Integer.parseInt(venueChoice) <= venueListSize && Integer.parseInt(venueChoice) > 0) {
                    handleVenueDetails(venueChoice);
                }
                else {
                    menu.printErrorMessage("Please select a valid number!");
                }
            }
            catch (NumberFormatException e) {
                menu.printErrorMessage("Please enter a valid choice!");
            }
        }
    }

    private void handleVenueDetails(String venueChoice) {
        int venueInt = Integer.parseInt(venueChoice);
        Venue venue = venueDAO.getVenueList().get(venueInt - 1);
        detailsIsRunning = true;
        while (detailsIsRunning) {
            menu.printVenueDetails(venue);
            String menuChoice = menu.getUserRespond();
            if (menuChoice.equals(VIEW_SPACES)) {
                handleListVenueSpaces(venue, spaceDAO.getSpaceByVenue(venue.getId()));
            }
            else if (menuChoice.equalsIgnoreCase(RETURN_TO_PREVIOUS_SCREEN)) {
                detailsIsRunning = false;
            }
            else {
                menu.printErrorMessage("Please enter valid choice!");
            }
        }
    }

    private void handleListVenueSpaces(Venue venue, List<Space> spaceList) {
        venueSpaceListRunning = true;
        while (venueSpaceListRunning) {
            menu.printListVenueSpaces(spaceList, venue);
            String userResponse = menu.getUserRespond();
            if (userResponse.equals(RESERVE_A_SPACE)) {
                handleReserveASpace(venue);
            }
            else if (userResponse.equalsIgnoreCase(RETURN_TO_PREVIOUS_SCREEN)) {
                venueSpaceListRunning = false;
            }
            else {
                menu.printErrorMessage("Please enter valid respond");
            }
        }
    }

    private void handleReserveASpace(Venue venue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            String getUserDate = menu.askUserDate();
            LocalDate startDate = LocalDate.parse(getUserDate, formatter);
            if (startDate.isBefore(LocalDate.now())) {
                menu.printErrorMessage(startDate + " is before today!");
                throw new Exception();
            }
            int getUserDays = Integer.parseInt(menu.askUserDays());
            LocalDate endDate = startDate.plusDays(getUserDays);
            if (getUserDays < 0) {
                menu.printErrorMessage("Day cannot be negative!");
                throw new Exception();
            }
            int getUserAttendees = Integer.parseInt(menu.askUserAttendees());
            if (getUserAttendees < 0) {
                menu.printErrorMessage("Attendees cannot be negative!");
                throw new Exception();
            }
            List<Space> availableSpaceList = spaceDAO.searchAvailableSpace(getUserAttendees, startDate,
                    endDate, venue.getId());
            menu.printAvailableSpaces(availableSpaceList, getUserDays);
            handleReservationReport(availableSpaceList, getUserAttendees, startDate, endDate, venue);
        }
        catch (DateTimeParseException e) {
            menu.printErrorMessage("Please put a date in the correct format!");
        }
        catch (Exception e) {
            menu.printErrorMessage("Invalid choice!");
        }
    }

    public void handleReservationReport(List<Space> availableSpaceList, int getUserAttendees,
                                        LocalDate startDate, LocalDate endDate, Venue venue) throws Exception {
        menu.askUserReserveChoice();
        int reserveRespond = Integer.parseInt(menu.getUserRespond());

        if (reserveRespond == 0) {
        }
        else {
            menu.askUserWhoSpaceFor();
            String userName = menu.getUserRespond();
            for (Space space : availableSpaceList) {
                if (space.getId() == reserveRespond) {
                    Space preferredSpace = space;
                    Reservation reservation = reservationDAO.makeReservation(preferredSpace.getId(), getUserAttendees, startDate,
                            endDate, userName);
                    menu.getConfirmationReport(preferredSpace, reservation, venue);
                    venueSpaceListRunning = false;
                    detailsIsRunning = false;
                    venueListRun = false;
                }
            }
        }
    }
}
