package com.final_project_ticket_box.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Event implements Parcelable {
    private String Title;
    private String Description;
    private String Poster;
    private String Time;
    private String Trailer;
    private double Imdb;
    private int Year;
    private double Price;
    private ArrayList<String> Genre;
    private Location Location;
    private String Date;

    // Default constructor
    public Event() {
        this.Title = null;
        this.Description = null;
        this.Poster = null;
        this.Time = null;
        this.Trailer = null;
        this.Imdb = 0.0;
        this.Year = 0;
        this.Price = 0.0;
        this.Genre = new ArrayList<>();
        this.Location = new Location();
        this.Date = null;
    }

    // Constructor for Parcel
    protected Event(Parcel in) {
        Title = in.readString();
        Description = in.readString();
        Poster = in.readString();
        Time = in.readString();
        Trailer = in.readString();
        Imdb = in.readDouble();
        Year = in.readInt();
        Price = in.readDouble();
        Genre = in.createStringArrayList();
        Location = in.readParcelable(Location.class.getClassLoader());
        Date = in.readString();
    }

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(Title);
        parcel.writeString(Description);
        parcel.writeString(Poster);
        parcel.writeString(Time);
        parcel.writeString(Trailer);
        parcel.writeDouble(Imdb);
        parcel.writeInt(Year);
        parcel.writeDouble(Price);
        parcel.writeStringList(Genre);
        parcel.writeParcelable(Location, flags);
        parcel.writeString(Date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Creator object
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    // Getters and Setters
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        Poster = poster;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTrailer() {
        return Trailer;
    }

    public void setTrailer(String trailer) {
        Trailer = trailer;
    }

    public double getImdb() {
        return Imdb;
    }

    public void setImdb(double imdb) {
        Imdb = imdb;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public ArrayList<String> getGenre() {
        return Genre;
    }

    public void setGenre(ArrayList<String> genre) {
        Genre = genre;
    }

    public Location getLocation() {
        return Location;
    }

    public void setLocation(Location location) {
        Location = location;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
