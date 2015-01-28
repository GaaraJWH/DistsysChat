package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import resource.Settings;


public class TCPServer extends Thread{
	private ServerSocket server = null;
	private Socket client = null;
	
	public TCPServer(){
        
        try {
			server = new ServerSocket(Settings.TCP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public void run(){
        try {
        	while(true){
        		client = server.accept();
                System.out.println(this.getClass().toString()+"accept one client.");
                accept(client);
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
	public void accept(Socket client){
        TCPServerReader reader = new TCPServerReader(client);
        reader.start();
    }
}
