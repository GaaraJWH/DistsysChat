package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;

import orderType.TotalOrder;
import resource.Settings;
import util.Command;
import util.GroupINFO;
import util.MemberINFO;

public class UDPReader extends Thread{
	
	//private DatagramPacket receivepacket = null;
	
	//private DatagramSocket dataSocket = null;
	
	private MulticastSocket multSocket = null;
	private DefaultListModel<GroupINFO> dlmChatGroup = null;
	private String orderType = null;
	private GroupINFO groupINFO = null;
	private boolean isLeader;
	private TotalOrder totalOrder = null;
	
	public UDPReader(MulticastSocket multSocket,DefaultListModel<GroupINFO> dlmChatGroup, String orderType){
		this.multSocket = multSocket;
		this.dlmChatGroup = dlmChatGroup;
		this.orderType  = orderType;
	}
	
	public UDPReader(MulticastSocket multSocket , GroupINFO groupINFO, boolean isLeader) {
		this.multSocket = multSocket;
		this.groupINFO  = groupINFO;
		this.isLeader =isLeader;
	}
	
	public UDPReader(MulticastSocket multSocket ,TotalOrder totalOrder, GroupINFO groupINFO, boolean isLeader) {
		this.multSocket = multSocket;
		this.groupINFO  = groupINFO;
		this.totalOrder  = totalOrder;
		this.isLeader =isLeader;
	}
	
	public void setOrderType(String orderType){
		this.orderType  = orderType;
		if(dlmChatGroup != null){
			this.dlmChatGroup.clear();
		}
	}
	
	public void run() {
		if(multSocket != null){
			try {
		        while (true) {
		        	byte[] data=new byte[Settings.DATA_LEN]; 
		        	DatagramPacket receivepacket=new DatagramPacket(data,data.length);  
					multSocket.receive(receivepacket);
					
					handleMessage(receivepacket);
					
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void handleMessage(DatagramPacket packet) {
		String recvMsg=new String(packet.getData(),0,packet.getLength());
		StringTokenizer ST;
		if(recvMsg != null){
			if(recvMsg.startsWith(Command.GroupMSG_StartWith)  ){
				System.out.println(this.getClass().toString()+"   "+recvMsg);
				ST = new StringTokenizer(recvMsg,Command.MSG_delimiter);
				ST.nextToken();
				String command = ST.nextToken();
				if( command.equals(Command.GroupMSG_Command_NewGroup) && dlmChatGroup != null  ){
					String GroupName = ST.nextToken();
					String GroupOrderType =  ST.nextToken();
					String GroupBroadcastIP = ST.nextToken();
					MemberINFO leaderINFO = new MemberINFO(ST.nextToken(),ST.nextToken()
							,Integer.valueOf(ST.nextToken()).intValue());
					int MembersNumber = Integer.valueOf(ST.nextToken()).intValue();
					GroupINFO newGroup = new GroupINFO(GroupName,GroupOrderType
							,GroupBroadcastIP,leaderINFO,MembersNumber);
					if(GroupOrderType.equals(orderType)){
						dlmChatGroup.addElement(newGroup);
					}
				}else if(command.equals(Command.GroupMSG_Command_ExistGroup) && dlmChatGroup != null  ){
					String GroupName = ST.nextToken();
					String GroupOrderType =  ST.nextToken();
					String GroupBroadcastIP = ST.nextToken();
					MemberINFO leaderINFO = new MemberINFO(ST.nextToken(),ST.nextToken()
							,Integer.valueOf(ST.nextToken()).intValue());
					int MembersNumber = Integer.valueOf(ST.nextToken()).intValue();
					GroupINFO newGroup = new GroupINFO(GroupName,GroupOrderType
							,GroupBroadcastIP,leaderINFO,MembersNumber);
					if(GroupOrderType.equals(orderType)){
						dlmChatGroup.addElement(newGroup);
					}
				}else if(command.equals(Command.GroupMSG_Command_ModifyGroup) && dlmChatGroup != null  ){
					String GroupName = ST.nextToken();
					String GroupOrderType =  ST.nextToken();
					String GroupBroadcastIP = ST.nextToken();
					MemberINFO leaderINFO = new MemberINFO(ST.nextToken(),ST.nextToken()
							,Integer.valueOf(ST.nextToken()).intValue());
					int MembersNumber = Integer.valueOf(ST.nextToken()).intValue();
					GroupINFO newGroup = new GroupINFO(GroupName,GroupOrderType
							,GroupBroadcastIP,leaderINFO,MembersNumber);
					int index = -1;
					for(int i = 0 ; i < dlmChatGroup.getSize() ; i++){
						GroupINFO dlmGroup = dlmChatGroup.elementAt(i);
						boolean equal = true;
						if(!dlmGroup.GroupName.equals(GroupName)) equal=false;
						if(!dlmGroup.GroupBroadcastIP.equals(GroupBroadcastIP)) equal=false;
						if(equal) { index = i; break; }
					}
					if(index!=-1 && GroupOrderType.equals(orderType)){
						dlmChatGroup.setElementAt(newGroup, index);
					}
				}else if(command.equals(Command.GroupMSG_Command_DeleteGroup) && dlmChatGroup != null  ){
					String GroupName = ST.nextToken();
					String GroupOrderType =  ST.nextToken();
					String GroupBroadcastIP = ST.nextToken();
					int index = -1;
					for(int i = 0 ; i < dlmChatGroup.getSize() ; i++){
						GroupINFO dlmGroup = dlmChatGroup.elementAt(i);
						boolean equal = true;
						if(!dlmGroup.GroupName.equals(GroupName)) equal=false;
						if(!dlmGroup.GroupBroadcastIP.equals(GroupBroadcastIP)) equal=false;
						if(equal) { index = i; break; }
					}
					if(index!=-1 && GroupOrderType.equals(orderType)){
						dlmChatGroup.remove(index);
					}
				}else if(command.equals(Command.GroupMSG_Command_FindGroup) && isLeader){
					UDPSender sender = new UDPSender();
					sender.sendGroupData(groupINFO,Command.GroupMSG_Command_ExistGroup
							,Settings.GROUPINFO_BROADCAST_IP);
				}
			}else if(recvMsg.startsWith(Command.ChatMSG_StartWith)){
				/**
				 * ¸ÅÂÊ¶ª°ü²âÊÔ
				 */
				/**Random random = new Random(System.currentTimeMillis());
				if(random.nextInt(100) < 50){
					return;
				}
				*/
				System.out.println(this.getClass().toString()+"   "+recvMsg);
				ST = new StringTokenizer(recvMsg,Command.MSG_delimiter);
				ST.nextToken();
				String command = ST.nextToken();
				if( command.equals(Command.ChatMSG_Command_ChatMessage)&&totalOrder != null){					
					int seqNum = Integer.valueOf(ST.nextToken()).intValue();
                	String message = ST.nextToken();
					totalOrder.DisplayMessage(seqNum, message);
				}else if(command.equals(Command.ChatMSG_Command_NewMember) && totalOrder != null){
					int seqNum = Integer.valueOf(ST.nextToken()).intValue();
					String MemberName = ST.nextToken();
                	String MemberIP = ST.nextToken();
                	int MemberPriority = Integer.valueOf(ST.nextToken()).intValue();
                	MemberINFO NewMember = new MemberINFO(MemberName,MemberIP,MemberPriority);
                	totalOrder.AddNewMember(seqNum, NewMember);
				}else if(command.equals(Command.ChatMSG_Command_LeaveGroup) && totalOrder != null){
					int seqNum = Integer.valueOf(ST.nextToken()).intValue();
					String MemberName = ST.nextToken();
                	String MemberIP = ST.nextToken();
                	int MemberPriority = Integer.valueOf(ST.nextToken()).intValue();
                	MemberINFO deleteMember = new MemberINFO(MemberName,MemberIP,MemberPriority);
                	totalOrder.DeleteMember(seqNum, deleteMember);
				}else if(command.equals(Command.ChatMSG_Command_MessageMiss) && totalOrder != null){
					int seqNum = Integer.valueOf(ST.nextToken()).intValue();
					totalOrder.retransmit(seqNum);
				}
			}
		}
	}
}
