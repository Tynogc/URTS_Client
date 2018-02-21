package lobby;

public class LobbyControle extends network.com.ConSupervisor{

	private boolean isHost;
	private boolean isReady;
	
	private ChatContainer chat;
	
	public LobbyControle(boolean host){
		isHost = host;
	}
	
	public void setChat(ChatContainer c){
		chat = c;
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
		System.out.println("Recived: >"+s+"< "+from);
	}
}
