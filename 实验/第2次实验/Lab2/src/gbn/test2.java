package gbn;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class test2 {
	public static void main(String args[]) {
		try {
			DatagramSocket socket = new DatagramSocket(9999);
			byte[] content = new byte[1024];
			DatagramPacket packet = new DatagramPacket(content, content.length);
			socket.receive(packet);
			byte[] data = packet.getData();
			int length = packet.getLength();
			InetAddress ip = packet.getAddress();
			int port = packet.getPort();
			System.out.println(new String(data));
			System.out.println(length);
			System.out.println(ip);
			System.out.println(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
