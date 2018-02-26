package lobby;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import main.Fonts;
import main.SeyprisMain;
import menu.AdvancedTextEnterField;
import menu.Container;
import network.Writable;

public class ChatContainer extends Container{

	private AdvancedTextEnterField ate;
	
	private Writable send;
	
	public static Font fontN;
	public static Font fontI;
	public static Font fontB;
	
	private ChatString[][] chatMsg;
	private List<String> newMsg;
	private int overwritePos;
	private static final int CHAT_BUFFER = 100;
	
	private ChatPreProcessor cpp;
	
	public ChatContainer(int x, int y) {
		super(x, y, 500, 400);
		
		ate = new AdvancedTextEnterField(0, 380, 500) {
			@Override
			protected void specialKey(int id) {
				if(id == menu.AdvancedTextEnter.BUTTON_ENTER){
					if(send != null)
						send.write(getText());
					addString(getText(), SeyprisMain.getMyName());
					setText("");
				}
			}
		};
		addInContainer(ate);
		ate.setColor(new Color(100,100,100,50));
		ate.setTextColor(Color.white);
		
		int size = 14;
		try {
			fontN = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeSans.ttf"));
			fontN = fontN.deriveFont((float) size);
			fontI = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeSansOblique.ttf"));
			fontI = fontI.deriveFont((float) size);
			fontB = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/FreeSansBold.ttf"));
			fontB = fontB.deriveFont((float) size);
		} catch (Exception e) {
			fontN = new Font(Font.MONOSPACED, Font.PLAIN, size);
			fontI = new Font(Font.MONOSPACED, Font.PLAIN, size);
			fontB = new Font(Font.MONOSPACED, Font.PLAIN, size);
			e.printStackTrace();
		}
		
		chatMsg = new ChatString[CHAT_BUFFER][0];
		newMsg = new ArrayList<>();
		
		cpp = new ChatPreProcessor();
	}
	
	@Override
	protected void paintIntern(Graphics g) {
		super.paintIntern(g);
		while(!newMsg.isEmpty()){
			try {
				processInput(newMsg.remove(0), (Graphics2D)g);
			} catch (Exception e) {
				e.printStackTrace();//TODO silent catch
			}
		}
		
		//Paint Chat
		int am = (boundaryY-30)/20;
		int i = CHAT_BUFFER*2+overwritePos-am;
		for (int j = 0; j < am; j++) {
			int pos = (i+j)%CHAT_BUFFER;
			int w = 0;
			for (int k = 0; k < chatMsg[pos].length; k++) {
				g.setColor(chatMsg[pos][k].col);
				g.setFont(chatMsg[pos][k].fon);
				g.drawString(chatMsg[pos][k].str, w+10, j*20+10);
				w+=chatMsg[pos][k].xSize;
			}
		}
	}
	
	public void addString(String s, String from){
		s = cpp.process(s);
		if(from != null){
			s = "###B#[ "+from+" ]###N# "+s;
		}
		newMsg.add(s);
	}
	
	public void setSend(Writable send) {
		this.send = send;
	}
	
	private void processInput(String s, Graphics2D g){
		String[] q = s.split("###");
		ChatString[] cs = new ChatString[q.length];
		for (int i = 0; i < q.length; i++) {
			cs[i] = new ChatString(q[i], i==0, g);
		}
		int dis = 0;
		int start = 0;
		for (int i = 0; i < cs.length; i++) {
			if(dis+cs[i].xSize > boundaryX-20){
				ChatString csn = cs[i].divide(boundaryX-20-dis, g);
				
				ChatString[] csa = new ChatString[i-start+1];
				for (int j = 0; j < csa.length; j++) {
					csa[j] = cs[j+start];
				}
				chatMsg[overwritePos] = csa;
				overwritePos++;
				if(overwritePos>=CHAT_BUFFER)overwritePos = 0;
				
				cs[i] = csn;
				start = i;
				if(cs[i] == null)
					start = i+1;
				dis = 0;
				i--;
			}else{
				dis += cs[i].xSize;
			}
		}
		ChatString[] csa = new ChatString[cs.length-start];
		for (int j = 0; j < csa.length; j++) {
			csa[j] = cs[j+start];
		}
		chatMsg[overwritePos] = csa;
		overwritePos++;
		if(overwritePos>=CHAT_BUFFER)overwritePos = 0;
	}

}
class ChatString{

	public String str;
	public Color col;
	public Font fon;
	
	public int xSize;
	
	public ChatString(String s, boolean nomod, Graphics2D g){
		if(nomod){
			col = Color.white;
			fon = ChatContainer.fontN;
			str = s;
		}else{
			String ch = s.split("#")[0];
			if(ch.length()>2){
				col = Color.white;
				fon = ChatContainer.fontN;
				str = "###"+s;
			}else{
				if(ch.contains("I")){
					fon = ChatContainer.fontI;
				}else if(ch.contains("B")){
					fon = ChatContainer.fontB;
				}else{
					fon = ChatContainer.fontN;
				}
				if(ch.contains("r")){
					col = Color.red;
				}else if(ch.contains("g")){
					col = Color.green;
				}else if(ch.contains("b")){
					col = new Color(60,100,255);
				}else if(ch.contains("m")){
					col = Color.magenta;
				}else if(ch.contains("c")){
					col = Color.cyan;
				}else if(ch.contains("y")){
					col = Color.yellow;
				}else if(ch.contains("p")){
					col = Color.pink;
				}else{
					col = Color.white;
				}
				str = s.substring(ch.length()+1);
			}
		}
		xSize = g.getFontMetrics(fon).stringWidth(str);
	}
	
	/**
	 * Used to split lines
	 */
	public ChatString divide(int width, Graphics2D g){
		if(width>xSize)
			return null;
		String[] st = str.split(" ");
		String s = "";
		FontMetrics fm = g.getFontMetrics(fon);
		int i = 0;
		do{
			s += st[i]+" ";
			i++;
		}while (fm.stringWidth(s)<width);
		i-=2;
		if(i>=0){
			ChatString cs = new ChatString("   "+str.substring(s.length()), true, g);
			cs.col = col;
			cs.fon = fon;
			str = str.substring(0, s.length());
			width = fm.stringWidth(str);
			return cs;
		}
		s = "";
		i = 0;
		do{
			s+=str.charAt(i);
			i++;
		}while (fm.stringWidth(s)<width);
		ChatString cs = new ChatString("   "+str.substring(i), true, g);
		cs.col = col;
		cs.fon = fon;
		str = str.substring(0, i);
		width = fm.stringWidth(str);
		return cs;
	}
	
}
