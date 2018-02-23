package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Tries a TCP-Connection for a given period of time (5 seconds). Will call done() if successful.
 * @author Sven T. Schneider
 */
public abstract class ConnectionThread implements Runnable{

	//Terminates the Connection
	private boolean stop;
	
	private boolean isRunning;
	
	private final String ip;
	private final int port;
	
	
	/**
	 * Will try to connect to a TCP client for aprox. 10 seconds.
	 * @param ip
	 * @param port
	 */
	public ConnectionThread(String ip, int port) {
		this.ip = ip;
		this.port = port;
		
		isRunning = true;
		
		new Thread(this, "Connection-Init to "+ip).start();
	}
	
	@Override
	public void run() {
		try {
			Socket s = new Socket();
			s.connect(new InetSocketAddress(ip, port), 5000);
			callDone(s);
			setRunning(false);
		} catch (IOException e) {
			debug.Debug.println("*Cant connect to "+ip, debug.Debug.WARN);
			debug.Debug.println(e.toString(), debug.Debug.SUBWARN);
		}
	}
	
	private synchronized void callDone(Socket c) throws IOException{
		if(!stop)
			done(c);
		else
			c.close();
	}
	
	/**
	 * Will return the TCPclient if successfully connected
	 * @param client a connected and ready TCPclient
	 */
	public abstract void done(Socket client);
	
	/**
	 * Will stop the connection and prevent done() from being called
	 */
	public synchronized void stopConnection() {
		stop = true;
	}
	
	public synchronized boolean isRunning() {
		return isRunning;
	}
	
	private synchronized void setRunning(boolean r){
		isRunning = r;
	}
}
