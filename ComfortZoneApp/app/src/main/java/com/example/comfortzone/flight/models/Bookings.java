package com.example.comfortzone.flight.models;

import com.google.gson.annotations.SerializedName;

public class Bookings {

    @SerializedName("data")
    private FlightBookings[] bookings;

    public static class FlightBookings {
        public String deep_link;

        public String getDeep_link() {
            return this.deep_link;
        }
    }

    public FlightBookings[] getBookings() {
        return bookings;
    }

}
