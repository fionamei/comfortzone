package com.example.comfortzone.models;

public class City {

    private int id;
    private String name;
    private String state;
    private Coordinates coord;

    public class Coordinates {
        private long lat;
        private long lon;

        public long getLat() {
            return this.lat;
        }

        public long getLon() {
            return this.lon;
        }
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getState() {
        return this.state;
    }

    public Coordinates getCoord() {
        return this.coord;
    }
}
