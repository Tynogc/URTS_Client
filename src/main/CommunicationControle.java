package main;

import crypto.RSAsaveKEY;
import network.com.ConnectionHandler;

/**
 * This class will handle all communication<br>
 * It provides access to the Com-Arrays, handle connections and controls the Game-Stats and -Modifiers<br>
 * It alos holds the connection to the main server
 * @author Sven T. Schneider
 */
public class CommunicationControle {

	private ConnectionHandler centralServer;
	
	private ConnectionHandler gameCon;
	
	private final String name;
	private final RSAsaveKEY key;
	
	public CommunicationControle(String n, RSAsaveKEY k){
		name = n;
		key = k;
	}
	
	//////////////////////////////Start-Operations/////////////////////////////////
	
	public void openServer(int port){
		
	}
	
	public void connectClient(String ip, int port){
		
	}
	
	public void setSupervisorForGame(network.com.ConSupervisor c){
		gameCon.bind(c);
	}
}
