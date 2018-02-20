package main;

import network.com.ConnectionHandler;

/**
 * This class will handle all communication<br>
 * It provides access to the Com-Arrays, handle connections and controls the Game-Stats and -Modifiers<br>
 * It alos holds the connection to the main server
 * @author Sven T. Schneider
 */
public class CommunicationControle {

	private ConnectionHandler centralServer;
	
	private ConnectionHandler lobbyCon;
	private ConnectionHandler gameCon;
}
