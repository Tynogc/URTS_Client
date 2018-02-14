package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import main.Fonts;
import main.PicLoader;
import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;

public class FrameMenu extends AbstractMenu{

	private Button close;
	private Button minimised;
	private Button maximised;
	
	private final SeyprisMain main;
	
	private boolean isFullScreen;
	private boolean showMaximise;
	
	private BufferedImage text;
	private BufferedImage textFS;
	private BufferedImage icon;
	
	public FrameMenu(SeyprisMain m) {
		super(0,0,SeyprisMain.sizeX(),30);
		
		main = m;
		
		icon = PicLoader.pic.getImage("res/win/seypris.png");
		
		close = new Button(SeyprisMain.sizeX()-50,0,"res/ima/cli/spb/R") {
			@Override
			protected void uppdate() {
			}
			@Override
			protected void isFocused() {
			}
			@Override
			protected void isClicked() {
				System.exit(1);
			}
		};
		add(close);
		minimised = new Button(SeyprisMain.sizeX()-150,0,"res/ima/cli/spb/B") {
			@Override
			protected void uppdate() {
			}
			@Override
			protected void isFocused() {
			}
			@Override
			protected void isClicked() {
				SeyprisMain.getFrame().setState(JFrame.ICONIFIED);
			}
		};
		add(minimised);
		maximised = new Button(SeyprisMain.sizeX()-100,0,"res/ima/cli/spb/C") {
			@Override
			protected void uppdate() {
			}
			@Override
			protected void isFocused() {
			}
			@Override
			protected void isClicked() {
				main.setFullScreen(!isFullScreen, false);
			}
		};
		add(maximised);
		
		moveAble = false;
		
		/////////////////////////////////////////////////////////////////////////////////////
		//Text-Images
		
		text = new BufferedImage(200, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics g = text.getGraphics();
		g.setFont(Fonts.font18);
		g.setColor(SeyprisMain.topRun);
		for (int i = -3; i <= 3; i++) {
			for (int j = -3; j <= 3; j++) {
				g.drawString(SeyprisMain.TITLE, 25+i, 20+j);
			}
		}
		g.setColor(Color.gray);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				g.drawString(SeyprisMain.TITLE, 25+i, 20+j);
			}
		}
		g.setColor(Color.black);
		g.drawString(SeyprisMain.TITLE, 25, 20);
		
		textFS = new BufferedImage(200, 30, BufferedImage.TYPE_INT_ARGB);
		g = textFS.getGraphics();
		g.setFont(Fonts.font18);
		g.setColor(Color.gray);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				g.drawString(SeyprisMain.TITLE, 25+i, 20+j);
			}
		}
		g.setColor(Color.white);
		
		g.drawString(SeyprisMain.TITLE, 25, 20);
	}
	
	public void resize(){
		xSize = SeyprisMain.sizeX();
		close.setxPos(SeyprisMain.sizeX()-50);
		if(showMaximise){
			minimised.setxPos(SeyprisMain.sizeX()-150);
			maximised.setxPos(SeyprisMain.sizeX()-100);
		}else{
			minimised.setxPos(SeyprisMain.sizeX()-100);
		}
		maximised.setVisible(showMaximise);
	}
	
	public void isFullScreen(boolean s){
		isFullScreen = s;
		minimised.setVisible(!s);
		if(s){
			maximised.setFilePath("res/ima/cli/spb/D");
		}else{
			maximised.setFilePath("res/ima/cli/spb/C");
		}
	}

	@Override
	protected void uppdateIntern() {
	}
	
	public void showMaximise(boolean showMaximise) {
		this.showMaximise = showMaximise;
	}

	@Override
	protected void paintIntern(Graphics g) {
		if(isFullScreen)
			g.drawImage(textFS, 0, 0, null);
		else
			g.drawImage(text, 0, 0, null);
		
		g.drawImage(icon, 3, 5, null);
	}

}
