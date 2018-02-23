package gui.sub;

import java.awt.Color;
import java.awt.Graphics2D;

import gui.UserMenu;
import main.Fonts;
import main.Language;
import main.SeyprisMain;
import menu.AdvancedTextEnterField;
import menu.Button;
import menu.GroupContainer;
import menu.Painter;

public class JoinGame extends UserMenu{

	private GroupContainer joinP2P;
	private AdvancedTextEnterField joinPort;
	private AdvancedTextEnterField joinIP;
	
	public JoinGame() {
		super(new String[]{Language.lang.text(11002)}, 2);
		
		joinP2P = new GroupContainer(0, 0, 400, 150, Language.lang.text(4014));
		add(joinP2P);
		joinP2P.addInContainer(new Painter() {
			@Override
			protected void paintIntern(Graphics2D g) {
				g.setColor(Color.gray);
				g.setFont(Fonts.fontSans14);
				g.drawString(Language.lang.text(101)+":", 10, 66);
				g.drawString(Language.lang.text(100)+":", 10, 36);
			}
		});
		joinIP = new AdvancedTextEnterField(50, 20, 200) {
			@Override
			protected void specialKey(int id) {}
		};
		joinIP.setText("127.0.0.1");
		joinP2P.addInContainer(joinIP);
		
		joinPort = new AdvancedTextEnterField(50, 50, 100) {
			@Override
			protected void specialKey(int id) {}
		};
		joinPort.setText("6102");//TODO default port
		joinP2P.addInContainer(joinPort);
		
		Button b1 = new Button(20, 80, "res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				joinP2P();
			}
		};
		b1.setTextColor(Button.gray);
		b1.setText(Language.lang.text(4006)+" "+Language.lang.text(4004));
		joinP2P.addInContainer(b1);
	}

	@Override
	protected void relocated() {
		joinP2P.setxPos(xSize-160-joinP2P.getBoundaryX());
		joinP2P.setyPos(ySize-10-joinP2P.getBoundaryY());
	}

	@Override
	protected void buttonClicked(int b) {
		
	}
	
	private void joinP2P(){
		int port = 0;
		try {
			port = Integer.parseInt(joinPort.getText());
			joinPort.setTextColor(Color.black);
		} catch (Exception e) {
			joinPort.setTextColor(Color.red);
			return;
		}
		lobby.LobbyControle l = new lobby.LobbyControle(false);
		
		SeyprisMain.getCom().clearGameConnection();
		SeyprisMain.getCom().newCon_connectClient(joinIP.getText(), port);
		
		SeyprisMain.enterLobby(l);
	}

	
}
