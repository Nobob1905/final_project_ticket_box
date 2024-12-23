package com.final_project_ticket_box.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ticket implements Parcelable {
    private String eventName;
    private String date;
    private String paymentMethod;
    private String selectedSeats;
    private String ticketCode;
    private double totalPrice;

    public Ticket() { }

    protected Ticket(Parcel in) {
        eventName = in.readString();
        date = in.readString();
        paymentMethod = in.readString();
        selectedSeats = in.readString();
        ticketCode = in.readString();
        totalPrice = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(date);
        dest.writeString(paymentMethod);
        dest.writeString(selectedSeats);
        dest.writeString(ticketCode);
        dest.writeDouble(totalPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "eventName='" + eventName + '\'' +
                ", date='" + date + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", selectedSeats='" + selectedSeats + '\'' +
                ", ticketCode='" + ticketCode + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
