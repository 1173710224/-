package fileTransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.FieldPosition;
import java.util.Scanner;

import org.omg.CORBA.ORB;

public class Client {
	public static int seqnum = 0;
	public static File file = new File("serverRecvdata.txt");
	public static void main(String args[]) throws FileNotFoundException {
		//client message
		System.out.println("Please choose a file that you want to get from server.");
		System.out.println("You can choose one of the three files as below:file1.txt,file2.txt,file3.txt.");
		System.out.println("Please input a number represent the file.\nIf you input an other number,you will be requested to input another number,");
		System.out.println("or you can input -1 to quit the application.");
		Scanner scanner = new Scanner(System.in);
		int fileNum = 0;
		while(fileNum != -1 && fileNum != 1 && fileNum != 2 && fileNum != 3) {
			System.out.println("input:");
			fileNum = scanner.nextInt();
		}
		if(fileNum == -1) return;
		try {
			//build new file.
			if (file.exists() == false) file.createNewFile();
			FileWriter writer = new FileWriter(file);
			DatagramSocket socket = new DatagramSocket(9999);
			//request the file that you want to get.
			byte[] content = new byte[1024];
			String prepareMessage = new String("request:" + new Integer(fileNum).toString());
			DatagramPacket preparePacket = new DatagramPacket(prepareMessage.getBytes(),prepareMessage.getBytes().length,InetAddress.getLocalHost(),6666);
			socket.send(preparePacket);
			while(true) {
				try {
					DatagramPacket packet = new DatagramPacket(content, content.length);
					socket.receive(packet);
					String[] message = new String(packet.getData()).split("seqnum");
					if(message[0].equals("")) {
						String ackMessage = "ack" + new Integer(-1).toString() + "ack";
						packet = new DatagramPacket(ackMessage.getBytes(),ackMessage.getBytes().length,InetAddress.getLocalHost(),6666);
						socket.send(packet);
						break;
					}
					int recnum = (int)(new Integer(message[1]));
					if (recnum == seqnum + 1) {
						writer.write(message[0]);
						seqnum += 1;
					}
					String ackMessage = "ack" + new Integer(recnum).toString() + "ack";
					packet = new DatagramPacket(ackMessage.getBytes(),ackMessage.getBytes().length,InetAddress.getLocalHost(),6666);
					socket.send(packet);
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
