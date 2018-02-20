package menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import main.Fonts;

/**
 * A Container with name and visible boarder
 * @author Sven T. Schneider
 */
public class GroupContainer extends Container{

	private String name;
	public String secondLine;
	
	private BufferedImage lambdaH;
	private BufferedImage lambdaV;
	
	public GroupContainer(int x, int y, int xS, int yS, String name) {
		super(x, y, xS, yS);
		this.name = name;
		redrawImg();
	}
	
	@Override
	public void paintYou(Graphics2D g) {
		super.paintYou(g);
		g.drawImage(lambdaH, xPos, yPos-20, null);
		g.drawImage(lambdaV, xPos-1, yPos, null);
		if(secondLine == null)
			return;
		g.setFont(Fonts.fontSans12);
		g.setColor(Color.gray);
		g.drawString(secondLine, xPos+12, yPos+12);
	}
	
	@Override
	public void setBoundaryX(int boundaryX) {
		super.setBoundaryX(boundaryX);
		redrawImg();
	}
	
	@Override
	public void setBoundaryY(int boundaryY) {
		super.setBoundaryY(boundaryY);
		redrawImg();
	}
	
	public void setName(String name) {
		this.name = name;
		redrawImg();
	}
	
	public String getName() {
		return name;
	}
	
	private void redrawImg(){
		lambdaH = new BufferedImage(boundaryX, 20, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = lambdaH.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(Fonts.fontSans16);
		g.setColor(Color.black);
		g.drawString(name, 9, 16);
		g.drawString(name, 11, 16);
		g.drawString(name, 10, 15);
		g.drawString(name, 10, 17);
		g.setColor(Color.lightGray);
		g.drawString(name, 10, 16);
		for (int i = 0; i < boundaryX; i++) {
			int q = (int)(244.0*(double)i/boundaryX);
			q = (255-q)<<24;
			lambdaH.setRGB(i, 18, 0x606080 | q);
			lambdaH.setRGB(i, 19, 0xd0d0d0 | q);
		}
		lambdaV = new BufferedImage(2, boundaryY, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < boundaryY; i++) {
			int q = (int)(244.0*(double)i/boundaryY);
			q = (255-q)<<24;
			lambdaV.setRGB(0, i, 0x606080 | q);
			lambdaV.setRGB(1, i, 0xd0d0d0 | q);
		}
	}

}
