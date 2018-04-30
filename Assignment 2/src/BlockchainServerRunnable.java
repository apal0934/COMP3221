import java.io.*;
import java.net.Socket;

public class BlockchainServerRunnable implements Runnable{

    private Socket clientSocket;
    private Blockchain blockchain;

    public BlockchainServerRunnable(Socket clientSocket, Blockchain blockchain) {
        // implement your code here
        this.clientSocket = clientSocket;
        this.blockchain = blockchain;
    }

    public void run() {
        // implement your code here
        try {
            InputStream clientInputStream = clientSocket.getInputStream();
            OutputStream clientOutputStream = clientSocket.getOutputStream();
            serverHandler(clientInputStream, clientOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


    }

    public void serverHandler (InputStream clientInputStream, OutputStream clientOutputStream) {
        BufferedReader inputReader =new BufferedReader( new InputStreamReader(clientInputStream) );
        PrintWriter outWriter = new  PrintWriter(clientOutputStream, true);

        // read and execute requests
        try {
            while (true) {
                String request = inputReader.readLine();
                if (request == null) {
                    return;
                }

                switch (request) {
                    case "cc" :
                        outWriter.close();
                        break;
                    case "pb" :
                        outWriter.print(blockchain.toString() + "\n");
                        outWriter.flush();
                        break;
                    default :
                        if (request.startsWith("tx")) {
                            boolean res = blockchain.addTransaction(request);
                            if (res) {
                                outWriter.print("Accepted\n\n");
                                outWriter.flush();
                            } else {
                                outWriter.print("Rejected\n\n");
                                outWriter.flush();
                            }
                        } else {
                            outWriter.print("Error\n\n");
                            outWriter.flush();
                        }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
    
    // implement any helper method here if you need any
}
