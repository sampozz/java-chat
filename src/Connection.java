import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Connection extends Thread {

    private Socket clientSocket;
    private User user;

    private InputStreamReader isr;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<PrintWriter> destOut;

    public Connection(Socket socket) {
        this.clientSocket = socket;
        this.destOut = new ArrayList<PrintWriter>();
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
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(getClientSocket().getOutputStream())
                ), true);

            // Save username
            out.println("Server: Welcome!");
            String username;
            do {
                out.println("Server: Insert a username: ");
                username = in.readLine();
                user = new User();
            } while(!user.login(username, "justastupidpassword"));

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

    private void setDestination(String[] dest) throws IOException {
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
