package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Semaphore;

import crypto.RSAsaveKEY;

/**
 * Enables the use of a SocketServer with all methods required for operation.<br>
 * Will start a Thread for handling new connections.<br>
 * Will also start a second thread, handling incoming request, if a connection is made and the start-parameters
 * are processed, the newly created TCPclient will be put on a waiting-list.<br>
 * If a new connection has been successfully processed hasNew() will return true.<br>
 * These clients can then be called for further processing by the method getNewlyConnected();
 * 
 * @author Sven T. Schneider
 */
public class TCPserver extends Thread {

	private ServerSocket server;

	public final int port;
	
	/**
	 * Acquire this semaphore to stop the Server
	 */
	private Semaphore isRunning;
	
	/**
	 * A Server handler to handle incoming connections
	 */
	private ServerHandler handler;
	
	private final String myName;
	private final RSAsaveKEY myKey;

	/**
	 * Creates a Server on the specified Port p. After creation, call Thread.start() to 
	 * start accepting requests.
	 * @param p Server Port
	 */
	public TCPserver(int p, String name, RSAsaveKEY key) {
		super("Server handler on Port "+p);
		port = p;
		isRunning = new Semaphore(1);
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(10000);//The server will have a ten-second hold before checking
		} catch (IOException e) {
			debug.Debug.println("*Exeption creating server! " + e.toString(), debug.Debug.ERROR);
			e.printStackTrace();
			isRunning.acquireUninterruptibly();
		}
		handler = new ServerHandler();
		myKey = key;
		myName = name;
	}

	@Override
	public void run() {
		try {
			while(isRunning.availablePermits()>0){
				waitForCon();
			}
		} catch (Exception e) {
			debug.Debug.println("*Server Exception: "+e.toString(), debug.Debug.ERROR);
			e.printStackTrace();
		}
		debug.Debug.println("Server Terminated", debug.Debug.WARN);
		try {
			server.close();
		} catch (IOException e) {
			debug.Debug.printExeption(e);
		}
	}
	
	/**
	 * Will wait for about ten seconds for a Connection-Request.<br>
	 * If a connection is acquired the resulting socket is given to the ServerHandler
	 * @throws IOException if error occurs
	 */
	private void waitForCon() throws IOException{
		try {
			Socket s = server.accept();
			//Create a new TCP-Client to handle the connection; Connection-Name will be a time-stamp (0-9+A-Z)
			TCPclient t = new TCPclient(s, myKey, myName, utility.UniqueTimeStamp.getTimeStamp(false, 36));
			handler.add(t);
		} catch (SocketTimeoutException e) {
			//Will be ignored, no connection within timeout-interval
		}
	}
	
	/**
	 * Calling this method will stop the Server within the next 10 seconds.
	 */
	public void closeServer(){
		isRunning.tryAcquire();
	}
	
	/**
	 * @return true if there are new clients ready
	 */
	public boolean hasNew(){
		return handler.hasNew();
	}
	
	/**
	 * @return A newly connected and processed TCPclient, null if there is none.
	 */
	public TCPclient getNewlyConnected(){
		return handler.getNewlyConnected();
	}
}
