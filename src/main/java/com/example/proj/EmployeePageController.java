package com.example.proj;

import com.example.proj.Algo.Bin;
import com.example.proj.Algo.TSCPsolve;
import com.example.proj.DB.Db;
import com.example.proj.DB.DbHelper;
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
import java.util.ArrayList;
import java.util.List;
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

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private ComboBox<String> areaComboBox;

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
            loadBinsOnly();
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

    public void clickstart() {
        clearMapMarkers(); // נקה סמנים קודמים

        // טען את הפחים מה-DB
        String selectedCity = cityComboBox.getValue();
        String selectedArea = areaComboBox.getValue();

        Bin startBin;
        switch (selectedCity) {
            case "Qiryat-Gat" -> startBin = new Bin(0, 31.5864, 34.7811, 0);
            case "Ashkelon"   -> startBin = new Bin(31, 31.642, 34.5506, 0);
            case "Sderot"     -> startBin = new Bin(62, 31.513470808277482, 34.600495683388026, 0);
            default -> {
                return;
            }
        }

        // שליפת הפחים לפי עיר ואזור שנבחרו
        List<Bin> bins = DbHelper.loadBinsForCityAndArea(selectedCity, selectedArea);;
        bins.removeIf(b -> b.getId() == startBin.getId());  // הימנע מכפילויות
        bins.addFirst(startBin);
        bins.addLast(startBin);


        // הפעל את האלגוריתם
        List<Integer> route = TSCPsolve.calculateOptimizedRoute(bins, startBin);

        // בניית URL ל-OSRM עם נקודות המסלול
        StringBuilder coordinates = new StringBuilder();
        for (int binId : route) {
            Bin bin = bins.stream()
                    .filter(b -> b.getId() == binId)
                    .findFirst()
                    .orElse(null);
            if (bin != null) {
                coordinates.append(bin.getLongitude()).append(",").append(bin.getLatitude()).append(";");
            }
        }
        // הסרת הסימן ';' האחרון
        coordinates.setLength(coordinates.length() - 1);

        String url = "http://router.project-osrm.org/route/v1/driving/" + coordinates + "?overview=full&geometries=geojson";

        // שליחה ל-JavaScript כדי לצייר את המסלול
        webv.getEngine().executeScript("fetch('" + url + "')"
                + ".then(response => response.json())"
                + ".then(data => {"
                + "    var route = data.routes[0].geometry.coordinates;"
                + "    var latLngs = route.map(coord => [coord[1], coord[0]]);"
                + "    drawRoute(latLngs);"
                + "});");

        // הוספת סמנים
        for (int binId : route) {
            Bin bin = bins.stream()
                    .filter(b -> b.getId() == binId)
                    .findFirst()
                    .orElse(null);

            if (bin != null) {
                String color = bin.getFillLevel() >= 70 ? "red" :
                        bin.getFillLevel() >= 30 ? "yellow" : "green";
                addMarker(bin.getLatitude(), bin.getLongitude(), "Bin ID: " + bin.getId(), color);
            }
        }
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
                    var circles = [];
                    var routeLayer;

                    function addMarker(lat, lon, label, color) {
                                 var marker = L.marker([lat, lon]).addTo(map).bindPopup(label);
                                 var circle = L.circle([lat, lon], {
                                     color: 'black',\s
                                     fillColor: color,\s
                                     fillOpacity: 0.3,
                                     radius: 30
                                 }).addTo(map);
                
                                 marker.on('click', function () {
                                     this.openPopup(); \s
                                 });
                
                                 markers.push(marker);
                                 circles.push(circle);
                             }

                    function drawRoute(routeCoords) {
                        if (routeLayer) map.removeLayer(routeLayer);
                        routeLayer = L.polyline(routeCoords, {color: 'blue', weight: 5}).addTo(map);
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

        // אתחול ComboBox
        cityComboBox.getItems().addAll("Qiryat-Gat", "Ashkelon", "Sderot");
        areaComboBox.getItems().addAll("A", "B", "C");

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                clickreset();
            }
        });

        // שינוי מיקום המפה לפי עיר שנבחרה
        cityComboBox.setOnAction(e -> {
            String city = cityComboBox.getValue();
            switch (city) {
                case "Qiryat-Gat" -> engine.executeScript("map.setView([31.6100, 34.7642], 13);");
                case "Ashkelon"   -> engine.executeScript("map.setView([31.6650, 34.5715], 13);");
                case "Sderot"     -> engine.executeScript("map.setView([31.5100, 34.5950], 13);");
            }

            if (areaComboBox.getValue() != null) {
                loadBinsOnly();
            }
        });

        areaComboBox.setOnAction(e -> {
            // טען פחים אם גם עיר נבחרה
            if (cityComboBox.getValue() != null) {
                loadBinsOnly();
            }
        });

    }

    public void loadBinsOnly() {
        clearMapMarkers(); // נקה סמנים קיימים

        String selectedCity = cityComboBox.getValue();
        String selectedArea = areaComboBox.getValue();

        if (selectedCity == null || selectedArea == null) return;

        List<Bin> bins = DbHelper.loadBinsForCityAndArea(selectedCity, selectedArea);

        for (Bin bin : bins) {
            String color = bin.getFillLevel() >= 70 ? "red" :
                    bin.getFillLevel() >= 30 ? "yellow" : "green";

            addMarker(bin.getLatitude(), bin.getLongitude(), "Bin ID: " + bin.getId(), color);
        }
    }

    public void clickreset() {
        clearMapMarkers(); // נקה את המפה
        List<Bin> bins = DbHelper.loadBinsFromDB(); // כל הפחים מכל הערים

        for (Bin bin : bins) {
            String color = bin.getFillLevel() >= 70 ? "red" :
                    bin.getFillLevel() >= 30 ? "yellow" : "green";
            addMarker(bin.getLatitude(), bin.getLongitude(), "Bin ID: " + bin.getId(), color);
        }
    }

    public void clearMapMarkers() {
        webv.getEngine().executeScript("markers.forEach(m => map.removeLayer(m)); markers = [];");
        webv.getEngine().executeScript("circles.forEach(c => map.removeLayer(c)); circles = [];");
        webv.getEngine().executeScript("if (typeof routeLayer !== 'undefined' && routeLayer) map.removeLayer(routeLayer);");
    }
}