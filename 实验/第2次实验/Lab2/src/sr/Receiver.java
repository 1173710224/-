package sr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.FieldPosition;
import java.util.HashMap;

public class Receiver {
	public static int seqnum = 0;
	public static File file = new File("sr_recvdata.txt");
	public static final int N = 5;
	public static int base = 1;
	public static HashMap<Integer, Integer> acked = new HashMap<Integer, Integer>();
	public static HashMap<Integer, String> cache = new HashMap<Integer, String>();
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
					DatagramPacket packet = new DatagramPacket(content, content.length);
					socket.receive(packet);
					String[] message = new String(packet.getData()).split("seqnum");
					int recnum = (int)(new Integer(message[1]));
					if(recnum < base || recnum >= base + N) continue;
					acked.put(new Integer(recnum),new Integer(1));
					cache.put(new Integer(recnum), message[0]);
					//send ack message
					if(message[0].equals("")) {
						String ackMessage = "ack" + new Integer(-1).toString() + "ack";
						packet = new DatagramPacket(ackMessage.getBytes(),ackMessage.getBytes().length,InetAddress.getLocalHost(),6666);
						socket.send(packet);
						break;
					}
					String ackMessage = "ack" + new Integer(recnum).toString() + "ack";
					packet = new DatagramPacket(ackMessage.getBytes(),ackMessage.getBytes().length,InetAddress.getLocalHost(),6666);
					socket.send(packet);
				} catch (Exception e) {
					System.out.println();
				}
				while(acked.get(base).equals(new Integer(1))) {
					writer.write(cache.get(base));
					base ++;
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
