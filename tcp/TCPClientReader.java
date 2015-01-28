package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import resource.Settings;
import util.Command;

public class TCPClientReader extends Thread{
	private BufferedReader reader;
    private TCPClientSender sender;
    
    public TCPClientReader(Socket socket,TCPClientSender sender){
    	 this.sender = sender;
         
         try {
			reader = new BufferedReader( new InputStreamReader( socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void run(){
        try {
        	while( true ){
                if(reader.ready()){
                    String message = reader.readLine();
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
				if(command.equals(Command.Initial_INFO)){
					 
				}else if(command.equals(Command.Sequence)){
		            //message = message.replace("Sequence#", "");
		            //int seqNum = Integer.parseInt(message);
		            //messageInfo.sequenceNumber = seqNum;
		            //sender.UDPSendMessage("255.255.255.255", "ChatMessage:@"  + seqNum + "@" + messageInfo.sendingMessage);
		            //totalOrder.saveMessage(seqNum + "@" + messageInfo.sendingMessage);
		        }
        	}
        }
    }
}
