
import java.io.*;
import java.net.*;
import java.util.*;

public class DMVClient {

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 12121;

        // R1's routing table
        Map<String, Integer> routingTable = new HashMap<>();
        routingTable.put("1.2.3.0", 2);
        routingTable.put("1.2.4.0", 10);
        routingTable.put("1.2.5.0", 5);

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Send the routing table entries to R2
            for (Map.Entry<String, Integer> entry : routingTable.entrySet()) {
                String line = entry.getKey() + " " + entry.getValue();
                out.println(line);
            }
            // Indicate the end of the routing table
            out.println("END");

            // Close the socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
