import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

    public static final int PORT = 5000; // porta al di fuori del range 1-4096 !
    public static ArrayList<User> connectedUsers = new ArrayList<User>(0);

    public static void main(String[] args) throws IOException {
        
        ServerSocket serverSocket = new ServerSocket(PORT);
        
        System.out.println("Server: Up and running");
        System.out.println("Server: Server socket " + serverSocket);            
            
        while(true) {
            try {
                System.out.println("Server: Waiting for connection...");                
                Socket client = serverSocket.accept();
                
                User user = new User(client);
                user.start();   
                connectedUsers.add(user);
                
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
