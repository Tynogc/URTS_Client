package lobby;

public class LobbyControle extends network.com.ConSupervisor{

	private boolean isHost;
	private boolean isReady;
	
	private ChatContainer chat;
	
	private static final String STR_CHAT = "#CHAT";
	
	public LobbyControle(boolean host){
		isHost = host;
	}
	
	/**
	 * Sends a Chat-Message
	 * @param s the Chat-Message
	 */
	private void chatSend(String s){
		send(STR_CHAT+s, null);
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
			
		}
	}
}
