package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import resource.Settings;
import util.Command;

public class TCPServerReader extends Thread{
	private PrintWriter output;
	private BufferedReader input;
	
	public TCPServerReader(Socket socket){
        
        try {
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
        	e.printStackTrace();
        }
        System.out.println("TCPServerReaderCreate  "+socket.toString()+"  "+output.toString()+"  "+input.toString());
    }
	public void run(){
        try {
        	while(true){
        		if(input.ready()){
                    String message = input.readLine();
                    HandleMessage(message);
                }
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
	public void HandleMessage(String message){
        System.out.println(this.getClass().toString()+message);
        StringTokenizer ST;
        if(message != null){
        	if(message.startsWith(Command.TCP_StartWith)){
                ST = new StringTokenizer(message,Command.MSG_delimiter);
				ST.nextToken();
				String command = ST.nextToken();
                
                if(command.equals(Command.Ask_Initial_INFO)){
                	
                }else if(command.equals(Command.Ask_Sequence)){
                    output.println("Sequence#" );
                    output.flush();
                }
        	}
        }
    }
}
