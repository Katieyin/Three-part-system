import java.io.*;
import java.net.*;

public class Server {
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;

	public Server() {
		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(6900);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	public void process() {
		boolean running = true;
		boolean validation = true;
		while (running) {
			byte data[] = new byte[100];
			receivePacket = new DatagramPacket(data, data.length);
			System.out.println("Server: Waiting for Packet.\n");

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
			System.out.println("Server: Packet received:");
			System.out.println("From host: " + receivePacket.getAddress());
			System.out.println("Host port: " + receivePacket.getPort());
			int len = receivePacket.getLength();
			System.out.println("Length: " + len);
			int clientPort = receivePacket.getPort();

			// Form a String from the byte array.
			String received = new String(data, 0, len);
			System.out.println("Containing in String: " + received);
			System.out.println("Containing in byte: " + data);
			System.out.println("\n");

			// checking validation
			if (data[0] != (byte) 0) {
				validation = false;
			}
			if (data[1] != (byte) 1 && data[1] != (byte) 2) {
				validation = false;
			}
			int count = 0;
			for (int i = 0; i <= data.length; i++) {
				if (data[i] == (byte) 0) {
					count++;
				}
				if (count == 3) {
					break;
				}
			}
			if (count != 3) {
				validation = false;
			}
			// if the packet is invalid, the server throws an exception and quit
			if (!validation) {

				throw new ArithmeticException("Request is invalid, closing server");
			}

			byte[] response = null;
			if (data[1] == (byte) 1) {
				response = new byte[] { 0, 3, 0, 1 };
			} else if (data[1] == (byte) 2) {
				response = new byte[] { 0, 4, 0, 0 };
			}

			sendPacket = new DatagramPacket(response, response.length, receivePacket.getAddress(), clientPort);

			System.out.println("Server: Sending packet:");
			System.out.println("To host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			len = sendPacket.getLength();
			System.out.println("Length: " + len);
			System.out.println("Containing: " + sendPacket.getData());
			System.out.print("Containing(byte array format): ");
			for (int j = 0; j < 4; j++) {
				System.out.print(sendPacket.getData()[j]);
			}
		
			
			// Send the datagram packet to the client via the send socket.
			try {
				sendSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Server: packet sent");

		}
	}

	public static void main(String args[]) {
		Server c = new Server();
		c.process();
	}
}
