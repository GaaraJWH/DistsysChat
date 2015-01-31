package distsysChat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.LineBorder;

import orderType.TotalOrder;
import resource.Settings;
import resource.StringResource;
import tcp.TCPClientReader;
import tcp.TCPClientSender;
import tcp.TCPServer;
import udp.UDPReader;
import udp.UDPSender;
import util.Command;
import util.GroupINFO;
import util.MemberINFO;


public class ChatRoom extends JFrame implements  ActionListener{

	/**
	 * ChatRoom Class: 
	 * 功能:完成了ChatRoom界面实现
	 * 监听信息发送及接送
	 * 
	 * @author 
	 */
	private static final long serialVersionUID = 1L;
	
	private Color panelBackground  = new Color(200,230,240);
	GroupINFO groupINFO;
	MemberINFO memberINFO;
	TotalOrder totalOrder;
	private boolean isLeader ;
	
	private UDPReader groupINFOReader;
	private UDPReader chatMSGReader;
    private TCPServer tcpServer;
    private Socket tcpClientSocket;
    private TCPClientReader tcpClientReader;
    private TCPClientSender tcpClientSender;
    private UDPSender sender = new UDPSender();
	
	
	public ChatRoom(GroupINFO groupINFO,boolean isLeader){
		this.groupINFO = groupINFO;
		this.isLeader = isLeader;
		this.add(ChatRoomPanel(),BorderLayout.NORTH);
		
		//设置主窗口参数
		this.setTitle(this.groupINFO.GroupName);
		this.setMinimumSize(new Dimension(Settings.ChatRoomMinWidth,Settings.ChatRoomMinHeight));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width-Settings.ChatRoomMinWidth)/2,
				(screenSize.height-Settings.ChatRoomMinHeight)/2);//窗口居中
		this.setVisible(true); 
	    
	    //关闭窗口时退出程序
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	    CreatSocket();
	}
	

	private void CreatSocket() {
		if(isLeader){
			try {
				MulticastSocket receive = new MulticastSocket(Settings.BROADCAST_PORT);
				InetAddress multicastIP=InetAddress.getByName(Settings.GROUPINFO_BROADCAST_IP);
				receive.joinGroup(multicastIP);
				//receive.setLoopbackMode(false);//设置本MulticastSocket发送的数据报被回送到自身
				groupINFOReader = new UDPReader(receive,this.groupINFO,this.isLeader);
				groupINFOReader.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	memberINFO = groupINFO.leaderINFO;
	    	dlmMembers.addElement(memberINFO);
	    	if(groupINFO.GroupOrderType.equals(Settings.TotalOrderType)){
	    		totalOrder = new TotalOrder(1,textAreaMessageDisplay);
		    	tcpServer = new TCPServer(dlmMembers,this.groupINFO,totalOrder);
				tcpServer.start();
	    	}
	    }else{
	    	try {
	    		String memberName = InetAddress.getLocalHost().getHostName();
				String memberIP = InetAddress.getLocalHost().getHostAddress();
				InetAddress leader = InetAddress.getByName(this.groupINFO.leaderINFO.MemberIP);
				tcpClientSocket = new Socket(leader, Settings.TCP_PORT);
                tcpClientSender = new TCPClientSender(tcpClientSocket, sender);
		    	memberINFO = new MemberINFO(memberName,memberIP,0);
		    	if(groupINFO.GroupOrderType.equals(Settings.TotalOrderType)){
		    		totalOrder = new TotalOrder(0,textAreaMessageDisplay);
					tcpClientReader = new TCPClientReader(tcpClientSocket, tcpClientSender,dlmMembers
							,this.groupINFO,memberINFO,totalOrder);
	                tcpClientReader.start();
		    	}else{
		    		
		    	}
                String msg = Command.TCP_StartWith+Command.MSG_delimiter+Command.Ask_Initial_INFO
                		+Command.MSG_delimiter+memberINFO.toSendString();
                tcpClientSender.SendMessage(msg);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    if(groupINFO.GroupOrderType.equals(Settings.TotalOrderType)){
	    	try {
				MulticastSocket receive = new MulticastSocket(Settings.BROADCAST_PORT);
				InetAddress multicastIP=InetAddress.getByName(this.groupINFO.GroupBroadcastIP);
				receive.joinGroup(multicastIP);
				//receive.setLoopbackMode(false);//设置本MulticastSocket发送的数据报被回送到自身
				chatMSGReader = new UDPReader(receive,dlmMembers,totalOrder,this.groupINFO,this.isLeader);
				chatMSGReader.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }else{
	    	
	    }
	    
	}


	@Override
	public void actionPerformed(ActionEvent chatEvent) {
		if (chatEvent.getSource() == buttonSend){
			String textMSG = textFieldMessage.getText();
			if(isLeader){
				
			}else{
				totalOrder.setSentMessage(textMSG);
				String msg = Command.TCP_StartWith+Command.MSG_delimiter+Command.Ask_SequenceNumber;
				tcpClientSender.SendMessage(msg);
			}
		}else if(chatEvent.getSource() == buttonLeaveGroup){
			
			//groupINFO.MembersNumber = 45;
			//UDPSender sender = new UDPSender();
			//sender.sendGroupData(groupINFO, Command.GroupMSG_Command_ModifyGroup, Settings.GROUPINFO_BROADCAST_IP);
		}
	}
	
	//Creat ChatRoom Panel
	private JPanel ChatRoomPanel(){
		ChatRoomPanel = new JPanel(new BorderLayout());
		
		/** 聊天面板,包括消息显示文本域,聊天消息输入框和发送按钮 */
		textAreaMessageDisplay = new JTextArea(16, 32);
		textAreaMessageDisplay.setLineWrap(true);
		textAreaMessageDisplay.setEditable(false);
		textAreaMessageDisplay.setBorder(BorderFactory.createLoweredBevelBorder());
		scrollPaneMessageShow = new JScrollPane(textAreaMessageDisplay);
		textFieldMessage = new JTextField(32);
		textFieldMessage.setBorder(BorderFactory.createLoweredBevelBorder());
		scrollPaneMessage= new JScrollPane(textFieldMessage);
		buttonSend = new JButton(StringResource.sendButton);
		buttonSend.addActionListener(this);
		JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.setBackground(panelBackground);
		chatPanel.add(scrollPaneMessageShow, BorderLayout.NORTH);
		chatPanel.add(textFieldMessage, BorderLayout.CENTER);
		chatPanel.add(buttonSend, BorderLayout.EAST);
		chatPanel.setBorder(new LineBorder(chatPanel.getBackground(), 5));
		ChatRoomPanel.add(chatPanel, BorderLayout.CENTER);
		
		/** 成员列表面板,包括成员列表和退出按钮 */
		listMembers  = new JList<MemberINFO>();
		listMembers.setFixedCellHeight(90);
		listMembers.setFixedCellWidth(120);	
		dlmMembers = new DefaultListModel<MemberINFO>();
		listMembers.setModel(dlmMembers);
		listMembers.setVisibleRowCount(3);
		listMembers.setBorder(BorderFactory.createTitledBorder(StringResource.membersLabel));
		scrollPaneMembers = new JScrollPane(listMembers);
		buttonLeaveGroup = new JButton(StringResource.leaveGroupButton);
		buttonLeaveGroup.addActionListener(this);
		JPanel MembersPanel = new JPanel(new BorderLayout());
		MembersPanel.setBackground(panelBackground);
		MembersPanel.add(scrollPaneMembers, BorderLayout.NORTH);
		MembersPanel.add(buttonLeaveGroup, BorderLayout.CENTER);
		MembersPanel.setBorder(new LineBorder(MembersPanel.getBackground(), 5));
		ChatRoomPanel.add(MembersPanel, BorderLayout.EAST);
		
		return ChatRoomPanel;
	}
	
	private JPanel ChatRoomPanel;
	private JTextArea textAreaMessageDisplay;
	private JScrollPane scrollPaneMessageShow;
	private JTextField textFieldMessage;
	JScrollPane scrollPaneMessage;
	private JButton buttonSend;
	private JList<MemberINFO> listMembers;
	private DefaultListModel<MemberINFO> dlmMembers;
	private JScrollPane scrollPaneMembers;
	private JButton buttonLeaveGroup;
	
}