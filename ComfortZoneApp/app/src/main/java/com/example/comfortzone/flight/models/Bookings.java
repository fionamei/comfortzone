package com.example.comfortzone.flight.models;

import com.google.gson.annotations.SerializedName;

public class Bookings {

    @SerializedName("data")
    private FlightBooking[] booking;

    public static class FlightBooking {
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

    public FlightBooking[] getBooking() {
        return booking;
    }

}
