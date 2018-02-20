package lobby;

public class LobbyControle extends network.com.ConSupervisor{

	private boolean isHost;
	private boolean isReady;
	
	public LobbyControle(boolean host){
		isHost = host;
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
		
	}
}
