import java.io.*;
import java.net.*;
import java.util.*;

public class NTPServer {

    private static final int NTPPort = 1000;

    public static void main(String[] args) {
        try {
            System.out.println("Server started!");

            DatagramSocket serverSocket = new DatagramSocket(NTPPort);

            while (true) {
                byte[] receiveData = new byte[48];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                serverSocket.receive(receivePacket);

                // Start a new thread to handle the request
                ClientHandler handler = new ClientHandler(serverSocket, receivePacket);
                Thread t = new Thread(handler);
                t.start();
            }
        } catch (SocketException e) {
            System.err.println("Can't open socket");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Communication error!");
            e.printStackTrace();
        }
    }

    // Define the client handler class
    public static class ClientHandler implements Runnable {

        private DatagramSocket serverSocket;
        private DatagramPacket receivePacket;

        public ClientHandler(DatagramSocket serverSocket, DatagramPacket receivePacket) {
            this.serverSocket = serverSocket;
            this.receivePacket = receivePacket;
        }

        @Override
        public void run() {
            try {
                System.out.println("starting client thread");

                // Generate random delay
                Random rand = new Random();
                int d = rand.nextInt(10000) + 1; // Generates a random integer between 1 and 10000

                System.out.println("d=" + d);

                // Delay for d milliseconds
                Thread.sleep(d);

                // Process the NTP request and send reply
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                byte[] buf = receivePacket.getData();

                // Create NTPMessage from the received data
                NTPMessage msg = new NTPMessage(buf);

                // Create new NTPMessage for the response
                NTPMessage responseMsg = new NTPMessage();

                // Set response fields
                responseMsg.leapIndicator = 0;
                responseMsg.version = msg.version;
                responseMsg.mode = 4; // server mode
                responseMsg.stratum = 1;
                responseMsg.pollInterval = msg.pollInterval;
                responseMsg.precision = msg.precision;
                responseMsg.rootDelay = 0;
                responseMsg.rootDispersion = 0;
                responseMsg.referenceIdentifier = new byte[] {0, 0, 0, 0};
                responseMsg.referenceTimestamp = 0;

                // Copy client's transmit timestamp to originate timestamp
                responseMsg.originateTimestamp = msg.transmitTimestamp;

                // Set receive timestamp to current time
                responseMsg.receiveTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;

                // Set transmit timestamp to current time
                responseMsg.transmitTimestamp = responseMsg.receiveTimestamp;

                // Convert NTPMessage to byte array
                byte[] responseData = responseMsg.toByteArray();

                // Send response
                DatagramPacket sendPacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);

                System.out.println("stopping client thread");

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
