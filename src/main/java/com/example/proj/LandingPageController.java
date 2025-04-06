package com.example.proj;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LandingPageController {


    @FXML
    private RadioButton rolebtn1;

    @FXML
    private RadioButton rolebtn2;

    @FXML
    private ToggleGroup roles;

    @FXML
    private DatePicker datepicker;

    @FXML
    private TextField email1;

    @FXML
    private Hyperlink forgotpass_btn;

    @FXML
    private Hyperlink haveaccount_btn;

    @FXML
    private Button login_btn;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField password1;

    @FXML
    private Button signup_btn;

    @FXML
    private Button signupform_btn;

    @FXML
    private TextField username;

    @FXML
    private TextField username1;

    @FXML
    private AnchorPane loginpage;

    @FXML
    private AnchorPane signuppage;

    @FXML
    public int getroles(){
        if (rolebtn1.isSelected()){ return 1;}
        return 0;
    }

    private Connection connect;

    public String getDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return LocalDateTime.now().format(formatter);
    }
    public void exit(){System.exit(0);}

    public Connection Establish_Connect(){

        try{connect = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/myuserdb", "root", "");return connect;}
        catch (Exception e){e.printStackTrace();}
        return null;
    }
    public boolean formvalidation(ActionEvent event){

        try{
            connect = Establish_Connect();
            String query = "SELECT * FROM userdata WHERE username = '"+username1.getText() +"'";
            Statement statement = connect.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                username1.setStyle("-fx-border-color:red; -fx-text-inner-color: red;");
                return false;
            }
        }
        catch (Exception e){}

        if (username1.getText().length() == 0){
            username1.setStyle("-fx-border-color:red; -fx-text-inner-color: red;");
            return false;
        }
        else username1.setStyle("-fx-border-color:white; -fx-text-inner-color: white;");
        if (password1.getText().length() == 0){
            password1.setStyle("-fx-border-color:red; -fx-text-inner-color: red;");
            return false;
        }
        else password1.setStyle("-fx-border-color:white; -fx-text-inner-color: white;");
        if (email1.getText().length() == 0 || !email1.getText().contains("@")){
            email1.setStyle("-fx-border-color:red; -fx-text-inner-color: red;");
            return false;
        }
        else email1.setStyle("-fx-border-color:white; -fx-text-inner-color: white;");
        if (datepicker.getValue() == null|| datepicker.getValue().getYear() > 2015){
            datepicker.setStyle("-fx-border-color:red; -fx-text-inner-color: red;");
            return false;
        }
        else datepicker.setStyle("-fx-border-color:white; -fx-text-inner-color: white;");

        return true;
    }

    public void signup(ActionEvent event){
        if(formvalidation(event)) {
            try {
                connect = Establish_Connect();
                String query = "INSERT INTO userdata (username, password, bday, email, isEMP, EntityID) VALUES ('" + username1.getText() + "', '" + password1.getText() + "','" + datepicker.getValue() + "','" + email1.getText() + "', '" + getroles() + "', '" + getDate() + "')";
                Statement statement = connect.createStatement();
                statement.execute(query);
                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setTitle("Success");
                message.setContentText("Account has been created successfully");
                message.show();
                signuppage.setVisible(false);
                loginpage.setVisible(true);
            } catch (Exception e) {
                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setTitle("Fail");
                message.setContentText("One or more of the value entered are invalid.");
                message.show();
            }
        }
    }

    public void login(ActionEvent ae){

        try{
            connect = Establish_Connect();
            String query = "SELECT * FROM userdata WHERE username = '"+username.getText() +"' and password = '"+password.getText() +"'";
            Statement statement = connect.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){

                if (rs.getBoolean("isEMP")){

                    password.getScene().getWindow().hide();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EmployeePage-view.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.show();
                }
                else{
                    password.getScene().getWindow().hide();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CitizenPage-view.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.show();
                }
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