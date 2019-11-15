import java.io.*;
import java.net.Socket;

public class User extends Thread {

    private Socket clientSocket;
    private String name;

    InputStreamReader isr;
    BufferedReader in;
    OutputStreamWriter osw;
    BufferedWriter bufferedWriter;
    PrintWriter out;
    OutputStreamWriter destOsw;
    BufferedWriter destBufferedWriter;
    PrintWriter destOut;

    public User(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Server: Client connected - " + getClientSocket().getInetAddress() + ", "
                    + getClientSocket().getPort());

            // Create input stream from client socket
            isr = new InputStreamReader(getClientSocket().getInputStream());
            in = new BufferedReader(isr);

            // Create output stream on client socket
            osw = new OutputStreamWriter(getClientSocket().getOutputStream());
            bufferedWriter = new BufferedWriter(osw);
            out = new PrintWriter(bufferedWriter, true);
            destOsw = null;
            destBufferedWriter = null;
            destOut = null;

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
            out.println("Server: Hi " + getUsername() + "!\n"
                + "/dest <username>   # start chatting\n"
                + "/dest broadcast    # send to everyone\n"
                + "/list              # view connected users\n"
                + "/quit              # diconnect");

            // Message receive and redirect
            while (true) {

                // Wait for input from client
                String str = in.readLine();

                // End of transmission, closing socket
                if (str.equals("/quit")) {
                    for (int i = 0; i < Server.connectedUsers.size(); i++) {
                        if (Server.connectedUsers.get(i).getUsername() != null) {
                            if (Server.connectedUsers.get(i).getUsername().equals(getUsername())) {
                                Server.connectedUsers.remove(i);
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
                    if (setDestination(destName)) {
                        out.println("Server: Connection with " + destName + " established, enjoy your chat!");
                    } else {
                        out.println("Server: Destination username not found!");
                    }
                }

                // Reply to destination
                if (destOut != null) {
                    destOut.println(getUsername() + ": " + str);
                }
            }
        } catch (IOException e) { }
    }

    private Boolean userExist(String n) {
        for (int i = 0; i < Server.connectedUsers.size(); i++) {
            if (Server.connectedUsers.get(i).getUsername() != null) {
                if (Server.connectedUsers.get(i).getUsername().equals(n)) {
                    return true;
                }
            }
        }
        return false;
    }

    private User getDestSocket(String n) {
        for (int i = 0; i < Server.connectedUsers.size(); i++) {
            if (Server.connectedUsers.get(i).getUsername() != null) {
                if (Server.connectedUsers.get(i).getUsername().equals(n)) {
                    return Server.connectedUsers.get(i);
                }
            }
        }
        return null;
    }

    private boolean setDestination(String destName) throws IOException {
        if (userExist(destName)) {
            destOsw = new OutputStreamWriter(getDestSocket(destName).getClientSocket().getOutputStream());
            destBufferedWriter = new BufferedWriter(destOsw);
            destOut = new PrintWriter(destBufferedWriter, true);
        } else if (destName.equals("broadcast")) {
            // Send broadcast messages
            
        } else {
            return false;
        }
        return true;
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
    public String getUsername() {
        return name;
    }
}
