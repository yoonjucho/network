package com.bit2015.network.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class EchoReceiveThread extends Thread {
	private Socket socket = null;
	
	public EchoReceiveThread( Socket socket ) {
		this.socket = socket;
	}

	@Override
	public void run() {
		
		// 4. 데이터 읽고/쓰기
		InetSocketAddress inetSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
		System.out.println( 
			"[서버] 연결됨 from " + 
			inetSocketAddress.getHostName() +
			":" +
			inetSocketAddress.getPort()
		);
		
		BufferedReader reader = null;
		PrintWriter printWriter = null;
		try {
			reader = new BufferedReader( new InputStreamReader( socket.getInputStream(), "UTF-8" ) );
			printWriter = new PrintWriter( socket.getOutputStream() );
			
			while (true) {
				String data = reader.readLine();
				if( data == null ) {
					break;
				}
				System.out.println("[서버] 데이터 수신:" + data);
				
				printWriter.println( data );
				printWriter.flush();
			}
			
			// 스트림 닫기
			reader.close();
			printWriter.close();
			// 데이터 소켓 닫기
			if( socket.isClosed() == false ){
				socket.close();
			}
		} catch( IOException ex ) {
			System.out.println( "[서버] 에러:" + ex );
		}
	}
}