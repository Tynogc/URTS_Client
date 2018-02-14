package main;

import menu.AbstractMenu;

public class SubMenuStorage {
	
	private AbstractMenu[] menus;
	public int[][] aditionalButtons;
	
	public SubMenuStorage(){
		menus = new AbstractMenu[27];
		aditionalButtons = new int[27][0];
		
	}
	
	public AbstractMenu getMenu(int id){
		if(id<0 || id>=menus.length)
			return null;
		return menus[id];
	}
}
