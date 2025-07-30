package com.example.proj.Algo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ExactTSP {

    public static List<Integer> solve(List<Bin> bins, Bin startBin) {
        double[][] dist = buildDistanceMatrix(bins);

        int n = bins.size();
        List<Integer> indices = new ArrayList<>();
        for (int i = 1; i < n - 1; i++) {
            indices.add(i);
        }

        List<Integer> bestRoute = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;

        for (List<Integer> perm : generatePermutations(indices)) {
            List<Integer> route = new ArrayList<>();
            route.add(0);
            route.addAll(perm);
            route.add(n - 1);

            double total = totalDistance(route, dist);
            if (total < minDistance) {
                minDistance = total;
                bestRoute = new ArrayList<>(route);
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int idx : bestRoute) {
            result.add(bins.get(idx).getId());
        }
        return result;
    }

    private static List<List<Integer>> generatePermutations(List<Integer> list) {
        List<List<Integer>> result = new ArrayList<>();
        permuteHelper(list, 0, result);
        return result;
    }

    private static void permuteHelper(List<Integer> list, int index, List<List<Integer>> result) {
        if (index == list.size()) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = index; i < list.size(); i++) {
            Collections.swap(list, i, index);
            permuteHelper(list, index + 1, result);
            Collections.swap(list, i, index);
        }
    }

    private static double totalDistance(List<Integer> route, double[][] dist) {
        double sum = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            sum += dist[route.get(i)][route.get(i + 1)];
        }
        return sum;
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
            throw new RuntimeException("Failed to connect to OSRM", e);
        }

        return matrix;
    }
}

