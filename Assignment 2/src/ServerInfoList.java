import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
            int serverNumber = Integer.parseInt(properties.getProperty("servers"));

            for (int i = 0; i < serverNumber; i++) {
                String host = String.format("server%d.host", i);
                String port = String.format("server%d.port", i);

                if (properties.containsKey(host) && properties.containsKey(port)) {
                    if (Integer.parseInt(properties.getProperty(port)) >= 1025 || Integer.parseInt(properties.getProperty(port)) <= 65535) {
                        ServerInfo server = new ServerInfo(properties.getProperty(host), Integer.parseInt(properties.getProperty(port)));
                        serverInfos.add(server);
                    } else {

                    }
                }
            }

            Set<Map.Entry<Object, Object>> entries = properties.entrySet();

            int counter = 0;
            for (Map.Entry<Object, Object> entry : entries) {

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

        return false;
    }

    public boolean updateServerInfo(int index, ServerInfo newServerInfo) { 
        // implement your code here
        return false;
    }
    
    public boolean removeServerInfo(int index) { 
        // implement your code here
        return false;
    }

    public boolean clearServerInfo() { 
        // implement your code here
        return false;
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