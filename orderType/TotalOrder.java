package orderType;

import java.util.ArrayList;

public class TotalOrder {
	public ArrayList<String> bufferList;
	public ArrayList<String> sentMessage;
	public int expectedSeqNum;
	
	public TotalOrder(int expectedSeqNum){
		this.expectedSeqNum = expectedSeqNum;
		bufferList = new ArrayList<>();
		sentMessage = new ArrayList<>();
	}
}
