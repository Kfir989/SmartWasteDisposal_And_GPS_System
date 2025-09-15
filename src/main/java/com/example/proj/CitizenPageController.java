package com.example.proj;

import com.example.proj.DB.Db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.ResultSet;

public class CitizenPageController extends UserControllers {
    // Variables
    @FXML
    private TableView<Reports> myreports;

    @FXML
    private TableColumn<Reports, String> Datecolumn;

    @FXML
    private TableColumn<Reports, String> Numcolumn;

    @FXML
    private TableColumn<Reports, String> Reasoncolumn;

    @FXML
    private TableColumn<Reports, Void> optionscolumn;

    @FXML
    private TableColumn<Reports, String> Responsecolumn;

    @FXML
    private Label Citizenname;

    @FXML
    private AnchorPane dashboard;

    @FXML
    private AnchorPane Myreports;

    @FXML
    private Button edashbored_btn;

    @FXML
    private AnchorPane report;

    @FXML
    private Button report_btn;

    @FXML
    private Button myreport_btn;

    @FXML
    private TextField fbinid;

    @FXML
    private DatePicker fdate;

    @FXML
    private TextField fdescription;

    @FXML
    private TextField firstname;

    @FXML
    private TextField freason;

    @FXML
    private TextField lastname;

    @FXML
    private Label crresponded;

    @FXML
    private Label crtotal;

    @FXML
    private Label crwaiting;

    @FXML
    private Label responsedescription;

    @FXML
    private Label responsetitle;

    @FXML
    private AnchorPane ticketresponsepane;

    private final Db db = new Db();

    // Check that all required fields in the citizen's report have been filled out.
    public boolean formValidation(){
        String correctstyle = "-fx-border-color:black; -fx-text-inner-color: black;";
        String wrongstyle = "-fx-border-color:red; -fx-text-inner-color: red;";
        boolean validationflag = true;

        if (firstname.getText().isEmpty()){
            firstname.setStyle(wrongstyle);
            validationflag = false;
        }
        else firstname.setStyle(correctstyle);
        if (lastname.getText().isEmpty()){
            lastname.setStyle(wrongstyle);
            validationflag = false;
        }
        else lastname.setStyle(correctstyle);
        if (fbinid.getText().isEmpty()){
            fbinid.setStyle(wrongstyle);
            validationflag = false;
        }
        else fbinid.setStyle(correctstyle);
        if (fdescription.getText().isEmpty()){
            fdescription.setStyle(wrongstyle);
            validationflag = false;
        }
        else fdescription.setStyle(correctstyle);
        if (freason.getText().isEmpty()){
            freason.setStyle(wrongstyle);
            validationflag = false;
        }
        else freason.setStyle(correctstyle);
        if (fdate.getValue() == null){
            fdate.setStyle(wrongstyle);
            validationflag = false;
        }
        else fdate.setStyle(correctstyle);

        return validationflag;
    }

    // Clear the report form after successful submission or cancellation.
    public void makeformemepty(){
        firstname.setText("");
        lastname.setText("");
        freason.setText("");
        fbinid.setText("");
        fdate.setValue(null);
        fdescription.setText("");
    }

    // Clear the report form after successful submission or cancellation.
    public void submitTicket(ActionEvent event){
        if(formValidation()){
            try{
                String query = "INSERT INTO citizenreports (firstname, lastname, reason, binid, date, description, username, response, ID, respond) VALUES ('" + firstname.getText() + "', '" + lastname.getText() + "','" + freason.getText() + "','" + fbinid.getText() + "', '" + fdate.getValue().toString() + "', '" + fdescription.getText() + "','" + Citizenname.getText() + "', '"+"Not Responded"+"','" + ( 0 + getDate()) + "','"+""+"')";
                db.getupdate(query);
                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setTitle("Success");
                message.setContentText("Ticket has been submitted.");
                message.show();
                makeformemepty();
                Setusersession(Citizenname.getText());
                report.setVisible(false);
                Myreports.setVisible(true);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    // Load the current user's personal data and reports.
    public void Setusersession(String user){
        // User title setup
        Citizenname.setText(user);
        // Loads the reports from the database by username.
        try{

            String query = "SELECT * FROM citizenreports WHERE username = '"+user +"'";
            String buttonstyle = "-fx-background-color: transparent; -fx-border-radius: 30px;-fx-text-fill: white;-fx-border-width: 1;-fx-border-color: white;";
            ResultSet rs = db.getdata(query);
            int count = 1;
            int tickets = 0, responded = 0;
            ObservableList<Reports> dataList = FXCollections.observableArrayList();

            while (rs.next()){
                tickets++;
                if (rs.getString("response").equals("Responded")) responded++;
                // Creating buttons and setting style and functionality:
                int id = rs.getInt("ID");
                String response = rs.getString("respond");
                String reason = rs.getString("reason");
                Button delbutton = new Button("Delete");
                delbutton.setStyle(buttonstyle);
                delbutton.setOnMouseClicked(e ->{
                    db.getupdate("DELETE FROM citizenreports WHERE ID = " + id);
                    Setusersession(Citizenname.getText());
                });
                Button viewbutton = new Button("View Response");
                viewbutton.setStyle(buttonstyle);
                viewbutton.setOnMouseClicked(e ->{
                    db.getdata("SELECT * FROM citizenreports WHERE ID = " + id);
                    responsedescription.setText(response);
                    responsetitle.setText(reason);
                    ticketresponsepane.setVisible(true);
                });
                HBox buttons = new HBox(10, viewbutton, delbutton);
                buttons.setAlignment(Pos.CENTER);
                // setup for row in table as observablearray object
                dataList.add(new Reports("" + count++,rs.getString("reason"), rs.getString("date"),rs.getString("response"), buttons));
            }
            //dashboard setup
            crtotal.setText("" + tickets);
            crresponded.setText("" + responded);
            crwaiting.setText("" + (tickets - responded));

            //set-up columns
            Numcolumn.setCellValueFactory(new PropertyValueFactory<>("no"));
            Reasoncolumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
            Datecolumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            Responsecolumn.setCellValueFactory(new PropertyValueFactory<>("response"));
            optionscolumn.setCellValueFactory(new PropertyValueFactory<>("buttons"));
            //insert values
            myreports.setItems(dataList);
        }catch (Exception e){e.printStackTrace();}
    }

    // Close the box that displays the system's response to the report.
    public void hiderespondwindow(){
        ticketresponsepane.setVisible(false);
    }
    // Tabs switch
    public void switchform(ActionEvent e){

        if (e.getSource() == edashbored_btn){
            dashboard.setVisible(true);
            report.setVisible(false);
            Myreports.setVisible(false);
        }
        else if (e.getSource() == report_btn){
            dashboard.setVisible(false);
            report.setVisible(true);
            Myreports.setVisible(false);
        }
        else if (e.getSource() == myreport_btn){
            dashboard.setVisible(false);
            report.setVisible(false);
            Myreports.setVisible(true);
        }
    }

    // Log out of the system (return to the landing page).
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
