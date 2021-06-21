package com.techelevator.view;

import com.techelevator.model.Reservation;
import com.techelevator.model.Space;
import com.techelevator.model.Venue;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private Scanner scanner;

    public Menu() {
        scanner = new Scanner(System.in);
    }

    public static String currencyFormat(BigDecimal n) {
        return NumberFormat.getCurrencyInstance().format(n);
    }

    public void printMainMenu() {
        System.out.println("What would you like to do?\n" +
                "    1) List Venues\n" +
                "    Q) Quit");
    }

    public void printViewVenues(List<Venue> getVenueList) {
        int count = 0;
        System.out.println("Which venue would you like to view?");
        for (Venue venue : getVenueList) {
            count += 1;
            System.out.printf("%4s%s\n", count + ") " , venue.getName());
        }
        System.out.printf("%4s%s\n","R) ", "Return to Previous Screen");
    }

    public void printVenueDetails(Venue venue) {
        System.out.println(venue.getName());
        System.out.println("Location: " + venue.getCity());
        System.out.println("Categories: " + venue.getCategory());
        System.out.println();
        System.out.println("The perfect office space for those trying to get that next big project done. " +
                "Plenty of office supplies available and a lightning fast wifi network.");
        System.out.println();
        System.out.println("What would you like to do next?\n" +
                "    1) View Spaces\n" +
                "    R) Return to Previous Screen");
    }

    public void printListVenueSpaces(List<Space> getSpaceList, Venue venue) {
        int count = 0;
        String[] MONTHS = new String[]{"", "Jan.", "Feb.", "Mar.", "Apr.", "May.", "Jun.", "Jul.", "Aug.",
                "Sep.", "Oct.", "Nov.", "Dec."};
        System.out.println(venue.getName());
        System.out.printf("\n%-5s %-35s %-6s %-6s %-11s %s\n", "", "Name", "Open", "Close", "Daily Rate", "Max. Occupancy");
        for (Space space : getSpaceList) {
            count += 1;
            System.out.printf("%-5s %-35s %-6s %-6s %-11s %s\n", count, space.getName(), MONTHS[space.getOpen_from()],
                    MONTHS[space.getOpen_to()], currencyFormat(space.getDaily_rate()), space.getMax_occupancy());
        }
        System.out.println("\nWhat would you like to do next?\n" +
                "    1) Reserve a Space\n" +
                "    R) Return to Previous Screen");
    }

    public String askUserDate() {
        System.out.println("When do you need the space?(yyyy-MM-dd)");
        return getUserRespond();
    }

    public String askUserDays() {
        System.out.println("How many days will you need the space?");
        return getUserRespond();
    }

    public String askUserAttendees() {
        System.out.println("How many people will be in attendance?");
        return getUserRespond();

    }

    public void printAvailableSpaces(List<Space> getAvailableSpace, int getUserDays) {
        System.out.println("The following spaces are available based on your needs: ");
        System.out.printf("%-5s %-35s %-11s %-11s %-12s %s\n", "Space", "Name", "Daily Rate", "Max Occup.", "Accessible?", "Total Cost");
        for (Space space : getAvailableSpace) {
            BigDecimal totalCost = space.getDaily_rate().multiply(BigDecimal.valueOf(getUserDays));
            String isAccessible = space.isIs_accessible() ? "Yes" : "No";

            System.out.printf("%-5s %-35s %-11s %-11s %-12s %s\n", space.getId(), space.getName(), currencyFormat(space.getDaily_rate()),
                    space.getMax_occupancy(), isAccessible, currencyFormat(totalCost));
        }
    }

    public void askUserReserveChoice() {
        System.out.println("\nWhich space would you like to reserve (enter 0 to cancel)?");
    }

    public void askUserWhoSpaceFor() {
        System.out.println("\nWho is this reservation for?");
    }

    public void getConfirmationReport(Space space, Reservation reservation, Venue venue) {
        BigDecimal totalCost = space.getDaily_rate().multiply(BigDecimal.valueOf(reservation.getNumber_of_attendees()));
        System.out.println("\nThanks for submitting your reservation! The details for your event are listed below: \n");
        System.out.printf("%16s %s\n", "Confirmation #:", reservation.getId());
        System.out.printf("%16s %s\n", "Venue:", venue.getName());
        System.out.printf("%16s %s\n", "Space:", space.getName());
        System.out.printf("%16s %s\n", "Reserved For:", reservation.getReserved_for());
        System.out.printf("%16s %s\n", "Attendees:", reservation.getNumber_of_attendees());
        System.out.printf("%16s %s\n", "Arrival Date:", reservation.getStart_date());
        System.out.printf("%16s %s\n", "Depart Date:", reservation.getEnd_date());
        System.out.printf("%16s %s\n", "Total Cost:", currencyFormat(totalCost));
    }

    public String getUserRespond() {
        return scanner.nextLine();
    }

    public void printErrorMessage(String message) {
        System.out.println(message + "\n");
    }

}
