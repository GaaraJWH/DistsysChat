package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;

import orderType.TotalOrder;
import util.Command;
import util.GroupINFO;
import util.MemberINFO;

public class TCPServerReader extends Thread{
	private PrintWriter output;
	private BufferedReader input;
	DefaultListModel<MemberINFO> dlmMembers = null;
	private GroupINFO groupINFO;
	private TotalOrder totalOrder;
	
	public TCPServerReader(Socket socket, DefaultListModel<MemberINFO> dlmMembers, GroupINFO groupINFO, TotalOrder totalOrder){
		this.dlmMembers = dlmMembers;
		this.groupINFO = groupINFO;
		this.totalOrder = totalOrder;
        try {
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
        	e.printStackTrace();
        }
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
                	groupINFO.MembersNumber++;
                	groupINFO.MembersExpectPriority++;
                	for(int i = 0;i<dlmMembers.size();i++){
                		String msg = Command.TCP_StartWith+Command.MSG_delimiter+Command.Initial_INFO
                				+Command.MSG_delimiter+Command.Member_List+Command.MSG_delimiter
                				+dlmMembers.get(i).toSendString();
                    	output.println(msg);
                    	output.flush();
                	}
                	
                	String msg = Command.TCP_StartWith+Command.MSG_delimiter+Command.Initial_INFO+Command.MSG_delimiter
                			+Command.Member_Priority+Command.MSG_delimiter+groupINFO.MembersExpectPriority
                			+Command.MSG_delimiter+(totalOrder.expectedSeqNum+1);
                	output.println(msg);
                	output.flush();
                	
                	
                }else if(command.equals(Command.Ask_SequenceNumber)){
                	String msg = Command.TCP_StartWith+Command.MSG_delimiter+Command.SequenceNumber
                			+Command.MSG_delimiter+(totalOrder.expectedSeqNum+1);
                    output.println(msg);
                    output.flush();
                }
        	}
        }
    }
}
