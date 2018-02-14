package debug;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;

import org.omg.CORBA.FREE_MEM;

import term.TermColors;

public class Debug{
	
	public static final int ERROR = TermColors.ERROR;
	public static final int SUBERR = TermColors.SUBERR;
	
	public static final int FATAL = TermColors.FATAL;
	
	public static final int COM = TermColors.COM;
	public static final int SUBCOM = TermColors.SUBCOM;
	public static final int COMERR = TermColors.COMERR;
	public static final int PRICOM = TermColors.PRICOM;
	
	public static final int WARN = TermColors.WARN;
	public static final int SUBWARN = TermColors.SUBWARN;
	
	public static final int REMOTE = TermColors.REMOTE;
	
	public static final int MESSAGE = TermColors.MESSAGE;
	
	public static final int TEXT = TermColors.TEXT;
	
	public static final int sizeX = 500;
	public static final int sizeY = 800;
	
	public static int line = 0;
	private static int letter = 0;
	
	public static term.TermPrint terminal;
	
	public static final int textWidth = 9;
	
	private static long debMemoryFree = 0;
	private static long debMemoryUsed = 0;
	private static long debMemoryMax = 0;
	
	private static String theLineBefor = "";
	
	public static String logFilepath = "log/def.txt";
	
	private static Runtime runtime;
	
	private static boolean lastThingIsProgressBar;
	
	public static boolean showExtendedBootInfo = false;
	
	static{
		logFilepath = "log/"+
				new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new java.util.Date (System.currentTimeMillis()))+
				"-LOG.txt";
		//userInterface.ExitThread.setFilePath(logFilepath);
		
		runtime = Runtime.getRuntime();
		
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(logFilepath)); 
			writer.println("This is the Log of all Console Data: "+
					new java.text.SimpleDateFormat("dd.MM.yy").format(new java.util.Date (System.currentTimeMillis())));
			writer.println("-------------------------------------------");
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
	}
	
	public static void print(String s, int color){
		if(terminal != null)
			terminal.print(s, color);
		print(s, color, false);
	}
	
	private static void print(String s, int color, boolean b){
		if(s == null)return;
		if(!b)
		theLineBefor+=s;
		
		lastThingIsProgressBar = false;
	}
	
	public static void println(String s, int color){
		if(s == null)return;
		
		if(terminal != null)
			terminal.println(s, color);
		
		lastThingIsProgressBar = false;
		
		PrintWriter writer = null;
		if(theLineBefor.length()>1){
		try { 
			writer = new PrintWriter(new FileWriter(logFilepath, true),true);
			
			writer.println("[cont    ][  ]"+theLineBefor);
			theLineBefor = "";
			
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
		}
		
		String kse = getPreString(color);
		
		try { 
			writer = new PrintWriter(new FileWriter(logFilepath, true),true);
			writer.println(kse+" "+s);
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
		print(s, color, true);
	}
	
	private static String getPreString(int i){
		switch(i){
		case ERROR: return "[ER]";
		case SUBERR: return "[SE]";
		
		case COM: return "[co]";
		case SUBCOM: return "[sc]";
		case COMERR: return "[CE]";
		case PRICOM: return "[cp]";
		
		case WARN: return "[WA]";
		case SUBWARN: return "[SW]";
		
		case MESSAGE: return "[--]";
		
		case FATAL: return "[XX]";
		
		case TEXT: return "[--]";
		
		case REMOTE: return"[re]";
		}
		return "[??]";
	}
	
	public static void knowMemory(){
		debMemoryFree = runtime.freeMemory();
		debMemoryUsed = runtime.totalMemory();
		debMemoryMax = runtime.maxMemory();
	}
	
	public static void displayMemory(int color){
		println("  Free Memory: "+debMemoryFree, color);
		println("  Used Memory: "+debMemoryUsed+", Maximum Memory: "+debMemoryMax, color);
		println("  "+((debMemoryUsed-debMemoryFree)/1000000)+"Mb in use:  ", color);
		//printProgressBar((debMemoryUsed-debMemoryFree), debMemoryMax, color, true);
		println("  "+(debMemoryUsed)/1000000+"Mb reserved: ", color);
		//printProgressBar(debMemoryUsed, debMemoryMax, color, true);
	}
	
	public static void bootMsg(String s, int state){
		int i = s.length();
		for (; i < 20; i++) {
			s+=" ";
		}
		if(state == 0){
			println(s+"[ OK  ]");
			//remove(6);
			print(" OK  ",MESSAGE);
			print("]");
		}else if(state == 1){
			println(s+"[ERROR]");
			//remove(6);
			print("ERROR",ERROR);
			print("]");
		}else if(state == 2){
			println(s+"[WARN ]");
			//remove(6);
			print("WARN ",WARN);
			print("]");
		}else{
			println(s+"[ *** ]");
			//remove(6);
			print(" *** ",COM);
			print("]");
		}
		theLineBefor = "";
	}
	
	public static void println(String s){
		println(s,TEXT);
	}
	
	public static void print(String s){
		print(s,TEXT);
	}
	
	public static void printExeption(Exception e){
		PrintStream p = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				
			}
		}){
			@Override
			public void print(String s) {
				Debug.print(s, ERROR);
				super.print(s);
			}
			@Override
			public void println(String s) {
				Debug.println(s, ERROR);
				super.println(s);
			}
			@Override
			public void println(char[] x) {
				Debug.println(String.copyValueOf(x), ERROR);
				super.println(x);
			}
			public void print(char[] x) {
				Debug.print(String.copyValueOf(x), ERROR);
				super.print(x);
			}
		};
		
		e.printStackTrace(p);
		e.printStackTrace(System.err);
		println("");
	}
}
