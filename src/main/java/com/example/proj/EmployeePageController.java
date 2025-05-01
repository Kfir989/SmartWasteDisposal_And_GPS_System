package com.example.proj;

import com.example.proj.DB.Db;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.scene.chart.PieChart;
import javafx.collections.ObservableList;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class EmployeePageController extends UserControllers implements Initializable {

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private PieChart piechart;

    @FXML
    private AnchorPane dashboard;

    @FXML
    private Button home_btn;

    @FXML
    private AnchorPane map;

    @FXML
    private Button map_btn;

    @FXML
    private AnchorPane salery;

    @FXML
    private Button salery_btn;

    @FXML
    private AnchorPane schdule;

    @FXML
    private Button schedule_btn;

    @FXML
    private Label empName;

    @FXML
    private Label daysoff;

    @FXML
    private Label dayssick;

    @FXML
    private Label totalshifts;

    @FXML
    private GridPane schedulepane;

    @FXML
    private GridPane salerypane;

    @FXML
    private Button ticket_btn;

    @FXML
    private AnchorPane Tickets;

    @FXML
    private TableView<Reports> mytickets;

    @FXML
    private TableColumn<Reports, String> optionscolumn;

    @FXML
    private TableColumn<Reports, String> Usercolumn;

    @FXML
    private TableColumn<Reports, String> Datecolumn;

    @FXML
    private TableColumn<Reports, String> Numcolumn;

    @FXML
    private TableColumn<Reports, String> Reasoncolumn;

    @FXML
    private Label ticketdescription;

    @FXML
    private Label ticketnumber;

    @FXML
    private Label respondticketnumber;

    @FXML
    private AnchorPane ticketresponsepane;

    @FXML
    private Label ticketreason;

    @FXML
    private Label respondticketreason;

    @FXML
    private AnchorPane respondpane;

    @FXML
    private TextArea respondtext;

    @FXML
    private WebView webv;
    // 31.612148, 34.768159], 14) - KG
    @FXML
    private WebEngine engine;

    private final Db db = new Db();

    public void Setusersession(String user) {
        empName.setText(user);
        try {
            loadDashboardData(user);
            loadScheduleCards(user);
            loadSalaryCards(user);
            loadTicketTable(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDashboardData(String user) throws SQLException {
        String query = "SELECT * FROM userdashboard WHERE username = '" + user + "'";
        ResultSet rs = db.getdata(query);
        if (rs.next()) {
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Completed", rs.getInt("completed")),
                    new PieChart.Data("Ahead", rs.getInt("ahead")),
                    new PieChart.Data("Canceled", rs.getInt("shifts") - (rs.getInt("completed") + rs.getInt("ahead")))
            );
            piechart.setLegendVisible(false);
            piechart.setData(pieData);
            piechart.setPrefSize(300, 230);

            progressIndicator.setProgress(rs.getDouble("empindex"));
            totalshifts.setText("" + rs.getInt("shifts"));
            daysoff.setText("" + rs.getInt("offdays"));
            dayssick.setText("" + rs.getInt("sickdays"));
        }
    }

    private void loadScheduleCards(String user) throws Exception {
        String query = "SELECT * FROM userschedule WHERE username = '" + user + "'";
        ResultSet rs = db.getdata(query);
        int column = 0, row = 0;
        while (rs.next()) {
            FXMLLoader cardloader = new FXMLLoader(getClass().getResource("schdulecard.fxml"));
            AnchorPane card = cardloader.load();
            CardController CC = cardloader.getController();
            CC.setCarddata(
                    rs.getString("date"),
                    rs.getString("day"),
                    rs.getString("start"),
                    rs.getString("end"),
                    rs.getString("section")
            );
            schedulepane.add(card, column++, row);
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private void loadSalaryCards(String user) throws Exception {
        String query = "SELECT * FROM salerydata WHERE username = '" + user + "'";
        ResultSet rs = db.getdata(query);
        int row = 0;
        while (rs.next()) {
            FXMLLoader cardloader = new FXMLLoader(getClass().getResource("salerycard.fxml"));
            AnchorPane card = cardloader.load();
            SaleryController SC = cardloader.getController();
            SC.setCarddata(
                    rs.getInt("daysworked"),
                    rs.getInt("hoursworked"),
                    rs.getString("month"),
                    String.valueOf(rs.getFloat("salery")),
                    rs.getInt("serialnumber"),
                    rs.getInt("totalhours")
            );
            salerypane.add(card, 0, row++);
        }
    }

    private void loadTicketTable(String user) throws SQLException {
        ResultSet rs = db.getdata("SELECT * FROM citizenreports");
        ObservableList<Reports> dataList = FXCollections.observableArrayList();
        int count = 1;
        String buttonstyle = "-fx-background-color: transparent; -fx-border-radius: 30px;-fx-text-fill: white;-fx-border-width: 1;-fx-border-color: white;";

        while (rs.next()) {
            if ("Responded".equals(rs.getString("response"))) continue;

            String ID = rs.getString("ID");
            String reason = rs.getString("reason");
            String description = rs.getString("description");

            Button view = new Button("View ticket");
            view.setStyle(buttonstyle);
            view.setOnMouseClicked(e -> {
                ticketnumber.setText(ID);
                ticketreason.setText(reason);
                ticketdescription.setText(description);
                ticketresponsepane.setVisible(true);
            });

            Button respond = new Button("Respond ticket");
            respond.setStyle(buttonstyle);
            respond.setOnMouseClicked(e -> {
                respondticketnumber.setText(ID);
                respondticketreason.setText(reason);
                respondpane.setVisible(true);
            });

            HBox buttons = new HBox(10, view, respond);
            dataList.add(new Reports("" + count++, reason, rs.getString("date"), rs.getString("username"), buttons));
        }

        Numcolumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        Reasoncolumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        Datecolumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        Usercolumn.setCellValueFactory(new PropertyValueFactory<>("response"));
        optionscolumn.setCellValueFactory(new PropertyValueFactory<>("buttons"));
        mytickets.setItems(dataList);
    }

    public void switchform(ActionEvent e){

        if (e.getSource() == home_btn){
            dashboard.setVisible(true);
            schdule.setVisible(false);
            salery.setVisible(false);
            map.setVisible(false);
            Tickets.setVisible(false);
        }
        else if (e.getSource() == schedule_btn){
            dashboard.setVisible(false);
            schdule.setVisible(true);
            salery.setVisible(false);
            Tickets.setVisible(false);
            map.setVisible(false);
        }
        else if (e.getSource() == salery_btn){
            dashboard.setVisible(false);
            schdule.setVisible(false);
            salery.setVisible(true);
            Tickets.setVisible(false);
            map.setVisible(false);
        }
        else if (e.getSource() == map_btn){
            dashboard.setVisible(false);
            schdule.setVisible(false);
            salery.setVisible(false);
            Tickets.setVisible(false);
            map.setVisible(true);
        }
        else if (e.getSource() == ticket_btn){
            dashboard.setVisible(false);
            schdule.setVisible(false);
            salery.setVisible(false);
            map.setVisible(false);
            Tickets.setVisible(true);
        }
    }

    public void logout(ActionEvent event){
        try{
            home_btn.getScene().getWindow().hide();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LandingPage-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
        }catch (Exception e){e.printStackTrace();}

    }

    public void submitticketrespond(){
        db.getupdate("UPDATE citizenreports SET respond = '" + respondtext.getText() + "', response = 'Responded' WHERE ID = " + respondticketnumber.getText());
        try{
            loadTicketTable(empName.getText());
        }catch (Exception e){e.printStackTrace();}
        respondtext.setText("");
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.setTitle("Success");
        message.setContentText("Ticket has been replied.");
        message.show();
        respondpane.setVisible(false);
    }

    public void hiderespondwindow(){
        respondpane.setVisible(false);
        ticketresponsepane.setVisible(false);
    }


    // functions related to the algorithm:

    public void addMarker(double lat, double lon, String label, String color) {
        webv.getEngine().executeScript("window.javaConnector.addMarker(" + lat + "," + lon + ",'" + label + "','" + color + "');");
    }

    public void drawRoute(String jsonCoords) {
        webv.getEngine().executeScript("window.javaConnector.drawRoute('" + jsonCoords + "');");
    }

    public void fetchAndDrawOptimizedRoute(double[][] waypoints) {
        String routeData = OSRMClient.getOptimizedRoute(waypoints);
        if (routeData != null) {
            JSONObject json = new JSONObject(routeData);
            JSONArray coordinates = json.getJSONArray("trips").getJSONObject(0)
                    .getJSONObject("geometry").getJSONArray("coordinates");

            StringBuilder routeJson = new StringBuilder("[");
            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray coord = coordinates.getJSONArray(i);
                if (i > 0) routeJson.append(",");
                routeJson.append("[").append(coord.getDouble(1)).append(",").append(coord.getDouble(0)).append("]");
            }
            routeJson.append("]");

            drawRoute(routeJson.toString());
        }
    }

    public void clickstart(){
        double[][] waypoints = {
                {31.6240, 34.7745},
                {31.6230, 34.7725},
                {31.6260, 34.7690},
                {31.6280, 34.7715},
                {31.6220, 34.7665},
                {31.6255, 34.7680},

                {31.6115, 34.7630},
                {31.6100, 34.7625},
                {31.6125, 34.7650},
                {31.6130, 34.7645},
                {31.6095, 34.7660},
                {31.6105, 34.7615},

                {31.5985, 34.7580},
                {31.5990, 34.7600},
                {31.6000, 34.7620},
                {31.6020, 34.7635},
                {31.6035, 34.7595},
                {31.6040, 34.7610}
        };

        addMarker(31.6240, 34.7745, "Northern Kiryat Gat - 1", "Green");
        addMarker(31.6230, 34.7725, "Northern Kiryat Gat - 2", "red");
        addMarker(31.6260, 34.7690, "Northern Kiryat Gat - 3", "red");
        addMarker(31.6280, 34.7715, "Northern Kiryat Gat - 4", "red");
        addMarker(31.6220, 34.7665, "Northern Kiryat Gat - 5", "red");
        addMarker(31.6255, 34.7680, "Northern Kiryat Gat - 6", "red");

        addMarker(31.6115, 34.7630, "Central Kiryat Gat - 1", "red");
        addMarker(31.6100, 34.7625, "Central Kiryat Gat - 2", "red");
        addMarker(31.6125, 34.7650, "Central Kiryat Gat - 3", "red");
        addMarker(31.6130, 34.7645, "Central Kiryat Gat - 4", "yellow");
        addMarker(31.6095, 34.7660, "Central Kiryat Gat - 5", "red");
        addMarker(31.6105, 34.7615, "Central Kiryat Gat - 6", "red");

        addMarker(31.5985, 34.7580, "Southern Kiryat Gat - 1", "red");
        addMarker(31.5990, 34.7600, "Southern Kiryat Gat - 2", "red");
        addMarker(31.6000, 34.7620, "Southern Kiryat Gat - 3", "red");
        addMarker(31.6020, 34.7635, "Southern Kiryat Gat - 4", "red");
        addMarker(31.6035, 34.7595, "Southern Kiryat Gat - 5", "red");
        addMarker(31.6040, 34.7610, "Southern Kiryat Gat - 6", "red");
        addMarker(31.6240, 34.7745, "Northern Kiryat Gat - 1", "green");

        fetchAndDrawOptimizedRoute(waypoints);


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        engine = webv.getEngine();

        //set map
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>OSRM TSP Map</title>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"/>
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
            </head>
            <body>
                <div id="map" style="width: 100%; height: 600px;"></div>
                <script>
                    var map = L.map('map').setView([31.6100, 34.7642], 13); 
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; OpenStreetMap contributors'
                    }).addTo(map);

                    var markers = [];
                    var routeLayer;

                    function addMarker(lat, lon, label, color) {

                        var marker = L.marker([lat, lon]).addTo(map).bindPopup(label);
                                                var circle = L.circle([lat, lon], {
                            color: 'black', 
                            fillColor: color, 
                            fillOpacity: 0.3,
                            radius: 30
                        }).addTo(map);   
                        marker.on('click', function () {
                            this.openPopup();  
                        });
                        markers.push(marker); 
                    }

                    function drawRoute(routeCoords) {
                        if (routeLayer) map.removeLayer(routeLayer);
                        routeLayer = L.polyline(routeCoords, {color: 'blue'}).addTo(map);
                    }

                    window.javaConnector = {
                        addMarker: function(lat, lon, label, color) {
                            addMarker(lat, lon, label, color);
                        },
                        drawRoute: function(coords) {
                            drawRoute(JSON.parse(coords));
                        }
                    };
                </script>
            </body>
            </html>
        """;
        engine.loadContent(htmlContent);

    }

}
