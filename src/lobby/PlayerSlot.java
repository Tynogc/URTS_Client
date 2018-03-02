package lobby;

import java.awt.Color;

import main.PicLoader;
import menu.Container;
import menu.DataFiled;

public class PlayerSlot extends Container{

	/**
	 * Holds the State, either the Player name, Empty or an other state
	 */
	private DataFiled statusFieldDF;
	
	/**
	 * Holds the Players Color
	 */
	private DataFiled colorDF;
	
	private DataFiled teamDF;
	
	private DataFiled factionDF;
	
	private DataFiled readyDF;
	
	/**
	 * Holds the Status of the slot: Empty, Closed or Player-ID
	 */
	private int status;
	private PlayerInLobby currentPlayerOnSlot;
	
	public static final int EMPTY = 0;
	public static final int CLOSED = -1;
	public static final int CLOSED_BY_MAP = -2;
	public static final int CLOSED_SPAWN_MEX = -3;
	
	private final LobbyControle lc;
	
	private static Color btcolor = Color.black;
	
	public PlayerSlot(int x, int y, LobbyControle l) {
		super(x, y);
		
		lc = l;
		
		teamDF = new DataFiled(300, 0, 40, 24, btcolor) {
			private int l = -1;
			@Override
			protected void uppdate() {
				int u = -1;
				if(currentPlayerOnSlot != null)
					u = currentPlayerOnSlot.team;
				
				if(l != u){
					l = u;
					if(l == -1)
						return;
					setIcon(PicLoader.pic.getImage("res/ima/win/num/"+l+".png"));
				}
			}
			
			@Override
			protected void isClicked() {}
		};
	}
	
	public int getStatus(){
		return status;
	}
	
	public void setStatus(int s){
		status = s;
		if(s > EMPTY)
			currentPlayerOnSlot = lc.derefId(s);
	}

}