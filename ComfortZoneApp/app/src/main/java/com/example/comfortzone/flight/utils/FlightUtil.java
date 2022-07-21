package com.example.comfortzone.flight.utils;
import android.app.Activity;

import com.example.comfortzone.flight.callbacks.FlightBookingsCallback;
import com.example.comfortzone.flight.data.BookingClient;
import com.example.comfortzone.flight.models.Bookings;
import com.example.comfortzone.models.WeatherData;

import rx.Observable;
import rx.Subscriber;

public class FlightUtil {

    public static Observable getDeepLink(WeatherData cityData, String iata, Activity activity) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                BookingClient client = new BookingClient();
                client.getBookingLinks(iata, cityData.getIata(), activity, new FlightBookingsCallback() {
                    @Override
                    public void onGetFlightBooking(Bookings.FlightBooking flightBooking) {
                        subscriber.onNext(flightBooking.getDeepLink());
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

}
