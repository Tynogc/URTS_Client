package main;

import gui.FrameMenu;
import gui.OverMenu;
import gui.PerformanceMenu;
import utility.UniversalActionListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import crypto.RSAsaveKEY;
import debug.DebugFrame;

/**
 * @author Sven Schneider
 * (C) Sven T. Schneider
 * Uses the Seypris-ProControl Framework
 * Seypris-ProControle is (C) Sven T. Schneider
 * The STS-Logo and Seypris-Logo are created and Owned by Sven T. Schneider
 * Java and the Java-Logo are Oracle Trademarks
 *
 */

public class SeyprisMain extends JPanel{

	private static final long serialVersionUID = 1890361404691564161L;
	private static int xPos = 300;
	private static int yPos = 200;
	
	private static KeyListener key;
	private MouseListener mouse;
	
	private static JFrame frame;
	private BufferStrategy strategy;
	private BufferedImage back;
	
	private SubMenuStorage sms;
	
	private DebugFrame debFrame;
	private FrameMenu frameMenu;
	private OverMenu overMenu;
	private gui.CreditStarts startCredits;
	
	private GuiControle gui;
	private static boolean doStartAnim = true;
	
	private Semaphore incycle;
	
	private Color[] background;
	public static Color topRun = new Color(0, 108, 230, 150);
	
	public static boolean fullScreen = !true;
	public static boolean askConnection = false;
	
	public static final String VERSION = "0.1";
	public static final String TITLE = "URTS-Launcher v"+VERSION;
	
	private RSAsaveKEY myKey;
	
	private static SeyprisMain me;
	
	//Scaling variables (Strange JDK9 - HiDPI stuff)
	private double qq = 1.0;
	private double qq2;//Java version (Debug only)
	
	private String[] infoStrings = new String[]{"",""};
	
	public SeyprisMain(DebugFrame f, boolean graphicalStartup){
		debFrame = f;
		me = this;
		debFrame.setVisible(true);
		debug.Debug.println("* Starting: "+TITLE);
		//debFrame.setIcon("res/win/icon2.png");
		
		incycle = new Semaphore(1);
		
		new PicLoader();
		
		new Language();
		
		qq2 = Double.parseDouble(System.getProperty("java.version").substring(0, 3));//Get java version
		if(qq2>=1.89)//Is Java 1.9 or higher?
			qq = (double)Toolkit.getDefaultToolkit().getScreenResolution()/96.0;//DPI rescaled
		
		frame = new JFrame(TITLE){
			@Override
			public void paint(Graphics g) {
				if(!fullScreen){
					incycle.acquireUninterruptibly();
					g.drawImage(back, 0, 0, null);
					incycle.release();
				}else{
					super.paint(g);
				}
			}
		};
		frame.setBounds(10, 10, sizeX(), sizeY());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setUndecorated(true);
		frame.add(this);
		frame.setLayout(null);
		frame.setVisible(true);
		setVisible(true);
		
		//setFocusable(true);
		frame.setFocusable(true);
		key = new KeyListener();
		frame.addKeyListener(key);
		addKeyListener(key);
		mouse = new MouseListener();
		frame.addMouseListener(mouse);
		frame.addMouseMotionListener(mouse);
		frame.addMouseWheelListener(mouse);
		
		frame.setIconImage(PicLoader.pic.getImage("res/win/icon2.png"));
		
		Fonts.createAllFonts();
		
		setFullScreen(false, true);
		frameMenu = new FrameMenu(this);
		
		gui = new GuiControle(mouse, key);
		
		background = new Color[60];
		for (int i = 0; i < background.length; i++) {
			int u = 60-i;
			background[i] = new Color(u,u,u);
		}
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.createBufferStrategy(2);
		strategy = frame.getBufferStrategy();
		
		final MainThread mainThr = new MainThread(this);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainThr.start();
			}
		});
		
		//emots = new gui.utility.Emots();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		secondPhase();
	}
	
	private void secondPhase(){
		frameMenu.showMaximise(true);
		setFullScreen(fullScreen, false);
		gui.setFrameMenu(frameMenu);
		startCredits = new gui.CreditStarts();
		GuiControle.addMenu(startCredits);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {}
		
		fullStartUp();
	}
	
	private void fullStartUp(){
		try {
			myKey =	crypto.RSAsaveKEY.generateKey(1024, true, true, 3, null);
		} catch (Exception e1) {
			debug.Debug.printExeption(e1);
		}
		
		sms = new SubMenuStorage();
		
		overMenu = new OverMenu(gui, sms);
		startCredits.closeYou();
		startCredits = null;
		gui.setOverMenu(overMenu);
		
		//GuiControle.addMenu(new AlarmMenu());
		
		//TODO processConrole = new ProcessConrole();
		
		GuiControle.addMenu(new PerformanceMenu());
		
		//debFrame.setVisible(false);
	}
	
	public void loop(int fps, int sleep, int secFps, int thiFps){
		PerformanceMenu.startTime();
		
		PerformanceMenu.markTime(PerformanceMenu.Communication);
		
		mouse.x = (int)((MouseInfo.getPointerInfo().getLocation().x-frame.getX())*qq);
		mouse.y = (int)((MouseInfo.getPointerInfo().getLocation().y-frame.getY())*qq);
		
		if(key.requestTerminal){
			key.requestTerminal = false;
			//TODO
		}
		
		PerformanceMenu.markTime(PerformanceMenu.Process);
		
		gui.loop();
		mouse.leftClicked = false;
		mouse.rightClicked = false;
		PerformanceMenu.markTime(PerformanceMenu.UpdateGui);
		//emots.update(); TODO
		
		Graphics2D g = null;
		try {
			if(fullScreen){
				g = (Graphics2D)strategy.getDrawGraphics();
				g.scale(1/qq, 1/qq);
			}else{
				g = (Graphics2D)back.getGraphics();
			}
		} catch (Exception e) {
			debug.Debug.println(e.toString(), debug.Debug.ERROR);
			return;
		}
		incycle.acquireUninterruptibly();
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		int ymcc = 0;
		if(!fullScreen){
			g.setColor(Color.gray);
			g.drawRect(0, 0, sizeX(), 30);
			g.setColor(topRun);
			g.drawRect(1, 1, sizeX()-2, 28);
			ymcc = 30;
		}
		int ymc = (sizeY()+10)/(background.length);
		for (int i = 0; i < background.length; i++) {
			g.setColor(background[i]);
			g.fillRect(0, i*ymc+ymcc, sizeX(), ymc);
		}
		g.setColor(Color.gray);
		g.drawRect(0, 0, sizeX()-1, sizeY()-1);
		PerformanceMenu.markTime(PerformanceMenu.PaintBack);
		
		PerformanceMenu.markTime(PerformanceMenu.PaintEntity);
		
		gui.paint(g);
		PerformanceMenu.markTime(PerformanceMenu.PaintGui);
		
		if(mouse.mouseDraggStartX>0 && !fullScreen){
			if(mouse.mouseDraggStartY > 0 && mouse.mouseDraggStartY < 30 &&
					mouse.mouseDraggStartX < xPos-30)
			frame.setLocation(-mouse.mouseDraggStartX+mouse.mouseDraggX, -mouse.mouseDraggStartY+mouse.mouseDraggY);
		}
		
		g.setColor(Color.cyan);
		g.setFont(Fonts.font12);
		g.drawString("M:"+mouse.x+" "+mouse.y+" "+qq+" "+qq2, 0, 44);
		g.drawString("FPS: "+fps, 0, 57);
		g.drawString("T: ["+thiFps+"]", 0, 70);
		if(secFps<0)g.setColor(Color.red);
		//secFps = -secFps+MainThread.timeToFrameUppdate;
		double fpsDpe = (double)secFps/MainThread.timeToFrameUppdate;
		secFps = (int)(fpsDpe*100.0);
		g.drawString("Load:"+secFps/100+""+(secFps/10)%10+""+secFps%10+"%", 50, 57);
		
		g.setColor(Color.white);
		g.drawString(infoStrings[0], 0, 40);
		g.drawString(infoStrings[1], sizeX()-300, 40);
		
		g.dispose();
		incycle.release();
		if(fullScreen){
			strategy.show();
		}else{
			frame.repaint();
		}
		PerformanceMenu.markTime(PerformanceMenu.RedrawBack);
	}
	
	public static int sizeX(){
		return xPos;
	}
	
	public static int sizeY(){
		return yPos;
	}
	
	public static JFrame getFrame(){
		return frame;
	}
	
	public static KeyListener getKL(){
		return key;
	}
	
	public void setFullScreen(boolean fs, boolean start){
		fullScreen = fs;
		
		Dimension dim = getToolkit().getScreenSize();
		if(fullScreen){
			Rectangle ub = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			xPos = ub.width;
			yPos = ub.height;
			
			if(frameMenu != null){
				frameMenu.resize();
				frameMenu.isFullScreen(fs);
			}
			
			frame.setBounds(0, 0, xPos, yPos);
			
			xPos*=qq;
			yPos*=qq;
			
			//frame.setState(JFrame.MAXIMIZED_BOTH);
			frame.setBackground(null);
		}else{
			if(start){
				xPos = 1900;
				yPos = 900;
			}else{
				xPos = 1900;
				yPos = 900;
			}
			
			if(frameMenu != null){
				frameMenu.resize();
				frameMenu.isFullScreen(fs);
			}
			
			int mX = (int)(dim.width-xPos/qq)/2;
			int mY = (int)(dim.height-yPos/qq)/2-50;
			if(!start)
				mY = 20;
			frame.setBounds(mX, mY, xPos, yPos);
			frame.setBackground(topRun);
			
			back = new BufferedImage(xPos, yPos, BufferedImage.TYPE_INT_ARGB);
		}
		
		if(overMenu != null)
			overMenu.relocate();
		if(startCredits != null)
			startCredits.relocate();
	}
}
