package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final int SEC_IN_MINUTE = 60;
    private static final int SEC_IN_HOUR = 3_600;
    private static final int SEC_IN_DAY = 86_400;
    private static final int HOURS_IN_DAY = 24;

    private static final String REQUIRED_DEPARTURE_CITY = "Владивосток";
    private static final String REQUIRED_ARRIVAL_CITY = "Тель-Авив";

    private static ListTickets listTickets;
    private static ObjectMapper objectMapper;

    public static void main(String[] args) {
        System.out.println("Statistics of flights between \"" + REQUIRED_DEPARTURE_CITY + "\" and \"" + REQUIRED_ARRIVAL_CITY + "\":\n" +
                            "------------------------------------------------------------");
        String filePath = null;
        if (args.length > 0) {
            filePath = args[0];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Input file full path:");
            filePath = sc.nextLine();
            sc.close();
        }
        listTickets = new ListTickets();
        objectMapper = new ObjectMapper().findAndRegisterModules();
        JsonFileToPojo(filePath);

        if (listTickets != null && !listTickets.getTickets().isEmpty()) {
            List<Long> prices = new ArrayList<>();
            Map<String, LocalDateTime> minFlightTimeMap = new HashMap<>();
            SelectRequiredFlights(minFlightTimeMap, prices);

            Collections.sort(prices);
            double averagePrice = prices.stream().mapToLong(Long::longValue).average().getAsDouble();
            int size = prices.size();
            double medianPrice = 0.0;
            if (size == 1) {
                medianPrice = prices.getFirst();
            } else {
                if (size % 2 == 0) {
                    medianPrice = (prices.get(size / 2) + prices.get(size / 2 - 1)) / 2.0;
                } else {
                    medianPrice = prices.get(size / 2);
                }
            }
            System.out.printf("Average price : %.2f\n", averagePrice);
            System.out.printf("Median price : %.2f\n", medianPrice);
            System.out.printf("Average price - Median price = %.2f\n", averagePrice - medianPrice);
            System.out.println("Minimum flight time:");
            for (Map.Entry<String, LocalDateTime> entry : minFlightTimeMap.entrySet()) {
                LocalTime flightTime = entry.getValue().toLocalTime();
                flightTime.plusHours((entry.getValue().getDayOfMonth() - 1) * HOURS_IN_DAY);
                System.out.println(entry.getKey() + " - " + flightTime.toString());
            }
            System.exit(0);
        }
    }

    private static void SelectRequiredFlights(Map<String, LocalDateTime> minFlightTimeMap, List<Long> prices) {
        for (Ticket curTicket : listTickets.getTickets()) {
            if (curTicket.getOriginName().equals(REQUIRED_DEPARTURE_CITY) && curTicket.getDestinationName().equals(REQUIRED_ARRIVAL_CITY) ||
                    curTicket.getOriginName().equals(REQUIRED_ARRIVAL_CITY) && curTicket.getDestinationName().equals(REQUIRED_DEPARTURE_CITY)) {
                prices.add(curTicket.getPrice());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
                String strDepartureDateTime = curTicket.getDepartureDate() + " ";
                if (curTicket.getDepartureTime().charAt(1) == ':') strDepartureDateTime += "0";
                strDepartureDateTime += curTicket.getDepartureTime();
                String strArrivalDateTime = curTicket.getArrivalDate() + " " + curTicket.getArrivalTime();
                LocalDateTime departureDateTime = LocalDateTime.parse(strDepartureDateTime, formatter);
                LocalDateTime arrivalDateTime = LocalDateTime.parse(strArrivalDateTime, formatter);
                LocalDateTime curFlightDeltaDateTime = GetDeltaDateTime(arrivalDateTime, departureDateTime);

                String carrier = curTicket.getCarrier();
                if (minFlightTimeMap.containsKey(carrier)) {
                    if (curFlightDeltaDateTime.isBefore(minFlightTimeMap.get(carrier)))
                        minFlightTimeMap.replace(carrier, curFlightDeltaDateTime);
                } else {
                    minFlightTimeMap.put(carrier, curFlightDeltaDateTime);
                }
            }
        }
    }

    private static LocalDateTime GetDeltaDateTime(LocalDateTime arrivalDateTime, LocalDateTime departureDateTime) {
        Period flightPeriod = Period.between(departureDateTime.toLocalDate(), arrivalDateTime.toLocalDate());
        Duration flightDuration = Duration.between(departureDateTime.toLocalTime(), arrivalDateTime.toLocalTime());

        long seconds = flightDuration.getSeconds();
        if (seconds < 0) {
            flightPeriod = flightPeriod.minusDays(1);
            seconds = SEC_IN_DAY + seconds;
        }
        int hours = (int)(seconds / SEC_IN_HOUR);
        seconds -= ((long)hours * SEC_IN_HOUR);
        int minutes = (int)(seconds / SEC_IN_MINUTE);
        seconds -= ((long)minutes * SEC_IN_MINUTE);
        return LocalDateTime.of(flightPeriod.getYears(), flightPeriod.getMonths() + 1,
                        flightPeriod.getDays() + 1, hours, minutes, (int)seconds);
    }

    private static void JsonFileToPojo(String filePath) {
        File file = new File(filePath);
        boolean isExistFile = false;
        if (!filePath.isBlank()) isExistFile = file.exists();
        if (!isExistFile) {
            System.out.println("The file doesn't exist : " + filePath);
            System.exit(1);
        }
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            listTickets = objectMapper.readValue(file, ListTickets.class);
        } catch (Exception e) {
            System.out.println("An error while reading file : " + e.getMessage());
            System.exit(2);
        }
    }
}