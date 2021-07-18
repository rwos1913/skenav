package skenav.code.db;
import java.sql.*;

public class Database {
    Connection con;

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

    public static void createTable() {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/usercontent/database");
            Statement statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE table1 (file_id INT GENERATED ALWAYS AS IDENTITY , file_name varchar(255))");
            statement.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        };
    }

    public void addFile(String filename) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO table1 (file_name) VALUES (?)");
            statement.setString(1, filename);
            statement.executeUpdate();
            //stmt.executeUpdate("INSERT INTO table1 (file_name) VALUES ('hello')");
            // add parameterized query
            statement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
