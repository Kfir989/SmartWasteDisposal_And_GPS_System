package com.example.proj.DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Db {
    private Connection connect;
    // connect to DB - MYSQL
    private Connection Establish_Connect(){

        try{connect = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/myuserdb", "root", "");return connect;}
        catch (Exception e){e.printStackTrace();}
        return null;
    }
    // INSERT DELETE UPDATE
    public void getupdate(String query){

            try {
                connect = Establish_Connect();
                Statement statement = connect.createStatement();
                statement.execute(query);
            } catch (Exception e) {e.printStackTrace();}
    }
    // SELECT
    public ResultSet getdata(String query){

        try {
            connect = Establish_Connect();
            Statement statement = connect.createStatement();
            return statement.executeQuery(query);
        } catch (Exception e) {e.printStackTrace();return null;}
    }
}
