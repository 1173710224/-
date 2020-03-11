package gbn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.FieldPosition;

public class Receiver {
	public static int seqnum = 0;
	public static File file = new File("recvdata.txt");
	public static void main(String args[]) throws FileNotFoundException {
		//output redirect
		PrintStream output = new PrintStream(new File("mess.txt"));
		System.setOut(output);
		try {
			if (file.exists() == false) file.createNewFile();
			FileWriter writer = new FileWriter(file);
			DatagramSocket socket = new DatagramSocket(9999);
			byte[] content = new byte[1024];
//			int cnt = 1;
			while(true) {
				try {
//					System.out.println(cnt ++);
					DatagramPacket packet = new DatagramPacket(content, content.length);
					socket.receive(packet);
//					System.out.println(new String(packet.getData()));
					String[] message = new String(packet.getData()).split("seqnum");
					if(message[0].equals("")) {
						String ackMessage = "ack" + new Integer(-1).toString() + "ack";
						packet = new DatagramPacket(ackMessage.getBytes(),ackMessage.getBytes().length,InetAddress.getLocalHost(),6666);
						socket.send(packet);
//						System.out.println("send ack message:" + ackMessage);
						break;
					}
//					System.out.println(message[1]);
					int recnum = (int)(new Integer(message[1]));
					if (recnum == seqnum + 1) {
						writer.write(message[0]);
						seqnum += 1;
					}
					String ackMessage = "ack" + new Integer(recnum).toString() + "ack";
					packet = new DatagramPacket(ackMessage.getBytes(),ackMessage.getBytes().length,InetAddress.getLocalHost(),6666);
					socket.send(packet);
//					System.out.println("send ack message:" + ackMessage);
				} catch (Exception e) {
					System.out.println();
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
