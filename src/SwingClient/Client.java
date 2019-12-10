package SwingClient;

import java.io.*;
import java.net.*;
import java.util.Observable;

/**
 *
 * @author sam
 */
public class Client extends Observable implements Runnable {
    
    private String msg = "";
    private PrintWriter pw = null;
    
    public Client() { }

    @Override
    public void run() {
        String serverAddress = "127.0.0.1";
        
        try {
            
            // Create socket
            Socket socket = new Socket(serverAddress, 5000);
            System.out.println("Client: Client socket: " + socket);
            
            // Create input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Create output stream
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            
            // Read from server and print answer
            while (true) {
                String readline = in.readLine().replace(" ", "&nbsp;");
                if (!readline.startsWith("0x") || readline.startsWith("0x104")) {
                    continue;
                }
                if (!readline.startsWith("0x000")) {
                    readline = "<center>" + readline.substring(5) + "</center>";
                } else {
                    readline.substring(5);
                }
                setMsg(readline);
                
                
                // Disconnect and close socket and stream
                if (msg.equals("Server: Disconnecting...")) {
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
    
    public void setMsg(String msg) {
        this.msg = msg;
        setChanged();
        notifyObservers();
    }
    
    public String getMsg() {
        return this.msg;
    }
    
    public PrintWriter getPw() {
        return this.pw;
    }
    
}
