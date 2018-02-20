package gui.sub;

import java.awt.Graphics;

import gui.SuperButtonMenu;
import lobby.LobbyControle;
import main.Language;
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
	private final main.GuiControle guiControle;
	
	private long timeIn;
	private long timeOut;
	
	//////////Buttons
	private Button exit;
	
	private SuperButtonMenu[] sbm;
	
	public LobbyMenu(LobbyControle l, gui.OverMenu o, AbstractMenu lm, main.GuiControle gc) {
		super(340,40,10,10);
		controle = l;
		overMenu = o;
		lastMenu = lm;
		guiControle = gc;
		
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
		timeIn = System.currentTimeMillis();
		
		sbm = new SuperButtonMenu[1];
		sbm[0] = new SuperButtonMenu(0, 0, 2);
		sbm[0].setText(Language.lang.text(4000));
		
		for (int i = 0; i < sbm.length; i++) {
			add(sbm[i]);
			sbm[i].setVisible(false);
		}
		
		moveAble = false;
		relocate();
	}

	@Override
	protected void uppdateIntern() {
		if(timeOut>10){
			int t = (int)(System.currentTimeMillis()-timeOut);
			if(t>1000){
				overMenu.lock(false);
				super.closeYou();
				guiControle.setUserMenu(lastMenu);
				timeOut = 0;
			}
		}
		
		if(timeIn>10){
			int t = (int)(System.currentTimeMillis()-timeIn);
			if(t>1000){
				for (int i = 0; i < sbm.length; i++) {
					sbm[i].fadeIn(-sbm[i].getxPos()/2-sbm[i].getyPos()/6+100);
				}
				timeIn = 0;
			}
		}
		
		if(xSize != SeyprisMain.sizeX()-330 || ySize != SeyprisMain.sizeY()-40)
			relocate();
	}
	
	private void relocate(){
		xSize = SeyprisMain.sizeX()-330;
		ySize = SeyprisMain.sizeY()-40;
		for (int i = 0; i < sbm.length; i++) {
			sbm[i].setyPos(ySize-120);
			sbm[i].setxPos(110+i*160);
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		
	}

	@Override
	public void closeYou() {
		if(timeOut<10)
			timeOut = System.currentTimeMillis();
		for (int i = 0; i < sbm.length; i++) {
			sbm[i].fadeOut(-sbm[i].getxPos()/2-sbm[i].getyPos()/6+100);
		}
	}
}
