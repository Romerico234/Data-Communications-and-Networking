import java.io.*;
import java.net.*;

class DMVA1TCPServer {

    public static void main(String argv[]) throws Exception {

        ServerSocket welcomeSocket = new ServerSocket(33221);

        while (true) {

            Socket connectionSocket = welcomeSocket.accept();

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String clientDomainName = inFromClient.readLine();
            System.out.println("Recieved domain name:" + clientDomainName);

            String webPageContentLines;
            System.out.println("Webpage content:");
            while ((webPageContentLines = inFromClient.readLine()) != null) {
                System.out.println(webPageContentLines);
            }
        }
    }
}