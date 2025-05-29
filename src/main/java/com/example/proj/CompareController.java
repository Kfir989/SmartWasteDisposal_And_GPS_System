package com.example.proj;

import com.example.proj.Algo.Bin;
import com.example.proj.Algo.TSCPsolve;
import com.example.proj.Algo.ExactTSP;
import com.example.proj.DB.DbHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CompareController {

    @FXML private WebView webviewCustom;
    @FXML private WebView webviewTSP;
    @FXML private ComboBox<String> cityBox;
    @FXML private ComboBox<String> areaBox;
    @FXML private TableView<ComparisonRow> resultTable;
    @FXML private TableColumn<ComparisonRow, String> paramColumn;
    @FXML private TableColumn<ComparisonRow, String> customColumn;
    @FXML private TableColumn<ComparisonRow, String> tspColumn;

    private WebEngine engine1;
    private WebEngine engine2;

    @FXML
    public void initialize() {
        cityBox.setItems(FXCollections.observableArrayList("Qiryat-Gat", "Ashkelon", "Sderot"));
        areaBox.setItems(FXCollections.observableArrayList("A", "B", "C"));

        engine1 = webviewCustom.getEngine();
        engine2 = webviewTSP.getEngine();

        cityBox.setOnAction(e -> {
            String city = cityBox.getValue();
            String jsCommand = switch (city) {
                case "Qiryat-Gat" -> "map.setView([31.6100, 34.7642], 13);";
                case "Ashkelon"   -> "map.setView([31.6650, 34.5715], 13);";
                case "Sderot"     -> "map.setView([31.5100, 34.5950], 13);";
                default -> null;
            };

            if (jsCommand != null) {
                engine1.executeScript(jsCommand);
                engine2.executeScript(jsCommand);
            }
        });

        areaBox.setOnAction(e -> {
            String city = cityBox.getValue();
            String area = areaBox.getValue();
            if (city != null && area != null) {
                List<Bin> bins = DbHelper.loadBinsForCityAndArea(city, area);
                engine1.executeScript("clearMap()");
                engine2.executeScript("clearMap()");
                addMarkersForAllBins(engine1, bins);
                addMarkersForAllBins(engine2, bins);
            }
        });

        loadMapHTML(engine1);
        loadMapHTML(engine2);

        paramColumn.setCellValueFactory(data -> data.getValue().paramProperty());
        customColumn.setCellValueFactory(data -> data.getValue().customValueProperty());
        tspColumn.setCellValueFactory(data -> data.getValue().tspValueProperty());

        List<Bin> allBins = DbHelper.loadBinsFromDB();  // טוען את כל הפחים מכל הערים
        engine1.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                addMarkersForAllBins(engine1, allBins);
            }
        });
        engine2.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                addMarkersForAllBins(engine2, allBins);
            }
        });
    }

    private void loadMapHTML(WebEngine engine) {
        String html = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8">
          <title>Map</title>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"/>
          <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
        </head>
        <body>
          <div id="map" style="position: absolute; top: 0; bottom: 0; right: 0; left: 0;"></div>
          <script>
            var map = L.map('map').setView([31.61, 34.76], 13);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
              attribution: '&copy; OpenStreetMap contributors'
            }).addTo(map);

            var markers = [];
            var routeLine;

            window.drawRoute = function(coords) {
              if (routeLine) map.removeLayer(routeLine);
              routeLine = L.polyline(coords, {color: 'orange'}).addTo(map);
            }

            window.clearMap = function() {
              if (routeLine) map.removeLayer(routeLine);
              markers.forEach(m => map.removeLayer(m));
              markers = [];
            }

            window.addMarker = function(lat, lon, label, color) {
                      var circle = L.circle([lat, lon], {
                        radius: 8,
                        color: color,
                        fillColor: color,
                        fillOpacity: 0.8,
                        weight: 2
                      }).addTo(map).bindPopup(label);
                      markers.push(circle);
            }
          </script>
        </body>
        </html>
        """;
        engine.loadContent(html);
        engine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                engine.executeScript("map.invalidateSize();"); // מוודא שהמפה נטענת כראוי
            }
        });
    }

    @FXML
    public void startComparison() {
        String city = cityBox.getValue();
        String area = areaBox.getValue();
        if (city == null || area == null) return;

        Bin startBin = switch (city) {
            case "Qiryat-Gat" -> new Bin(0, 31.5864, 34.7811, 0);
            case "Ashkelon" -> new Bin(31, 31.642, 34.5506, 0);
            case "Sderot" -> new Bin(62, 31.513470808277482, 34.600495683388026, 0);
            default -> null;
        };

        List<Bin> bins = DbHelper.loadBinsForCityAndArea(city, area);
        bins.removeIf(b -> b.getId() == startBin.getId());
        bins.add(0, startBin);
        bins.add(startBin);

        addMarkersForAllBins(engine1, bins);
        addMarkersForAllBins(engine2, bins);
        engine1.executeScript("clearMap()");
        engine2.executeScript("clearMap()");

        // חישוב TSP שלך
        long t1 = System.nanoTime();
        List<Integer> route1 = TSCPsolve.calculateOptimizedRoute(new ArrayList<>(bins), startBin);
        long t2 = System.nanoTime();

        // חישוב TSP מדויק
        long t3 = System.nanoTime();
        List<Integer> route2 = ExactTSP.solve(new ArrayList<>(bins), startBin);
        long t4 = System.nanoTime();

        double timeYourAlgo = (t2 - t1) / 1e6;
        double timeExact = (t4 - t3) / 1e6;

        double dist1 = getRouteDistance(route1, bins);
        double dist2 = getRouteDistance(route2, bins);

        drawMarkers(engine1, route1, bins);
        drawMarkers(engine2, route2, bins);

        drawRouteOnMap(engine1, route1, bins);
        drawRouteOnMap(engine2, route2, bins);

        resultTable.setItems(FXCollections.observableArrayList(
                new ComparisonRow("זמן ריצה (ms)", format(timeYourAlgo), format(timeExact)),
                new ComparisonRow("אורך מסלול כולל (שניות)", format(dist1), format(dist2)),
                new ComparisonRow("פחים במסלול", String.valueOf(route1.size() - 2), String.valueOf(route2.size() - 2))
        ));
    }

    private String format(double val) {
        return String.format("%.2f", val);
    }

    private double getRouteDistance(List<Integer> routeIds, List<Bin> allBins) {
        List<Bin> route = new ArrayList<>();
        for (int id : routeIds) {
            for (Bin b : allBins) {
                if (b.getId() == id) {
                    route.add(b);
                    break;
                }
            }
        }

        try {
            StringBuilder coords = new StringBuilder();
            for (Bin b : route) {
                coords.append(b.getLongitude()).append(",").append(b.getLatitude()).append(";");
            }
            coords.setLength(coords.length() - 1);

            String urlStr = "http://router.project-osrm.org/route/v1/driving/" + coords + "?overview=false";
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject json = new JSONObject(response);
            return json.getJSONArray("routes").getJSONObject(0).getDouble("duration");
        } catch (IOException e) {
            return -1;
        }
    }

    private void drawMarkers(WebEngine engine, List<Integer> routeIds, List<Bin> bins) {
        for (int id : routeIds) {
            for (Bin b : bins) {
                if (b.getId() == id) {
                    String label = "Bin ID: " + b.getId();
                    engine.executeScript("addMarker(" + b.getLatitude() + ", " + b.getLongitude() + ", '" + label + "')");
                    break;
                }
            }
        }
    }

    private void drawRouteOnMap(WebEngine engine, List<Integer> routeIds, List<Bin> bins) {
        try {
            StringBuilder coordinates = new StringBuilder();
            for (int id : routeIds) {
                for (Bin b : bins) {
                    if (b.getId() == id) {
                        coordinates.append(b.getLongitude()).append(",").append(b.getLatitude()).append(";");
                        break;
                    }
                }
            }
            coordinates.setLength(coordinates.length() - 1);  // הסר את ה־; האחרון

            String url = "http://router.project-osrm.org/route/v1/driving/" + coordinates + "?overview=full&geometries=geojson";

            String script = """
            fetch('%s')
              .then(response => response.json())
              .then(data => {
                var route = data.routes[0].geometry.coordinates;
                var latLngs = route.map(coord => [coord[1], coord[0]]);
                drawRoute(latLngs);
              });
        """.formatted(url);

            engine.executeScript(script);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMarkersForAllBins(WebEngine engine, List<Bin> bins) {
        for (Bin bin : bins) {
            String color = bin.getFillLevel() >= 70 ? "red"
                    : bin.getFillLevel() >= 30 ? "yellow"
                    : "green";

            String js = String.format("addMarker(%f, %f, '%s', '%s')",
                    bin.getLatitude(),
                    bin.getLongitude(),
                    "Bin ID: " + bin.getId(),
                    color);
            engine.executeScript(js);
        }}
}
