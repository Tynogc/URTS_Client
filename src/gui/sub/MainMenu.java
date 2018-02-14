package gui.sub;

import gui.Credits;

import java.awt.Graphics;

import main.SeyprisMain;
import menu.AbstractMenu;

public class MainMenu extends AbstractMenu{

	private gui.Credits credits;
	
	public MainMenu() {
		super(0, 30, SeyprisMain.sizeX(), SeyprisMain.sizeY()-600);
		
		credits = new Credits(SeyprisMain.sizeX()-440, 30, false);
		add(credits);
		
		moveAble = false;
	}

	@Override
	protected void uppdateIntern() {
		
	}

	@Override
	protected void paintIntern(Graphics g) {
		
	}

}
