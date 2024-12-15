package com.final_project_ticket_box.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
    private String City;
    private String Region;
    private String Address;

    // Default constructor
    public Location() {
        this.City = null;
        this.Region = null;
        this.Address = null;
    }

    // Constructor for Parcel
    protected Location(Parcel in) {
        City = in.readString();
        Region = in.readString();
        Address = in.readString();
    }

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(City);
        parcel.writeString(Region);
        parcel.writeString(Address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Creator object
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    // Getters and Setters
    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
