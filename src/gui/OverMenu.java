package gui;

import java.awt.Graphics;

import main.EventCounter;
import main.GuiControle;
import main.Language;
import main.SeyprisMain;
import main.SubMenuStorage;
import menu.AbstractMenu;
import menu.Button;

public class OverMenu extends AbstractMenu{

	private SuperButtonMenu[] sbm;
	
	private SuperButtonMenu mainMenu;
	private SuperButtonMenu alarmMenu;
	
	private ButtonMover mover;
	
	private boolean stateIsMain = true;
	private long timeToMain;
	
	private long scrollUpAnim;
	private long scrollDownAnim;
	
	private final SubMenuStorage sms;
	private final GuiControle gui;
	
	private int activMenuID;
	private AbstractMenu activMenu;
	
	private static OverMenu ovm;
	
	public OverMenu(GuiControle g, SubMenuStorage s) {
		super(0,0,2000,600);
		
		sms = s;
		gui = g;
		
		ovm = this;
		
		sbm = new SuperButtonMenu[40];
		
		for (int i = 0; i < sbm.length; i++) {
			final int u = i;
			sbm[i] = new SuperButtonMenu(0, 0, i){
				@Override
				protected void isClicked() {
					super.isClicked();
					buttonClicked(u);
				}
			};
			add(sbm[i]);
			sbm[i].fadeIn(-300);
		}
		locateButton();
		relocate();
		
		for (int i = 0; i < sbm.length; i++) {
			if(sms.getMenu(i) == null){
				sbm[i].setDisabled(true);
				sbm[i].setBackPic(true);
			}else{
				sbm[i].setText(Language.lang.text(11000+i));
			}
		}
		
		mainMenu = new SuperButtonMenu(170, 240, 100){
			@Override
			protected void isClicked() {
				super.isClicked();
				buttonClickedMain();
			}
		};
		add(mainMenu);
		mainMenu.showBlue();
		mainMenu.setText(main.Language.lang.text(11100));
		mainMenu.setVisible(false);
		
		alarmMenu = new SuperButtonMenu(0, 240, 101){
			@Override
			protected void isClicked() {
				super.isClicked();
				//TODO
			}
		};
		add(alarmMenu);
		alarmMenu.setText(main.Language.lang.text(11101));
		
		gui.setUserMenu(sms.mainMenu);
		
		moveAble = false;
	}
	
	public void relocate(){
		xPos = SeyprisMain.sizeX()-1560-320;
		yPos = SeyprisMain.sizeY()-360;
	}

	@Override
	protected void uppdateIntern() {
		if(mover != null)
			if(mover.move()){
				mover = null;
				
				if(sms.aditionalButtons.length>activMenuID)
				if(sms.aditionalButtons[activMenuID] != null){
					for (int i = 0; i < sms.aditionalButtons[activMenuID].length; i++) {
						if(sms.aditionalButtons[activMenuID][i] < sbm.length){
							SuperButtonMenu m = sbm[sms.aditionalButtons[activMenuID][i]];
							m.setyPos(240);
							m.setxPos((i+3)*170);
							m.fadeIn(-i*300);
						}
					}
				}
			}
		
		if(timeToMain > 100){
			if(timeToMain < System.currentTimeMillis()){
				stateIsMain = true;
				timeToMain = 0;
				
				gui.setUserMenu(sms.mainMenu);
				
				locateButton();
			}
		}
		
		if(scrollUpAnim > 100){
			int m = 1400-(int)(System.currentTimeMillis()-scrollUpAnim);
			if(m<0){
				scrollUpAnim = 0;
				activMenu.yPos = 100;
			}else{
				activMenu.yPos = 100+m;
			}
		}
		if(scrollDownAnim > 100){
			int m = (int)(System.currentTimeMillis()-scrollDownAnim);
			activMenu.yPos = m+100;
			if(m>1400)
				scrollDownAnim = 0;
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		
	}
	
	private void locateButton(){
		int x = 340;
		int y = 0;
		for (int i = 0; i < sbm.length; i++) {
			if(i>26){
				sbm[i].setxPos(340);
				sbm[i].setyPos(240);
				sbm[i].setVisible(false);
				continue;
			}
			
			sbm[i].setxPos(x);
			sbm[i].setyPos(y);
			sbm[i].fadeIn(-sbm[i].getxPos()/2-sbm[i].getyPos()/6+100);
			sbm[i].setVisible(true);
			x+=170;
			if(x>=170*11-1){
				x = 340;
				y += 120;
			}
		}
	}
	
	private void buttonClickedMain(){
		timeToMain = System.currentTimeMillis()+1000;
		for (int i = 0; i < sbm.length; i++) {
			if(sbm[i].isVisible())
				sbm[i].fadeOut(-sbm[i].getxPos()/2-sbm[i].getyPos()/6+100);
		}
		mainMenu.fadeOut(-mainMenu.getxPos()/2-mainMenu.getyPos()/6+100);
		scrollDownAnim = System.currentTimeMillis();
		
		debug.Debug.println("Menu Changed: ", debug.Debug.SUBCOM);
		debug.Debug.print("MAIN", debug.Debug.MESSAGE);
	}
	
	private void buttonClicked(int i){
		activMenu = sms.getMenu(i);
		if(activMenu == null)
			return;
		for (int j = 0; j < sbm.length; j++) {
			if(i != j){
				if(sbm[j].isVisible() || stateIsMain)
					sbm[j].fadeOut(-sbm[j].getxPos()/2-sbm[j].getyPos()/6+100);
			}else{
				mover = new ButtonMover(sbm[j], 340,  240, 500);
			}
		}
		
		debug.Debug.println("Menu Changed: ", debug.Debug.SUBCOM);
		debug.Debug.print(sbm[i].getText());
		
		activMenuID = i;
		gui.setUserMenu(activMenu);
		if(stateIsMain){
			scrollUpAnim = System.currentTimeMillis();
			activMenu.yPos = SeyprisMain.sizeY();
		}else{
			activMenu.yPos = 100;
		}
		stateIsMain = false;
		if(!mainMenu.isVisible())
			mainMenu.fadeIn(-500);
	}
	
	public static void buttonClickedStatic(int i){
		if(ovm != null)
			ovm.buttonClicked(i);
	}
	
	public static void mainClickedStatic(){
		if(ovm != null)
			ovm.buttonClickedMain();
	}

}

class ButtonMover{
	private final Button bu;
	private final int destinationX;
	private final int destinationY;
	
	private long t;
	
	public ButtonMover(Button b, int x, int y, int te){
		bu = b;
		destinationX = x;
		destinationY = y;
		t = System.currentTimeMillis()+te;
	}
	
	public boolean move(){
		int u = (int)(System.currentTimeMillis()-t);
		if(u<1)
			return false;
		
		EventCounter.event();
		
		t+=u;
		
		if(bu.getxPos()<destinationX){
			bu.setxPos(bu.getxPos()+u);
			if(bu.getxPos()>destinationX){
				bu.setxPos(destinationX);
			}
		}else if(bu.getxPos()>destinationX){
			bu.setxPos(bu.getxPos()-u);
			if(bu.getxPos()<destinationX){
				bu.setxPos(destinationX);
			}
		}else if(bu.getyPos()<destinationY){
			bu.setyPos(bu.getyPos()+u);
			if(bu.getyPos()>destinationY){
				bu.setyPos(destinationY);
			}
		}else if(bu.getyPos()>destinationY){
			bu.setyPos(bu.getyPos()-u);
			if(bu.getyPos()<destinationY){
				bu.setyPos(destinationY);
			}
		}
		
		return bu.getxPos()==destinationX && bu.getyPos()==destinationY;
	}
}
