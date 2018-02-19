package gui.sub;

import gui.UserMenu;
import main.Language;
import menu.GroupContainer;

public class HostGame extends UserMenu{

	private GroupContainer hostP2P;
	private GroupContainer hostOnServer;
	
	public HostGame() {
		super(new String[]{Language.lang.text(11003)}, 3);
		
		hostOnServer = new GroupContainer(20, 0, 300, 100, Language.lang.text(4010));
		hostP2P = new GroupContainer(340, 0, 300, 100, Language.lang.text(4011));
		add(hostOnServer);
		add(hostP2P);
		
		relocated();
	}

	@Override
	protected void relocated() {
		hostOnServer.setyPos(ySize-120);
		hostP2P.setyPos(ySize-120);
	}

	@Override
	protected void buttonClicked(int b) {
		
	}

}
