import java.util.Date;

public class MessageNode {
	MessageNode next = null;
	MessageNode prev = null;
	String user = "";   	// user name
	Date date	= null;		// date sent
	String message = "";
	 
	public MessageNode(String user, Date date, String message) {
		this.user = user;
		this.date = date;
		this.message = message;
	}
}