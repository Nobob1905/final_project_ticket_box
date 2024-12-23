package com.final_project_ticket_box.Models;

import java.util.Date;

public class Order {
    private String ticketCode;
    private String eventName;
    private String customerName;
    private String customerEmail;
    private String selectedSeats;  // This will store seat positions as a string
    private String paymentMethod;
    private Date date;
    private double totalPrice;

    // Default constructor required for Firebase
    public Order() {}



    // Constructor
    public Order(String ticketCode, String eventName, String customerName, String customerEmail,
                 String selectedSeats, String paymentMethod, double totalPrice, Date date) {
        this.ticketCode = ticketCode;
        this.eventName = eventName;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.selectedSeats = selectedSeats;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.date = date;
    }

    // Getters and Setters
    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getDate() {
        return date;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
