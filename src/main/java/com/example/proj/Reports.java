package com.example.proj;

import javafx.scene.layout.HBox;

public class Reports {
    // Variables
    private String no;
    private String reason;
    private String date;
    private String response;
    private HBox buttons;

    // Constructor
    public Reports(String no, String reason, String date, String response, HBox buttons) {
        this.no = no;
        this.reason = reason;
        this.date = date;
        this.response = response;
        this.buttons = buttons;
    }

    // Getters
    public String getNo() {return no;}
    public String getReason() {return reason;}
    public String getDate() {return date;}
    public String getResponse() {return response;}
    public HBox getButtons() {return buttons;}
}
