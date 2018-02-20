package menu;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
	
	private boolean disabled;
	private boolean visible;
	
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

	
}
