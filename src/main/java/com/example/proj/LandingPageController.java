package com.example.proj;

import com.example.proj.DB.Db;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LandingPageController extends Controllers {
    // var init:

    @FXML
    private RadioButton rolebtn1;

    @FXML
    private DatePicker datepicker;

    @FXML
    private TextField email1;

    @FXML
    private Hyperlink forgotpass_btn;

    @FXML
    private Hyperlink haveaccount_btn;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField password1;

    @FXML
    private Button signup_btn;

    @FXML
    private TextField username;

    @FXML
    private TextField username1;

    @FXML
    private AnchorPane loginpage;

    @FXML
    private AnchorPane signuppage;
    // DB init:
    private final Db db = new Db();

    // account releted functions:
    public boolean formvalidation(ActionEvent event){

        String correctstyle = "-fx-border-color:white; -fx-text-inner-color: white;";
        String wrongstyle = "-fx-border-color:red; -fx-text-inner-color: red;";
        boolean validationflag = true;
        // username - validation:
        try{
            String query = "SELECT * FROM userdata WHERE username = '"+username1.getText() +"'";
            ResultSet rs = db.getdata(query);
            if (rs.next()){
                username1.setStyle(wrongstyle);
                validationflag = false;
            }
        }
        catch (Exception e){}
        // other param validation:
        if (username1.getText().isEmpty()){
            username1.setStyle(wrongstyle);
            validationflag = false;
        }
        else username1.setStyle(correctstyle);
        if (password1.getText().isEmpty()){
            password1.setStyle(wrongstyle);
            validationflag = false;
        }
        else password1.setStyle(correctstyle);
        if (email1.getText().isEmpty() || !email1.getText().contains("@")){
            email1.setStyle(wrongstyle);
            validationflag = false;
        }
        else email1.setStyle(correctstyle);
        if (datepicker.getValue() == null|| datepicker.getValue().getYear() > 2015){
            datepicker.setStyle(wrongstyle);
            validationflag = false;
        }
        else datepicker.setStyle(correctstyle);
        return validationflag;
    }

    public void signup(ActionEvent event){

        if(formvalidation(event)) {
            try {
                String query = "INSERT INTO userdata (username, password, bday, email, isEMP, EntityID) VALUES ('" + username1.getText() + "', '" + password1.getText() + "','" + datepicker.getValue() + "','" + email1.getText() + "', '" + (rolebtn1.isSelected()?"1":"0") + "', '" + getDate() + "')";
                db.getupdate(query);
                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setTitle("Success");
                message.setContentText("Account has been created successfully");
                message.show();
                signuppage.setVisible(false);
                loginpage.setVisible(true);
            } catch (Exception e) {}
        }
        else{
            Alert message = new Alert(Alert.AlertType.INFORMATION);
            message.setTitle("Fail");
            message.setContentText("One or more of the value entered are invalid.");
            message.show();
        }
    }

    public void login(ActionEvent ae){

        try{
            String query = "SELECT * FROM userdata WHERE username = '"+username.getText() +"' and password = '"+password.getText() +"'";
            ResultSet rs = db.getdata(query);

            if (rs.next()){

                password.getScene().getWindow().hide();
                boolean isEmployee = rs.getBoolean("isEMP");
                String fxml = isEmployee ? "EmployeePage-view.fxml" : "CitizenPage-view.fxml";
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
                Scene scene = new Scene(fxmlLoader.load());

                if (isEmployee){
                    EmployeePageController emp = fxmlLoader.getController();
                    emp.Setusersession(username.getText());
                }
                else{
                    CitizenPageController cpc = fxmlLoader.getController();
                    cpc.Setusersession(username.getText());
                }

                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            }
            else{
                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setTitle("Failed to login");
                message.setContentText("Invalid Login Cardentials");
                message.show();
            }
        }
        catch (Exception e){e.printStackTrace();}
    }
    // switching tabs:
    public void switchform(ActionEvent e){

        if (e.getSource() == haveaccount_btn){
            signuppage.setVisible(false);
            loginpage.setVisible(true);
        }
        else if (e.getSource() == signup_btn){
            signuppage.setVisible(true);
            loginpage.setVisible(false);
        }
    }
}