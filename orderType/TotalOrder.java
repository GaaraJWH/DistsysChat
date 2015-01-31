package orderType;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JTextArea;

import util.Command;

public class TotalOrder {
	private ArrayList<String> bufferList;
	private LinkedList<String> sentMessage;
	public int MemberINSeqNum;
	public int expectedSeqNum;
	private JTextArea textAreaMessageDisplay = null;
	
	public TotalOrder(int expectedSeqNum, JTextArea textAreaMessageDisplay){
		this.expectedSeqNum = expectedSeqNum;
		this.MemberINSeqNum = expectedSeqNum;
		bufferList = new ArrayList<String>();
		sentMessage = new LinkedList<String>();
		this.textAreaMessageDisplay  = textAreaMessageDisplay;
	}
	
	public void DisplayMessage(int seqNum,String message){
		if(seqNum < expectedSeqNum){
            return;  // drop this packet for it was received again
        }else if (seqNum == expectedSeqNum) {  //check if the seq# is the next expected num
        	expectedSeqNum++;
        	textAreaMessageDisplay.append(message);
        	for (int i = 0; i < bufferList.size();) {
        		String temp = bufferList.get(i);
        		String msg[]  = temp.split(Command.MSG_delimiter);
                seqNum = Integer.parseInt(msg[1]);
                if (seqNum == expectedSeqNum) {
                	expectedSeqNum++;
                	textAreaMessageDisplay.append(seqNum+" "+msg[2]);
                	bufferList.remove(i);
                }
        	}
        }
	}
	
	public void setBufferList(int seqNum,String message){
		bufferList.add(seqNum+Command.MSG_delimiter+message);
	}
	public String getBufferList(int index){
		return bufferList.get(index);
	}
	
	public void setSentMessage(String message){
		sentMessage.add(message);
	}
	public String getSentMessage(){
		return sentMessage.poll();
	}
}
