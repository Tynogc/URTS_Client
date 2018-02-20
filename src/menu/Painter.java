package menu;

import java.awt.Graphics2D;

public abstract class Painter implements ButtonInterface{

	private ButtonInterface next;
	
	public int xPos;
	public int yPos;
	
	public Painter(int x, int y) {
		xPos = x;
		yPos = y;
	}
	
	public Painter() {
		xPos = 0;
		yPos = 0;
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
		g.translate(xPos, yPos);
		paintIntern(g);
		g.translate(-xPos, -yPos);
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
	
	protected abstract void paintIntern(Graphics2D g);
}
