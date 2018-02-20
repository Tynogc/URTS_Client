package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.EventCounter;
import main.PicLoader;
import menu.Button;

public class SuperButtonMenu extends Button{
	
	private BufferedImage backBlack;
	private BufferedImage backBlue;
	private boolean showBlue;
	private boolean showRed;
	private BufferedImage backRed;
	private BufferedImage backYellow;
	
	private utility.AlarmValue alarm;
	
	private boolean fadeIn;
	private boolean fadeOut;
	private long fadeCounter;
	
	private long glitter;
	
	private static Color glitter1 = new Color(3, 180, 245, 200);
	private static Color glitter2 = new Color(130, 220, 240, 100);
	
	private BufferedImage[] backDis;
	private boolean showBackPic;
	private int backPicX;
	private int backPicY;
	private long startTime;
	
	private BufferedImage icon;
	private BufferedImage iconText;

	public SuperButtonMenu(int x, int y, int ico) {
		super(x, y, "res/ima/cli/spb/SuperButton");
		backBlack = PicLoader.pic.getImage("res/ima/cli/spb/SuperButtonBLACK.png");
		backBlue = PicLoader.pic.getImage("res/ima/cli/spb/SuperButtonBLUE.png");
		backYellow = PicLoader.pic.getImage("res/ima/cli/spb/SuperButtonYELLOW.png");
		backRed = PicLoader.pic.getImage("res/ima/cli/spb/SuperButtonRED.png");
		
		icon = PicLoader.pic.getImage("res/men/"+ico+".png");
		
		alarm = new utility.AlarmValue();
	}
	
	@Override
	public void paintYou(Graphics2D g) {
		next.paintYou(g);
		
		if(!visible && !fadeOut && !fadeIn)
			return;
		
		int f = 0;
		if(fadeIn){
			long n = System.currentTimeMillis();
			if(n>fadeCounter){
				f = (int)(n-fadeCounter);
				if(f>1000){
					f = 0;
					fadeIn = false;
					visible = true;
				}
			}else{
				f = 1;
			}
		}
		if(fadeOut){
			long n = System.currentTimeMillis();
			if(n>fadeCounter){
				visible = false;
				f = 1000-(int)(n-fadeCounter);
				if(f<=0){
					fadeOut = false;
					return;
				}
			}
		}
		if(f>0){
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)f/1000f));
			EventCounter.event();
		}
		
		if(disabled){
			if(showBackPic){
				int u = (int)((System.currentTimeMillis()+startTime)%30000*backDis.length);
				int k1 = u/30000;
				int k2 = u-k1*30000;
				if(k2<25000){//Paint one
					g.drawImage(backDis[k1], xPos, yPos, null);
				}else{//Paint two
					EventCounter.eventSmal();
					g.drawImage(backDis[k1], xPos, yPos, null);
					float km = (float)(k2-25000)/5000f;
					if(km>1)
						return;
					if(km<0)
						return;
					
					if(f>0){
						g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, km*(float)f/1000f));
					}else{
						g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, km));
					}
					
					g.drawImage(backDis[(k1+1)%backDis.length], xPos, yPos, null);
					
					if(f>0){
						g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)f/1000f));
					}else{
						g.setComposite(AlphaComposite.SrcOver);
					}
				}
			}
			if(!icPaintB(xPos, yPos, state1, g)){
				g.setColor(Color.black);
				g.drawRect(xPos, yPos, xSize, ySize);
			}
			if(showBackPic){
				if(f>0){
					g.setComposite(AlphaComposite.SrcOver);
				}
				return;
			}
		}
		
		if(alarm.alarmStatus == utility.AlarmValue.ALL_ALARM ||
				(alarm.alarmStatus == utility.AlarmValue.ALL_ALARM_AKUT && System.currentTimeMillis()%1000<500) || 
				showRed){
			g.drawImage(backRed, xPos, yPos, null);
		}else if(alarm.alarmStatus == utility.AlarmValue.ALL_WARN){
			g.drawImage(backYellow, xPos, yPos, null);
		}else if(showBlue){
			g.drawImage(backBlue, xPos, yPos, null);
		}else{
			g.drawImage(backBlack, xPos, yPos, null);
		}
		
		g.drawImage(icon, 50+xPos, 6+yPos, null);
		if(iconText != null)
			g.drawImage(iconText, 10+xPos, 71+yPos, null);
		
		if(status){
			if(!icPaintB(xPos, yPos, state2, g)){
				g.setColor(new Color(100,100,100,100));
				g.fillRect(xPos, yPos, xSize, ySize);
			}
		}else{
			if(!icPaintB(xPos, yPos, state1, g)){
				g.setColor(Color.black);
				g.drawRect(xPos, yPos, xSize, ySize);
			}
		}
		if(focused || glitter > 100){
			if(!icPaintSmooth(xPos-focusOffsetX, yPos-focusOffsetY, stateFoc, g)){
			g.setColor(Color.blue);
			g.drawRect(xPos, yPos, xSize, ySize);
			}
			g.setColor(Color.yellow);
			if(subtext != null)g.drawString(subtext, mouseX+2, mouseY);
		}
		
		if(glitter > 100){
			int u = (int)(System.currentTimeMillis()-glitter);
			if(u < 500){
				u /= 70;
				g.setColor(glitter2);
				u++;
				g.drawRect(xPos-u, yPos-u, xSize+u*2, ySize+u*2);
				u+=2;
				g.drawRect(xPos-u, yPos-u, xSize+u*2, ySize+u*2);
				g.setColor(glitter1);
				u--;
				g.drawRect(xPos-u, yPos-u, xSize+u*2, ySize+u*2);
			}else{
				glitter = 0;
			}
		}
		
		if(f>0){
			g.setComposite(AlphaComposite.SrcOver);
		}
	}

	@Override
	protected void isClicked() {
		glitter = System.currentTimeMillis();
	}

	@Override
	protected void isFocused() {
		
	}

	@Override
	protected void uppdate() {
		
	}
	
	@Override
	public void setText(String text) {
		this.text = text;
		int x = 0;
		iconText = new BufferedImage(140, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics g = iconText.getGraphics();
		g.setFont(main.Fonts.fontSans16);
		x = 70-g.getFontMetrics().stringWidth(text)/2;
		g.setColor(Color.black);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				g.drawString(text, x+1+i, 20+j);
			}
		}
		g.setColor(Color.white);
		g.drawString(text, x+1, 20);
	}
	
	public void fadeIn(int t){
		fadeOut = false;
		fadeIn = true;
		fadeCounter = System.currentTimeMillis()-t;
	}
	
	public void fadeOut(int t){
		fadeIn = false;
		fadeOut = true;
		fadeCounter = System.currentTimeMillis()-t;
	}
	
	public boolean isFading(){
		return fadeIn || fadeOut;
	}
	
	public void dontFade(){
		fadeIn = false;
		fadeOut = false;
	}
	
	public void showBlue(){
		showBlue = true;
	}
	public void showRed(boolean b){
		showRed = b;
	}
	public void showBlue(boolean b){
		showBlue = b;
	}
	
	public void setBackPic(boolean b){
		backPicX = xPos;
		backPicY = yPos;
		showBackPic = b;
		if(b)
			generateDisImages();
		
		startTime = (-getxPos()-getyPos()/3)*2;
	}
	
	private void generateDisImages(){
		try {
			BufferedImage b1 = regenerate(PicLoader.pic.getImage("res/ima/bck/1.JPG"));
			BufferedImage b2 = regenerate(PicLoader.pic.getImage("res/ima/bck/2.JPG"));
			BufferedImage b3 = regenerate(PicLoader.pic.getImage("res/ima/bck/3.JPG"));
			if(b1 == null){
				showBackPic = false;
				System.out.println("No Pic fount! "+xPos+" "+yPos);
				return;
			}
			if(b2 == null){
				backDis = new BufferedImage[]{b1};
			}else if(b3 == null){
				backDis = new BufferedImage[]{b1, b2};
			}else{
				backDis = new BufferedImage[]{b1, b2, b3};
			}
		} catch (Exception e) {
			debug.Debug.printExeption(e);
		}
	}
	
	public void setAlarmValue(utility.AlarmValue a){
		alarm = a;
	}
	
	private BufferedImage regenerate(BufferedImage b){
		if(backPicX<340)
			return null;
		if(backPicX-340+xSize >= b.getWidth())
			return null;
		if(backPicY+ySize >= b.getHeight())
			return null;
		return b.getSubimage(backPicX-340, backPicY, xSize, ySize);
	}

}
