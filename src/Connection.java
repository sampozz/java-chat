import java.io.*;
import java.net.Socket;

public class Connection extends Thread {
    
    private Socket clientSocket;
    private String name;
    
    public Connection(Socket socket) {
        this.clientSocket = socket;
    }
    
    private Boolean checkName(String n, PrintWriter out) {
        for (int i = 0; i < JChatServer.sockets.size(); i++) {
            if (JChatServer.sockets.get(i).getConnectionName() != null) {
                if (JChatServer.sockets.get(i).getConnectionName().equals(n)) {
                    out.println("JChatServer: Username già utilizzato! ");
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("JChatServer: Connessione avvenuta " + getClientSocket().getInetAddress() + ", " + getClientSocket().getPort());

            // creazione stream di input da clientSocket:
            InputStreamReader stringaIn = new InputStreamReader(getClientSocket().getInputStream());
            BufferedReader in = new BufferedReader(stringaIn);

            // creazione stream di output su clientSocket:
            OutputStreamWriter stringaOut = new OutputStreamWriter(getClientSocket().getOutputStream());
            BufferedWriter buffer = new BufferedWriter(stringaOut);
            PrintWriter out = new PrintWriter(buffer, true);
            OutputStreamWriter destStringaOut = null;
            BufferedWriter destBuffer = null;
            PrintWriter destOut = null;
            
            // salvataggio username
            out.println("JChatServer: Benvenuto!");
            String tname;
            do {
                out.println("JChatServer: Inserisci il tuo username: ");
                tname = in.readLine();
            } while (!checkName(tname, out));
            this.name = tname;
            out.println("JChatServer: Ciao " + getConnectionName() + "!\n/dest <username>   # inizia la chat\n/quit              # disconnetti");

            // ciclo di ricezione dal client e invio di risposta
            while (true) {
                
                String str = in.readLine();
                
                // Fine trasmissione, chiusura socket
                if (str.equals("/quit")) {
                    for (int i = 0; i < JChatServer.sockets.size(); i++) {
                        if (JChatServer.sockets.get(i).getConnectionName() != null) {
                            if (JChatServer.sockets.get(i).getConnectionName().equals(getConnectionName())) {
                                JChatServer.sockets.remove(i);
                            }
                        }
                    }                    
                    out.println("JChatServer: Disconnessione...");
                    // chiusura di stream e socket
                    out.close();
                    in.close();
                    getClientSocket().close();
                    break;
                    
                // Set destinatario
                } else if (str.contains("/dest")) {
                    // ricerca interlocutore
                    String destName = str.split(" ")[1];
                    Boolean exist = false;
                    for (int i = 0; i < JChatServer.sockets.size(); i++) {
                        if (JChatServer.sockets.get(i).getConnectionName() != null) {
                            if (JChatServer.sockets.get(i).getConnectionName().equals(destName)) {
                                exist = true;
                                destStringaOut = new OutputStreamWriter(JChatServer.sockets.get(i).getClientSocket().getOutputStream());
                                destBuffer = new BufferedWriter(destStringaOut);
                                destOut = new PrintWriter(destBuffer, true);
                                out.println("Connessione con " + destName + " avvenuta, enjoy your chat!");
                            }
                        }
                    }
                    if (!exist) {
                        out.println("JChatServer: username destinatario non trovato!");
                    }
                    
                // Inoltro messaggio a destinatario
                } else if (destOut != null) {
                    destOut.println(getConnectionName() + ": " + str);
                }
            }
        } catch (IOException e) { }
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
