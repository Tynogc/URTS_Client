package main;

import java.net.Socket;

import crypto.RSAsaveKEY;
import network.TCPclient;
import network.com.ConnectionHandler;

/**
 * This class will handle all communication<br>
 * It provides access to the Com-Arrays, handle connections and controls the Game-Stats and -Modifiers<br>
 * It also holds the connection to the main server
 * @author Sven T. Schneider
 */
public class CommunicationControle {

	private ConnectionHandler centralServer;
	
	private ConnectionHandler gameCon;
	
	private final String name;
	private final RSAsaveKEY key;
	
	private network.ConnectionThread conThread;
	private int state = STATE_EMPTY;
	
	public static final int STATE_WAITING = 10;
	public static final int STATE_ERROR = 30;
	public static final int STATE_CONECTED = 20;
	public static final int STATE_EMPTY = 0;
	
	public CommunicationControle(String n, RSAsaveKEY k){
		name = n;
		key = k;
	}
	
	//////////////////////////////Start-Operations/////////////////////////////////
	
	/**
	 * Must always be called before invoking a newCon_() Method!<br>
	 * Calling it while a newCon-Operation is running, will result in that connection being terminated.
	 */
	public synchronized void clearGameConnection(){
		if(conThread != null){
			conThread.stopConnection();
			conThread = null;
		}
		if(gameCon != null){
			gameCon.disconnect();
			gameCon = null;
		}
		state = STATE_EMPTY;
	}
	
	/**
	 * Creates a Server on the specified Port. Clear Game-Connection before calling!
	 * @param port Server-Port
	 * @throws IllegalStateException if the Game-Connection is occupied
	 */
	public synchronized void newCon_openServer(int port) throws IllegalStateException{
		if(gameCon != null || conThread != null)
			throw new IllegalStateException("Game-Connection Occupied!");
		
		gameCon = new network.com.ConServer(name, key, port);
		state = STATE_CONECTED;
	}
	
	/**
	 * Creates a New TCP-Connection. Clear Game-Connection before calling!
	 * @param ip address of the Server
	 * @param port Server-Port
	 * @throws IllegalStateException if the Game-Connection is occupied
	 */
	public synchronized void newCon_connectClient(String ip, int port) throws IllegalStateException{
		if(gameCon != null || conThread != null)
			throw new IllegalStateException("Game-Connection Occupied!");
		
		state = STATE_WAITING;
		conThread = new network.ConnectionThread(ip, port) {
			@Override
			public void done(Socket client) {
				conThread = null;
				network.com.EndNode c = new network.com.EndNode(name, key);
				c.addClient(new TCPclient(client, key, name, null));
				setCon(c);
			}
		};
	}
	
	/**
	 * @param c will be Bound to the Game-Connection
	 */
	public synchronized void setSupervisorForGame(network.com.ConSupervisor c){
		if(gameCon != null)
			gameCon.bind(c);
	}
	
	public synchronized int getState() {
		return state;
	}
	
	private synchronized void setCon(network.com.ConnectionHandler c){
		gameCon = c;
		state = STATE_CONECTED;
	}
}
