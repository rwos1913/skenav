package skenav.core.db;
import skenav.core.Cache;
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
            String directory = Cache.INSTANCE.getUploaddirectory();
            con = DriverManager.getConnection("jdbc:h2:" + directory + "database");
            System.out.println("directory from db class is: " + directory);
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
    public void createTable() {
        try {
            Statement statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE table1 (file_id varchar(255) , file_name varchar(255), file_type varchar(255), upload_datetime varchar(50), owner varchar(255), authorized_users varchar(255))");
            statement.executeUpdate("CREATE TABLE appdata (key_ varchar(255), value_ varchar(max))");
            statement.executeUpdate("CREATE TABLE users (username varchar(255), password_hash varchar(255), authorization_ varchar(255), cookie_info varchar (255), account_creation_date  varchar (255), invited_by varchar(255), invite_date varchar(255), invite_accept_date varchar(255))");
            statement.executeUpdate("CREATE TABLE invites (invite_code varchar(255), invited_by varchar(255), admin_nickname varchar(255))");
            statement.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
// adds file to database when uploaded
    //TODO: add method for adding authorized user to a file
    public void addFile(String filehash, String filename, String filetype, String datetime, String owner) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO table1 (file_id, file_name, file_type, upload_datetime, owner) VALUES (?, ?, ?, ?, ?) ");
            statement.setString(1, filehash);
            statement.setString(2, filename);
            statement.setString(3, filetype);
            statement.setString(4, datetime);
            statement.setString(5, owner);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }
    //arraylist of arraylist to store metadata for selected files
    public ArrayList<ArrayList<String>> viewFiles(String search, int limit, int sortby, String owner) {
        ArrayList<ArrayList<String>> fileinfo = new ArrayList<>();
        // checks that query limit and search string length are reasonable
        if (!LogicValidation.intInRange(limit,1,100) || !LogicValidation.intInRange(search.length(), 0, 100) || !LogicValidation.intInRange(sortby, 0, 3)){
            throw new WebApplicationException(400);
        }
        String sortstring;
        String standardstring = "SELECT file_name, file_type, upload_datetime FROM table1 WHERE LOWER(file_name) LIKE LOWER(?) AND OWNER = ?";
        // sorts by most recently uploaded
        if (sortby == 0) {
        sortstring = " ORDER BY upload_datetime DESC LIMIT ?";
        }
        //sorts by least recently uploaded
        else if (sortby == 1) {
            sortstring = " ORDER BY upload_datetime LIMIT ?";
        }
        // sorts alphabetically
        else if (sortby == 2) {
            sortstring = " ORDER BY LOWER(file_name) LIMIT ?";
        }
        // sorts reverse alphabetically
        else if (sortby == 3) {
            sortstring = " ORDER BY LOWER(file_name) DESC LIMIT ?";
        }
        else {
            throw new WebApplicationException(400);
        }
        String selectquery = standardstring + sortstring;
        // SQL parameterized select query with specified params
        try {
            PreparedStatement statement = con.prepareStatement(selectquery);
            // passing params into SQL query
            statement.setString(1, "%" + search + "%");
            statement.setString(2, owner);
            statement.setInt(3, limit);
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
            PreparedStatement statement = con.prepareStatement("INSERT INTO APPDATA (KEY_, VALUE_) VALUES (?, ?)");
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
            System.out.println("trying to add user to db");
            PreparedStatement statement = con.prepareStatement("INSERT INTO USERS (USERNAME, PASSWORD_HASH, AUTHORIZATION_) VALUES (?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, passwordhash);
            statement.setInt(3, authorization);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getAppData(String key) {
        String value = new String();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT VALUE_ FROM APPDATA WHERE KEY_ = ?");
            statement.setString(1, key);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                value = rs.getString("value_");
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
            PreparedStatement statement = con.prepareStatement("SELECT AUTHORIZATION_ from USERS WHERE USERNAME = ?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                authzlevel = rs.getInt("authorization_");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return authzlevel;
    }
//TODO: handle files with same name
    public boolean checkFileOwner(String filename, String unverifiedowner) {
        String owner = null;
        try {
            PreparedStatement statement = con.prepareStatement("SELECT OWNER FROM TABLE1 WHERE FILE_NAME = ?");
            statement.setString(1, filename);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                owner = rs.getString("owner");
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        if(owner.equals(unverifiedowner)) {
            return true;
        }
        return false;
    }

    public String getSkenavOwner () {
        String skenavowner = null;
        try{
            PreparedStatement statement = con.prepareStatement("SELECT USERNAME FROM USERS WHERE AUTHORIZATION_ = ?");
            statement.setInt(1, 0);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                skenavowner = rs.getString("username");
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return skenavowner;
    }
    public boolean checkInviteCode(String userinvitecode) {
        boolean isinvited = false;
        try {
        PreparedStatement statement = con.prepareStatement("SELECT ADMIN_NICKNAME FROM INVITES WHERE INVITE_CODE = ?");
        statement.setString(1, userinvitecode);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            isinvited = true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return isinvited;
    }

    public void addInvite (String invitecode, String nickname, String inviter) {
        try{
            PreparedStatement statement = con.prepareStatement("INSERT INTO INVITES (INVITE_CODE, INVITED_BY, ADMIN_NICKNAME) VALUES ( ?, ?, ? )");
            statement.setString(1, invitecode);
            statement.setString(2, inviter);
            statement.setString(3, nickname);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
