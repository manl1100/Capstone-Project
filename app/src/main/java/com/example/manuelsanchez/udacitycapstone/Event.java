package com.example.manuelsanchez.udacitycapstone;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Event implements Parcelable {

    private String eventId;
    private String eventDate;
    private String venueName;
    private String country;
    private String region;
    private String regionAbbr;
    private String venueCity;
    private String venueAddress;
    private String postalCode;
    private double latitude;
    private double longitute;
    private List<Artist> artists;

    public Event() {

    }

    protected Event(Parcel in) {
        eventId = in.readString();
        eventDate = in.readString();
        venueName = in.readString();
        country = in.readString();
        region = in.readString();
        regionAbbr = in.readString();
        venueCity = in.readString();
        venueAddress = in.readString();
        postalCode = in.readString();
        latitude = in.readDouble();
        longitute = in.readDouble();
        artists = in.createTypedArrayList(Artist.CREATOR);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegionAbbr() {
        return regionAbbr;
    }

    public void setRegionAbbr(String regionAbbr) {
        this.regionAbbr = regionAbbr;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHeadLinerName() {
        return artists.get(0).artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(eventDate);
        dest.writeString(venueName);
        dest.writeString(country);
        dest.writeString(region);
        dest.writeString(regionAbbr);
        dest.writeString(venueCity);
        dest.writeString(venueAddress);
        dest.writeString(postalCode);
        dest.writeDouble(latitude);
        dest.writeDouble(longitute);
        dest.writeTypedList(artists);
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

        public Builder regionAbbr(String region) {
            event.setRegionAbbr(region);
            return this;
        }

        public Builder region(String region) {
            event.setRegion(region);
            return this;
        }

        public Builder address(String address) {
            event.setVenueAddress(address);
            return this;
        }

        public Builder postalCode(String postalCode) {
            event.setPostalCode(postalCode);
            return this;
        }

        public Builder city(String city) {
            event.setVenueCity(city);
            return this;
        }

        public Builder country(String country) {
            event.setCountry(country);
            return this;
        }

        public Builder eventId(String eventId) {
            event.setEventId(eventId);
            return this;
        }

        public Event build() {
            return event;
        }

    }


}
