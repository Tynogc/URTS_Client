package gui.sub;

import java.awt.Graphics;

import lobby.LobbyControle;
import main.GuiControle;
import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;

public class LobbyMenu extends AbstractMenu{

	private final LobbyControle controle;
	
	/**
	 * Restore past state
	 */
	private final gui.OverMenu overMenu;
	private final AbstractMenu lastMenu;
	
	//////////Buttons
	private Button exit;
	
	public LobbyMenu(LobbyControle l, gui.OverMenu o, AbstractMenu lm) {
		super(300,40,SeyprisMain.sizeX()-330, SeyprisMain.sizeY()-40);
		controle = l;
		overMenu = o;
		lastMenu = lm;
		
		exit = new Button(400, 400, "res/ima/cli/G") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				closeYou();
			}
		};
		exit.setText("Exit Lobby");
		add(exit);
	}

	@Override
	protected void uppdateIntern() {
		
	}

	@Override
	protected void paintIntern(Graphics g) {
		
	}

	@Override
	public void closeYou() {
		overMenu.setVisible(true);
		GuiControle.setSuperMenu(lastMenu);
		super.closeYou();
	}
}
