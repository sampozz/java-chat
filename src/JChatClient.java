import java.net.*;
import java.io.*;

public class JChatClient {

    public static void main(String[] args) throws IOException {
        
        InetAddress indirizzo;
        if (args.length == 0) {
            indirizzo = InetAddress.getByName(null);
        } else {
            indirizzo = InetAddress.getByName(args[0]);
        }
        
        try {
            
            // creazione socket
            Socket socket = new Socket(indirizzo, JChatServer.PORT);
            System.out.println("JChatClient: Socket del client: " + socket);
            
            // creazione stream di input da socket
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            
            // creazione stream di output su socket
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter out = new PrintWriter(bw, true);
            ClientWriter cw = new ClientWriter(out);
            cw.start();
            
            // ciclo di lettura da server e print della risposta
            while (true) {
                
                String readline = in.readLine();
                System.out.println(readline);
                if (readline.equals("JChatServer: Disconnessione...")) {
                    out.close();
                    bw.close();
                    osw.close();
                    in.close();
                    isr.close();
                    socket.close();
                }
                
            }
            
        // Errore durante la connessione al server
        } catch (UnknownHostException e) {
            System.out.println("JChatClient: Host sconosciuto, " + indirizzo);
        // Errore di trasmissione
        } catch (IOException e) {
            System.out.println("JChatClient: Chiusura trasmissione...");
        // Gestione eccezioni
        } catch (Exception e) { 
            System.out.println(e);
        }
        System.out.println("JChatClient: Connessione terminata, arrivederci");
    }
}
