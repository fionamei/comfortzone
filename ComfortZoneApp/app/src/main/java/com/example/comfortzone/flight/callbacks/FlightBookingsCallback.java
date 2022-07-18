package com.example.comfortzone.flight.callbacks;

import com.example.comfortzone.flight.models.Bookings.FlightBookings;

public interface FlightBookingsCallback {
    void onGetFlightBooking(FlightBookings flightBookings);
}
