import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;

public class NTPClient {

	public static void main(String[] args) throws IOException {

		String serverName = "localhost";

		// Send request
		DatagramSocket socket = new DatagramSocket();
		InetAddress address = InetAddress.getByName(serverName);

		NTPMessage msg = new NTPMessage();

		// Print "sending request"
		System.out.println("sending request");

		// Set the transmit timestamp just before sending the packet
		msg.transmitTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;

		byte[] buf = msg.toByteArray();

		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 1000);

		socket.send(packet);

		// Get response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);

		// Immediately record the incoming timestamp
		double destinationTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;

		// Process response
		NTPMessage respMsg = new NTPMessage(packet.getData());

		// Corrected, according to RFC2030 errata
		double roundTripDelay = (destinationTimestamp - respMsg.originateTimestamp)
				- (respMsg.transmitTimestamp - respMsg.receiveTimestamp);

		double localClockOffset = ((respMsg.receiveTimestamp - respMsg.originateTimestamp)
				+ (respMsg.transmitTimestamp - destinationTimestamp)) / 2;

		// Display response
		System.out.println("NTP reply received from server:");
		System.out.println(respMsg.toString());

		System.out.println("Dest. timestamp:     " + NTPMessage.timestampToString(destinationTimestamp));

		System.out.println("Round-trip delay: " + new DecimalFormat("0.00").format(roundTripDelay * 1000) + " ms");

		System.out.println("Local clock offset: " + new DecimalFormat("0.00").format(localClockOffset * 1000) + " ms");

		socket.close();
	}
}
