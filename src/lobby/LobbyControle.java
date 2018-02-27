package lobby;

import java.util.ArrayList;
import java.util.List;

import main.Language;
import main.SeyprisMain;

public class LobbyControle extends network.com.ConSupervisor{

	private boolean isHost;
	private boolean isReady;
	
	private ChatContainer chat;
	
	private static final String STR_CHAT = "#CHAT";
	
	/**
	 * Holds all currently connected Players
	 */
	private List<PlayerInLobby> players;
	private int lastConHashcode;
	
	public LobbyControle(boolean host){
		isHost = host;
	}
	
	/**
	 * Sends a Chat-Message
	 * @param s the Chat-Message
	 */
	private void chatSend(String s){
		if(s.length()<1)
			return;
		send(STR_CHAT+s, null);
		chat.addString(s, SeyprisMain.getMyName());
	}
	
	public void setChat(ChatContainer c){
		chat = c;
		chat.setSend(new network.Writable(){
			public void write(String s){
				chatSend(s);
			}
		});
	}
	
	public void readyClicked(){
		isReady = !isReady;
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	public boolean isHost() {
		return isHost;
	}

	@Override
	public void recieve(String s, String from) {
		if(s.startsWith(STR_CHAT)){
			PlayerInLobby p = derefConName(from);
			if(p != null){
				String f = p.name+" <"+from+">";
				chat.addString(s.substring(STR_CHAT.length()), f);
			}
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		//Check Hashcode of ConnectionHandler
		int hc = ch.hashCode();
		if(hc != lastConHashcode){
			lastConHashcode = hc;
			
			//Generate new List to replace the current playerList (players)
			List<PlayerInLobby> newList = new ArrayList<>();
			
			//Update connections...
			String[] cN = ch.getConnectionNames();//ConnectionNames
			String[] uN = ch.getUserNames();//UserNames
			chat.setKeyStrings(uN);
			
			for (int i = 0; i < cN.length; i++) {
				//ConName already known?
				PlayerInLobby pl = derefConName(cN[i]);
				if(pl == null){
					//Not registered
					pl = new PlayerInLobby();
					pl.connName_NSy = cN[i];
					pl.name = uN[i];
					
					//Connects
					if(!uN[i].matches(SeyprisMain.getMyName()))
						chat.addString(Language.lang.parametricText("###Bb#"+uN[i]+"###N#", 4100), null);
				}else{
					players.remove(pl);
				}
				newList.add(pl);
			}
			if(players != null)
				for (PlayerInLobby p : players) {
					//Disconnects
					chat.addString(Language.lang.parametricText("###Bb#"+p.name+"###N#", 4101), null);
				}
			
			players = newList;
		}
	}
	
	/**
	 * Dereferences the connectectionName to a Player
	 * @param c the Connection name
	 * @return The Player that is known by the connection
	 */
	public PlayerInLobby derefConName(String c){
		if(players == null)
			return null;
		
		for (PlayerInLobby p : players) {
			if(p.connName_NSy.matches(c))
				return p;
		}
		
		return null;
	}
	
	/**
	 * Dereferences the Player-ID to a Player
	 * @param id the id searched for
	 * @return The Player that is known by that id
	 */
	public PlayerInLobby derefId(int id){
		if(players == null)
			return null;
		
		for (PlayerInLobby p : players) {
			if(p.id == id)
				return p;
		}
		
		return null;
	}
}
