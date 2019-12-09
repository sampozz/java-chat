package ConsoleClient;

import java.io.*;

/**
 * This thread allows user input while the client is receiving 
 * data from the server
 * @author sam
 */
public class ConsoleWriter extends Thread {
    
    PrintWriter out;
    
    public ConsoleWriter(PrintWriter out) {    
        this.out = out;
    }
    
    @Override
    public void run() {
        
        // Create input stream from keyboard
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        
        try {
            // Wait for input from keyboard
            while (true) {
                userInput = stdIn.readLine();
                out.println(userInput);
                if (userInput.equals("/quit")) {
                    stdIn.close();
                    return;
                }
            }             
        }
        
        // Exception handler
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
