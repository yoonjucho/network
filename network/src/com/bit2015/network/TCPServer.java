package com.bit2015.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	
	private static final int PORT = 10002;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		
		try {
			//1. 서버 소켓 생성
			serverSocket = new ServerSocket();
			
			//2. 바인딩
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostAddress = inetAddress.getHostAddress();
			serverSocket.bind( new InetSocketAddress( hostAddress, PORT ) );
			System.out.println( "[서버] 바인딩 " + hostAddress + ":" + PORT );
			
			//3. 연결 요청 대기
			System.out.println( "[서버] 연결 기다림" );
			Socket socket = serverSocket.accept();
			
			
			
			
			// 4. 데이터 읽고/쓰기
			InetSocketAddress inetSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
			System.out.println( 
				"[서버] 연결됨 from " + 
				inetSocketAddress.getHostName() +
				":" +
				inetSocketAddress.getPort()
			);
			
			InputStream is = null;
			OutputStream os = null;
			
			try {
				is = socket.getInputStream();
				os = socket.getOutputStream();

				while (true) {
					byte[] buffer = new byte[128];
					int readByteCount = is.read(buffer);
					if ( readByteCount < 0 ) { // 클라이언트가 정상적으로 종료
						System.out.println("[서버] 클라이언트로부터연결 끊김");
						break;
					}

					String data = new String(buffer, 0, readByteCount, "UTF-8");
					System.out.print("[서버] 데이터 수신:" + data);

					os.write(data.getBytes("UTF-8"));
					os.flush();
				}
				
				// 스트림 닫기
				is.close();
				os.close();
				// 데이터 소켓 닫기
				if( socket.isClosed() == false ){
					socket.close();
				}
			} catch( IOException ex ) {
				System.out.println( "[서버] 에러:" + ex );
			}
			
		} catch( IOException ex ) {
			ex.printStackTrace();
		} finally {
			if( serverSocket != null && serverSocket.isClosed() == false ){
				try {
					serverSocket.close();
				} catch( IOException ex ) {
					ex.printStackTrace();
				}
			}
		}
	}

}
