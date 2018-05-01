import java.io.*;
import java.net.*;

public class DateClient {
    public static void main(String[] args) {
        try {
            Socket requests = new Socket("localhost", 6013);
            InputStream serverInputStream = requests.getInputStream();
            BufferedReader bin = new BufferedReader(new InputStreamReader(serverInputStream));

            String line;
            while ((line = bin.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
