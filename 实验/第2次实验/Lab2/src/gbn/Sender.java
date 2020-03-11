package gbn;

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
	private static final int N = 5;
	public static final int timeout = 2000;
	public static int base = 1;
	public static int seqnum = 1;
	public static Timer timer = new Timer();
	private static HashMap<Integer,String> unacked = new HashMap<Integer, String>();
	public static void main(String args[]) throws FileNotFoundException {
		PrintStream output = new PrintStream(new File("mess.txt"));
		System.setOut(output);
//		timer.schedule(new myTimer(timer), time);
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
				if(seqnum != 50) {
					String message = buffer.toString();
					message = message + "seqnum" + new Integer(seqnum) + "seqnum";
					packet = new DatagramPacket(message.getBytes(),message.getBytes().length,InetAddress.getLocalHost(),9999);
					socket.send(packet);
					unacked.put(new Integer(seqnum), message);
				}
				String message = buffer.toString();
				message = message + "seqnum" + new Integer(seqnum) + "seqnum";
				packet = new DatagramPacket(message.getBytes(),message.getBytes().length,InetAddress.getLocalHost(),9999);
				socket.send(packet);
				System.out.println("send message " + new Integer(seqnum).toString());
				unacked.put(new Integer(seqnum), message);
				if(seqnum == base) {
					//begin timer
					timer = new Timer();
					timer.schedule(new myTimer(timer), timeout);
				}
				seqnum ++;
				System.out.println("base");
				System.out.println(base);
				System.out.println("seqnum");
				System.out.println(seqnum);
			}
			System.out.println("zaiwaimian");
		}
//		thread.stop();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	public static void func() throws IOException {
		DatagramSocket socket = Sender.socket;
		for (int i = base;i < seqnum;i ++) {
			DatagramPacket packet = new DatagramPacket(unacked.get(new Integer(i)).getBytes(),unacked.get(new Integer(i)).getBytes().length,InetAddress.getLocalHost(),9999);
			socket.send(packet);
			System.out.println("send message " + new Integer(i).toString());
		}
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
				int next = (int)(new Integer(ans.split("ack")[1])) + 1;
				if(next == 0) break;
				Sender.base = Math.max(Sender.base, next);
				if(Sender.base == Sender.seqnum) Sender.timer.cancel();
				else {
					Sender.timer = new Timer();
					Sender.timer.schedule(new myTimer(Sender.timer), Sender.timeout);
				}
//				System.out.println(Sender.base);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
class myTimer extends TimerTask{
	private Timer timer;
	public myTimer(Timer timer) {
		this.timer = timer;
	}
	public void run() {
		try {
			Sender.func();
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer.cancel();
		return;
	}
}