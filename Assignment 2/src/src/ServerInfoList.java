import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class ServerInfoList {

    ArrayList<ServerInfo> serverInfos;

    public ServerInfoList() {
        serverInfos = new ArrayList<>();
    }

    public void initialiseFromFile(String filename) {
        // implement your code here
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(filename));
            int serverNumber;
            try {
                try {
                    serverNumber = Integer.parseInt(properties.getProperty("servers.num"));
                } catch (Exception e) {
                    serverInfos.add(null);
                    return;
                }
                for (int i = 0; i < serverNumber; i++) {
                    String host = String.format("server%d.host", i);
                    String port = String.format("server%d.port", i);

                    if (properties.containsKey(host) && properties.containsKey(port)) {
                        if (Integer.parseInt(properties.getProperty(port)) >= 1024 && Integer.parseInt(properties.getProperty(port)) <= 65535) {
                            /*if (properties.getProperty(host).matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")) {

                            }*/
                            if (!properties.getProperty(host).isEmpty()) {
                                ServerInfo server = new ServerInfo(properties.getProperty(host), Integer.parseInt(properties.getProperty(port)));
                                serverInfos.add(server);
                            } else {
                                serverInfos.add(null);
                            }

                        } else {
                            serverInfos.add(null);
                        }
                    } else {
                        serverInfos.add(null);
                    }
                }
            } catch (NumberFormatException nfe) {

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ServerInfo> getServerInfos() {
        return serverInfos;
    }

    public void setServerInfos(ArrayList<ServerInfo> serverInfos) {
        this.serverInfos = serverInfos;
    }

    public boolean addServerInfo(ServerInfo newServerInfo) { 
        // implement your code here
        serverInfos.add(newServerInfo);
        return true;
    }

    public boolean updateServerInfo(int index, ServerInfo newServerInfo) { 
        // implement your code here
        serverInfos.set(index, newServerInfo);
        return true;
    }

    public boolean removeServerInfo(int index) {
        // implement your code here
        serverInfos.set(index, null);
        return true;
    }

    public boolean clearServerInfo() { 
        // implement your code here
        serverInfos.clear();
        return true;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < serverInfos.size(); i++) {
            if (serverInfos.get(i) != null) {
                s += "Server" + i + ": " + serverInfos.get(i).getHost() + " " + serverInfos.get(i).getPort() + "\n";
            }
        }
        return s;
    }

    // implement any helper method here if you need any
}