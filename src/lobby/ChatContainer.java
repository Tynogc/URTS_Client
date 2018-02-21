package lobby;

import java.awt.Graphics;

import menu.AdvancedTextEnterField;
import menu.Container;

public class ChatContainer extends Container{

	private AdvancedTextEnterField ate;
	
	public ChatContainer(int x, int y) {
		super(x, y);
		
		ate = new AdvancedTextEnterField(300, 300, 500) {
			@Override
			protected void specialKey(int id) {
				if(id == menu.AdvancedTextEnter.BUTTON_ENTER){
					System.out.println(">>>"+getText());
					setText("");
				}
			}
		};
		addInContainer(ate);
	}
	
	@Override
	protected void paintIntern(Graphics g) {
		super.paintIntern(g);
		
	}

}
