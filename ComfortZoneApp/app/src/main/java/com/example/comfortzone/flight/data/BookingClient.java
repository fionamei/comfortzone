package com.example.comfortzone.flight.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.comfortzone.BuildConfig;
import com.example.comfortzone.flight.callbacks.FlightBookingsCallback;
import com.example.comfortzone.flight.models.Bookings;
import com.example.comfortzone.flight.models.Bookings.FlightBookings;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookingClient extends OkHttpClient {

    public static final String TAG = "bookingclient";
    public static final String BOOKING_BASE_URL = "https://tequila-api.kiwi.com/v2/search";
    public static final String KIWI_API_KEY = BuildConfig.KIWI_API_KEY;
    public static final String KEY_API_KEY = "apikey";
    public static final String KEY_FLY_FROM = "fly_from";
    public static final String KEY_FLY_TO = "fly_to";
    public static final String KEY_DATE_FROM = "date_from";
    public static final String KEY_CURR = "curr";
    public static final String CURR = "USD";

    private String getBookingUrl(String flyFrom, String flyTo, String dateFrom) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BOOKING_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(KEY_FLY_FROM, flyFrom);
        urlBuilder.addQueryParameter(KEY_FLY_TO, flyTo);
        urlBuilder.addQueryParameter(KEY_DATE_FROM, dateFrom);
        urlBuilder.addQueryParameter(KEY_CURR, CURR);
        return urlBuilder.build().toString();
    }

    public void getBookingLinks(String flyFrom, String flyTo, String dateFrom, FlightBookingsCallback callback) {
        String url = getBookingUrl(flyFrom, flyTo, dateFrom);
        final Request request = new Request.Builder()
                .url(url)
                .header(KEY_API_KEY, KIWI_API_KEY)
                .build();
        newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "unable to get booking links " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    Gson gson = new Gson();
                    Bookings bookings = gson.fromJson(data, Bookings.class);
                    FlightBookings[] flightBookings = bookings.getBookings();
                    callback.onFlightBookingList(flightBookings);
                } catch (IOException e) {
                    Log.e(TAG, "could not get body" + e);
                }
            }
        });
    }

}
