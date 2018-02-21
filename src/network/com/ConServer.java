package network.com;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import crypto.RSAsaveKEY;
import network.StaticComStrings;
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

	@Override
	public void update() {
		if(server.hasNew()){//Add new connection to the List
			sema.acquireUninterruptibly();
			connectedClients.add(server.getNewlyConnected());
			sema.release();
			//Update Hashcode
			updateHashCode();		
			//Send Connection-Update to clients
			String[] s1 = getConnectionNames();
			String[] s2 = getUserNames();
			String ts = StaticComStrings.WHO_IS_CONNECTED;
			for (int i = 0; i < s1.length; i++) {
				if(i != 0)
					ts += StaticComStrings.DIV;
				
				ts += s1[i]+StaticComStrings.DIV+s2[i];
			}
			sendTo(ts, null);
		}
		super.update();
	}

	@Override
	public void disconnect() {
		server.closeServer();
		super.disconnect();
	}
}
