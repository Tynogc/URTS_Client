package network.com;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import crypto.RSAsaveKEY;
import network.StaticComStrings;
import network.TCPclient;

/**
 * This class extends the connection-handler to prepare it either as an central Server, a Server-Derived or
 * a PierToPier-Host
 * @author Sven T. Schneider
 */
public class ConCentralNode extends ConnectionHandler{

	/**
	 * A List of all connected Clients. ATTENTION must always acquire the Semaphore before modifying 
	 */
	protected List<TCPclient> connectedClients;
	protected Semaphore sema;
	
	protected int hashCode;
	
	public final String myAdress;
	
	public ConCentralNode(String myName, RSAsaveKEY key) {
		super(myName, key);
		connectedClients = new ArrayList<>();
		sema = new Semaphore(1);
		myAdress = utility.UniqueTimeStamp.getTimeStamp(false, 36);
	}
	

	@Override
	public int getSize() {
		sema.acquireUninterruptibly();
		int s = connectedClients.size();
		sema.release();
		return s;
	}

	@Override
	public String[] getConnectionNames() {
		sema.acquireUninterruptibly();
		String[] s = new String[connectedClients.size()];
		int i = 0;
		for (TCPclient t : connectedClients) {
			s[i] = t.getConnectionName();
			i++;
		}
		sema.release();
		return s;
	}
	
	@Override
	public String[] getUserNames() {
		sema.acquireUninterruptibly();
		String[] s = new String[connectedClients.size()];
		int i = 0;
		for (TCPclient t : connectedClients) {
			s[i] = t.getOtherName();
			i++;
		}
		sema.release();
		return s;
	}
	
	@Override
	public void addClient(TCPclient c) {
		sema.acquireUninterruptibly();
		connectedClients.add(c);
		sema.release();
		//Update Hashcode
		updateHashCode();		
		//Send Connection-Update to clients
		String[] s1 = getConnectionNames();
		String[] s2 = getUserNames();
		String ts = StaticComStrings.WHO_IS_CONNECTED;
		ts += myAdress+StaticComStrings.DIV+myName;
		for (int i = 0; i < s1.length; i++) {
			ts += StaticComStrings.DIV+s1[i]+StaticComStrings.DIV+s2[i];
		}
		for (TCPclient t : connectedClients) {
			t.send(ts);
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
		
		for (TCPclient t : connectedClients) {
			if(t.getConnectionName().startsWith(who)){
				if(t.isConnected()) //Send String and Frome-Tag
					t.send(to+StaticComStrings.TAG_FROM+myAdress+StaticComStrings.TAGEND+s);
			}
		}
	}
	
	@Override
	public void update() {
		for (Iterator<TCPclient> i = connectedClients.iterator(); i.hasNext();) {
			TCPclient t = i.next();
			
			//Check connect
			if(!t.isRunning() || !t.isConnected()){
				i.remove(); //This Client is dead
				continue;
			}
			
			String s = t.getNextInput();
			if(s == null) //Nothing to process
				continue;
			
			if(s.startsWith(StaticComStrings.TAG_TO)){ //Forward Message
				String sq = s.substring(StaticComStrings.TAG_TO.length());
				
				//Split Tag
				int u = sq.indexOf(StaticComStrings.TAGEND);
				String to = sq.substring(0, u);
				
				if(to.matches(StaticComStrings.TAG_ALL)){
					//Send to everyone
					for (TCPclient t2 : connectedClients) {
						if(t2.isConnected() && t2 != t)
							t2.send(sq.substring(u+1));//Remove To-Tag
					}
					recieved(sq);
				}else if(!to.matches(t.getConnectionName())){
					//Search for match and send
					for (TCPclient t2 : connectedClients) {
						if(to.matches(t2.getConnectionName()) && t2.isConnected())
							t2.send(s);
					}
				}else if(to.matches(myAdress)){
					recieved(sq);
				}else{
					debug.Debug.println("Unable to forward Msg. to: "+to);
				}
			}else{
				recieved(s);
			}
		}
	}
	
	/**
	 * Must be called every time, there are changes to the connection<br>
	 * IMPORTANT the Semaphore must not be acquired by the Thread calling this method
	 */
	protected void updateHashCode(){
		
		String[] connections = getConnectionNames();
		String[] conUserNames = getUserNames();
		
		if(conUserNames.length != connections.length){//Should never happen, just for safety
			System.err.println("Lengths don't match (ConCentralNode.updateHashCode() )");
			return;
		}
		
		int hc = 0x1<<connections.length;
		
		for (int i = 0; i < conUserNames.length; i++) {
			int u = 0;
			for (int j = 0; j < conUserNames[i].length(); j++) {
				u += conUserNames[i].charAt(j);
			}
			for (int j = 0; j < connections[i].length(); j++) {
				u += connections[i].charAt(j);
			}
			
			u = Integer.rotateLeft(u, i);
			
			hc += u;
		}
		
		sema.acquireUninterruptibly();
		hashCode = hc^connections.length;
		sema.release();
	}
	
	@Override
	public int hashCode() {
		sema.acquireUninterruptibly();
		int h = hashCode;
		sema.release();
		return h;
	}
	
	@Override
	public void disconnect() {
		sema.acquireUninterruptibly();
		for (TCPclient t : connectedClients) {
			t.closeConnection("Server Closed");
		}
		sema.release();
	}

}
