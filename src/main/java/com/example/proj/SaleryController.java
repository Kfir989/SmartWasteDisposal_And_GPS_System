package com.example.proj;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SaleryController {

    @FXML
    private Label daysworked;

    @FXML
    private Label hoursworked;

    @FXML
    private Label month;

    @FXML
    private Label payment;

    @FXML
    private Label serialnum;

    @FXML
    private Label totalhours;

    public void setCarddata(int Daysworked, int Hoursworked, String Month, String Payment, int Serialnum, int Totalhours ){
        daysworked.setText("" + Daysworked);
        hoursworked.setText("" + Hoursworked);
        month.setText(Month);
        payment.setText(Payment);
        serialnum.setText("" + Serialnum);
        totalhours.setText("" + Totalhours);
    }

}
