package com.example.proj.Algo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class TSCPsolve {

    private static final double TRUCK_CAPACITY = 500.0;


    public static List<Integer> calculateOptimizedRoute(List<Bin> bins, Bin startBin) {

        bins = bins.stream()
                .filter(b -> b.getFillLevel() > 0)
                .collect(Collectors.toList());

        List<Bin> redBins = bins.stream().filter(b -> b.getFillLevel() >= 70).collect(Collectors.toList());
        List<Bin> yellowBins = bins.stream().filter(b -> b.getFillLevel() >= 30 && b.getFillLevel() < 70).collect(Collectors.toList());
        List<Bin> greenBins = bins.stream().filter(b -> b.getFillLevel() < 30).collect(Collectors.toList());

        List<Bin> sortedBins = new ArrayList<>();
        sortedBins.addAll(sortByDistance(startBin, redBins));
        sortedBins.addAll(sortByDistance(startBin, yellowBins));
        sortedBins.addAll(sortByDistance(startBin, greenBins));

        //בדיקה
        List<Integer> sortedBinsTemp = new ArrayList<>();
        for (Bin bin : sortedBins){
            sortedBinsTemp.add(bin.getId());
        }
        System.out.println("sorted bins: " + sortedBinsTemp);

        List<Bin> selectedBins = new ArrayList<>();
        double currentLoad = 0.0;

        for (Bin bin : sortedBins) {
            if (currentLoad + bin.getFillLevel() <= TRUCK_CAPACITY) {
                selectedBins.add(bin);
                currentLoad += bin.getFillLevel();
            }
        }

        //בדיקה
        List<Integer> selectedBinsTemp = new ArrayList<>();
        for (Bin bin : selectedBins){
            selectedBinsTemp.add(bin.getId());
        }
        System.out.println("selected bins: " + selectedBinsTemp);

        List<Bin> routeBins = new ArrayList<>();
        routeBins.add(startBin);
        routeBins.addAll(selectedBins);
        routeBins.add(startBin); // חזרה לתחנה

        //בדיקה
        List<Integer> routeBinsTemp = new ArrayList<>();
        for (Bin bin : routeBins){
            routeBinsTemp.add(bin.getId());
        }
        System.out.println("route bins: " + routeBinsTemp);

        double[][] distanceMatrix = buildDistanceMatrix(routeBins);
        List<Integer> routeIndices = solveTSP(distanceMatrix);
        List<Integer> routeIds = new ArrayList<>();

        for (int index : routeIndices) {
            Bin bin = routeBins.get(index);
            routeIds.add(bin.getId());
        }
        System.out.println("distance matrix: " + Arrays.deepToString(distanceMatrix));
        System.out.println("route Ids: " + routeIds);

        return routeIds;
    }

    private static List<Bin> sortByDistance(Bin from, List<Bin> bins) {
        return bins.stream()
                .sorted(Comparator.comparingDouble(b -> distance(from, b)))
                .collect(Collectors.toList());
    }

    private static double distance(Bin a, Bin b) {
        double dx = a.getLatitude() - b.getLatitude();
        double dy = a.getLongitude() - b.getLongitude();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static double[][] buildDistanceMatrix(List<Bin> bins) {
        int size = bins.size();
        double[][] matrix = new double[size][size];

        try {
            StringBuilder coordinates = new StringBuilder();
            for (Bin bin : bins) {
                coordinates.append(bin.getLongitude()).append(",").append(bin.getLatitude()).append(";");
            }
            coordinates.setLength(coordinates.length() - 1);

            String urlStr = "http://router.project-osrm.org/table/v1/driving/" + coordinates + "?annotations=duration";
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject json = new JSONObject(response);
            if (!json.has("durations")) {
                throw new RuntimeException("Missing 'durations' in OSRM response:\n" + json.toString(2));
            }

            JSONArray durations = json.getJSONArray("durations");

            for (int i = 0; i < size; i++) {
                JSONArray row = durations.getJSONArray(i);
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = row.getDouble(j);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to OSRM service", e);
        }

        return matrix;
    }

    private static List<Integer> solveTSP(double[][] distanceMatrix) {
        int n = distanceMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        int current = 0;
        path.add(current);
        visited[current] = true;

        for (int i = 1; i < n - 1; i++) {
            double minDist = Double.MAX_VALUE;
            int next = -1;

            for (int j = 1; j < n - 1; j++) {
                if (!visited[j] && distanceMatrix[current][j] < minDist) {
                    minDist = distanceMatrix[current][j];
                    next = j;
                }
            }

            if (next != -1) {
                path.add(next);
                visited[next] = true;
                current = next;
            }
        }

        path.add(n - 1); // return to depot
        return applyTwoOpt(path, distanceMatrix);
    }

    private static List<Integer> applyTwoOpt(List<Integer> route, double[][] dist) {
        boolean improvement = true;
        int size = route.size();

        while (improvement) {
            improvement = false;
            for (int i = 1; i < size - 2; i++) {
                for (int j = i + 1; j < size - 1; j++) {
                    double before = dist[route.get(i - 1)][route.get(i)] + dist[route.get(j)][route.get(j + 1)];
                    double after = dist[route.get(i - 1)][route.get(j)] + dist[route.get(i)][route.get(j + 1)];
                    if (after < before) {
                        Collections.reverse(route.subList(i, j + 1));
                        improvement = true;
                    }
                }
            }
        }

        return route;
    }
}
