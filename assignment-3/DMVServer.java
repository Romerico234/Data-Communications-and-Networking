import java.io.*;
import java.net.*;
import java.util.*;

public class DMVServer {
    public static void main(String[] args) {
        int serverPort = 12121;

        // R2's initial routing table
        Map<String, Integer> routingTable = new HashMap<>();
        routingTable.put("1.2.3.0", 4);
        routingTable.put("1.2.4.0", 8);

        // Copy of the original routing table for printing
        Map<String, Integer> originalRoutingTable = new HashMap<>(routingTable);

        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Server listening on port " + serverPort);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // R1's routing table received from the client
            Map<String, Integer> r1RoutingTable = new HashMap<>();

            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("END")) {
                    break;
                }
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    String destination = parts[0];
                    int cost = Integer.parseInt(parts[1]);
                    r1RoutingTable.put(destination, cost);
                }
            }

            // Apply the DVR algorithm
            int costToR1 = 0; // Cost from R2 to R1

            // Print R2's original routing table
            System.out.println("Original routing table:");
            printRoutingTable(originalRoutingTable);

            // Update R2's routing table based on R1's information
            for (Map.Entry<String, Integer> entry : r1RoutingTable.entrySet()) {
                String destination = entry.getKey();
                int costViaR1 = costToR1 + entry.getValue();

                if (!routingTable.containsKey(destination)) {
                    // Add new route
                    routingTable.put(destination, costViaR1);
                } else {
                    int currentCost = routingTable.get(destination);
                    if (costViaR1 < currentCost) {
                        // Update existing route with a better cost
                        routingTable.put(destination, costViaR1);
                    }
                }
            }

            // Print R2's updated routing table
            System.out.println("Updated routing table:");
            printRoutingTable(routingTable);

            // Close connections
            clientSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to print the routing table
    private static void printRoutingTable(Map<String, Integer> routingTable) {
        System.out.println("Destination Network\tCost");
        for (Map.Entry<String, Integer> entry : routingTable.entrySet()) {
            System.out.println(entry.getKey() + "\t\t" + entry.getValue());
        }
    }
}
