package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.PicLoader;
import main.SeyprisMain;
import menu.AbstractMenu;
import menu.Button;

public abstract class UserMenu extends AbstractMenu{

	private BufferedImage lowerStop;
	
	private int lastY;
	private int clickX;
	
	private Button bx;
	private Button[] btl;
	
	private BufferedImage k1;
	private BufferedImage k2;
	
	private static Color[] backClrs;
	
	private static Color rim1 = new Color(120, 120, 120);
	private static Color rim2 = new Color(80, 80, 80);
	
	private String name = "";
	
	public UserMenu(String[] but, int id) {
		super(340,0,1500,670);
		
		lowerStop = PicLoader.pic.getImage("res/ima/cli/spb/lowerPoint.png");
		k1 = PicLoader.pic.getImage("res/ima/cli/spb/UserTop.png");
		k2 = PicLoader.pic.getImage("res/men/"+id+"s.png");
		
		bx = new Button(xSize-32, 0, "res/ima/cli/spb/UserTopX") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				OverMenu.mainClickedStatic();
			}
		};
		add(bx);
		
		btl = new Button[but.length];
		for (int i = 0; i < but.length; i++) {
			final int u = i;
			btl[i] = new HigherButton(40+i*200) {
				@Override
				protected void uppdate() {}
				@Override
				protected void isFocused() {}
				@Override
				protected void isClicked() {
					btlClck(u);
				}
			};
			btl[i].setText(but[i]);
			add(btl[i]);
		}
		btl[0].setDisabled(true);
		
		moveAble = false;
		
		if(backClrs == null){
			backClrs = new Color[40];
			for (int i = 0; i < backClrs.length; i++) {
				backClrs[i] = new Color(i+10,i+10,i+10);
			}
		}
		
		if(but.length>=1)
			name = but[0];
	}
	
	@Override
	protected void paintIntern(Graphics g) {
		g.setColor(rim2);
		g.drawRect(0, 0, xSize-1, ySize+2);
		g.setColor(rim1);
		g.drawRect(1, 1, xSize-3, ySize);
		
		if(yPos < 110)
			g.drawImage(lowerStop, clickX-xPos, ySize, null);
		
		g.drawImage(k1, 0, 0, null);
		g.drawImage(k2, 4, 1, null);
	}
	
	@Override
	protected void uppdateIntern() {
		if(SeyprisMain.sizeY() != lastY){
			lastY = SeyprisMain.sizeY();
			
			ySize = lastY-230;
			xSize = SeyprisMain.sizeX()-380;
			
			clickX = SeyprisMain.sizeX()-1560;
			
			bx.setxPos(xSize-32);
			
			relocated();
		}
	}
	
	protected abstract void relocated();
	
	private void btlClck(int b){
		for (int i = 0; i < btl.length; i++) {
			btl[i].setDisabled(i == b);
		}
		buttonClicked(b);
	}
	
	protected abstract void buttonClicked(int b);
	
	public String getName() {
		return name;
	}
	
}

abstract class HigherButton extends Button{

	public HigherButton(int x) {
		super(x, 0, "res/ima/cli/spb/UserTop");
	}
	
	@Override
	public void paintYou(Graphics2D g) {
		next.paintYou(g);
		
		if(disabled)
			icPaintB(xPos, yPos, stateDis, g);
		else
			icPaintB(xPos, yPos, state1, g);
		
		if(focused)
			icPaintSmooth(xPos, yPos, stateFoc, g);
		
		if(status)
			icPaintB(xPos, yPos, state2, g);
		
		g.setFont(main.Fonts.fontSans16);
		int x = xPos+100-g.getFontMetrics().stringWidth(text)/2;
		g.setColor(Color.black);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				g.drawString(text, x+1+i, 22+j);
			}
		}
		g.setColor(Color.white);
		g.drawString(text, x+1, 22);
	}
}
