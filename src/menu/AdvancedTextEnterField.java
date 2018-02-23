package menu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Fonts;
import main.KeyListener;

public abstract class AdvancedTextEnterField implements ButtonInterface{

	private ButtonInterface next;
	
	private int xPos;
	private int yPos;
	private int xSize;
	private int ySize;
	
	private BufferedImage buffer;
	private String lastString = "";
	
	private boolean active;
	private AdvancedTextEnter ate;
	
	private Color col = Color.white;
	private Color textCol = Color.black;
	
	private boolean disabled;
	private boolean visible = true;
	
	public AdvancedTextEnterField(int x, int y, int xs) {
		xPos = x;
		yPos = y;
		xSize = xs;
		ySize = 20;
		
		ate = new AdvancedTextEnter() {
			
			@Override
			protected void specialKey(int id) {
				AdvancedTextEnterField.this.specialKey(id);
			}
			
			@Override
			protected boolean isSpecialChar(char c) {
				return false;
			}
		};
	}
	
	@Override
	public ButtonInterface add(ButtonInterface b) {
		next = next.add(b);
		return this;
	}

	@Override
	public void leftClicked(int x, int y) {
		next.leftClicked(x, y);
		if(isMouseHere(x, y)){
			if(!active){
				active = true;
				KeyListener.forwardKey = ate;
			}
		}else{
			if(active){
				active = false;
				KeyListener.forwardKey = null;
			}
		}
	}

	@Override
	public void leftReleased(int x, int y) {
		next.leftReleased(x, y);
	}

	@Override
	public void rightReleased(int x, int y) {
		next.rightReleased(x, y);
		
	}

	@Override
	public void checkMouse(int x, int y) {
		next.checkMouse(x, y);
		
		
	}

	@Override
	public void paintYou(Graphics2D g) {
		next.paintYou(g);
		
		if(!isVisible())
			return;
		
		g.setColor(col);
		g.fillRect(xPos, yPos, xSize, ySize);
		g.setColor(DataFiled.l1);
		g.drawRect(xPos, yPos, xSize, ySize);
		g.setColor(DataFiled.l2);
		g.drawRect(xPos+1, yPos+1, xSize-2, ySize-2);
		g.setColor(DataFiled.l3);
		g.drawLine(xPos+2, yPos+2, xPos+xSize-2, yPos+2);
		g.drawLine(xPos+xSize-2, yPos+3, xPos+xSize-2, yPos+ySize-2);
		
		g.setFont(Fonts.font14);
		g.setColor(textCol);
		g.drawString(ate.text, 10+xPos, 15+yPos);
		
		if(active && System.currentTimeMillis()%1000>500){
			int p = g.getFontMetrics().stringWidth(ate.text.substring(0, ate.tebpos));
			g.drawRect(xPos+p+11, yPos+4, 1, 14);
		}
	}

	@Override
	public ButtonInterface remove(ButtonInterface b) {
		if(b == this)
			return next;
		next = next.remove(b);
		return this;
	}

	@Override
	public void longTermUpdate() {
		next.longTermUpdate();
	}

	@Override
	public void setNext(ButtonInterface b) {
		next = b;
	}
	
	/**
	 * Envokes if a special-key is pressed on the Keyboard
	 * @param id AdvancedTextEnter.BUTTON_
	 */
	protected abstract void specialKey(int id);
	
	protected boolean isMouseHere(int x, int y){
		if(disabled)return false;
		if(!visible)return false;
		return x>=xPos&&y>=yPos&&x<=(xPos+xSize)&&y<=(yPos+ySize); 
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setColor(Color col) {
		this.col = col;
	}
	
	public void setTextColor(Color textCol) {
		this.textCol = textCol;
	}
	
	public String getText(){
		return ate.text;
	}
	
	public void setText(String t){
		ate.text = t;
		ate.tebpos = t.length();
	}
}
