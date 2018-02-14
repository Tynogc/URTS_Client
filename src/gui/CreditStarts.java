package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.PicLoader;
import main.SeyprisMain;
import menu.AbstractMenu;

public class CreditStarts extends AbstractMenu{

	private Credits cr;
	private BufferedImage ima;
	
	public CreditStarts() {
		super(0, 0, 0, 0);
		cr = new Credits(360, 20, false);
		add(cr);
		
		ima = PicLoader.pic.getImage("res/ima/bck/1.jpg");
		int x = 170*9;
		int y = 335;
		if(ima.getWidth()<x)
			x = ima.getWidth();
		if(ima.getHeight()<y)
			y = ima.getHeight();
		ima = ima.getSubimage(0, 0, x, y);
		
		relocate();
	}

	@Override
	protected void uppdateIntern() {
		
	}
	
	public void relocate(){
		xPos = SeyprisMain.sizeX()-1560-320;
		yPos = SeyprisMain.sizeY()-340;
	}

	@Override
	protected void paintIntern(Graphics g) {
		g.drawImage(ima, 340, 0, null);
		g.setColor(Color.gray);
		g.drawRect(340, 0, ima.getWidth(), ima.getHeight());
		g.setColor(Color.darkGray);
		g.drawRect(341, 1, ima.getWidth()-2, ima.getHeight()-2);
	}

}
