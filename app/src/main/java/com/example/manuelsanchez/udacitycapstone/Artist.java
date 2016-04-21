package com.example.manuelsanchez.udacitycapstone;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {

    String artistName;
    int eventCount;
    String imageUrl;


    public Artist() {
    }

    public Artist(String artistName) {
        this.artistName = artistName;
    }

    protected Artist(Parcel in) {
        artistName = in.readString();
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
    }

    public static class Builder {
        private Artist artist = new Artist();

        public Builder withEventCount(int eventCount) {
            artist.setEventCount(eventCount);
            return this;
        }

        public Builder withArtistName(String name) {
            artist.setArtistName(name);
            return this;
        }

        public Builder withUrl(String url) {
            artist.setImageUrl(url);
            return this;
        }

        public Artist build() {
            return artist;
        }
    }
}
