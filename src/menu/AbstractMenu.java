package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public abstract class AbstractMenu {

	private ButtonInterface buttons;
	
	private MenuControle controle;
	
	public byte allOk = 0;
	
	public int xPos;
	public int yPos;
	public int xSize;
	public int ySize;
	
	public boolean moveAble = true;
	public boolean closeOutside = false;
			
	public boolean activ = true;
	
	protected boolean disabled = false;
	
	protected boolean visible = true;
	
	public AbstractMenu(int x, int y, int xs, int ys){
		buttons = new EndButtonList();
		xPos = x;
		yPos = y;
		
		xSize = xs;
		ySize = ys;
	}
	
	public void paintYou(Graphics2D g){
		g.translate(xPos, yPos);
		if(moveAble){
			g.setColor(Color.red);
			g.drawRect(0, 0, xSize-1, 20);
		}
		paintIntern(g);
		buttons.paintYou(g);
		g.translate(-xPos,-yPos);
	}
	
	public void setControle(MenuControle c){
		controle = c;
	}
	
	public void add(ButtonInterface b){
		buttons = buttons.add(b);
	}
	
	public void remove(ButtonInterface b){
		buttons = buttons.remove(b);
	}
	
	public void closeYou(){
		if(controle != null){
			controle.setActivMenu(null);
		}
	}
	
	public void changeActivMenu(AbstractMenu m){
		if(controle != null){
			controle.setActivMenu(m);
		}
	}
	
	public ButtonInterface getActivButtons(){
		return buttons;
	}
	
	public void updateMenu(){
		uppdateIntern();
	}
	
	public void scrolled(int i){
		
	}
	
	public void leftClick(int x, int y){
		
	}
	
	public void leftClickForFocus(int x, int y){
		
	}
	
	public void reightClick(int x, int y){
		
	}
	
	public void maousAt(int x, int y){
		
	}
	
	public void maousAtOnlyIfFocused(int x, int y){
		
	}
	
	protected abstract void uppdateIntern();
	
	protected abstract void paintIntern(Graphics g);
	
	public byte getStatus(){
		return allOk;
	}
	
	public void longTermUpdate(){};
	
	public boolean isDisabled(){
		return disabled;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
