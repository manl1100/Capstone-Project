package com.example.manuelsanchez.udacitycapstone;

import java.util.List;

public class Event {

    private String eventDate;
    private String venueName;
    private double latitude;
    private double longitute;
    private List<Artist> artists;

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getHeadLinerName() {
        return artists.get(0).artistName;
    }

    public static class Builder {

        Event event = new Event();

        public Builder venueName(String venueName) {
            event.setVenueName(venueName);
            return this;
        }

        public Builder latitude(double lat) {
            event.setLatitude(lat);
            return this;
        }

        public Builder longitude(double lon) {
            event.setLongitute(lon);
            return this;
        }

        public Builder artists(List<Artist> artists) {
            event.setArtists(artists);
            return this;
        }

        public Builder eventDate(String date) {
            event.setEventDate(date);
            return this;
        }

        public Event build() {
            return event;
        }

    }


}
