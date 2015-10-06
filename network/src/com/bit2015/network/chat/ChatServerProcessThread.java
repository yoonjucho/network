package com.bit2015.network.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ChatServerProcessThread extends Thread {
	private static final String PROTOCOL_DIVIDER = ":";
	
	private String nickname;
	private Socket socket;
	private List<PrintWriter> listPrintWriters;
	
	public ChatServerProcessThread( Socket socket, List<PrintWriter> listPrintWriters ) {
		this.socket = socket;
		this.listPrintWriters = listPrintWriters;
	}

	@Override
	public void run() {
		BufferedReader bufferedReader = null;
		PrintWriter printWriter = null;
		
		try {
			//1. 스트림 얻기
			bufferedReader = 
					new BufferedReader( new InputStreamReader( socket.getInputStream(), "UTF-8" ) );
			printWriter =
					new PrintWriter( socket.getOutputStream() );
			
			
			//2. 리모트 호스트 정보 얻기
			InetSocketAddress inetSocketAddress = 
					(InetSocketAddress)socket.getRemoteSocketAddress();
			String remoteHostAddress = inetSocketAddress.getHostName();
			int remoteHostPort = inetSocketAddress.getPort();
			ChatServer.log( "연결됨 from " + remoteHostAddress + ":" + remoteHostPort );
			
			//3.요청처리
			while( true ) {
				String request = bufferedReader.readLine();
				if( request == null ) {
					ChatServer.log( "클라이언트로 부터 연결 끊김" );
					doQuit( printWriter );
					break;
				}
				
				String[] tokens = request.split( PROTOCOL_DIVIDER );
				if( "join".equals( tokens[0] ) ) {
					doJoin( printWriter, tokens[1] );
				} else if( "message".equals( tokens[0] ) ){
					doMessage( tokens[1] );
				} else if( "quit".equals( tokens[0] ) ){
					doQuit( printWriter );
					break;
				} else {
					ChatServer.log( "에러: 알수 없는 요청명령(" + tokens[0] + ")" );
				}
				
			}
			
			//4.자원정리
			bufferedReader.close();
			printWriter.close();
			if( socket.isClosed() == false ) {
				socket.close();
			}
		} catch( IOException ex ) {
			ChatServer.log( "error:" + ex );
			// 클라이언트의 비정상 종료 ( 명시적으로 소켓을 닫지 않음 )
			doQuit( printWriter );
		}
	}
	
	private void doQuit( PrintWriter printWriter ) {
		// PrintWriter 제거
		removePrintWriter( printWriter );
		
		//퇴장 메세지 브로드캐스팅
		String data = nickname + "님이 퇴장하였습니다.";
		broadcast( data );
	}
	
	private void doMessage( String message ) {
		String data = nickname + ":" + message;
		broadcast( data );
	}
	
	private void doJoin( PrintWriter printWriter, String nickname ) {
		//1. 닉네임 저장
		this.nickname = nickname;

		//2. 메세지 브로드캐스팅
		String message = nickname + "님이 입장했습니다.";
		broadcast( message );
		
		//3.
		addPrintWriter( printWriter );

		//4. ack
		printWriter.println( "join:ok" );
		printWriter.flush();
	}
	
	private void addPrintWriter( PrintWriter printWriter ) {
		synchronized( listPrintWriters ) {
			listPrintWriters.add( printWriter );
		}
	}
	
	private void removePrintWriter( PrintWriter printWriter ) {
		synchronized( listPrintWriters ) {
			listPrintWriters.remove( printWriter );
		}
	}
	
	private void broadcast( String data ) {
		synchronized( listPrintWriters ) {
	//		for( PrintWriter printWriter : listPrintWriters ) {
	//           printWriter.println( data );		
	//		}
			int count = listPrintWriters.size();
			for( int i = 0; i < count; i++ ) {
				PrintWriter printWriter = listPrintWriters.get( i );
				printWriter.println( data );
				printWriter.flush();
			}
		}
	}
}
