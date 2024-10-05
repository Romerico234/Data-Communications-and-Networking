import java.io.*;
import java.net.*;

class DMVA1TCPClient {
  public static void main(String argv[]) throws Exception {
    String domainName;
    String webPageLines;

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    Socket clientSocket = new Socket("localhost", 33221);

    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

    System.out.println("enter the last two components in the domain name for a web server W as a string exactly in\n" +
        "the form name.suf (for example, enter towson.edu for Towson University's web server W");

    domainName = inFromUser.readLine();

    outToServer.writeBytes(domainName + '\n');

    try {
      URI uri = new URI("https://" + domainName);
      URL url = uri.toURL();

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", "Mozilla/5.0");
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      while ((webPageLines = in.readLine()) != null) {
        System.out.println(webPageLines);
        outToServer.writeBytes(webPageLines + "\n");

      }
      in.close();

    } catch (Exception e) {
      System.out.println("Error: Unable to connect to server");
    }
    clientSocket.close();
  }

}
