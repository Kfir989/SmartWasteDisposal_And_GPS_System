package com.example.proj;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;


public class EmployeePageController implements Initializable {

    @FXML
    private AnchorPane dashboard;

    @FXML
    private Button home_btn;

    @FXML
    private Button logout_btn;

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
    private BorderPane pane;

    @FXML
    private WebView webv;
    // 31.612148, 34.768159], 14)
    @FXML
    private WebEngine engine;

    public void exit(){System.exit(0);}

    public void switchform(ActionEvent e){

        if (e.getSource() == home_btn){
            dashboard.setVisible(true);
            schdule.setVisible(false);
            salery.setVisible(false);
            map.setVisible(false);
        }
        else if (e.getSource() == schedule_btn){
            dashboard.setVisible(false);
            schdule.setVisible(true);
            salery.setVisible(false);
            map.setVisible(false);
        }
        else if (e.getSource() == salery_btn){
            dashboard.setVisible(false);
            schdule.setVisible(false);
            salery.setVisible(true);
            map.setVisible(false);
        }
        else if (e.getSource() == map_btn){
            dashboard.setVisible(false);
            schdule.setVisible(false);
            salery.setVisible(false);
            map.setVisible(true);
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
