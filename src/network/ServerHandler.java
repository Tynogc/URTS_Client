package network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Will handle incoming connections on a server.<br>
 * New TCPclients can be added to the process-cue by calling add();<br>
 * If there is a successfully started client, hasNew() will return true<br>
 * those connections can then be retrieved by calling getNewlyConnected();
 * 
 * @author Sven T. Schneider
 */
public class ServerHandler{
	
	/**
	 * Will hold all clients, currently connecting. IMPORTANT: to access this Object, the Semaphore must
	 * be acquired first!
	 */
	private List<TCPclient> clients;
	
	/**
	 * The Handler-Semaphore
	 */
	private Semaphore sema;
	
	/**
	 * Maximum time to process initial connection; Clients will be disconnected after that amount
	 * of time has passed without successful connecting.
	 */
	public static final int MAX_CONNECTION_TIME = 10000;

	public ServerHandler() {
		sema = new Semaphore(1);
		clients = new ArrayList<>();
	}
	
	/**
	 * Will also check for Clients timed out or no longer active and will close them.
	 * @return true if there are new clients to retrieve
	 */
	public boolean hasNew(){
		boolean b = false;
		sema.acquireUninterruptibly();
		try {
			for (Iterator<TCPclient> iterator = clients.iterator(); iterator.hasNext();) {
				TCPclient t = iterator.next();
				if(t.isConnected() && t.isRunning()){
					b = true; //Something new
				}else if(!t.isRunning()){
					iterator.remove();
				}else{
					if(System.currentTimeMillis()-t.connectionStartTime > MAX_CONNECTION_TIME){
						t.closeConnection("Connection timed out");
						iterator.remove();
					}
				}
			}
		} catch (Exception e) {
			debug.Debug.printExeption(e);
		}
		sema.release();
		return b;
	}
	
	/**
	 * @return A newly connected and processed TCPclient, null if there is none.
	 */
	public TCPclient getNewlyConnected(){
		TCPclient toReturn = null;
		sema.acquireUninterruptibly();
		try {
			for (Iterator<TCPclient> iterator = clients.iterator(); iterator.hasNext();) {
				TCPclient t = iterator.next();
				if(t.isConnected() && t.isRunning()){
					toReturn = t;
					iterator.remove();
					break;
				}
			}
		} catch (Exception e) {
			debug.Debug.printExeption(e);
		}
		sema.release();
		return toReturn;
	}
	
	/**
	 * Adds a new TCPclient to the processing
	 * @param t
	 */
	public void add(TCPclient t){
		sema.acquireUninterruptibly();
		clients.add(t);
		sema.release();
	}

}
