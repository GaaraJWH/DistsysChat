package orderType;

import java.util.ArrayList;
import java.util.StringTokenizer;

import udp.UDPSender;
import util.Command;
import util.GroupINFO;


public class CheckBufferList extends Thread{
	
	 private GroupINFO groupINFO;
	private ArrayList<String>  bufferList;
	private int expectedSeqNum;
	
	public void run() {
        while (true) {
        	if (!bufferList.isEmpty()) {  // there must be some packet was lost
        		String temp = bufferList.get(bufferList.size() - 1);        		
        		StringTokenizer ST;
     			ST = new StringTokenizer(temp,Command.MSG_delimiter);
                int maxSeqNum = Integer.parseInt(ST.nextToken());
                boolean exist[] = new boolean[maxSeqNum + 1];
                for (int i = 0; i < maxSeqNum; i++) {
                    exist[i] = false;
                }
                for (String s : bufferList) {
                	ST = new StringTokenizer(s,Command.MSG_delimiter);
                    int t = Integer.parseInt(ST.nextToken());
                    exist[t] = true;
                }
                for (int i = expectedSeqNum; i < maxSeqNum; i++) {  // retransmit all lost packet
                    if (!exist[i]) {
                    	UDPSender sender = new UDPSender();
    					sender.sendChatData(groupINFO, Command.ChatMSG_Command_MessageMiss,i,"0");
                    }
                }
                try {										// check buffer list every 5s 
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}    
        	}
        }
	}
	
	public void setSeqNum(int expectedSeqNum) {
        this.expectedSeqNum = expectedSeqNum;
    }
	
	public CheckBufferList(GroupINFO groupINFO, ArrayList<String> bufferList, int expectedSeqNum) {
	        this.groupINFO = groupINFO;
	        this.bufferList = bufferList;
	        this.expectedSeqNum = expectedSeqNum;
	}
}
