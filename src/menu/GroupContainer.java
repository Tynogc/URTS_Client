package menu;

import java.awt.Color;
import java.awt.Graphics;

import main.Fonts;

/**
 * A Container with name and visible boarder
 * @author Sven T. Schneider
 */
public class GroupContainer extends Container{

	public String name;
	
	public GroupContainer(int x, int y, int xS, int yS, String name) {
		super(x, y, xS, yS);
		this.name = name;
	}
	
	@Override
	protected void paintIntern(Graphics g) {
		super.paintIntern(g);
		g.setColor(Color.lightGray);
		g.setFont(Fonts.fontSans14);
		g.drawString(name, 10, 12);
	}

}
