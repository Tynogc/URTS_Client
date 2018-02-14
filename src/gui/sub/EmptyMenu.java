package gui.sub;

import gui.UserMenu;

public class EmptyMenu extends UserMenu{

	public EmptyMenu() {
		super(new String[]{"EMPTY"}, 25);
	}

	@Override
	protected void relocated() {
		
	}

	@Override
	protected void buttonClicked(int b) {
		
	}

}
