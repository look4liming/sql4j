package lee.bright.sql4j.proxy.mysql;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientTest {

	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 6666);
		OutputStream out = socket.getOutputStream();
		for (int i = 0; i < 10; i++) {
			out.write('z');
		}
		out.flush();
		InputStream in = socket.getInputStream();
		int c = -1;
		while ((c = in.read()) != -1) {
			System.out.print((char) c);
		}
		out.close();
		in.close();
		socket.close();
	}

}
