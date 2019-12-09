package ConsoleClient;

import java.net.*;
import java.io.*;

/**
 * This is the client of the application
 * This client can be distributed to every user
 * It can connect to the server to start the chat
 * @author sam
 */
public class ConsoleClient {

    public static void main(String[] args) throws IOException {
        
        String serverAddress = "127.0.0.1";
        
        try {
            
            // Create socket
            Socket socket = new Socket(serverAddress, 5000);
            System.out.println("Client: Client socket: " + socket);
            
            // Create input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Create output stream
            ConsoleWriter cw = new ConsoleWriter(new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true
                ));
            cw.start();
            
            // Read from server and print answer
            while (true) {
                String readline = in.readLine();
                System.out.println(readline);

                // Disconnect and close socket and stream
                if (readline.equals("Server: Disconnecting...")) {
                    in.close();
                    socket.close();
                }
                
            }
            
        // Error during connection to the server
        } catch (UnknownHostException e) {
            System.out.println("Client: Unknown host, " + serverAddress);
        // Transmission error
        } catch (IOException e) {
            System.out.println("Client: Closing transmission...");
        // Exception handler
        } catch (Exception e) { 
            System.out.println(e);
        }
        System.out.println("Client: Connection ended, goodbye");
    }
}
