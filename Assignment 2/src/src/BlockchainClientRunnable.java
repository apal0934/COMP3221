import java.io.*;
import java.net.Socket;

public class BlockchainClientRunnable implements Runnable {

    private String reply;
    private int serverNumber;
    private String serverName;
    private int portNumber;
    private String message;

    public BlockchainClientRunnable(int serverNumber, String serverName, int portNumber, String message) {
        this.reply = "Server" + serverNumber + ": " + serverName + " " + portNumber + "\n"; // header string
        this.serverName = serverName;
        this.serverNumber = serverNumber;
        this.portNumber = portNumber;
        this.message = message;
    }

    public void run() {
        // implement your code here
        try {
            Socket requests  = new Socket(serverName, portNumber);
            OutputStream serverOutputStream = requests.getOutputStream();
            InputStream serverInputStream = requests.getInputStream();

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
            PrintWriter outWriter = new PrintWriter(serverOutputStream, true);

            outWriter.println(message);
            outWriter.flush();


            String serverResponse;
            //System.out.println("Probably blocked here");
            Thread.sleep(100);
            while (inputReader.ready()) {
                serverResponse = inputReader.readLine() + "\n";
                //System.out.println("Server response: " + serverResponse);
                reply += serverResponse;
            }
            outWriter.println("cc");
            outWriter.flush();
            outWriter.close();
            inputReader.close();
        } catch (IOException ioe) {
            reply += "Server is not available\n\n";
        } catch (InterruptedException ie) {
            reply += "Server is not available\n\n";
        } catch (Exception e) {
            reply += "Server is not available\n\n";
        }
    }

    public String getReply() {
        return reply;
    }

    // implement any helper method here if you need any
}