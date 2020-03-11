package sr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Sender {
	public static DatagramSocket socket = null;
	private static final int N = 1;
	public static final int timeout = 2000;
	public static int base = 1;
	public static int seqnum = 1;
	public static Timer timer = new Timer();
	private static HashMap<Integer,String> unacked = new HashMap<Integer, String>();
	public static HashMap<Integer, Integer> acked = new HashMap<Integer, Integer>();
//	public static ArrayList<myTimer> tasks = new ArrayList<myTimer>();
	public static HashMap<Integer, myTimer> tasks = new HashMap<Integer, myTimer>();
	public static void main(String args[]) throws FileNotFoundException {
		PrintStream output = new PrintStream(new File("mess.txt"));
		System.setOut(output);
		try {
		//prepare data
		File file = new File("data.txt");
		Reader reader = new FileReader(file);
		//prepare socket
		socket = new DatagramSocket(6666);
		//prepare data container
		DatagramPacket packet = null;
		char[] buffer = new char[1024];
		Thread thread = new recvAck();
		thread.start();
		while(true) {
			if(seqnum < base + N) {
				int next = reader.read(buffer);
				if(next == -1) {
					String message = "seqnum" + new Integer(-1).toString() + "seqnum";
					packet = new DatagramPacket(message.getBytes(),message.getBytes().length,InetAddress.getLocalHost(),9999);
					socket.send(packet);
					return;
				}
				String message = buffer.toString();
				message = message + "seqnum" + new Integer(seqnum) + "seqnum";
				packet = new DatagramPacket(message.getBytes(),message.getBytes().length,InetAddress.getLocalHost(),9999);
				socket.send(packet);
				myTimer task = new myTimer(seqnum);
				timer.schedule(task, timeout);
				tasks.put(new Integer(seqnum), task);
				System.out.println("send message " + new Integer(seqnum).toString());
				unacked.put(new Integer(seqnum), message);
				seqnum ++;
			}
			System.out.println("zaiwaimian");
		}
//		thread.stop();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	public static void func(int n) throws IOException {
		DatagramSocket socket = Sender.socket;
		DatagramPacket packet = new DatagramPacket(unacked.get(new Integer(n)).getBytes(),unacked.get(new Integer(n)).getBytes().length,InetAddress.getLocalHost(),9999);
		socket.send(packet);
		System.out.println("send message " + new Integer(n).toString());
	}
}
class recvAck extends Thread{
	public void run() {
		try {
			byte[] content = new byte[1024];
			DatagramSocket socket  = Sender.socket;
			while(true) {
				DatagramPacket packet = new DatagramPacket(content, content.length);
				socket.receive(packet);
				String ans = new String(packet.getData());
				System.out.println("receive ack " + ans);
				int tmpAckNum = (int)(new Integer(ans.split("ack")[1]));
				Sender.acked.put(new Integer(tmpAckNum),new Integer(1));
				//judge whether update the base of sender or not
				while(Sender.acked.get(new Integer(Sender.base)).equals(new Integer(1))) {
					Sender.base = Sender.base + 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
class myTimer extends TimerTask{
	//declare the number that you want to retransmit
	private int n;
	public myTimer(int n) {
		this.n = n;
	}
	public void run() {
		try {
			//retransmit the packet that response timeout
			Sender.func(n);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.cancel();
		return;
	}
}