package com.final_project_ticket_box.Models;

public class SliderItems {
    private String video;

    // Default constructor
    public SliderItems() {
        this.video = "";
    }

    // Constructor with parameter
    public SliderItems(String video) {
        this.video = video;
    }

    // Getter
    public String getVideo() {
        return video;
    }

    // Setter
    public void setVideo(String video) {
        this.video = video;
    }
}
