package com.example.proj;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CitizenPageController {

    @FXML
    private AnchorPane dashboard;

    @FXML
    private Button edashbored_btn;

    @FXML
    private AnchorPane report;

    @FXML
    private Button report_btn;

    public void exit(){System.exit(0);}

    public void switchform(ActionEvent e){

        if (e.getSource() == edashbored_btn){
            dashboard.setVisible(true);
            report.setVisible(false);
        }
        else if (e.getSource() == report_btn){
            dashboard.setVisible(false);
            report.setVisible(true);
        }
    }

    public void logout(ActionEvent event) {
        try {
            report_btn.getScene().getWindow().hide();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LandingPage-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
