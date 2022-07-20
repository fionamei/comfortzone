package com.example.comfortzone.flight.utils;

import com.example.comfortzone.flight.callbacks.FlightBookingsCallback;
import com.example.comfortzone.flight.data.BookingClient;
import com.example.comfortzone.flight.models.Bookings;
import com.example.comfortzone.models.WeatherData;

import rx.Observable;
import rx.Subscriber;

public class FlightUtil {

    public static Observable getDeepLink(WeatherData cityData, String iata) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                BookingClient client = new BookingClient();
                client.getBookingLinks(iata, cityData.getIata(), new FlightBookingsCallback() {
                    @Override
                    public void onGetFlightBooking(Bookings.FlightBookings flightBookings) {
                        subscriber.onNext(flightBookings.getDeepLink());
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

}
