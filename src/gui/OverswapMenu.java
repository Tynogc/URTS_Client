package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.EventCounter;
import main.SeyprisMain;
import menu.AbstractMenu;

public abstract class OverswapMenu extends AbstractMenu{
	
	protected BufferedImage[] imas;
	protected long closeCounter;
	protected int closeAnim = 0;
	
	public OverswapMenu(){
		super(0,(int)SeyprisMain.getFrame().getSize().getHeight()/2-100,
				(int)SeyprisMain.getFrame().getSize().getWidth(),200);
		moveAble = false;
	}
	
	public void closeIntern(){
		closeCounter = System.currentTimeMillis();
		EventCounter.event();
	}
	
	@Override
	protected void uppdateIntern() {
		if(closeCounter != 0){
			disabled = true;
			closeAnim = (int)((System.currentTimeMillis()-closeCounter)/10);
			if(closeAnim>=100)
				closeYou();
		}
	}
	
	@Override
	public void paintYou(Graphics2D g) {
		if(closeAnim>5){
			float fade = 1f-(float)closeAnim/100f;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
			g.translate(-closeAnim*2, 0);
			super.paintYou(g);
			g.translate(closeAnim*2, 0);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}else{
			super.paintYou(g);
		}
	}

	@Override
	protected void paintIntern(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 34, xSize, ySize-68);
		int u = (int)((System.currentTimeMillis()/100)%51);
		for (int i = 0; i < xSize+54; i+=51) {
			g.drawImage(imas[0], i-u+closeAnim*2, -closeAnim, null);
		}
		
		for (int i = -51; i < xSize; i+=51) {
			g.drawImage(imas[0], i+u+closeAnim*2, ySize-34+closeAnim, null);
		}
	}

}
