import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PeriodicHeartBeatRunnable implements Runnable {

    private HashMap<ServerInfo, Date> serverStatus;
    private int sequenceNumber;
    private int port;

    public PeriodicHeartBeatRunnable(HashMap<ServerInfo, Date> serverStatus, int port) {
        this.serverStatus = serverStatus;
        this.sequenceNumber = 0;
        this.port = port;
    }

    @Override
    public void run() {
        while(true) {
            // broadcast HeartBeat message to all peers
            ArrayList<Thread> threadArrayList = new ArrayList<>();
            //System.out.println(serverStatus.size());
            for (ServerInfo si : serverStatus.keySet()) {
                //System.out.println(si.getPort());
                //System.out.println(si.getHost());
                Thread thread = new Thread(new HeartBeatClientRunnable(si.getHost(), "hb|" + port + "|" + sequenceNumber, si.getPort()));
                threadArrayList.add(thread);
                thread.start();
            }

            for (Thread thread : threadArrayList) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                }
            }

            // increment the sequenceNumber
            sequenceNumber += 1;

            // sleep for two seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
    }
}