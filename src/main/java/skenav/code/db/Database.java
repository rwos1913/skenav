package skenav.code.db;
import java.sql.*;
import java.util.ArrayList;

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
    private void endQuery() {

    }

    public static void createTable() {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/usercontent/database");
            Statement statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE table1 (file_id INT GENERATED ALWAYS AS IDENTITY , file_name varchar(255), file_type varchar(255))");
            statement.close();
            con.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addFile(String filename, String filetype) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO table1 (file_name, file_type) VALUES (?, ?) ");
            statement.setString(1, filename);
            statement.setString(2, filetype);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }
    public ArrayList<ArrayList<String>> viewFiles() {
        ArrayList<ArrayList<String>> fileinfo = new ArrayList<>();
        try{
            PreparedStatement statement = con.prepareStatement("SELECT file_name, file_type FROM table1");
            ResultSet rs =  statement.executeQuery();

            while (rs.next()) {
                ArrayList<String> file = new ArrayList<String>();
                // adds file name to file array
                file.add(rs.getString("file_name"));
                // adds file type to file array
                file.add(rs.getString("file_type"));
                //adds this instance of file to fileinfo
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
