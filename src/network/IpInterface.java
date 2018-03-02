package network;

import crypto.RSAsaveKEY;

/**
 * Envelops communication over the InternetProtocoll, will be extended for TCP and UDP.<br>
 * @author Sven T. Schneider
 */
public interface IpInterface {

	/**
	 * @param s String that should be send over the connection
	 */
	public void send(String s);
	
	/**
	 * Returns the next String the connection has received, will return null if there's nothing new
	 * @return Next Message or null
	 */
	public String getNextInput();
	
	/**
	 * Closes this connection
	 * @param reason will be send to the other end, together with the CLOSE-ORDER
	 */
	public void closeConnection(String reason);
	
	/**
	 * @return true if the connection was initially acquired
	 */
	public boolean isConnected();
	
	/**
	 * @return true if the connection is currently running/trying to acquire connection.
	 * Will only be false, if close() was called, or an Error was encountered.
	 */
	public boolean isRunning();
	
	/**
	 * @return the other-Sides Public-RSA-Key
	 */
	public RSAsaveKEY getOtherKey();
	
	/**
	 * @return the user/server-name connected to
	 */
	public String getOtherName();
	
	/**
	 * @return a String to identify the connection. 
	 * This will be used for message forwarding, FROM and TO Tags etc.
	 */
	public String getConnectionName();
}
