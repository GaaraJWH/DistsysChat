package util;

import resource.StringResource;

public class GroupINFO{
	
	/**
	 * 
	 */
	
	public String GroupName;
	public String GroupOrderType;
	public String GroupBroadcastIP;
	public MemberINFO leaderINFO;
	public int MembersNumber;
	public int MembersExpectPriority;
	
	public GroupINFO(String GroupName,String GroupOrderType,String GroupBroadcastIP
			,MemberINFO leaderINFO, int MembersNumber){
		this.GroupName = GroupName;
		this.GroupOrderType = GroupOrderType;
		this.GroupBroadcastIP = GroupBroadcastIP;
		this.leaderINFO = leaderINFO;
		this.MembersNumber = MembersNumber;
		this.MembersExpectPriority = 1;
	}
	
	public String toString(){
		return StringResource.chatGroupLabelGroupName+this.GroupName
				+StringResource.chatGroupLabelMembersNumber+this.MembersNumber;
	}
	
	public String toSendString(){
		return this.GroupName+Command.MSG_delimiter+this.GroupOrderType+Command.MSG_delimiter+this.GroupBroadcastIP+Command.MSG_delimiter
				+this.leaderINFO.toSendString()+Command.MSG_delimiter+this.MembersNumber;
	}
	
	public boolean equals(GroupINFO groupINFO){
		boolean isEquals = true;
		if (groupINFO == null){
			isEquals = false;
		}
		if (groupINFO.GroupName != this.GroupName){
			isEquals = false;
		}
		if (groupINFO.GroupBroadcastIP != this.GroupBroadcastIP){
			isEquals = false;
		}
		return isEquals;
	}
}