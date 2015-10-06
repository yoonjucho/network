package com.bit2015.network.echo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
	private static final String SERVER_ADDRESS = "222.106.22.80";
	private static final int SERVER_PORT = 10002;
	
	public static void main(String[] args) {
		
		Socket socket = null;
		Scanner scanner = null;
		
		try {
			scanner = new Scanner( System.in );
			socket = new Socket();
			
			socket.connect( new InetSocketAddress( SERVER_ADDRESS, SERVER_PORT ) );
			
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			
			// 쓰고/받기
			while( true ) {
				
				System.out.print( ">>" );
				String data = scanner.nextLine();
				if( "exit".equals( data ) == true ){
					break;
				}
				data = data + "\n";
				os.write( data.getBytes( "UTF-8" ) );
				
				byte[] buffer = new byte[ 128 ];
				int readByteCount = is.read( buffer );
				data = new String( buffer, 0, readByteCount, "UTF-8" );
				System.out.print( "<<" + data );
			}
			
			os.close();
			is.close();
			if( socket.isClosed() == false ) {
				socket.close();
			}
			
		} catch( IOException ex ) {
			System.out.println( "<<에러:" + ex );
		}
	}

}
