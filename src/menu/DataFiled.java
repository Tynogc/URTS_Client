package menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

public abstract class DataFiled extends Button{
	
	private Color col;
	
	public static final Color l1 = new Color(120,120,120);
	public static final Color l2 = new Color(70,70,70);
	public static final Color l3 = new Color(0,0,0,100);
	
	private boolean blinking = false;
	
	private BufferedImage ico;
	
	public DataFiled(int x, int y, int wi, int hi, Color c) {
		super(x, y, wi, hi);
		col = c;
		textColor = black;
		text = "EMPTY";
		setBold(false);
		setBig(false);
	}

	public Color getColor() {
		return col;
	}

	public void setColor(Color col) {
		this.col = col;
	}

	@Override
	protected void isFocused() {
		
	}
	
	public void paintYou(Graphics2D g){
		if(rescaleText){
			setTextOffset(g);
			rescaleText = false;
		}
		if(!isVisible()){
			next.paintYou(g);
			return;
		}
		boolean bli = blinking && System.currentTimeMillis()/500%2 == 0;
		if(disabled){
			g.setFont(font);
			g.setColor(textColor);
			g.drawString(text, xPos+offsettext+2, yPos+(ySize/2)+5);
			g.setColor(l1);
			g.drawRect(xPos, yPos, xSize, ySize);
		}else{
			g.setColor(col);
			if(bli)
				g.setColor(textColor);
			g.fillRect(xPos, yPos, xSize, ySize);
			g.setColor(l1);
			g.drawRect(xPos, yPos, xSize, ySize);
			g.setColor(l2);
			g.drawRect(xPos+1, yPos+1, xSize-2, ySize-2);
			g.setColor(l3);
			g.drawLine(xPos+2, yPos+2, xPos+xSize-2, yPos+2);
			g.drawLine(xPos+xSize-2, yPos+3, xPos+xSize-2, yPos+ySize-2);
			
			if(ico != null){
				g.drawImage(ico, xPos+xSize/2-ico.getWidth()/2, yPos+ySize/2-ico.getHeight()/2, null);
			}
			
			g.setFont(font);
			g.setColor(textColor);
			if(bli)
				g.setColor(col);
			//g.drawString(text, xPos+offsettext+2, yPos+(ySize/2)+5);
			if(subtext2 != null){
				if(text != null)
					g.drawString(text, xPos+offsettext, yPos+(ySize/2));
				g.drawString(subtext2, xPos+offsetSubtext2, yPos+(ySize/2)+10);
			}else{
				if(text != null)
					g.drawString(text, xPos+offsettext, yPos+(ySize/2)+5);
			}
		}
		
		next.paintYou(g);
	}
	
	public boolean isBlinking() {
		return blinking;
	}

	public void setBlinking(boolean blinking) {
		this.blinking = blinking;
	}

	public void setData(String s){
		setText(s);
	}

	public void setIcon(BufferedImage i){
		ico = i;
	}
}
