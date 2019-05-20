package chatroom.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
/**
 * 这个类为客户端类
 * @author xiaobao
 *
 */
public class Client {
	Socket socket;
	Scanner scan;
	public Client() {
		try {
			System.out.println("正在连接服务器");
			socket = new Socket("localhost",7777);
			System.out.println("服务器连接成功");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void start() {
		try {
			scan = new Scanner(System.in);
			OutputStream out = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(out,"utf-8");
			BufferedWriter bw = new BufferedWriter(osw);
			PrintWriter pw = new PrintWriter(bw,true);
			//创建输入流获取服务端发送过来的消息
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
			while(true) {
				Receive r = new Receive();
				Thread t = new Thread(r);
				t.start();
				String str = scan.nextLine();
				pw.println(str);
			}
		} catch (Exception e) {
		}
	}
	public static void main(String[] args) {
		Client c = new Client();
		c.start();	
	}
	class Receive implements Runnable{
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in,"utf-8");
				BufferedReader br = new BufferedReader(isr);
				String str = null;
				while((str = br.readLine()) != null) {
					System.out.println(str);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
