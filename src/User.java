import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The User class defines a user model for the application
 * @author sam
 */
public class User {

    private String username;

    public User() { }

    /**
     * Returns true if username and password correspond to an entry in the db
     * @param username
     * @param passwd
     * @return 
     */
    public boolean login(String username, String passwd) {
        DataLayer repo = new DataLayer();
        if (!repo.userExist(username)) {
            // Cannot login if user doesn't exist
            return false;
        }
        // Hashed password is needed for login
        passwd = hashIt(passwd);
        if (repo.loginUser(username, passwd)) {
            // Login successful
            this.username = username;
            return true;
        } else {
            // Nope
            return false;
        }
        
    }

    /**
     * Register an user
     * Return code descriptions:
     *  0 - Registration successful
     *  1 - Username already used
     *  2 - Password encryption error
     * @param username
     * @param passwd
     * @return
     */
    public int register(String username, String passwd) {
        DataLayer repo = new DataLayer();
        if (repo.userExist(username)) {
            // Cannot register an existing user
            return 1;
        }
        // Hashed passord is needed for registration
        passwd = hashIt(passwd);
        if (repo.registerUser(username, passwd)) {
            // Registration successful
            this.username = username;
            return 0;
        }
        // Nope
        return 2;
    }
    
    /**
     * Hash a string
     * @param passwd
     * @return 
     */
    private String hashIt(String passwd) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        // Final digest
        byte[] hash = digest.digest(passwd.getBytes(StandardCharsets.UTF_8));
        
        // Convert digest to string
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Return the username
     * @return 
     */
    public String getUsername() {
        return this.username;
    }

}