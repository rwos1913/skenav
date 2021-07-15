package skenav.code.db;

import java.sql.*;

public class Database {
    private static Database database = new Database();
    private Database(){}
    public static Database getInstance(){
        return database;
    }
    public static void addFile(String filename) {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/usercontent/database");
            Statement stmt1 = con.createStatement();
            stmt1.executeUpdate("CREATE TABLE table1 (file_id INT GENERATED ALWAYS AS IDENTITY , file_name varchar(255))");
            PreparedStatement stmt2 = con.prepareStatement("INSERT INTO table1 (file_name)" + "VALUES (?)");
            stmt2.setString(1, filename);
            //stmt.executeUpdate("INSERT INTO table1 (file_name) VALUES ('hello')");
// add parameterized query
            ResultSet rs = stmt1.executeQuery("SELECT * FROM table1");

            while (rs.next()) {
                String fileid = rs.getString("file_id");
                System.out.println(fileid);
                String filenamefromdb = rs.getString("file_name");
                System.out.println(filenamefromdb);
            }
            stmt1.close();
            stmt2.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
