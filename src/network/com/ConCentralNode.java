package network.com;

import java.util.ArrayList;
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
public abstract class ConCentralNode extends ConnectionHandler{

	/**
	 * A List of all connected Clients. ATTENTION must always acquire the Semaphore before modifying 
	 */
	protected List<TCPclient> connectedClients;
	protected Semaphore sema;
	
	public ConCentralNode(String myName, RSAsaveKEY key) {
		super(myName, key);
		connectedClients = new ArrayList<>();
		sema = new Semaphore(1);
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
					t.send(to+StaticComStrings.TAG_FROM+t.getConnectionName()+StaticComStrings.TAGEND+s);
			}
		}
	}
	
	@Override
	public void update() {
		for (TCPclient t : connectedClients) {
			String s = t.getNextInput();
			if(s == null)
				continue;
			
			if(s.startsWith(StaticComStrings.TAG_TO)){ //Forward Message
				String sq = s.substring(StaticComStrings.TAG_TO.length());
				
				//Split Tag
				int u = sq.indexOf(StaticComStrings.TAGEND);
				String to = sq.substring(0, u);
				
				if(!to.matches(t.getConnectionName())){
					//Search for match and send
					for (TCPclient t2 : connectedClients) {
						if(to.matches(t2.getConnectionName()))
							t2.send(s);
					}
				}
			}
		}
	}

}
