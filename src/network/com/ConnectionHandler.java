package network.com;

import network.StaticComStrings;
import network.TCPclient;
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
	
	private ConSupervisor connectionSuperviser;
	
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
	 * @return all connected Users; these Names do NOT belong to the connection
	 */
	public abstract String[] getUserNames();
	
	/**
	 * Add a connected TCPclient to the Processing.
	 * @throws IllegalStateException if all spaces are occupied
	 */
	public abstract void addClient(TCPclient c) throws IllegalStateException;
	
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
	
	/**
	 * Binds a ConSuperviser to this handler
	 */
	public void bind(ConSupervisor c){
		connectionSuperviser = c;
		c.z_counterBind(this);
	}
	
	/**
	 * Recieved a String; removes and processes the Fome-Tag
	 * @param sthe Message recieved
	 */
	protected void recieved(String s){
		if(connectionSuperviser == null)
			return;
		
		int u = s.indexOf(StaticComStrings.TAG_FROM);
		String f = "";
		if(u >= 0){
			s = s.substring(u+StaticComStrings.TAG_FROM.length());
			u = s.indexOf(StaticComStrings.TAGEND);
			f = s.substring(0, u);
			s = s.substring(u);
		}
		connectionSuperviser.recieve(s, f);
	}
	
	/**
	 * Should close all Connections from/to this Handler
	 */
	public abstract void disconnect();
}
