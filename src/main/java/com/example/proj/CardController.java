package com.example.proj;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CardController {

    @FXML
    private Label carddate;

    @FXML
    private Label cardday;

    @FXML
    private Label cardend;

    @FXML
    private Label cardsection;

    @FXML
    private Label cardstart;

    public void setCarddata(String date, String day, String start, String end, String Section ){
        carddate.setText(date);
        cardday.setText(day);
        cardend.setText(end);
        cardsection.setText(Section);
        cardstart.setText(start);
    }
}
