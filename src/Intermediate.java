import java.io.*;
import java.net.*;

public class Intermediate {
	DatagramSocket receiveSocket;
	DatagramSocket sendAndReceiveSocket;
	DatagramPacket sendPacket;
	DatagramPacket receivePacket;

	public Intermediate() {
		try {
			receiveSocket = new DatagramSocket(2300);
			sendAndReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	public void process() {
		while (true) {
			// receiving data from client
			byte data[] = new byte[100];
			receivePacket = new DatagramPacket(data, data.length);
			System.out.println("Intermediate: Waiting for Packet.\n");

			// Block until a datagram packet is received from receiveSocket.
			try {
				System.out.println("Waiting..."); // so we know we're waiting
				receiveSocket.receive(receivePacket);
			} catch (IOException e) {
				System.out.print("IO Exception: likely:");
				System.out.println("Receive Socket Timed Out.\n" + e);
				e.printStackTrace();
				System.exit(1);
			}

			// Process the received datagram.
			System.out.println("Intermediate: Packet received:");
			System.out.println("From host: " + receivePacket.getAddress());
			System.out.println("Host port: " + receivePacket.getPort());
			int len = receivePacket.getLength();
			System.out.println("Length: " + len);
			int clientPort = receivePacket.getPort();

			// Form a String from the byte array.
			String received = new String(data, 0, len);
			System.out.println("Containing in String: " + received);
			System.out.println("Containing in byte: " + data);
			System.out.println("");
			
			// sending request to server
			// the host forms a packet to send containing exactly what it received
			try {
				sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 6900);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}

			System.out.println("Intermediate: Sending packet:");
			System.out.println("To host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			len = sendPacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");
			System.out.println(new String(sendPacket.getData(), 0, len));

			// Send the datagram packet to the server via the send/receive socket.
			try {
				sendAndReceiveSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Intermediate: Packet sent.\n");

			// receiving data from server
			byte dataFromServer[] = new byte[100];
			receivePacket = new DatagramPacket(dataFromServer, dataFromServer.length);

			try {
				// Block until a datagram is received via sendReceiveSocket.
				sendAndReceiveSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			System.out.println("Intermediate: Packet received:");
			System.out.println("From host: " + receivePacket.getAddress());
			System.out.println("Host port: " + receivePacket.getPort());
			len = receivePacket.getLength();
			System.out.println("Length: " + len);
			System.out.println("Containing: " + dataFromServer);
			System.out.print("Containing(byte array format): ");
			for (int j = 0; j < 4; j++) {
				System.out.print(dataFromServer[j]);
			}
			System.out.println("\n");

			// sending response to client
			// it forms a packet to send back to the host sending the request
			try {
				sendPacket = new DatagramPacket(dataFromServer, dataFromServer.length, InetAddress.getLocalHost(),
						clientPort);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}

			// creating a DatagramSocket to use to send this request
			
//			try {
//				sentToClientSocket = new DatagramSocket();
//			} catch (SocketException se) {
//				se.printStackTrace();
//				System.exit(1);
//			}

			System.out.println("Intermediate: Sending packet:");
			System.out.println("To host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			len = sendPacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");
			System.out.println(sendPacket.getData());

			// Send the datagram packet to the server via the send/receive socket.
			try {
				DatagramSocket sentToClientSocket = new DatagramSocket();
				sentToClientSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Intermediate: Packet sent.\n");
		}
	}

	public static void main(String[] args) {
		Intermediate i = new Intermediate();
		i.process();
	}
}
