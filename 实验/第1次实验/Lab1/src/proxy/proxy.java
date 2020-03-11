package proxy;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.standard.Severity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ServerSocket;

public class proxy {
	public static HashMap<String, ArrayList<Integer>> cache = new HashMap<String, ArrayList<Integer>>();
	public static void main(String args[]) throws Exception {
		int port = 8080;
		ServerSocket proxyServerSocket = new ServerSocket(port);
		while (true) {
			System.out.println(1);
			Socket socket = proxyServerSocket.accept();
			(new processClient(socket)).start();
		}
	}
}
class processClient extends Thread{
/**
 * define some websites that couldn't be visited!
 */
	private String wall1 = "acm.hit.edu.cn";
	private String wall2 = "www.4399.com";
/**
 * fishing source web-site and target web-site
 */
	private String source = "www.4399.com";
	private String target = "acm.hit.edu.cn";
	private String acmString = "GET http://acm.hit.edu.cn/favicon.ico HTTP/1.1\r\n" + 
			"Host: acm.hit.edu.cn\r\n" + 
			"Proxy-Connection: keep-alive\r\n" + 
			"Pragma: no-cache\r\n" + 
			"Cache-Control: no-cache\r\n" + 
			"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36\r\n" + 
			"Accept: image/webp,image/apng,image/*,*/*;q=0.8\r\n" + 
			"Referer: http://acm.hit.edu.cn/\r\n" + 
			"Accept-Encoding: gzip, deflate\r\n" + 
			"Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n" + 
			"Cookie: _ga=GA1.3.330588374.1568702630; _gid=GA1.3.2116379659.1572489757\r\n\r\n";
/**
 * wall user
 */
	private String wallUser = "Mozilla/5.0";
	private Socket clientSocket;
	processClient(Socket socket){
		clientSocket = socket;
	}
	public void run(){
		InputStream inputStream = null;
		OutputStream outputStream = null;
		InputStream serverInputStream = null;
		OutputStream serverOutputStream = null;
		byte[] bytes = new byte[4096];
		try {
			
			inputStream = clientSocket.getInputStream();
			outputStream= clientSocket.getOutputStream();
			inputStream.read(bytes);
			String message = new String(bytes);
//			System.out.println(message);
			String patternString = "Host: [0-9a-zA-Z.]+";
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher=pattern.matcher(message);
			if(matcher.find()) {
				String host = matcher.group().replace("Host: ", "");
				System.out.println("----------------" + host);
//				/**
//				* judge whether the host name is the website that walled.
//				*/
//				if(judgeOK(host) == false) {
//					String mm = "This website is walled!\nYou can not visit it.";
//					System.out.println(mm);
//					return;
//				}
				/**
				* fishing for xg.hit.edu.cn to acm.hit.edu.cn
				*/
//				host = Fish(host);
//				/**
//				* wall myself
//				*/
//				if(judgeUserOK(message) == false) {
//					System.out.println("This user is blacked!");
//					return;
//				}
//				if(haveThisFile(host) == false) {
				if(true) {
					//ordinary operation
					message = new String(bytes);
					if(host.equals(source)) {
						message = acmString;
						host = "acm.hit.edu.cn";
					}
					System.out.println(host);
					Socket serverSocket = new Socket(host, 80);
					serverOutputStream = serverSocket.getOutputStream();
					System.out.println(message);
					serverOutputStream.write(message.getBytes());
//					(new thread2(inputStream,serverOutputStream)).start();
					serverInputStream = serverSocket.getInputStream();
					ArrayList<Integer> tmp = new ArrayList<Integer>();
					while(true) {
						int next = serverInputStream.read();
						if(next == -1) break;
						outputStream.write(next);
						tmp.add(new Integer(next));
					}
					proxy.cache.put(host, tmp);
					File file = new File(host + ".txt");
					file.createNewFile();
					PrintStream printor = new PrintStream(file);
					printor.write(tmp.toString().getBytes());
					printor.close();
				}else {
					System.out.println("have this file!!!!!!!!!!!!");
					File file = new File(host + ".txt");
					System.out.println(file.getPath());
					long lastModified = file.lastModified();
					System.out.println("lastModified time");
					System.out.println(lastModified);
					Socket serverSocket = new Socket(host,80);
					serverOutputStream = serverSocket.getOutputStream();
					serverInputStream = serverSocket.getInputStream();
					/** 
					 * try
					 */
					if(lastModified > getUpdateTime(host)) {
						System.out.println("upload from localhost");
						int cnt = proxy.cache.get(host).size();
						for(int i = 0;i < cnt;i ++) outputStream.write(proxy.cache.get(host).get(i));
					}else {
						serverOutputStream.write(bytes);
						(new thread2(inputStream,serverOutputStream)).start();
						ArrayList<Integer> tmp = new ArrayList<Integer>();
						while(true) {
							int next = serverInputStream.read();
							if(next == -1) break;
							outputStream.write(next);
							tmp.add(new Integer(next));
						}
						proxy.cache.put(host, tmp);
						PrintStream printor = new PrintStream(file);
						printor.write(tmp.toString().getBytes());
						printor.close();
					}
//					String checkMessage = new String("Host: " + host + "\r\n");
//					checkMessage += "If-modified-since: " + new Long(lastModified).toString()
//                            + "\r\n";
//					System.out.println("checkMessage");
//					System.out.println(checkMessage);
//					serverOutputStream.write(bytes);
//					serverOutputStream.write(new String("\r\n").getBytes());
//					serverOutputStream.write((checkMessage + "\r\n").getBytes());
//					System.out.println("write to server over!");
//					byte[] info = new byte[100000];
////					serverInputStream.read();
//					System.out.println("info from server:");
//					serverInputStream.read(bytes);
////					serverInputStream.close();
//					System.out.println(new String(bytes));
//					System.out.println(info.toString());
//					while(serverInputStream.read(bytes) != -1) {
//						System.out.println(new String(bytes));
//					}
				}
			}
		} catch (IOException e) {
			System.out.println("Maybe something wrong happened!");
		}
	}
	/**
	 * web filter
	 * the method that used to check whether the host name is walled.
	 * @param name host name
	 * @return
	 */
	private boolean judgeOK(String name) {
		if(name.equals(wall1)) return false;
		if(name.equals(wall2)) return false;
		return true;
	}
	/**
	 * fishing
	 * the method that use to change the source website to some other website.
	 * @param name
	 * @return
	 */
	private String Fish(String name) {
		if(name.equals(source))
			return target;
		return name;
	}
	/**
	 * wall user myself
	 * 
	 * @param message
	 * @return
	 */
	private boolean judgeUserOK(String message) {
		String patternString = "User-Agent: [a-zA-Z0-9.]+";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(message);
		matcher.find();
		String user = matcher.group().replace("User-Agent: ", "");
		if(user.equals(wallUser)) return false;
		return true;
	}
	/**
	 * judge whether the file exists or not.
	 * @param fileName
	 * @return
	 */
	private boolean haveThisFile(String fileName) {
		File file = new File(fileName + ".txt");
		return file.exists();
	}
//	private long getTimeStamp(String date) {
//		SimpleDateFormat format = new SimpleDateFormat("");
//	}
	/**
	 * check the last update time of the web-site 
	 * @param host
	 * @return
	 * @throws IOException
	 */
	private long getUpdateTime(String host) throws IOException {
		URL u = new URL("http://" + host);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        http.setRequestMethod("HEAD");
        Date lastModify =new Date(http.getLastModified());
        return lastModify.getTime();
	}
}
/**
 * transmit message to server
 * @author ³Â²´ÖÛ
 *
 */
class thread2 extends Thread {

    private InputStream input;
    private OutputStream output;

    public thread2(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void run() {
        try {
            while (true) {
            	int next = input.read();
            	if(next == -1) break;
            	output.write(next);
            }
        } catch (IOException e) {
            System.out.println("Maybe something wrong happened!");
        }
    }
}
