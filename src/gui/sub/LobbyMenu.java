package gui.sub;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import gui.SuperButtonMenu;
import lobby.ChatContainer;
import lobby.LobbyControle;
import main.GuiControle;
import main.Language;
import main.PicLoader;
import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;
import menu.MoveMenu;

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
	
	private SuperButtonMenu[] sbm;
	
	private BufferedImage[] imas;
	
	private boolean showReady;
	
	private ChatContainer chat;
	
	private WaitingMenu wait;
	
	public LobbyMenu(LobbyControle l, gui.OverMenu o, AbstractMenu lm, main.GuiControle gc) {
		super(340,40,10,10);
		controle = l;
		overMenu = o;
		lastMenu = lm;
		guiControle = gc;
		
		timeIn = System.currentTimeMillis();
		
		sbm = new SuperButtonMenu[6];
		sbm[0] = new SuperButtonMenu(100, 0, 2);
		sbm[0].setText(Language.lang.text(4000));
		
		if(controle.isHost()){
			sbm[1] = new SuperButtonMenu(310, 0, 103){
				@Override
				public void paintYou(Graphics2D g) {
					super.paintYou(g);
					paintReady(g, xPos, yPos);
				}
				@Override
				protected void isClicked() {
					super.isClicked();
					controle.readyClicked();
				};
			};
			sbm[1].setText(Language.lang.text(4053));
		}else{
			sbm[1] = new SuperButtonMenu(310, 0, 103){
				@Override
				public void paintYou(Graphics2D g) {
					super.paintYou(g);
					paintReady(g, xPos, yPos);
				}
				@Override
				protected void isClicked() {
					super.isClicked();
					controle.readyClicked();
				};
			};
			sbm[1].setText(Language.lang.text(4052)+"?");
			sbm[1].showBlue();
		}
		
		sbm[2] = new SuperButtonMenu(520, 0, 19);
		sbm[2].setText(Language.lang.text(11019));
		
		sbm[3] = new SuperButtonMenu(690, 0, 20);
		sbm[3].setText(Language.lang.text(11020));
		
		sbm[4] = new SuperButtonMenu(860, 0, 104);
		sbm[4].setText(Language.lang.text(4050));
		
		sbm[5] = new SuperButtonMenu(1070, 0, 102){
			@Override
			protected void isClicked() {
				super.isClicked();
				closeYou();
			}
		};
		sbm[5].showRed(true);
		sbm[5].setText(Language.lang.text(4051)+" "+Language.lang.text(4000));
		
		for (int i = 0; i < sbm.length; i++) {
			add(sbm[i]);
			sbm[i].setVisible(false);
		}
		
		imas = new BufferedImage[]{
			PicLoader.pic.getImage("res/ima/cli/spb/SuperButtonReady1.png"),
			PicLoader.pic.getImage("res/ima/cli/spb/SuperButtonReady2.png"),
			PicLoader.pic.getImage("res/ima/cli/spb/SuperButtonReady3.png")
		};
		
		chat = new ChatContainer(300, 300);
		add(chat);
		controle.setChat(chat);
		
		moveAble = false;
		relocate();
		
		wait = new WaitingMenu(){
			@Override
			public void subClose() {
				LobbyMenu.this.closeYou();
			}
		};
		GuiControle.setSuperMenu(wait);
	}

	@Override
	protected void uppdateIntern() {
		if(timeOut>10){
			int t = (int)(System.currentTimeMillis()-timeOut);
			if(t>2000){
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
		
		if(showReady != controle.isReady()){
			showReady = controle.isReady();
			if(controle.isHost()){
				sbm[1].showBlue(showReady);
				if(showReady){
					sbm[1].setText(Language.lang.text(4054));
				}else{
					sbm[1].setText(Language.lang.text(4053));
				}
			}else{
				if(showReady){
					sbm[1].setText(Language.lang.text(4052)+"!");
				}else{
					sbm[1].setText(Language.lang.text(4052)+"?");
				}
			}
		}
		
		if(!controle.isBound()){
			SeyprisMain.getCom().setSupervisorForGame(controle);
		}else if(wait != null){
			wait.closeYou();
			wait = null;
		}else{
			controle.update();
		}
	}
	
	private void relocate(){
		xSize = SeyprisMain.sizeX()-330;
		ySize = SeyprisMain.sizeY()-40;
		for (int i = 0; i < sbm.length; i++) {
			sbm[i].setyPos(ySize-120);
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		
	}
	
	private void paintReady(Graphics2D g, int x, int y){
		if(!showReady)
			return;
		if(controle.isHost()){
			int u = (int)((System.currentTimeMillis()/10)%360);
			for (int i = 0; i < 3; i++) {
				int intens = (int)(Math.cos(Math.toRadians(u-i*120))*3.0+1.5);
				for (int j = 0; j < intens; j++) {
					g.drawImage(imas[1], x-10 +i*17, y+3, null);
					g.drawImage(imas[2], x+133 -i*17, y+3, null);
				}
			}
		}else{
			g.drawImage(imas[0], x+50, y+6, null);
		}
	}

	@Override
	public void closeYou() {
		if(timeOut<10)
			timeOut = System.currentTimeMillis();
		for (int i = 0; i < sbm.length; i++) {
			sbm[i].fadeOut(-sbm[i].getxPos()/2-sbm[i].getyPos()/6+100);
		}
		
		//Close game-connection
		SeyprisMain.getCom().clearGameConnection();
	}
}
abstract class WaitingMenu extends MoveMenu{

	public WaitingMenu() {
		super(0, 0, PicLoader.pic.getImage("res/ima/mbe/m400x200.png"), Language.lang.text(4005)+"...");
		
		xPos = SeyprisMain.sizeX()/2-xSize/2;
		yPos = SeyprisMain.sizeY()/2-ySize/2;
		
		Button b1 = new Button(xSize/2-75, 160, "res/ima/cli/G") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				subClose();
				closeYou();
			}
		};
		b1.setText(Language.lang.text(3));
		add(b1);
	}

	@Override
	protected void paintSecond(Graphics g) {
		
	}

	@Override
	protected boolean close() {
		subClose();
		return true;
	}

	@Override
	protected void uppdateIntern() {
		
	}
	
	public abstract void subClose();
	
}
