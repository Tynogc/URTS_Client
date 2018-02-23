package main;

import gui.sub.EmptyMenu;
import menu.AbstractMenu;

/**
 * Holds Sub-Menus for use.
 * @author Sven T. Schneider
 */
public class SubMenuStorage {
	
	private AbstractMenu[] menus;
	public int[][] aditionalButtons;
	
	public AbstractMenu mainMenu;
	
	public SubMenuStorage(){
		menus = new AbstractMenu[27];
		aditionalButtons = new int[27][0];
		
		mainMenu = new gui.sub.MainMenu();
		
		EmptyMenu em = new EmptyMenu();
		menus[0] = em;
		menus[1] = em;
		menus[2] = new gui.sub.JoinGame();
		aditionalButtons[2] = new int[]{27,3};
		menus[3] = new gui.sub.HostGame();
		aditionalButtons[3] = new int[]{27,2};
		
		menus[9] = em;
		menus[10] = em;
		
		menus[18] = em;
		menus[19] = em;
		menus[20] = em;
		menus[21] = em;
		
		menus[8] = em;
		menus[17] = em;
		menus[25] = em;
		menus[26] = em;
	}
	
	public AbstractMenu getMenu(int id){
		if(id<0 || id>=menus.length)
			return null;
		return menus[id];
	}
}
