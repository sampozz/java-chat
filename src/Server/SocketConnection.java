package Server;

import Server.Server;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Every connection to the server is identified by the socket and 
 * the connected user
 * @author sam
 */
public class SocketConnection extends Thread {

    private final Socket clientSocket;
    private User user;

    private InputStreamReader isr;
    private BufferedReader in;
    private PrintWriter out;
    // destOut is an array to allow multiple destinations
    private ArrayList<PrintWriter> destOut;

    public SocketConnection(Socket socket) {
        this.clientSocket = socket;
        this.destOut = new ArrayList<PrintWriter>();
    }

    @Override
    public void run() {
        try {
            System.out.println("Server: Client connected - " 
                    + getClientSocket().getInetAddress() + ", "
                    + getClientSocket().getPort());

            // Create input stream from client socket
            isr = new InputStreamReader(getClientSocket().getInputStream());
            in = new BufferedReader(isr);

            // Create output stream on client socket
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(getClientSocket().getOutputStream())
                ), true);

            // Login or register
            String selection = "";
            do {
                out.println("Server: Welcome!\n"
                    + "[1] Login\n"
                    + "[2] Register\n"
                    + "Select an option: ");
                selection = in.readLine();
            } while (!(selection.equals("1") || selection.equals("2")));
            
            if (selection.equals("1")) {
                // Login
                String username;
                String password;
                while (true) {
                    out.println("Server: Insert your username: ");
                    username = in.readLine();
                    out.println("Server: Insert your password: ");
                    password = in.readLine();
                    user = new User();
                    if (user.login(username, password)) {
                        break;
                    }
                    out.println("Server: Incorrect username or password");
                }
            } else {
                // Register
                String username;
                String password;
                while (true) {
                    out.println("Server: Insert your username: ");
                    username = in.readLine();
                    out.println("Server: Insert your password: ");
                    password = in.readLine();
                    user = new User();
                    int retCode = user.register(username, password);
                    if (retCode == 0) {
                        out.println("Server: Registration completed!");
                        break;
                    } else if (retCode == 1) {
                        out.println("Server: Username already used");
                    } else {
                        out.println("Server: An error occured, try again later");
                    }
                }
            }
            
            // Connection established
            out.println("Server: Hi " + user.getUsername() + "!\n"
                + "/dest <username>     # start chatting\n"
                + "/dest <usr1>, <usr2> # chat with multiple users\n"
                + "/list                # view connected users\n"
                + "/quit                # diconnect");

            // Message receive and redirect
            while (true) {

                // Wait for input from client
                String str = in.readLine();

                // End of transmission, closing socket
                if (str.equals("/quit")) {
                    Server.endConnection(getUser().getUsername());
                    out.println("Server: Disconnecting...");
                    // Closing stream and socket
                    out.close();
                    in.close();
                    getClientSocket().close();
                    break;
                }

                // Set destination
                if (str.split(" ")[0].equals("/dest")) {
                    // Search for destination user
                    String dest = str.replace("/dest ", "").replace(" ", "");
                    setDestination(dest.split(","));
                    continue;
                }

                // Reply to destination
                for (PrintWriter p: destOut) {
                    if (p != null) {
                        p.println(getUser().getUsername() + ": " + str);
                    }
                }
                
            }
        } catch (IOException e) { }
    }

    /**
     * Update destOut with new destination(s)
     * @param dest
     * @throws IOException 
     */
    private void setDestination(String[] dest) throws IOException {
        // Clear old destinations
        destOut.clear();
        for (String destName: dest) {
            if (Server.isConnected(destName)) {
                destOut.add(new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(Server.getConnection(destName).getClientSocket().getOutputStream())
                    ), true));
                out.println("Server: Connection with " + destName + " established, enjoy your chat!");
            } else {
                out.println("Server: Cannot connect to " + destName + ", user is not connected");
            }
        }
    }

    /**
     * @return the clientSocket
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * @return the name
     */
    public User getUser() {
        return this.user;
    }
}
