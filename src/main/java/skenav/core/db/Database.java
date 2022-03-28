package skenav.core.db;
import skenav.core.OS;
import skenav.core.security.LogicValidation;

import javax.ws.rs.WebApplicationException;
import java.sql.*;
import java.util.ArrayList;

public class Database {
    Connection con;
// constructor that calls method to connect to DB
    public Database() {
        connect();
    }

    private void connect() {
        try{
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:" + OS.getUserContentDirectory() + "database");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void disconnect() {
        try {
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void endQuery() {

    }
// creates db file and table. Called staticly from the application class if db does not already exist
    public static void createTable() {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:" + OS.getUserContentDirectory() + "database");
            Statement statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE table1 (file_id varchar(255) , file_name varchar(255), file_type varchar(255), upload_datetime varchar(50))");
            statement.executeUpdate("CREATE TABLE appdata (key varchar(255), value varchar(max))");
            statement.executeUpdate("CREATE TABLE users (username varchar(255), password_hash varchar(255), authorization varchar(255), cookie_info varchar (255), account_creation_date  varchar (255), invited_by varchar(255), invite_date varchar(255), invite_accept_date varchar(255))");
            statement.close();
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
// adds file to database when uploaded
    public void addFile(String filehash, String filename, String filetype, String datetime) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO table1 (file_id, file_name, file_type, upload_datetime) VALUES (?, ?, ?, ?) ");
            statement.setString(1, filehash);
            statement.setString(2, filename);
            statement.setString(3, filetype);
            statement.setString(4, datetime);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }
    //arraylist of arraylist to store metadata for selected files
    public ArrayList<ArrayList<String>> viewFiles(String search, int limit, int sortby) {
        ArrayList<ArrayList<String>> fileinfo = new ArrayList<>();
        // checks that query limit and search string length are reasonable
        if (!LogicValidation.intInRange(limit,1,100) || !LogicValidation.intInRange(search.length(), 0, 100) || !LogicValidation.intInRange(sortby, 0, 3)){
            throw new WebApplicationException(400);
        }
        String selectquery = "";
        // sorts by most recently uploaded
        if (sortby == 0) {
            selectquery = "SELECT file_name, file_type, upload_datetime FROM table1 WHERE LOWER(file_name) LIKE LOWER(?) ORDER BY upload_datetime DESC LIMIT ?";
        }
        //sorts by least recently uploaded
        else if (sortby == 1) {
            selectquery = "SELECT file_name, file_type, upload_datetime FROM table1 WHERE LOWER(file_name) LIKE LOWER(?) ORDER BY upload_datetime LIMIT ?";
        }
        // sorts alphabetically
        else if (sortby == 2) {
            selectquery = "SELECT file_name, file_type, upload_datetime FROM table1 WHERE LOWER(file_name) LIKE LOWER(?) ORDER BY LOWER(file_name) LIMIT ?";
        }
        // sorts reverse alphabetically
        else if (sortby == 3) {
            selectquery = "SELECT file_name, file_type, upload_datetime FROM table1 WHERE LOWER(file_name) LIKE LOWER(?) ORDER BY LOWER(file_name) DESC LIMIT ?";
        }
        else {
            throw new WebApplicationException(400);
        }
        // SQL parameterized select query with specified params
        try {
            PreparedStatement statement = con.prepareStatement(selectquery);
            // passing params into SQL query
            statement.setString(1, "%" + search + "%");
            statement.setInt(2, limit);
            ResultSet rs =  statement.executeQuery();

            while (rs.next()) {
                ArrayList<String> file = new ArrayList<String>();
                // adds file name to file array
                file.add(rs.getString("file_name"));
                // adds file type to file array
                file.add(rs.getString("file_type"));
                // adds upload date and time to file array
                file.add(rs.getString("upload_datetime"));
                // adds current instance of file array to array of files (fileinfo)
                fileinfo.add(file);
            }
            //System.out.println(fileinfo);
            rs.close();
            statement.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return fileinfo;
    }
    public void addToAppData (String key, String value) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO APPDATA (KEY, VALUE) VALUES (?, ?)");
            statement.setString(1, key);
            statement.setString(2, value);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    // permissions: 0 = owner 1 = admin 2= normal user
    public void addUser (String username, String passwordhash, int authorization) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO USERS (USERNAME, PASSWORD_HASH, AUTHORIZATION) VALUES (?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, passwordhash);
            statement.setInt(3, authorization);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public String getAppData(String key) {
        String value = new String();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT VALUE FROM APPDATA WHERE KEY = ?");
            statement.setString(1, key);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                value = rs.getString("value");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return value;
    }


    public String getPasswordHash(String username) {
        String hashedpassword = new String();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT PASSWORD_HASH FROM USERS WHERE USERNAME = ?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                hashedpassword = rs.getString("password_hash");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return hashedpassword;
    }

    // 0 is owner 1 is admin 2 is normal user
    // TODO: make method for getting both password and authorization
    public Integer getAuthzLevel(String username) {
        Integer authzlevel = null;
        try{
            PreparedStatement statement = con.prepareStatement("SELECT AUTHORIZATION from USERS WHERE USERNAME = ?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                authzlevel = rs.getInt("authorization");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return authzlevel;
    }
}
