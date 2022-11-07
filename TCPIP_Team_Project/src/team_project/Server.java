package team_project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	static ServerSocket server;
	public static Socket openServer(int port) {
		try {
			server = new ServerSocket();
			server.setReuseAddress(true);
			System.out.println("연결 대기 중...");
			server.bind(new InetSocketAddress("192.168.1.115", port));
			System.out.println(server.toString());
			System.out.println("bind() 성공");
			
			Socket socket = server.accept();
			System.out.println("연결되었습니다.");
			System.out.println(socket.toString());
			
			return socket;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Socket connAsCli(int port) {
		try {
			Socket socket = new Socket("192.168.25.4", port);
			System.out.println("접속 성공");
			return socket;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void sendMsg(Socket sock, String outMsg) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			out.write(outMsg + "\n");
			out.flush();
			System.out.println("send 성공 " + outMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static String recvMsg(Socket sock) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String inMsg = in.readLine();
			return inMsg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void closeSock() {
		try {
			server.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
