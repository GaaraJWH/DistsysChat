package util;

public class MemberINFO {
	/**
	 * 
	 */
	
	public String MemberName;
	public String MemberIP;
	public int MemberPriority;
	public int MessageNum;
	
	public MemberINFO(String MemberName,String MemberIP,int MemberPriority){
		this.MemberName = MemberName;
		this.MemberIP = MemberIP;
		this.MemberPriority = MemberPriority;
		this.MessageNum = 0;
	}
	
	public String toString(){
		return MemberName;
	}
	
	public String toSendString(){
		return this.MemberName+Command.MSG_delimiter+this.MemberIP+Command.MSG_delimiter
				+this.MemberPriority;
	}
	
	public boolean equals(MemberINFO memberINFO){
		boolean isEquals = true;
		if (memberINFO == null){
			isEquals = false;
		}
		if (memberINFO.MemberName != this.MemberName){
			isEquals = false;
		}
		if (memberINFO.MemberIP != this.MemberIP){
			isEquals = false;
		}
		if (memberINFO.MemberPriority != this.MemberPriority){
			isEquals = false;
		}
		return isEquals;
	}
}
