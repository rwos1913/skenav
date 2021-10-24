package skenav.core.db;
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
            con = DriverManager.getConnection("jdbc:h2:~/usercontent/database");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        };
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
            Connection con = DriverManager.getConnection("jdbc:h2:~/usercontent/database");
            Statement statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE table1 (file_id varchar(255) , file_name varchar(255), file_type varchar(255), upload_datetime varchar(50))");
            statement.executeUpdate("CREATE TABLE appdata (key varchar(255), value varchar(255))");
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
}
