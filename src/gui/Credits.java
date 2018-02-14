package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import main.Fonts;
import main.PicLoader;
import menu.Container;

public class Credits extends Container{

	private final boolean all;
	private long time;
	private List<String[]> m;
	
	public Credits(int x, int y, boolean a) {
		super(x, y);
		all = a;
		time = System.currentTimeMillis();
		
		m = new ArrayList<>();
		try {
			FileReader fr = new FileReader("ABOUT.txt");
			BufferedReader br = new BufferedReader(fr);
			
			String u = "";
			for (String s = br.readLine(); s != null; s = br.readLine()) {
				if(s.startsWith("###")){
					m.add(u.split(" ::: "));
					u = "";
				}else{
					u += s+" ::: ";
				}
			}
			
			br.close();
		} catch (Exception e) {
			debug.Debug.println("Error loading Credits: "+e.toString(), debug.Debug.ERROR);
		}
	}
	
	@Override
	protected void paintIntern(Graphics g) {
		super.paintIntern(g);
		
		if(!all){
			long k = System.currentTimeMillis()-time;
			k /= 3000;
			draw(g, 0, (int)(k%m.size()), -1);
		}else{
			int cursor = 0;
			for (int i = 0; i < m.size(); i++) {
				cursor = draw(g, cursor, i, -1);
			}
		}
		
	}
	
	private int draw(Graphics g, int cursor, int p, int fade){
		String[] s = m.get(p);
		Color c1 = Color.black;
		Color c2 = Color.white;
		Color c3 = Color.gray;
		g.setFont(Fonts.fontSans14);
		
		int x;
		String u;
		for (int i = 0; i < s.length; i++) {
			u = s[i];
			x = 20;
			if(u.startsWith("#")){
				x = 0;
				u = u.substring(1);
			}
			g.setColor(c1);
			g.drawString(u, x, cursor+1);
			g.drawString(u, x+1, cursor);
			g.drawString(u, x, cursor-1);
			g.drawString(u, x-1, cursor);
			g.setColor(c2);
			if(u.startsWith("("))
				g.setColor(c3);
			g.drawString(u, x, cursor);
			cursor += 20;
		}
		
		return cursor+10;
	}

}
