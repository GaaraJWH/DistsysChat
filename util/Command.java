package util;

public class Command {

	public static final String MSG_delimiter = "|";
	
	//MainWindow_GroupMSG
	public static final String GroupMSG_StartWith = "LotusGroupMSG";
	public static final String GroupMSG_Command_NewGroup = "NewGroup";
	public static final String GroupMSG_Command_DeleteGroup = "DeleteGroup";//Last One Send
	public static final String GroupMSG_Command_ModifyGroup = "ModifyGroup";//Only Leader Send
	public static final String GroupMSG_Command_FindGroup = "FindGroup";
	public static final String GroupMSG_Command_ExistGroup = "ExistGroup";//Only Leader Send
	
	//ChatRoom_ChatMSG
	public static final String ChatMSG_StartWith = "LotusChatMSG";
	public static final String ChatMSG_Command_FindLeder = "FindLeder";
	public static final String ChatMSG_Command_NewMember = "NewMember";//Only Leader Send
	public static final String ChatMSG_Command_DeleteMember = "DeleteMember";//Only Leader Send
	public static final String ChatMSG_Command_ChatMessage = "ChatMessage";
	public static final String ChatMSG_Command_MessageMiss = "MessageMiss";//MessageMiss Send
	public static final String ChatMSG_Command_LeaveGroup = "LeaveGroup";
	public static final String ChatMSG_Command_NewLeder = "NewLeder";
	public static final String ChatMSG_Command_LeaderMiss = "LeaderMiss";
	
	//TCP
	public static final String TCP_StartWith = "ClientServerMSG";
	public static final String Ask_Initial_INFO = "AskInitialINFO";
	public static final String Member_Priority = "MemberPriority";
	public static final String Member_List = "MemberList";
	public static final String Initial_INFO = "InitialINFO";
	public static final String Ask_SequenceNumber = "AskSequenceNumber";
	public static final Object SequenceNumber = "SequenceNumber";
	
	public static final String Sequence = "Sequence";

	
}
