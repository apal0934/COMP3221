import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockchainServer {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }

        int portNumber = Integer.parseInt(args[0]);
        Blockchain blockchain = new Blockchain();

        PeriodicCommitRunnable pcr = new PeriodicCommitRunnable(blockchain);
        Thread pct = new Thread(pcr);
        pct.start();

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            while (true) {
                Socket socket = serverSocket.accept();
                BlockchainServerRunnable bsr = new BlockchainServerRunnable(socket, blockchain);
                Thread server = new Thread(bsr);
                server.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // implement your code here
        pcr.setRunning(false);
        try {
            pct.join();
        } catch (InterruptedException e) {
            return;
        }
    }

    // implement any helper method here if you need any
}
