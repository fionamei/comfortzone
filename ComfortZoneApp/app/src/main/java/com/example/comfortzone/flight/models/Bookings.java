package com.example.comfortzone.flight.models;

import com.google.gson.annotations.SerializedName;

public class Bookings {

    @SerializedName("data")
    private FlightBookings[] bookings;

    public static class FlightBookings {
        @SerializedName("deep_link")
        public String deepLink;
        public int price;

        public String getDeepLink() {
            return this.deepLink;
        }

        public int getPrice() {
            return this.price;
        }
    }

    public FlightBookings[] getBookings() {
        return bookings;
    }

}
