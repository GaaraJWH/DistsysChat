package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import resource.Settings;
import util.Command;
import util.GroupINFO;

public class UDPSender {
	DatagramSocket sendSocket;
	
	public void sendFindGroupData(String groupCommand,String BROADCAST_IP) {
		try {
			sendSocket=new DatagramSocket();
			String msg = Command.GroupMSG_StartWith+Command.MSG_delimiter
					+groupCommand;
			byte[] bytebuf=new byte[Settings.DATA_LEN];
			bytebuf=msg.getBytes();
			DatagramPacket packet=new DatagramPacket(bytebuf,bytebuf.length,
											InetAddress.getByName(BROADCAST_IP),Settings.BROADCAST_PORT);
			
			sendSocket.send(packet);
			sendSocket.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendGroupData(GroupINFO groupINFO,String groupCommand,String BROADCAST_IP){
		try {
			sendSocket=new DatagramSocket();
			String msg = Command.GroupMSG_StartWith+Command.MSG_delimiter
					+groupCommand+Command.MSG_delimiter
					+groupINFO.toSendString();
			byte[] bytebuf=new byte[Settings.DATA_LEN];
			bytebuf=msg.getBytes();
			DatagramPacket packet=new DatagramPacket(bytebuf,bytebuf.length,
											InetAddress.getByName(BROADCAST_IP),Settings.BROADCAST_PORT);
			
			sendSocket.send(packet);
			sendSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendChatData(GroupINFO groupINFO,String chatCommand,String textMSG ){
		try {
			sendSocket=new DatagramSocket();
			String msg = Command.ChatMSG_StartWith+Command.MSG_delimiter
					+chatCommand+Command.MSG_delimiter
					+textMSG;
			byte[] bytebuf=new byte[Settings.DATA_LEN];
			bytebuf=msg.getBytes();
			DatagramPacket packet=new DatagramPacket(bytebuf,bytebuf.length,
										InetAddress.getByName(groupINFO.GroupBroadcastIP),Settings.BROADCAST_PORT);
			
			sendSocket.send(packet);
			sendSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
