package com.example.proj.DB;

import com.example.proj.Algo.Bin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {
    public static List<Bin> loadBinsFromDB() {
        List<Bin> bins = new ArrayList<>();

        try (Connection conn = Db.getConnection()) {
            String sql = "SELECT ID, lat, lon, fillpercent FROM bins";
            PreparedStatement stmt = ((java.sql.Connection) conn).prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("ID");
                double lat = rs.getDouble("lat");
                double lon = rs.getDouble("lon");
                int fill = rs.getInt("fillpercent");

                bins.add(new Bin(id, lat, lon, fill));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bins;
    }

    public static List<Bin> loadBinsForCityAndArea(String city, String area) {
        List<Bin> bins = new ArrayList<>();
        try {
            Connection conn = new Db().getConnection();
            String query = "SELECT * FROM bins WHERE city = ? AND area = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, city);
            stmt.setString(2, area);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bins.add(new Bin(
                        rs.getInt("ID"),
                        rs.getDouble("lat"),
                        rs.getDouble("lon"),
                        rs.getInt("fillpercent")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bins;
    }

}
