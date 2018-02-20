package main;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import menu.AbstractMenu;
import menu.MenuControle;

public class GuiControle {

	private main.MouseListener mouse;
	
	private MenuControle debugMenu;
	private MenuControle topMenu;
	private static MenuControle[] menus;
	private MenuControle frameMenu;
	private MenuControle userMenu;
	private MenuControle overMenu;
	private static MenuControle superMenu;
	
	public static int mouseX;
	public static int mouseY;
	
	public GuiControle(main.MouseListener m, main.KeyListener k){
		mouse = m;
		topMenu = new MenuControle();
		debugMenu = new MenuControle();
		overMenu = new MenuControle();
		menus = new MenuControle[10];
		for (int i = 0; i < menus.length; i++) {
			menus[i] = new MenuControle();
		}
		frameMenu = new MenuControle();
		userMenu = new MenuControle();
		superMenu = new MenuControle();
	}
	
	public boolean loop(){
		boolean left = mouse.left||mouse.leftClicked;
		boolean right = mouse.right || mouse.rightClicked;
		boolean clicked = false;
		
		boolean leftForFocus = left;
		
		mouseX = mouse.x;
		mouseY = mouse.y;
		MouseListener.lastRot = mouse.rot;
		mouse.rot = 0;
		
		if(frameMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			left = false;
			right = false;
			clicked = true;
		}
		
		if(superMenu.isActiv()){
			if(superMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
				clicked = true;
			}
			left = false;
			right = false;
		}
		
		if(leftForFocus)
			frameMenu.leftClickForFocus(mouse.x, mouse.y);
		
		for (int i = menus.length-1; i >= 0; i--) {
			if(menus[i].mouseState(mouse.x, mouse.y, left, right, !clicked)){
				left = false;
				right = false;
				clicked = true;
			}
			if(leftForFocus)
				menus[i].leftClickForFocus(mouse.x, mouse.y);
		}
		
		if(userMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			left = false;
			right = false;
			clicked = true;
		}
		if(overMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			//Nothing, is everywhere!
		}
		
		if(debugMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			left = false;
			right = false;
			clicked = true;
		}
		if(leftForFocus)
			debugMenu.leftClickForFocus(mouse.x, mouse.y);
		
		if(topMenu.mouseState(mouse.x, mouse.y, left, right, !clicked)){
			left = false;
			right = false;
			clicked = true;
		}
		if(leftForFocus)
			topMenu.leftClickForFocus(mouse.x, mouse.y);
		
		return clicked;
	}
	
	public void paint(Graphics2D g){
		if(superMenu.isActiv() && SeyprisMain.fullScreen){
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
		}
		topMenu.paintYou(g);
		userMenu.paintYou(g);
		overMenu.paintYou(g);
		debugMenu.paintYou(g);
		for (int i = 0; i < menus.length; i++) {
			menus[i].paintYou(g);
		}	
		
		if(superMenu.isActiv() && SeyprisMain.fullScreen){
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
		}
		frameMenu.paintYou(g);
		superMenu.paintYou(g);
	}
	
	public static boolean addMenu(AbstractMenu  m){
		//shutdown old menus
		for (int i = menus.length-1; i >= 0; i--) {
			if(!menus[i].isActiv()){
				MenuControle mc = menus[i];
				for (int j = i; j < menus.length-1; j++) {
					menus[j] = menus[j+1];
				}
				menus[menus.length-1] = mc;
			}
		}
		
		for (int i = 0; i < menus.length; i++) {
			if(!menus[i].isActiv()){
				menus[i].setActivMenu(m);
				return true;
			}
		}
		return false;
	}
	
	public void setdebugMenu(AbstractMenu m){
		debugMenu.setActivMenu(m);
	}
	
	public void setFrameMenu(AbstractMenu m){
		frameMenu.setActivMenu(m);
	}
	
	public void setTopMenu(AbstractMenu m){
		topMenu.setActivMenu(m);
	}
	
	public void setUserMenu(AbstractMenu m){
		userMenu.setActivMenu(m);
	}
	
	public void setOverMenu(AbstractMenu m){
		overMenu.setActivMenu(m);
	}
	
	public static void setSuperMenu(AbstractMenu m){
		superMenu.setActivMenu(m);
	}
	
	public AbstractMenu getUserMenu() {
		return userMenu.getMenu();
	}
}
