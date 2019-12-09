package Server;

import java.sql.*;

/**
 * Connect to db
 */
public class DataLayer {
    
    private java.sql.Connection dbConnection;

    public DataLayer() {
        dbConnection = connect();
    }

    /**
     * Register an user
     * Password is supposed to be already hashed, 64 characters length, SHA-256
     * @param username
     * @param passwd
     * @return 
     */
    public boolean registerUser(String username, String passwd) {
        try {
            Statement stm = dbConnection.createStatement();
            // Insert to User table
            stm.executeUpdate("INSERT INTO User (Username, Passwd) VALUES ('"
                + username + "', '" + passwd + "')");
            return true;
        } catch (SQLException ex) { 
            System.out.println(ex+ "HAY");
        }
        return false;
    }
    
    /**
     * Returns true if username and password correspond to a registered user
     * @param username
     * @param passwd
     * @return 
     */
    public boolean loginUser(String username, String passwd) {
        try {
            Statement stm = dbConnection.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM User");
            while (rs.next()) {
                if (rs.getString(1).equals(username)) {
                    if (rs.getString(2).equals(passwd)) {
                        // Username and password correspond
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    /**
     * Checks the existence of an user
     * @param username
     * @return 
     */
    public boolean userExist(String username) {
        try {
            Statement stm = dbConnection.createStatement();
            // Return every user
            ResultSet rs = stm.executeQuery("SELECT * FROM User");
            while (rs.next()) {
                if (rs.getString(1).equals(username)) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    /**
     * Connect to the database
     * @return 
     */
    public java.sql.Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(
                "jdbc:sqlite:db/javachat.db"
            );
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
