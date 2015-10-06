package com.bit2015.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
public class TCPClient {
	private static final String SERVER_ADDRESS = "222.106.22.80";
	private static final int SERVER_PORT = 10002;
	public static void main(String[] args) {
		Socket socket = null;
		try {
			socket = new Socket();
			System.out.println( "[클라이언트] 연결요청" );
			socket.connect( new InetSocketAddress( SERVER_ADDRESS, SERVER_PORT ) );
			System.out.println( "[클라이언트] 연결성공" );
			// 쓰고/받기
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			
			String data = "Hello World\n";
			os.write( data.getBytes( "UTF-8" ) );
			
			byte[] buffer = new byte[ 128 ];
			int readByteCount = is.read( buffer );
			data = new String( buffer, 0, readByteCount, "UTF-8" );
			System.out.println( "[클라이언트] 데이터 수신:" + data );
			
			if( socket.isClosed() == false ) {
				socket.close();
			}  
		} catch( IOException ex ) {
			System.out.println( "[클라이언트] 에러:" + ex );
		}
	}

}
