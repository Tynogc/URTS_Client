package network.com;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import crypto.RSAsaveKEY;
import network.TCPclient;
import network.TCPserver;

/**
 * A Class providing a TCP-Server including a connection-handler and some basic methods. <br>
 * Will invoke a TCP-Server Object, which will handle all critical Server-Functionalities.<br>
 * It will also automatically forward messages if requested.
 * @author Sven T. Schneider
 */
public class ConServer extends ConCentralNode{
	
	private TCPserver server;
	
	/**
	 * Will open a TCP-Server at the specified port
	 * @param myName If this is a hosted PierToPier game, set to User-Name; if it's a central server,
	 *  set to StaticComStrings.SERVER
	 * @param key your RSA-Private-Key
	 * @param port Port to open the Server on
	 */
	public ConServer(String myName, RSAsaveKEY key, int port) {
		super(myName, key);
		server = new TCPserver(port, myName, myKey);
	}

	/**
	 * This Method is undefined for Servers and will just do nothing :D
	 */
	@Override
	public void connect(String ip, int port) {}

	/**
	 * This Method is undefined for Servers and will just do nothing :D
	 */
	@Override
	public void connect(InetAddress adress, int port) {}

	@Override
	public void update() {
		if(server.hasNew()){
			sema.acquireUninterruptibly();
			connectedClients.add(server.getNewlyConnected());
			sema.release();
		}
		super.update();
	}

}
