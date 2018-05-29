import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class BlockchainServerRunnable implements Runnable {

    private Socket clientSocket;
    private Blockchain blockchain;
    private HashMap<ServerInfo, Date> serverStatus;

    public BlockchainServerRunnable(Socket clientSocket, Blockchain blockchain, HashMap<ServerInfo, Date> serverStatus) {
        this.clientSocket = clientSocket;
        this.blockchain = blockchain;
        this.serverStatus = serverStatus;
    }

    public void run() {
        try {
            serverHandler(clientSocket.getInputStream(), clientSocket.getOutputStream());
            clientSocket.close();
        } catch (IOException e) {
        }
    }

    public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {
        String localIP = (((InetSocketAddress) clientSocket.getLocalSocketAddress()).getAddress()).toString().replace("/", "");
        String remoteIP = (((InetSocketAddress) clientSocket.getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
        if (remoteIP.equals("localhost")) remoteIP = "127.0.0.1";
        int portLocal = clientSocket.getLocalPort();
        int port = clientSocket.getPort();
        BufferedReader inputReader = new BufferedReader(
                new InputStreamReader(clientInputStream));
        PrintWriter outWriter = new PrintWriter(clientOutputStream, true);

        try {
            while (true) {
                String inputLine = inputReader.readLine();
                if (inputLine == null) {
                    break;
                }

                String[] tokens = inputLine.split("\\|");
                switch (tokens[0]) {
                    case "tx":
                        if (blockchain.addTransaction(inputLine))
                            outWriter.print("Accepted\n\n");
                        else
                            outWriter.print("Rejected\n\n");
                        outWriter.flush();
                        break;
                    case "pb":
                        outWriter.print(blockchain.toString() + "\n");
                        outWriter.flush();
                        break;
                    case "cc":
                        return;
                    case "hb":
                        //System.out.print("Recv: " + inputLine + "\n");
                        ServerInfo si = new ServerInfo(remoteIP, Integer.parseInt(tokens[1]));
                        ServerInfo fuckLocalHost = new ServerInfo("localhost", Integer.parseInt(tokens[1]));
                        if (serverStatus.containsKey(si) || serverStatus.containsKey(fuckLocalHost)) {
                            serverStatus.replace(si, new Date());
                        } else {
                            serverStatus.put(si, new Date());
                        }
                        if (tokens[2].equals("0")) {
                            ArrayList<Thread> threadArrayList = new ArrayList<>();
                            for (ServerInfo si2 : serverStatus.keySet()) {
                                //System.out.print(si2.getHost() + "\nSi2Port: " + si2.getPort() + "\nPort: " + port + "\nPortLocal: " + portLocal + "\n");
                                //System.out.print("RIP: " + remoteIP + "\nlocalIP: " + localIP + "\n");
                                if ((si2.getHost().equals(remoteIP) && si2.getPort() ==  Integer.parseInt(tokens[1])) || (si2.getHost().equals(localIP) && si2.getPort() == portLocal)) {
                                    continue;
                                }
                                Thread thread = new Thread(new HeartBeatClientRunnable(si2.getHost(), "si|" + portLocal + "|" + si.getHost() + "|" + si.getPort(), si2.getPort()));
                                threadArrayList.add(thread);
                                thread.start();
                            }
                            for (Thread thread : threadArrayList) {
                                thread.join();
                            }
                        }
                        break;
                    case "si":
                       // System.out.println("Recv: " + inputLine);
                        boolean exists = false;
                        for (ServerInfo si2 : serverStatus.keySet()) {
                            if (si2.getHost().equals(tokens[2]) && si2.getPort() == Integer.parseInt(tokens[3])) {
                                exists = true;
                            }
                        }
                        if (exists) {
                           // System.out.print("Broke\n");
                            break;
                        }
                        ServerInfo si3 = new ServerInfo(tokens[2], Integer.parseInt(tokens[3]));
                        ArrayList<Thread> threadArrayList = new ArrayList<>();
                        for (ServerInfo si2 : serverStatus.keySet()) {
                            //System.out.print("SIHost: " + si2.getHost() + "\nSIPort: " + si2.getPort() + "\nTKPort: " + tokens[1] + "\n-------------\n");
                            if (si2.getHost().equals(remoteIP) && si2.getPort() == Integer.parseInt(tokens[1])) {
                               // System.out.print("Skipped\n");
                                continue;
                            }
                            Thread thread = new Thread(new HeartBeatClientRunnable(si2.getHost(), "si|" + portLocal + "|" + tokens[2] + "|" + tokens[3], si2.getPort()));
                            threadArrayList.add(thread);
                            thread.start();
                        }

                        for (Thread thread : threadArrayList) {
                            thread.join();
                        }
                        serverStatus.put(si3, new Date());
                    default:
                        outWriter.print("Error\n\n");
                        outWriter.flush();
                }
            }
        } catch (IOException e) {

        } catch (InterruptedException e) {

        }
    }
}