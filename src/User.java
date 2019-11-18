public class User {

    private String username;
    
    public User() { }

    public boolean login(String username, String passwd) {
        if (username.equals("broadcast")) 
            return false;
        this.username = username;
        return true;
    }

    public String getUsername() {
        return this.username;
    }

}