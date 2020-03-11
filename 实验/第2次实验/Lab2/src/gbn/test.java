package gbn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class test {
	public static void main(String[] args) throws Exception {
//		try {
//			DatagramSocket socket = new DatagramSocket(6666);
//			String string = "hello world!";
//			DatagramPacket packet = new DatagramPacket(string.getBytes(), string.getBytes().length,InetAddress.getLocalHost(),9999);
//			socket.send(packet);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Timer timer = new Timer();
//		HashMap<String, String> map = new HashMap<String, String>();
//		System.out.println(map.get("1"));
////		timer.schedule(new myTimer(timer), 1000);
//		System.out.println(1);
//		File file = new File("data.txt");
//		FileReader reader = new FileReader(file);
//		char[] buffer = new char[1024];
//		System.out.println(reader.read(buffer));
//		System.out.println(buffer.toString());
//		reader.close();
//		FileWriter writer = new FileWriter(file);
//		for(int i = 1;i <= 100000;i ++) writer.write("hello world!\n");
		byte[] content = new byte[1024];
		DatagramSocket socket = new DatagramSocket(666);
		socket = new DatagramSocket(666);
		return;
	}
}
