import java.io.*;
import java.net.Socket;

public class Connection extends Thread {

    private Socket clientSocket;
    private String name;

    public Connection(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Server: Client connected - " + getClientSocket().getInetAddress() + ", "
                    + getClientSocket().getPort());

            // Create input stream from client socket
            InputStreamReader stringaIn = new InputStreamReader(getClientSocket().getInputStream());
            BufferedReader in = new BufferedReader(stringaIn);

            // Create output stream on client socket
            OutputStreamWriter stringaOut = new OutputStreamWriter(getClientSocket().getOutputStream());
            BufferedWriter buffer = new BufferedWriter(stringaOut);
            PrintWriter out = new PrintWriter(buffer, true);
            OutputStreamWriter destStringaOut = null;
            BufferedWriter destBuffer = null;
            PrintWriter destOut = null;

            // Save username
            out.println("Server: Welcome!");
            while (true) {
                out.println("Server: Insert a username: ");
                String tmpname = in.readLine();
                if (!userExist(tmpname) && !tmpname.equals("broadcast")) {
                    this.name = tmpname;
                    break;
                }
                out.println("Server: Username already taken! ");
            }
            out.println("Server: Hi " + getConnectionName()
                    + "!\n/dest <username>   # start chatting\n/quit              # diconnect");

            // Message receive and redirect
            while (true) {

                // Wait for input from client
                String str = in.readLine();

                // End of transmission, closing socket
                if (str.equals("/quit")) {
                    for (int i = 0; i < Server.sockets.size(); i++) {
                        if (Server.sockets.get(i).getConnectionName() != null) {
                            if (Server.sockets.get(i).getConnectionName().equals(getConnectionName())) {
                                Server.sockets.remove(i);
                            }
                        }
                    }
                    out.println("Server: Disconnecting...");
                    // Closing stream and socket
                    out.close();
                    in.close();
                    getClientSocket().close();
                    break;
                }

                // Set destination
                if (str.contains("/dest")) {
                    // Search for destination user
                    String destName = str.split(" ")[1];
                    if (userExist(destName)) {
                        destStringaOut = new OutputStreamWriter(getDestSocket(destName).getClientSocket().getOutputStream());
                        destBuffer = new BufferedWriter(destStringaOut);
                        destOut = new PrintWriter(destBuffer, true);
                        out.println("Server: Connection with " + destName + " established, enjoy your chat!");
                    } else if (destName.equals("broadcast")) {
                        // Send broadcast messages
                    } else {
                        out.println("Server: Destination username not found!");
                    }
                }

                // Reply to destination
                if (destOut != null) {
                    destOut.println(getConnectionName() + ": " + str);
                }
            }
        } catch (IOException e) {
        }
    }

    private Boolean userExist(String n) {
        for (int i = 0; i < Server.sockets.size(); i++) {
            if (Server.sockets.get(i).getConnectionName() != null) {
                if (Server.sockets.get(i).getConnectionName().equals(n)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Connection getDestSocket(String n) {
        for (int i = 0; i < Server.sockets.size(); i++) {
            if (Server.sockets.get(i).getConnectionName() != null) {
                if (Server.sockets.get(i).getConnectionName().equals(n)) {
                    return Server.sockets.get(i);
                }
            }
        }
        return null;
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
    public String getConnectionName() {
        return name;
    }
}
