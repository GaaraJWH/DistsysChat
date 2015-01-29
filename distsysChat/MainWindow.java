package distsysChat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import resource.Settings;
import resource.StringResource;
import udp.UDPReader;
import udp.UDPSender;
import util.Command;
import util.GroupINFO;
import util.MemberINFO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainWindow extends JFrame implements  ActionListener{
	/**
	 * GameClient Class: 
	 * 功能:完成了CreateGroup及JoidGroup界面实现
	 * 并且监听网络中的Group信息
	 * 
	 * @author 
	 */
	private static final long serialVersionUID = 1L;
	
	private UDPReader reader = null;
	private String OrderType = null;
	private GroupINFO selectGroupINFO = null;
	
	public static void main(String[] args){
		new MainWindow();
	}
	
	public MainWindow(){
		this.add(MainWindowPanel(),BorderLayout.NORTH);
		
	    //设置主窗口参数
		this.setTitle(Settings.MainWindowName);
		this.setMinimumSize(new Dimension(Settings.MainWindowMinWidth,Settings.MainWindowMinHeight));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width-Settings.MainWindowMinWidth)/2,
				(screenSize.height-Settings.MainWindowMinHeight)/2);//窗口居中
		this.setVisible(true); 
	    //关闭窗口时退出程序
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	//检测组名是否合法函数
	public static boolean checkGroupName(String groupName) {
		String regex = "([a-z]|[A-Z]|[0-9])+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(groupName);
		return m.matches();
	}
	
	//按钮监听函数
	public void actionPerformed(ActionEvent mainEvent) {
		if (mainEvent.getSource() == buttonCreateGroup){
			String groupName = textFieldGroupName.getText();
			if(checkGroupName(groupName)){
				if(OrderType != null){
					try {
						String leaderName = InetAddress.getLocalHost().getHostName();
						String leaderIP = InetAddress.getLocalHost().getHostAddress(); 
						MemberINFO leaderINFO = new MemberINFO(leaderName,leaderIP,1);
						
						StringTokenizer ST = new StringTokenizer(leaderIP,".");
						ST.nextToken();ST.nextToken();
						int Host1 = Integer.valueOf(ST.nextToken()).intValue();
						int Host2 = Integer.valueOf(ST.nextToken()).intValue();
						String GroupBroadcastIP =  Settings.GroupHostHeader+Host1+"."+Host2;
						
						GroupINFO newGroup = new GroupINFO(groupName
								,OrderType,GroupBroadcastIP,leaderINFO,1);
						
						UDPSender sender = new UDPSender();
						sender.sendGroupData(newGroup,Command.GroupMSG_Command_NewGroup
								,Settings.GROUPINFO_BROADCAST_IP);
						
						new ChatRoom(newGroup,true);
						reader.stop();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}else{
					JOptionPane.showMessageDialog(this,StringResource.orderTypeError,
							StringResource.errorMessageDialogTittle,JOptionPane.ERROR_MESSAGE ); 
				}
			}else{
				JOptionPane.showMessageDialog(this,StringResource.groupNameError,
						StringResource.errorMessageDialogTittle,JOptionPane.ERROR_MESSAGE); 
			}
		}
		else if (mainEvent.getSource() == buttonJoinGroup){
			if(selectGroupINFO != null){
				new ChatRoom(selectGroupINFO,false);
				reader.stop();
			}else{
				JOptionPane.showMessageDialog(this,StringResource.groupSelectError,
						StringResource.errorMessageDialogTittle,JOptionPane.ERROR_MESSAGE); 
			}
			
		}else if (mainEvent.getSource() == totalOrder){
			OrderType = Settings.TotalOrderType;
		    if(reader == null){
		    	try {
					MulticastSocket receive = new MulticastSocket(Settings.BROADCAST_PORT);
					InetAddress multicastIP=InetAddress.getByName(Settings.GROUPINFO_BROADCAST_IP);
					receive.joinGroup(multicastIP);
					//receive.setLoopbackMode(false);//设置本MulticastSocket发送的数据报被回送到自身
					reader = new UDPReader(receive, dlmChatGroup,OrderType);
					reader.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }else{
		    	reader.setOrderType(OrderType);
		    }
		    
		    UDPSender sender = new UDPSender();
			sender.sendFindGroupData(Command.GroupMSG_Command_FindGroup
					,Settings.GROUPINFO_BROADCAST_IP);
		    
		}else if (mainEvent.getSource() == causalOrder){
			OrderType = Settings.CausalOrderOrderType;
			if(reader == null){
				try {
					MulticastSocket receive = new MulticastSocket(Settings.BROADCAST_PORT);
					InetAddress multicastIP=InetAddress.getByName(Settings.GROUPINFO_BROADCAST_IP);
					receive.joinGroup(multicastIP);
					//receive.setLoopbackMode(false);//设置本MulticastSocket发送的数据报被回送到自身
					reader = new UDPReader(receive, dlmChatGroup,OrderType);
					reader.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
		    	reader.setOrderType(OrderType);
		    }
		}
	}
	
	private void do_list_valueChanged(ListSelectionEvent arg0) {
		selectGroupINFO = listChatGroup.getSelectedValue();
	}
	
	//Creat MainWindow Panel
	private JPanel MainWindowPanel(){
		MainWindowPanel= new JPanel(new GridBagLayout());
		
		//组件初始化
		labelGroupName = new JLabel(StringResource.groupNameLabel);
		textFieldGroupName = new JTextField();
		buttonCreateGroup = new JButton(StringResource.createGroupButton);
		buttonCreateGroup.addActionListener(this);
		buttonJoinGroup = new JButton(StringResource.joinGroupButton);
		buttonJoinGroup.addActionListener(this);
		totalOrder = new JRadioButton(StringResource.totalOrderButton);
		causalOrder = new JRadioButton(StringResource.causalOrderButton);
		totalOrder.addActionListener(this);
		causalOrder.addActionListener(this);
		ButtonGroup OrderBG = new ButtonGroup();
		OrderBG.add(totalOrder);
		OrderBG.add(causalOrder);
		listChatGroup = new JList<GroupINFO>();
		dlmChatGroup = new DefaultListModel<GroupINFO>();
		listChatGroup.setModel(dlmChatGroup);
		listChatGroup.setVisibleRowCount(3);
		listChatGroup.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				do_list_valueChanged(arg0);
			}
		});
		listChatGroup.setBorder(BorderFactory.createTitledBorder(StringResource.chatGroupLabel));
		scrollPaneChatGroup = new JScrollPane(listChatGroup);
		scrollPaneChatGroup.setPreferredSize(new Dimension(50,50));
				
		//增加各个组件
		MainWindowPanel.add(labelGroupName,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		MainWindowPanel.add(textFieldGroupName,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		MainWindowPanel.add(buttonCreateGroup,   new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
				,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		MainWindowPanel.add(totalOrder,          new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
				,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		MainWindowPanel.add(causalOrder,         new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
				,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		MainWindowPanel.add(scrollPaneChatGroup, new GridBagConstraints(0, 3, 2, 4, 0.0, 0.0
				,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		MainWindowPanel.add(buttonJoinGroup,     new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0
				,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		return MainWindowPanel;
	}
	
	private JPanel MainWindowPanel;
	private JLabel labelGroupName;
	private JTextField textFieldGroupName;
	private JButton buttonCreateGroup;
	private JButton buttonJoinGroup;
	private JRadioButton totalOrder;
	private JRadioButton causalOrder;
	private JList<GroupINFO> listChatGroup;
	private DefaultListModel<GroupINFO> dlmChatGroup;
	private JScrollPane scrollPaneChatGroup;
	
}