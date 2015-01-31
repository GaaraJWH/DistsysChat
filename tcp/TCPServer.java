package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.DefaultListModel;

import orderType.TotalOrder;
import resource.Settings;
import util.GroupINFO;
import util.MemberINFO;


public class TCPServer extends Thread{
	private ServerSocket server = null;
	private Socket client = null;
	DefaultListModel<MemberINFO> dlmMembers = null;
	private GroupINFO groupINFO;
	private TotalOrder totalOrder;
	
	public TCPServer(DefaultListModel<MemberINFO> dlmMembers, GroupINFO groupINFO, TotalOrder totalOrder){
		this.dlmMembers = dlmMembers;
		this.groupINFO = groupINFO;
		this.totalOrder = totalOrder;
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
        TCPServerReader reader = new TCPServerReader(client,dlmMembers,groupINFO,totalOrder);
        reader.start();
    }
}
