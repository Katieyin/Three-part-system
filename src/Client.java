import java.io.*;
import java.net.*;

public class Client {
	DatagramSocket clientSocket;
	DatagramPacket writePacket, readPacket;

	public Client() {
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	public void process() {
		for (int i = 0; i <= 11; i++) {
			// declaring the message send to server
			byte[] msg = new byte[100];

			// declaring the file name and mode and zero byte
			String fileName = "test.txt";
			byte[] fileNameInByte = fileName.getBytes();
			byte[] zero = new byte[] { 0 };
			String mode = "ocTEt";
			byte[] modeInByte = mode.getBytes();

			if (i != 11) {
				// alternating between read and write request
				if (i % 2 == 0) {
					// adding first two bytes to message for read request
					byte[] read = { 0, 1 };
					System.arraycopy(read, 0, msg, 0, read.length);
				} else {
					// adding first two bytes to message for write request
					byte[] write = { 0, 2 };
					System.arraycopy(write, 0, msg, 0, write.length);
				}
				// adding file name to message
				System.arraycopy(fileNameInByte, 0, msg, 2, fileNameInByte.length);
				// adding 0 byte to message
				System.arraycopy(zero, 0, msg, fileNameInByte.length + 2, 1);
				// adding mode to message
				System.arraycopy(modeInByte, 0, msg, fileNameInByte.length + 3, modeInByte.length);
				// adding the final 0 byte to message
				System.arraycopy(zero, 0, msg, fileNameInByte.length + 2 + 1 + modeInByte.length, 1);
			} else {
				msg = "invalid request".getBytes();
			}

			// the client prints out the information it has put in the packet
			System.out.println("message in byte: " + msg);
			String msgInString = new String(msg, 0, msg.length);
			System.out.println("message in String: " + msgInString);

			// Constructing a datagram packet that is to be sent to a specified port
			try {
				writePacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 2300);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}

			System.out.println("Client: Sending packet:");
			System.out.println("To host: " + writePacket.getAddress());
			System.out.println("Destination host port: " + writePacket.getPort());
			int len = writePacket.getLength();
			System.out.println("Length: " + len);
			System.out.println("Containing: " + writePacket.getData());

			// Send the datagram packet to the server via the send/receive socket.
			try {
				clientSocket.send(writePacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Client: Packet sent.\n");

			byte data[] = new byte[100];
			readPacket = new DatagramPacket(data, data.length);

			try {
				// Block until a datagram is received via sendReceiveSocket.
				clientSocket.receive(readPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Client: Packet received:");
			System.out.println("From host: " + readPacket.getAddress());
			System.out.println("Host port: " + readPacket.getPort());
			len = readPacket.getLength();
			System.out.println("Length: " + len);
			System.out.println("Containing: " + data);
			System.out.print("Containing(byte array format): ");
			for (int j = 0; j < 4; j++) {
				System.out.print(data[j]);
			}
			System.out.println("\n");

		}

		// We're finished, so close the socket.
		clientSocket.close();
	}

	public static void main(String[] args) {
		Client c = new Client();
		c.process();
	}
}
