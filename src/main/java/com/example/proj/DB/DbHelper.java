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
            String sql = "SELECT id, lat, lon, fillpercent FROM bins";
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
}
