package com.final_project_ticket_box.Models;

public class Seat {
    private SeatStatus status;
    private String name;
    private boolean isSelected; // Track whether the seat is selected or not

    public Seat(SeatStatus status, String name) {
        this.status = status;
        this.name = name;
        this.isSelected = false; // Default to not selected
    }

    // Getter và Setter cho status
    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    // Getter và Setter cho name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected; // Set the selection status of the seat
    }

    // Enum SeatStatus
    public enum SeatStatus {
        AVAILABLE,
        SELECTED,
        UNAVAILABLE,
        VIP
    }
}
