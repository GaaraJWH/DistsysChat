package orderType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import udp.UDPSender;
import util.Command;
import util.GroupINFO;
import util.MemberINFO;

public class TotalOrder {
	private ArrayList<String> bufferList;
	private ArrayList<String> allSentMessage;//已发送信息
	private LinkedList<String> sendMessage;//待发送信息
	public int totalSeqNum = 1;
	private int expectedSeqNum;
	private JTextArea textAreaMessageDisplay = null;
	private DefaultListModel<MemberINFO> dlmMembers = null;
	private CheckBufferList retransmitThread;
	private GroupINFO groupINFO;
	
	public TotalOrder(int expectedSeqNum, GroupINFO groupINFO, JTextArea textAreaMessageDisplay,
			DefaultListModel<MemberINFO> dlmMembers){
		this.expectedSeqNum = expectedSeqNum;
		this.groupINFO = groupINFO;
		bufferList = new ArrayList<String>();
		allSentMessage = new ArrayList<String>();
		sendMessage = new LinkedList<String>();
		this.dlmMembers  = dlmMembers;
		this.textAreaMessageDisplay  = textAreaMessageDisplay;
		retransmitThread = new CheckBufferList(groupINFO,bufferList,expectedSeqNum);
        retransmitThread.start();
	}
	
	public void DisplayMessage(int seqNum,String message){
		System.out.println("seqNum:"+seqNum+"expectedSeqNum:"+expectedSeqNum);
		if(seqNum < expectedSeqNum || textAreaMessageDisplay == null){
            return;  // drop this packet for it was received again
        }else if (seqNum == expectedSeqNum) {  //check if the seq# is the next expected num
        	expectedSeqNum++;
        	textAreaMessageDisplay.append(seqNum+"."+message+"\n");
        	CheckOtherBuffer();
        	// only receive new message expectedSeqNum will change
            retransmitThread.setSeqNum(expectedSeqNum);
        }else if(seqNum > expectedSeqNum){
        	if(bufferList.isEmpty()){
                setBufferList(seqNum,message);
                return;
            }
            
            for(int i=0;i<bufferList.size();i++){
            	String temp = bufferList.get(i);
                StringTokenizer ST;
     			ST = new StringTokenizer(temp,Command.MSG_delimiter);
                int num = Integer.parseInt(ST.nextToken());
                if(seqNum<num){
                	setBufferList(i,seqNum,message);
                    break;
                }   
            }
        }
	}
	
	public void AddNewMember(int seqNum, MemberINFO newMember) {
		System.out.println("seqNum:"+seqNum+"expectedSeqNum:"+expectedSeqNum);
		if(seqNum < expectedSeqNum || dlmMembers == null){
            return;  // drop this packet for it was received again
        }else if (seqNum == expectedSeqNum) {  //check if the seq# is the next expected num
        	expectedSeqNum++;
        	textAreaMessageDisplay.append(seqNum+"."+newMember+" join in \n");
        	dlmMembers.addElement(newMember);
        	CheckOtherBuffer();
        	// only receive new message expectedSeqNum will change
            retransmitThread.setSeqNum(expectedSeqNum);
        }else if(seqNum > expectedSeqNum){
        	if(bufferList.isEmpty()){
        		setBufferList(seqNum,Command.ChatMSG_Command_NewMember+Command.MSG_delimiter
            			+newMember.toSendString());
                return;
            }
            
            for(int i=0;i<bufferList.size();i++){
                String temp = bufferList.get(i);
                StringTokenizer ST;
    			ST = new StringTokenizer(temp,Command.MSG_delimiter);
                int num = Integer.parseInt(ST.nextToken());
                if(seqNum<num){
                	setBufferList(i,seqNum,Command.ChatMSG_Command_NewMember+Command.MSG_delimiter
                			+newMember.toSendString());
                    break;
                }   
            }
        }
	}
	
	public void DeleteMember(int seqNum, MemberINFO deleteMember) {
		System.out.println("seqNum:"+seqNum+"expectedSeqNum:"+expectedSeqNum);
		if(seqNum < expectedSeqNum || dlmMembers == null){
            return;  // drop this packet for it was received again
        }else if (seqNum == expectedSeqNum) {  //check if the seq# is the next expected num
        	expectedSeqNum++;
        	textAreaMessageDisplay.append(seqNum+"."+deleteMember+" leave group \n");
        	int index = -1;
			for(int ind = 0 ; ind < dlmMembers.getSize() ; ind++){
				MemberINFO dlmMember= dlmMembers.elementAt(ind);
				boolean equal = true;
				if(!dlmMember.MemberName.equals(deleteMember.MemberName)) equal=false;
				if(!dlmMember.MemberIP.equals(deleteMember.MemberIP)) equal=false;
				if(equal) { index = ind; break; }
			}
			if(index!=-1){
				dlmMembers.remove(index);
			}
        	CheckOtherBuffer();
        	// only receive new message expectedSeqNum will change
            retransmitThread.setSeqNum(expectedSeqNum);
        }else if(seqNum > expectedSeqNum){
        	if(bufferList.isEmpty()){
        		setBufferList(seqNum,Command.ChatMSG_Command_LeaveGroup+Command.MSG_delimiter
            			+deleteMember.toSendString());
                return;
            }
            
            for(int i=0;i<bufferList.size();i++){
                String temp = bufferList.get(i);
                StringTokenizer ST;
    			ST = new StringTokenizer(temp,Command.MSG_delimiter);
                int num = Integer.parseInt(ST.nextToken());
                if(seqNum<num){
                	setBufferList(i,seqNum,Command.ChatMSG_Command_LeaveGroup+Command.MSG_delimiter
                			+deleteMember.toSendString());
                    break;
                }   
            }
        }
	}
	
	public void CheckOtherBuffer(){
		int seqNum;
		for (int i = 0; i < bufferList.size();) {// check if buffer queue has expected message
    		String temp = bufferList.get(i);
            StringTokenizer ST;
 			ST = new StringTokenizer(temp,Command.MSG_delimiter);
 			seqNum = Integer.parseInt(ST.nextToken());
 			if(seqNum < expectedSeqNum){
 				bufferList.remove(i);
 			}else if (seqNum == expectedSeqNum) {
            	expectedSeqNum++;
            	String msg = ST.nextToken();
            	if(msg.equals(Command.ChatMSG_Command_NewMember)){
            		String MemberName = ST.nextToken();
                	String MemberIP = ST.nextToken();
                	int MemberPriority = Integer.valueOf(ST.nextToken()).intValue();
                	MemberINFO MemberList = new MemberINFO(MemberName,MemberIP,MemberPriority);
                	textAreaMessageDisplay.append(seqNum+"."+MemberName+" join in \n");
                	dlmMembers.addElement(MemberList);
            	}else if(msg.equals(Command.ChatMSG_Command_LeaveGroup)){
            		String MemberName = ST.nextToken();
                	String MemberIP = ST.nextToken();
                	textAreaMessageDisplay.append(seqNum+"."+MemberName+" leave group \n");
                	int index = -1;
					for(int ind = 0 ; ind < dlmMembers.getSize() ; ind++){
						MemberINFO dlmMember= dlmMembers.elementAt(ind);
						boolean equal = true;
						if(!dlmMember.MemberName.equals(MemberName)) equal=false;
						if(!dlmMember.MemberIP.equals(MemberIP)) equal=false;
						if(equal) { index = ind; break; }
					}
					if(index!=-1){
						dlmMembers.remove(index);
					}
            	}else{
            		textAreaMessageDisplay.append(seqNum+"."+msg+"\n");
            	}
            	bufferList.remove(i);
            }
    	}
	}
	
	public void retransmit(int seqNum){
		for (String s : allSentMessage) {
			System.out.println("retransmit:"+s);
			StringTokenizer ST;
			ST = new StringTokenizer(s,Command.MSG_delimiter);
			int num = Integer.parseInt(ST.nextToken());
            if (num == seqNum) {
            	String msg = ST.nextToken();
            	UDPSender sender = new UDPSender();
            	if(msg.equals(Command.ChatMSG_Command_NewMember)){
            		String MemberName = ST.nextToken();
                	String MemberIP = ST.nextToken();
                	int MemberPriority = Integer.valueOf(ST.nextToken()).intValue();
                	MemberINFO MemberList = new MemberINFO(MemberName,MemberIP,MemberPriority);
                	
            		sender.sendChatData(groupINFO, Command.ChatMSG_Command_NewMember
            				,seqNum,MemberList.toSendString());
            	}else if(msg.equals(Command.ChatMSG_Command_LeaveGroup)){
            		String MemberName = ST.nextToken();
                	String MemberIP = ST.nextToken();
                	int MemberPriority = Integer.valueOf(ST.nextToken()).intValue();
                	MemberINFO MemberList = new MemberINFO(MemberName,MemberIP,MemberPriority);
                	
            		sender.sendChatData(groupINFO, Command.ChatMSG_Command_LeaveGroup
            				,seqNum,MemberList.toSendString());
            	}else{
            		sender.sendChatData(groupINFO, Command.ChatMSG_Command_ChatMessage,seqNum,msg);
            	}
            	
                break;
            }
        }
	}
	
	public void setExpectedSeqNum(int expectedSeqNum){
		this.expectedSeqNum = expectedSeqNum;
		retransmitThread.setSeqNum(expectedSeqNum);
	}
	
	public void setBufferList(int index,int seqNum, String message){
		bufferList.add(index,seqNum+Command.MSG_delimiter+message);
	}
	public void setBufferList(int seqNum, String message){
		bufferList.add(seqNum+Command.MSG_delimiter+message);
	}
	public String getBufferList(int index){
		return bufferList.get(index);
	}
	
	public void setAllSentMessage(int seqNum,String message){
		allSentMessage.add(seqNum+Command.MSG_delimiter+message);
	}
	
	public void setSendMessage(String message){
		sendMessage.add(message);
	}
	public String getSendMessage(){
		return sendMessage.poll();
	}
}
