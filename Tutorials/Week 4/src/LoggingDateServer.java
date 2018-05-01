import java.io.*;
import java.net.*;


public class LoggingDateServer {
    public static void main(String[] args) {
        String log, date;
        int index = 0;
        try {
            ServerSocket serverSocket = new ServerSocket(6013);

            while (true) {
                Socket socket = serverSocket.accept();
                PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
                date = new java.util.Date().toString();
                log = "log" + index + ".txt";
                PrintWriter logger = new PrintWriter(log, "UTF-8");
                logger.println(socket.getInetAddress().getHostName());
                logger.println(date);
                logger.close();
                index++;
                pout.println(date);
            }

        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
