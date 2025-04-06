package com.example.proj;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OSRMClient {
    private static final String OSRM_URL = "https://router.project-osrm.org/trip/v1/driving/";

    public static String getOptimizedRoute(double[][] waypoints) {
        try {
            StringBuilder url = new StringBuilder(OSRM_URL);
            for (int i = 0; i < waypoints.length; i++) {
                if (i > 0) url.append(";");
                url.append(waypoints[i][1]).append(",").append(waypoints[i][0]); // lon, lat
            }
            url.append("?source=first&destination=last&roundtrip=false&overview=full&geometries=geojson");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
