package tcp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import udp.UDPSender;

public class TCPClientSender {
	private Socket socket;
    private PrintWriter sender;
    private UDPSender udpSocket;
    
    public TCPClientSender(Socket socket, UDPSender udpSocket){
    	this.socket = socket;
        this.udpSocket = udpSocket;
        try {
			sender = new PrintWriter( new OutputStreamWriter( socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void SendMessage(String message){
    	System.out.println(this.getClass().toString()+message);
    	sender.println(message);
    	if( sender.checkError() ){     // leader lost connection
    		// broadcast to select a new leader
    		
    	}else{
            sender.flush();
        }
    }
	public void Close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
