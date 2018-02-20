package network.com;

/**
 * Can be overwritten and bound to a ConnectionHandler
 * @author Sven T. Schneider
 */
public abstract class ConSupervisor {

	private ConnectionHandler ch;
	
	/**
	 * Send Message to someone
	 * @param s the Message
	 * @param to to who to send
	 */
	public void send(String s, String to){
		ch.sendTo(s, to);
	}
	
	/**
	 * Recieved a Massage
	 * @param s the Message
	 * @param from who send it
	 */
	public abstract void recieve(String s, String from);
	
	/**
	 * Do not use (only for Bind-Operations)
	 */
	public void z_counterBind(ConnectionHandler c){
		ch = c;
	}
}
