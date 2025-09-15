package com.example.proj;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CardController {
    // Variables
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

    private EmployeePageController parentController;

    // EmployeePageController Constructor
    public void setParentController(EmployeePageController controller) {
        this.parentController = controller;
    }

    // Variables Constructor
    public void setCarddata(String date, String day, String start, String end, String Section ){
        carddate.setText(date);
        cardday.setText(day);
        cardend.setText(end);
        cardsection.setText(Section);
        cardstart.setText(start);
    }

    // Event click function.
    @FXML
    private void handleCardClick() {
        if (parentController != null) {
            String area = cardsection.getText();
            parentController.showRouteForShift(area);  // שליחה לעיר Qiryat-Gat ול־Area שנבחר
        }
    }
}
