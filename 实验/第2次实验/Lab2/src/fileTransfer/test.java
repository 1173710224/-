package fileTransfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class test {
	public static void main(String args[]) {
		try {
			File  file = new File("file/file1.txt");
//			FileReader reader = new FileReader(file);
			BufferedReader reader = new BufferedReader(new FileReader(file));
//			char[] buffer = new char[1024];
//			reader.read(buffer);
			System.out.println(reader.readLine());
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
