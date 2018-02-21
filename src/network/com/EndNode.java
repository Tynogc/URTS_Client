package network.com;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import network.StaticComStrings;
import network.TCPclient;
import crypto.RSAsaveKEY;

/**
 * Single-Connection to an central Node
 * @author Sven T. Schneider
 */
public class EndNode extends ConnectionHandler {

	/**
	 * The TCP-Client; Attention: always aqquire the Semaphore before calling
	 */
	private TCPclient tcp;
	private Semaphore sema;
	
	/**
	 * A virtual representation of connections, will be set by the server.
	 */
	private String[] connections;
	private String[] conUserNames;
	
	private int hashCode = 0;
	
	public EndNode(String myName, RSAsaveKEY key) {
		super(myName, key);
		connections = new String[0];
		conUserNames = new String[0];
		sema = new Semaphore(1);
	}

	@Override
	public int getSize() {
		return connections.length;
	}

	@Override
	public synchronized String[] getConnectionNames() {
		return connections;
	}
	
	@Override
	public synchronized String[] getUserNames() {
		return conUserNames;
	}
	
	/**
	 * Sets the Con-Names and Con-User-Names according to the transmitted String
	 */
	private synchronized void setConnectionStrings(String s){
		String[] st = s.split(StaticComStrings.DIV);
		if(st.length/2 != connections.length){
			connections = new String[st.length/2];
			conUserNames = new String[st.length/2];
		}
		for (int i = 0; i < connections.length; i++) {
			connections[i] = st[i*2];
			conUserNames[i] = st[i*2+1];
		}
		
		//Update Hash-Code//////////////////////////////
		
		hashCode = 0x1<<connections.length;
		
		for (int i = 0; i < conUserNames.length; i++) {
			int u = 0;
			for (int j = 0; j < conUserNames[i].length(); j++) {
				u += conUserNames[i].charAt(j);
			}
			for (int j = 0; j < connections[i].length(); j++) {
				u += connections[i].charAt(j);
			}
			
			u = Integer.rotateLeft(u, i);
			
			hashCode += u;
		}
		
		hashCode = hashCode^connections.length;
	}

	@Override
	public void addClient(TCPclient c) throws IllegalStateException {
		sema.acquireUninterruptibly();
		if(tcp != null){//Already occupied
			sema.release();
			throw new IllegalStateException("Client already Occupied!");
		}
		tcp = c;
		sema.release();
	}

	@Override
	public void update() {
		if(!sema.tryAcquire())
			return;
		if(tcp == null){
			sema.release();
			return;
		}
		String s = tcp.getNextInput();
		sema.release();
		
		if(s == null)
			return;
		
		if(s.startsWith(StaticComStrings.WHO_IS_CONNECTED)){//Process Connection names and Strings
			s = s.substring(StaticComStrings.WHO_IS_CONNECTED.length());
			setConnectionStrings(s);
		}else{
			recieved(s);
		}
	}

	@Override
	public void sendTo(String s, String who) {
		String to;
		if(who == null){
			who = "";
			to = StaticComStrings.TAG_TO+StaticComStrings.TAG_ALL+StaticComStrings.TAGEND; //<TO ALL>+s
		}else{
			to = StaticComStrings.TAG_TO+who+StaticComStrings.TAGEND; //<TO who>+s
		}
		
		sema.acquireUninterruptibly();
		if(tcp == null){
			sema.release();
			return;
		}
		String from = StaticComStrings.TAG_FROM+tcp.getConnectionName()+StaticComStrings.TAGEND;
		
		tcp.send(to+from+s);
		sema.release();
	}
	
	/**
	 * Returns a hash-Code, changes every time there are changes to the Connection-Names
	 */
	@Override
	public synchronized int hashCode() {
		return hashCode;
	}

	@Override
	public void disconnect() {
		sema.acquireUninterruptibly();
		if(tcp != null)
			tcp.closeConnection("Disconnected");
		sema.release();
	}
}
