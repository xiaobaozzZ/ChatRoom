package chatroom.core;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
/**
 * 这个类启动后用作服务器
 * @author xiaobao
 *
 */
public class Server {
	private ServerSocket server;
	/*
	 * 该数组用于保存所有ClientHandler对应客户端的输出流,便于这些ClientHandler转发消息
	 */
	private Collection<PrintWriter> pws = new ArrayList();
	public Server() {
		try {
			System.out.println("正在启动客户端");
			server = new ServerSocket(7777);
			System.out.println("客户端启动成功");
		} catch (Exception e) {
		}
	}
	public void start() {
		try {
			System.out.println("等待客户端连接");
			while(true) {
				Socket socket = server.accept();
				ClientHandler ch = new ClientHandler(socket);
				Thread t = new Thread(ch);
				t.start();
			}
		} catch (IOException e) {
		}
		
	}
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	
	//匿名内部类
	class ClientHandler implements Runnable{
		private Socket socket;
		private int sum;
		private String host;
		private String name;
		private PrintWriter pw;
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);
				/*
				 * 通过Socket获取输出流,以便将消息发送给客户端
				 */
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out,"utf-8");
				BufferedWriter bw = new BufferedWriter(osw);
				PrintWriter pw = new PrintWriter(bw,true);
				this.pw = pw;
				/*
				 * 将当前ClientHandler对应客户端的输出流放入共享数组allOut中,以便其他的ClientHandler也可以
				 * 访问到,从而给这个客户端转发消息
				 */
				synchronized (pws) {
					pws.add(pw);
				}
				String str = null;
				pw.println("请输入你的昵称:");
				name = br.readLine();
				pw.println("欢迎进入001号聊天室");
				System.out.println(name+"已经上线,当前在线人数:"+pws.size());
				while((str = br.readLine()) != null) {
					for(PrintWriter pws : pws) {
						if(pw == pws) {
							continue;
						}
						synchronized(pws) {
							pws.println(name+"说"+str);
						}
					}
					
				}
			} catch (IOException e) {
			}finally {
				//处理客户端断开连接后的操作
				try {
					synchronized (pws) {
						pws.remove(pw);
						System.out.println(name+"已经下线,当前在线人数:"+pws.size());
					}
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
}