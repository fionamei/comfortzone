package com.example.comfortzone.flight.models;

import com.google.gson.annotations.SerializedName;

public class Airport {

    @SerializedName("data")
    private Iata[] IataList;

    public class Iata {
        private String iataCode;

        public String getIataCode() {
            return this.iataCode;
        }
    }

    public Iata[] getIataList() {
        return this.IataList;
    };

}
