import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

    private String username;

    public User() { }

    public boolean login(String username, String passwd) {
        DataLayer repo = new DataLayer();
        if (!repo.userExist(username)) {
            return false;
        }
        passwd = hashIt(passwd);
        if (repo.loginUser(username, passwd)) {
            this.username = username;
            return true;
        } else {
            return false;
        }
        
    }

    /**
     * Return code descriptions:
     *  0 -> Registration successful
     *  1 -> Username already used
     *  2 -> Password encryption error
     * @param username
     * @param passwd
     * @return
     */
    public int register(String username, String passwd) {
        DataLayer repo = new DataLayer();
        if (repo.userExist(username)) {
            return 1;
        }
        passwd = hashIt(passwd);
        if (repo.registerUser(username, passwd)) {
            this.username = username;
            return 0;
        }
        return 2;
    }
    
    private String hashIt(String passwd) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) { }
        byte[] hash = digest.digest(passwd.getBytes(StandardCharsets.UTF_8));
        
        StringBuffer hexString = new StringBuffer();
        
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getUsername() {
        return this.username;
    }

}