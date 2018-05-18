import java.rmi.server.ExportException;
import java.util.Scanner;
import java.util.ArrayList;
public class BlockchainClient {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }
        String configFileName = args[0];

        ServerInfoList pl = new ServerInfoList();
        pl.initialiseFromFile(configFileName);

        Scanner sc = new Scanner(System.in);
        BlockchainClient bc = new BlockchainClient();

        while (sc.hasNextLine()) {
            String message = sc.nextLine();
            // implement your code here
            if (message == null) {
                return;
            }

            if (message.startsWith("ad")) {
                try {
                    String tokens[] = message.split("\\|");
                    ServerInfo newServerInfo = new ServerInfo(tokens[1], Integer.parseInt(tokens[2]));
                    pl.addServerInfo(newServerInfo);
                    System.out.print("Succeeded\n\n");
                } catch (Exception e) {
                    System.out.print("Failed\n\n");
                }
            } else if (message.startsWith("rm")) {
                try {
                    String tokens[] = message.split("\\|");
                    pl.getServerInfos().set(Integer.parseInt(tokens[1]), null);
                    System.out.print("Succeeded\n\n");
                } catch (Exception e) {
                    System.out.print("Failed\n\n");
                }
            } else if (message.startsWith("up")) {
                try {
                    String tokens[] = message.split("\\|");
                    ServerInfo newServerInfo = new ServerInfo(tokens[2], Integer.parseInt(tokens[3]));
                    pl.updateServerInfo(Integer.parseInt(tokens[1]), newServerInfo);
                    System.out.print("Succeeded\n\n");
                } catch (Exception e){
                    System.out.print("Failed\n\n");
                    e.printStackTrace();
                }
            } else if (message.startsWith("tx")) {
                bc.broadcast(pl, message);
            } else if (message.startsWith("pb")) {
                String tokens[] = message.split("\\|");
                switch (tokens.length) {
                    case 1 :
                        bc.broadcast(pl, "pb");
                        break;
                    case 2 :
                        bc.unicast(Integer.parseInt(tokens[1]), pl.getServerInfos().get(Integer.parseInt(tokens[1])), "pb");
                        break;
                    default :
                        ArrayList<Integer> serverIndices = new ArrayList<Integer>(tokens.length - 1);
                        for (int i = 1; i < tokens.length; i++) {
                            serverIndices.add(Integer.parseInt(tokens[i]));
                        }
                        bc.multicast(pl, serverIndices, "pb");
                        break;
                }
            } else if (message.equals("ls")) {
                System.out.print(pl.toString() + "\n");

            } else if (message.equals("cl")) {
                try {
                    for (int i = 0; i < pl.getServerInfos().size(); i++) {
                        if (pl.getServerInfos().get(i) == null) {
                            pl.getServerInfos().remove(i);
                        }
                    }
                    System.out.print("Succeeded\n\n");
                } catch (Exception e) {
                    System.out.print("Failed\n\n");
                }
            } else if (message.startsWith("sd")) {
                return;
            } else {
                System.out.print("Unknown Command\n\n");
            }
        }
    }

    public void unicast (int serverNumber, ServerInfo p, String message) {
        // implement your code here
        BlockchainClientRunnable bcr = new BlockchainClientRunnable(serverNumber, p.getHost(), p.getPort(), message);
        Thread unicast = new Thread(bcr);
        unicast.start();
        try {
            unicast.join();
            System.out.print(bcr.getReply());
        } catch (InterruptedException ie) {

        }
    }

    public void broadcast (ServerInfoList pl, String message) {
        // implement your code here
        ArrayList<ServerInfo> p = pl.getServerInfos();
        Thread threads[] = new Thread[p.size()];
        BlockchainClientRunnable bcrs[] = new BlockchainClientRunnable[p.size()];
        for (int i = 0; i < p.size(); i++) {
            if (p.get(i) != null) {
                BlockchainClientRunnable bcr = new BlockchainClientRunnable(i, p.get(i).getHost(), p.get(i).getPort(), message);
                bcrs[i] = bcr;
                Thread broadcast = new Thread(bcr);
                threads[i] = broadcast;
                broadcast.start();
            }

        }
        for (Thread thread : threads) {
            try {
                if (thread != null) {
                    thread.join();
                }
            } catch (InterruptedException ie) {
            }
        }
        for (BlockchainClientRunnable bcr : bcrs) {
            if (bcr != null) {
                System.out.print(bcr.getReply());
            }
        }



    }

    public void multicast (ServerInfoList serverInfoList, ArrayList<Integer> serverIndices, String message) {
        // implement your code here
        ArrayList<ServerInfo> p = serverInfoList.getServerInfos();
        Thread threads[] = new Thread[serverIndices.size()];
        BlockchainClientRunnable bcrs[] = new BlockchainClientRunnable[serverIndices.size()];
        for(int i = 0; i < serverIndices.size(); i++) {
            int index = serverIndices.get(i);
            BlockchainClientRunnable bcr = new BlockchainClientRunnable(index, p.get(index).getHost(), p.get(index).getPort(), message);
            bcrs[i] = bcr;
            Thread multicast = new Thread(bcr);
            threads[i] = multicast;
            multicast.start();
        }
        for (Thread thread : threads) {
            try {
                if (thread != null) {
                    thread.join();
                }
            } catch (InterruptedException ie) {
            }
        }
        for (BlockchainClientRunnable bcr : bcrs) {
            if (bcr != null) {
                System.out.print(bcr.getReply());
            }
        }
    }

    // implement any helper method here if you need any
}