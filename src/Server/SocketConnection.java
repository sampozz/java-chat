package Server;

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
    private User user = null;

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
            
            out.println("0x100 " + Server.codes.get("0x100"));
            out.println(getHelp());

            // Message receive and redirect
            while (true) {

                // Wait for input from client
                String str = in.readLine();
                
                // Execute command
                if (str.charAt(0) == '/') {
                    String retCode = runCommand(str);
                    out.println(retCode + " " + Server.codes.get(retCode));
                    continue;
                }
                
                // User not authenticated
                if (user == null) {
                    out.println("0x230 " + Server.codes.get("0x230"));
                    continue;
                }

                // Reply to destination
                for (PrintWriter p: destOut) {
                    if (p != null) {
                        p.println("0x000 " + getUser().getUsername() + ": " + str);
                    }
                }
                
            }
        } catch (IOException e) { }
    }
    
    /**
     * Return command code:
     * 1 - Quit
     * 2 - Set Destination
     * 3 - List connected users
     * 4 - Login
     * 5 - Register
     * 42 - Help
     * @param msg
     * @return 
     */
    private String runCommand(String msg) throws IOException {
        String[] parse = msg.split(" ");
        if (parse[0].equals("/quit"))
            return cmdQuit();
        if (parse[0].equals("/dest"))
            return cmdDest(msg);
        if (parse[0].equals("/list"))
            return "";
        if (parse[0].equals("/login"))
            return cmdLogin(msg);
        if (parse[0].equals("/register"))
            return cmdRegister(msg);
        return getHelp();
    }
    
    private String getHelp() {
        return "0x104 Commands:\n"
                + "/dest username       # start chatting\n"
                + "/dest usr1, usr2     # chat with multiple users\n"
                + "/list                # view connected users\n"
                + "/login user, pwd     # login\n"
                + "/register user, pwd  # register\n"
                + "/quit                # diconnect\n"
                + "/help                # get help";
    }
    
    /**
     * Register user
     * @param param
     * @return 
     */
    private String cmdRegister(String param) {
        if (user != null) {
            // already authenticated
            return "0x231";
        }
        if (!param.contains(",")) {
            // invalid syntax
            return "0x211";
        }
        String[] parse = param.replace("/register", ""). replace(" ", "").split(",");
        if (parse.length != 2) {
            // invalid syntax
            return "0x211";
        }
        user = new User();
        int retCode = user.register(parse[0], parse[1]);
        if (retCode == 0) {
            // signup completed
            return "0x102";
        }
        user = null;
        if (retCode == 1) {
            // user already exist
            return "0x210";
        }
        // unknown error
        return "0x2FF"; 
    }
    
    /**
     * Login user
     * @param param
     * @return 
     */
    private String cmdLogin(String param) {
        if (user != null) {
            // already authenticated
            return "0x231";
        }
        if (!param.contains(",")) {
            // syntax error
            return "0x202";
        }
        String[] parse = param.replace("/login", ""). replace(" ", "").split(",");
        if (parse.length != 2) {
            // syntax error
            return "0x202";
        }
        user = new User();
        if (user.login(parse[0], parse[1])) {
            // successful
            return "0x101";
        }
        user = null;
        // incorrect username or password
        return "0x201";
    }
    
    /**
     * Update destOut with new destination(s)
     * @param param
     * @return
     * @throws IOException 
     */
    private String cmdDest(String param) throws IOException {
        // User not authenticated
        if (user == null) {
            return "0x230";
        }
        String[] dest = param.replace("/dest ", "").replace(" ", "").split(",");
        // Validate Command
        if (dest[0].equals("")) {
            return "0x221";
        }
        // Clear old destinations
        destOut.clear();
        String dests = "";
        for (String destName: dest) {
            // Search for destination user
            if (Server.isConnected(destName)) {
                destOut.add(new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(Server.getConnection(destName).getClientSocket().getOutputStream())
                    ), true));
                dests += destName + ", ";
            }
        }
        if (dests.equals("")) {
            // user not found
            return "0x220";
        }
        // successful
        return "0x103";
    }
    
    /**
     * End of transmission, closing socket
     * @return 
     */
    private String cmdQuit() throws IOException {
        Server.endConnection(getClientSocket());
        // Closing stream and socket
        out.close();
        in.close();
        getClientSocket().close();
        // Disconnect
        return "0x1FF";
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
