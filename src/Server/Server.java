package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the server of the application
 * You should run only one instance of this server and every client connects here
 * @author sam
 */
public class Server {

    public static final int PORT = 5000; // porta al di fuori del range 1-4096 !
    public static ArrayList<SocketConnection> connections = new ArrayList<SocketConnection>(0);
    public static HashMap<String, String> codes = new HashMap<String, String>();

    /**
     * Run me!
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        ServerSocket serverSocket = new ServerSocket(PORT);
        
        System.out.println("Server: Up and running");
        System.out.println("Server: Server socket " + serverSocket);
        initializeCodes();
            
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
     * @param clientSocket 
     */
    static void endConnection(Socket clientSocket) {
        for (int i = 0; i < Server.connections.size(); i++) {
            if (Server.connections.get(i).getClientSocket() != null) {
                if (Server.connections.get(i).getClientSocket().equals(clientSocket)) {
                    System.out.println("Server: Client disconnected - " + clientSocket.toString());
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
        for (SocketConnection c: connections) {
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
        for (SocketConnection c: connections) {
            if (c.getUser() != null) {
                if (c.getUser().getUsername().equals(username)) {
                    return c;
                }
            }
        }
        return null;
    }
    
    static void initializeCodes() {
        if (!codes.isEmpty())
            return;
        codes.put("0x000", "Chat message");
        codes.put("0x100", "Connected");
        codes.put("0x101", "Logged in");
        codes.put("0x102", "Signed up");
        codes.put("0x103", "Connected to destination");
        codes.put("0x104", "Help");
        codes.put("0x105", "Disconnected from every destination");
        codes.put("0x1FF", "Disconnect");
        codes.put("0x200", "User doesn't exist");
        codes.put("0x201", "Incorrect username or password");
        codes.put("0x202", "Login syntax error, specify username and password");
        codes.put("0x210", "User already exists");
        codes.put("0x211", "Register syntax error, specify username and password");
        codes.put("0x220", "Destination user not found");
        codes.put("0x221", "Destination syntax error, specify destination");
        codes.put("0x230", "User not authenticated");
        codes.put("0x231", "User already authenticated");
        codes.put("0x2FF", "An error occured, try again later");
    }
}
