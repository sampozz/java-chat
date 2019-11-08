import java.net.*;
import java.io.*;

public class Client {

    public static void main(String[] args) throws IOException {
        
        InetAddress indirizzo;
        if (args.length == 0) {
            indirizzo = InetAddress.getByName(null);
        } else {
            indirizzo = InetAddress.getByName(args[0]);
        }
        
        try {
            
            // Create socket
            Socket socket = new Socket(indirizzo, Server.PORT);
            System.out.println("Client: Client socket: " + socket);
            
            // Create input stream
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            
            // Create output stream
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter out = new PrintWriter(bw, true);
            ClientWriter cw = new ClientWriter(out);
            cw.start();
            
            // Read from server and print answer
            while (true) {
                String readline = in.readLine();
                System.out.println(readline);

                // Disconnect and close socket and stream
                if (readline.equals("Server: Disconnecting...")) {
                    out.close();
                    bw.close();
                    osw.close();
                    in.close();
                    isr.close();
                    socket.close();
                }
                
            }
            
        // Error during connection to the server
        } catch (UnknownHostException e) {
            System.out.println("Client: Unknown host, " + indirizzo);
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
