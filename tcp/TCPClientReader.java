package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;

import orderType.TotalOrder;
import udp.UDPSender;
import util.Command;
import util.GroupINFO;
import util.MemberINFO;

public class TCPClientReader extends Thread{
	private BufferedReader reader;
    private TCPClientSender sender;
	private DefaultListModel<MemberINFO> dlmMembers = null;
	private MemberINFO memberINFO;
	private TotalOrder totalOrder;
	private GroupINFO groupINFO;
    
    public TCPClientReader(Socket socket,TCPClientSender sender,
    		DefaultListModel<MemberINFO> dlmMembers, GroupINFO groupINFO, MemberINFO memberINFO, TotalOrder totalOrder){
    	 this.sender = sender;
         this.dlmMembers = dlmMembers;
         this.groupINFO =groupINFO;
         this.memberINFO = memberINFO;
         this.totalOrder = totalOrder;
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
				if(command.equals(Command.TCP_Initial_INFO)){
					command = ST.nextToken();
					if(command.equals(Command.TCP_Member_Priority)){
						memberINFO.MemberPriority = Integer.valueOf(ST.nextToken()).intValue();
						int seqNum = Integer.valueOf(ST.nextToken()).intValue();
						totalOrder.setExpectedSeqNum(seqNum);
						UDPSender sender = new UDPSender();
						sender.sendChatData(groupINFO, Command.ChatMSG_Command_NewMember,
								seqNum,memberINFO.toSendString());
					}else if(command.equals(Command.TCP_Member_List) && dlmMembers != null){
						String MemberName = ST.nextToken();
	                	String MemberIP = ST.nextToken();
	                	int MemberPriority = Integer.valueOf(ST.nextToken()).intValue();
	                	MemberINFO MemberList = new MemberINFO(MemberName,MemberIP,MemberPriority);
	                	dlmMembers.addElement(MemberList);
					}
				}else if(command.equals(Command.TCP_SequenceNumber)){
					int seqNum = Integer.valueOf(ST.nextToken()).intValue();
					String msg = totalOrder.getSendMessage();
					totalOrder.setAllSentMessage(seqNum, msg);
					UDPSender sender = new UDPSender();
					sender.sendChatData(groupINFO, Command.ChatMSG_Command_ChatMessage,seqNum,msg);
				}
        	}
        }
    }
}
