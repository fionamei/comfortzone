package com.example.comfortzone.flight.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.comfortzone.BuildConfig;
import com.example.comfortzone.flight.callbacks.IataCallback;
import com.example.comfortzone.flight.models.AccessToken;
import com.example.comfortzone.flight.models.Airport;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IataClient extends OkHttpClient {

    public static final String TAG = "IataClient";
    public static final String AMADEUS_IATA_URL = "https://test.api.amadeus.com/v1/reference-data/locations/airports";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_HEADER = "Authorization";
    public static final String KEY_GRANT_TYPE = "grant_type";
    public static final String BODY_GRANT_TYPE = "client_credentials";
    public static final String KEY_CLIENT_ID = "client_id";
    public static final String AMADEUS_API_KEY = BuildConfig.AMADEUS_API_KEY;
    public static final String KEY_CLIENT_SECRET = "client_secret";
    public static final String AMADEUS_API_SECRET = BuildConfig.AMADEUS_SECRET_KEY;
    public static final String AMADEUS_BEARER_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";

    private String getIataURL(String lat, String lon) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(AMADEUS_IATA_URL).newBuilder();
        urlBuilder.addQueryParameter(KEY_LATITUDE, lat);
        urlBuilder.addQueryParameter(KEY_LONGITUDE, lon);
        return urlBuilder.build().toString();
    }

    private void getIataCode(String lat, String lon, IataCallback callback, String bearerToken) {
        String url = getIataURL(lat, lon);
        final Request request = new Request.Builder()
                .url(url)
                .header(KEY_HEADER, String.format("Bearer %s", bearerToken))
                .build();
        newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "failed getting IATA code " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    Gson gson = new Gson();
                    Airport airport = gson.fromJson(data, Airport.class);
                    callback.onGetIata(airport.getIataList()[0].getIataCode());
                } catch (IOException e) {
                    Log.e(TAG, "could not get body" + e);
                }
            }
        });
    }

    public void getIataResponse(String lat, String lon, IataCallback callback) {
        Request bearerTokenRequest = getBearerTokenRequest();
        newCall(bearerTokenRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    Gson gson = new Gson();
                    AccessToken accessToken = gson.fromJson(data, AccessToken.class);
                    String accessTokenString = accessToken.getAccessToken();
                    getIataCode(lat, lon, callback, accessTokenString);
                } catch (IOException e) {
                    Log.e(TAG, "failed to get bearer token" + e);
                }
            }
        });
    }

    @NonNull
    private Request getBearerTokenRequest() {
        RequestBody body = new FormBody.Builder()
                .add(KEY_CLIENT_ID, AMADEUS_API_KEY)
                .add(KEY_CLIENT_SECRET, AMADEUS_API_SECRET)
                .add(KEY_GRANT_TYPE, BODY_GRANT_TYPE)
                .build();
        return new Request.Builder()
                .url(AMADEUS_BEARER_URL)
                .post(body)
                .build();
    }
}
