package com.example.manuelsanchez.udacitycapstone;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {

    String artistName;

    public Artist(String artistName) {
        this.artistName = artistName;
    }

    protected Artist(Parcel in) {
        artistName = in.readString();
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
}
