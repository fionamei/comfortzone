package com.example.comfortzone.flight.utils;

import static com.example.comfortzone.flight.ui.FlightFragment.LOC_IATA;

import android.app.Activity;

import com.example.comfortzone.flight.callbacks.FlightBookingsCallback;
import com.example.comfortzone.flight.data.BookingClient;
import com.example.comfortzone.flight.models.Bookings;
import com.example.comfortzone.models.WeatherData;

import rx.Observable;
import rx.Subscriber;

public class FlightUtil {

    public static Observable getDeepLink(WeatherData cityData, Activity activity) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                BookingClient client = new BookingClient();
                client.getBookingLinks(LOC_IATA, cityData.getIata(), activity, new FlightBookingsCallback() {
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
