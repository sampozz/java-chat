import java.sql.*;

/**
 * Connect to db
 */
public class DataLayer {

    public DataLayer() {
    }

    public boolean registerUser(String username, String passwd) {
        try {
            Statement stm = connect().createStatement();
            stm.executeQuery("INSERT INTO User (Username, Passwd) VALUES ('"
                + username + "', '" +passwd + "')");
            return true;
        } catch (SQLException ex) { 
            System.out.println(ex);
        }
        return false;
    }
    
    public boolean loginUser(String username, String passwd) {
        try {
            Statement stm = connect().createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM User");
            while (rs.next()) {
                if (rs.getString(1).equals(username)) {
                    if (rs.getString(2).equals(passwd)) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }

    public boolean userExist(String username) {
        try {
            Statement stm = connect().createStatement();
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

    public java.sql.Connection connect() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mariadb://10.0.0.3:3306/javachat", "dbiusr", "dbiusr"
            );
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
