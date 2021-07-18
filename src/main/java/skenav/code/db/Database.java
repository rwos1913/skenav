package skenav.code.db;

import skenav.code.SkenavApplication;
import skenav.code.SkenavConfiguration;

import java.io.File;
import java.sql.*;

public class Database {
    public Database() {
        dbConnect();
    }
    Connection con;
    public void dbConnect() {
        try{
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:~/usercontent/database");
        }
        catch (Exception e){};
    }

    public static void createTable() {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/usercontent/database");
            Statement stmt1 = con.createStatement();
            stmt1.executeUpdate("CREATE TABLE table1 (file_id INT GENERATED ALWAYS AS IDENTITY , file_name varchar(255))");
            stmt1.close();
            con.close();
        }
        catch (Exception e){};
    }
    public void addFile(String filename) {
        try {
            PreparedStatement stmt2 = con.prepareStatement("INSERT INTO table1 (file_name)" + "VALUES (?)");
            stmt2.setString(1, filename);
            stmt2.executeUpdate();
            //stmt.executeUpdate("INSERT INTO table1 (file_name) VALUES ('hello')");
// add parameterized query
            stmt2.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
