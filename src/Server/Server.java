package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * This is the server of the application
 * You should run only one instance of this server and every client connects here
 * @author sam
 */
public class Server {

    public static final int PORT = 5000; // porta al di fuori del range 1-4096 !
    public static ArrayList<SocketConnection> connections = new ArrayList<SocketConnection>(0);

    /**
     * Run me!
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        ServerSocket serverSocket = new ServerSocket(PORT);
        
        System.out.println("Server: Up and running");
        System.out.println("Server: Server socket " + serverSocket);            
            
        while(true) {
            try {
                System.out.println("Server: Waiting for connection...");                
                Socket client = serverSocket.accept();
                
                // A client asked for connection
                SocketConnection connection = new SocketConnection(client);
                connection.start();
                // Add connection to the array
                connections.add(connection);
                
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Terminates the connection of an user
     * @param username 
     */
    static void endConnection(String username) {
        for (int i = 0; i < Server.connections.size(); i++) {
            if (Server.connections.get(i).getUser() != null) {
                if (Server.connections.get(i).getUser().getUsername().equals(username)) {
                    Server.connections.remove(i);
                }
            }
        }
    }

    /**
     * Checks if an user is connected
     * @param username
     * @return 
     */
    static Boolean isConnected(String username) {
        for (SocketConnection c : connections) {
            if (c.getUser() != null) {
                if (c.getUser().getUsername().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the connection object of an user
     * @param username
     * @return 
     */
    static SocketConnection getConnection(String username) {
        for (SocketConnection c : connections) {
            if (c.getUser() != null) {
                if (c.getUser().getUsername().equals(username)) {
                    return c;
                }
            }
        }
        return null;
    }
}
