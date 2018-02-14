package debug;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;

import main.Fonts;
import term.VisualisedTerminal;

/**
 * Will Provide a small Frame with Debug-Information (Quick and Dirty implementation D: )
 * @author Sven T. Schneider
 */
public class DebugFrame extends JPanel implements Runnable{

	private static final long serialVersionUID = 2260887043737960246L;
	
	private JFrame frame;
	
	/**
	 * A Visualized Debug-Terminal
	 */
	private VisualisedTerminal term;
	
	public DebugFrame(){
		frame = new JFrame("Network-Debug");
		frame.setVisible(false);
		frame.setBounds(50,100,320,400);
		
		new Fonts();
		
		term = new VisualisedTerminal(300, 800);
		Debug.terminal = term;
		
		Debug.println("*Starting Network Debug-Frame");
		
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
		
		new Thread(this, "Updater").start();
	}
	
	/**
	 * Set's Visibility of the corresponding Frame
	 */
	@Override
	public void setVisible(boolean aFlag) {
		frame.setVisible(aFlag);
	}
	
	@Override
	public void paint(Graphics g) {
		term.paintYou(g, 0, getHeight()-term.ySize);
	}
	
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			frame.repaint();
		}
	}

}
