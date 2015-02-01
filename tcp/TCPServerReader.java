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
import resource.Settings;
import udp.UDPSender;
import util.Command;
import util.GroupINFO;
import util.MemberINFO;

public class TCPServerReader extends Thread{
	private PrintWriter output;
	private BufferedReader input;
	DefaultListModel<MemberINFO> dlmMembers = null;
	private GroupINFO groupINFO;
	private TotalOrder totalOrder;
	
	public TCPServerReader(Socket socket, DefaultListModel<MemberINFO> dlmMembers
			, GroupINFO groupINFO, TotalOrder totalOrder){
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
                
                if(command.equals(Command.TCP_Ask_Initial_INFO)){
                	groupINFO.MembersNumber++;
                	groupINFO.MembersExpectPriority++;
                	for(int i = 0;i<dlmMembers.size();i++){
                		String msg = Command.TCP_StartWith+Command.MSG_delimiter+Command.TCP_Initial_INFO
                				+Command.MSG_delimiter+Command.TCP_Member_List+Command.MSG_delimiter
                				+dlmMembers.get(i).toSendString();
                    	output.println(msg);
                    	output.flush();
                	}
                	
                	String msg = Command.TCP_StartWith+Command.MSG_delimiter+Command.TCP_Initial_INFO
                			+Command.MSG_delimiter+Command.TCP_Member_Priority+Command.MSG_delimiter
                			+groupINFO.MembersExpectPriority+Command.MSG_delimiter+(totalOrder.totalSeqNum++);
                	output.println(msg);
                	output.flush();
                	
                	UDPSender sender = new UDPSender();
        			sender.sendGroupData(groupINFO, Command.GroupMSG_Command_ModifyGroup,
        					Settings.GROUPINFO_BROADCAST_IP);
                }else if(command.equals(Command.TCP_LeaveGroup)){
                	groupINFO.MembersNumber--;
                	String MemberName = ST.nextToken();
                	String MemberIP = ST.nextToken();
                	int MemberPriority = Integer.valueOf(ST.nextToken()).intValue();
                	MemberINFO leaveMember = new MemberINFO(MemberName,MemberIP,MemberPriority);
                	int seqNum = totalOrder.totalSeqNum++;
					totalOrder.setAllSentMessage(seqNum, Command.ChatMSG_Command_LeaveGroup
							+Command.MSG_delimiter+leaveMember.toSendString());
					
					UDPSender sender = new UDPSender();
					sender.sendChatData(groupINFO, Command.ChatMSG_Command_LeaveGroup
							,seqNum,leaveMember.toSendString());
					
        			sender.sendGroupData(groupINFO, Command.GroupMSG_Command_ModifyGroup,
        					Settings.GROUPINFO_BROADCAST_IP);
                }else if(command.equals(Command.TCP_Ask_SequenceNumber)){
                	String msg = Command.TCP_StartWith+Command.MSG_delimiter+Command.TCP_SequenceNumber
                			+Command.MSG_delimiter+(totalOrder.totalSeqNum++);
                    output.println(msg);
                    output.flush();
                }
        	}
        }
    }
}
