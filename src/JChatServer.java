import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class JChatServer {

    public static final int PORT = 5000; // porta al di fuori del range 1-4096 !
    public static ArrayList<Connection> sockets = new ArrayList();

    public static void main(String[] args) throws IOException {
        
        ServerSocket serverSocket = new ServerSocket(PORT);
        
        System.out.println("JChatServer: Up and running");
        System.out.println("JChatServer: Socket del server " + serverSocket);            
            
        while(true) {
            try {
                System.out.println("JChatServer: Waiting for connection...");                
                Socket client = serverSocket.accept();
                
                Connection connessione = new Connection(client);
                connessione.start();   
                sockets.add(connessione);
                
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
