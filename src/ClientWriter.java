import java.io.*;

public class ClientWriter extends Thread {
    
    PrintWriter out;
    
    public ClientWriter(PrintWriter out) {    
        this.out = out;
    }
    
    @Override
    public void run() {
        
        // creazione stream di input da tastiera
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        
        try {
            // Attesa di lettura da tastiera ed invio al server
            while (true) {
                userInput = stdIn.readLine();
                out.println(userInput);
                if (userInput.equals("/quit")) {
                    stdIn.close();
                    return;
                }
            }             
        }
        
        // Gestione eccezioni
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
