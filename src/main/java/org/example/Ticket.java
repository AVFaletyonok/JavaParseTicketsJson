package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticket {
    private String origin;
    @JsonSetter("origin_name")
    private String originName;
    private String destination;
    @JsonSetter("destination_name")
    private String destinationName;

    @JsonSetter("departure_date")
    private String departureDate;
    @JsonSetter("departure_time")
    private String departureTime;
    @JsonSetter("arrival_date")
    private String arrivalDate;
    @JsonSetter("arrival_time")
    private String arrivalTime;

    private String carrier;
    private int stops;
    private long price;
}
