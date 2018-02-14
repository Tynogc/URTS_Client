package network.com;

import java.net.InetAddress;

import crypto.RSAsaveKEY;

/**
 * Will either handle a Server-Node, a Pier-To-Pier-System or a Central-Server <br>
 * This class provides basic methods for the network functionality. <br>
 * Note that the functionality shouldn't change between operation-modes.
 * @author Sven T. Schneider
 */
public abstract class ConnectionHandler {
	
	public final String myName;
	
	public final RSAsaveKEY myKey;
	
	public ConnectionHandler(String myName, RSAsaveKEY key){
		this.myName = myName;
		myKey = key;
	}

	/**
	 * @return The current number of connected Clients
	 */
	public abstract int getSize();
	
	/**
	 * @return all Connection-Names of the current connections
	 */
	public abstract String[] getConnectionNames();
	
	/**
	 * Should invoke a connection-method connecting to the specified address
	 */
	public abstract void connect(String ip, int port);
	
	/**
	 * Should invoke a connection-method connecting to the specified address
	 */
	public abstract void connect(InetAddress adress, int port);
	
	/**
	 * Should process one Input from every connected TCP-Client/All incoming messages if only one TCP-Client
	 * is connected.
	 */
	public abstract void update();
	
	/**
	 * Sends the String to the specified client; Note: who has to be the abstract connection-name, not the
	 * user Name!
	 * @param s The Message to be send
	 * @param who the Connection-name, null if it should be send to all connected clients
	 */
	public abstract void sendTo(String s, String who);
}
