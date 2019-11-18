import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

    public static final int PORT = 5000; // porta al di fuori del range 1-4096 !
    public static ArrayList<Connection> connections = new ArrayList<Connection>(0);

    public static void main(String[] args) throws IOException {
        
        ServerSocket serverSocket = new ServerSocket(PORT);
        
        System.out.println("Server: Up and running");
        System.out.println("Server: Server socket " + serverSocket);            
            
        while(true) {
            try {
                System.out.println("Server: Waiting for connection...");                
                Socket client = serverSocket.accept();
                
                Connection connection = new Connection(client);
                connection.start();   
                connections.add(connection);
                
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    static void endConnection(String username) {
        for (int i = 0; i < Server.connections.size(); i++) {
            if (Server.connections.get(i).getUser() != null) {
                if (Server.connections.get(i).getUser().getUsername().equals(username)) {
                    Server.connections.remove(i);
                }
            }
        }
    }

    static Boolean isConnected(String username) {
        for (Connection c : connections) {
            if (c.getUser() != null) {
                if (c.getUser().getUsername().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    static Connection getConnection(String username) {
        for (Connection c : connections) {
            if (c.getUser() != null) {
                if (c.getUser().getUsername().equals(username)) {
                    return c;
                }
            }
        }
        return null;
    }
}
