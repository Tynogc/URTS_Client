package gui.sub;

import java.awt.Color;
import java.awt.Graphics2D;

import gui.UserMenu;
import main.Fonts;
import main.Language;
import main.SeyprisMain;
import menu.Button;
import menu.GroupContainer;
import menu.Painter;
import menu.TextEnterButton;

public class HostGame extends UserMenu{

	private GroupContainer hostP2P;
	private TextEnterButton tebP2P;
	
	private GroupContainer hostOnServer;
	
	public HostGame() {
		super(new String[]{Language.lang.text(11003)}, 3);
		
		hostOnServer = new GroupContainer(20, 0, 300, 100, Language.lang.text(4010));
		hostP2P = new GroupContainer(640, 0, 300, 100, Language.lang.text(4011));
		add(hostOnServer);
		add(hostP2P);
		hostP2P.secondLine = "("+Language.lang.text(4013)+")";
		
		//Setup P2P-Container
		hostP2P.addInContainer(new Painter() {
			@Override
			protected void paintIntern(Graphics2D g) {
				g.setColor(Color.gray);
				g.setFont(Fonts.fontSans14);
				g.drawString(Language.lang.text(101)+":", 10, 35);
			}
		});
		tebP2P = new TextEnterButton(60, 20, 70, 20, Color.white, SeyprisMain.getKL()) {
			@Override
			protected void textEntered(String text) {}
		};
		tebP2P.setText("6102");//TODO default port
		hostP2P.addInContainer(tebP2P);
		Button b1 = new Button(20, 60, "res/ima/cli/B") {
			@Override
			protected void uppdate() {}
			@Override
			protected void isFocused() {}
			@Override
			protected void isClicked() {
				startP2P();
			}
		};
		b1.setTextColor(Button.gray);
		b1.setText(Language.lang.text(4003)+" "+Language.lang.text(4004));
		hostP2P.addInContainer(b1);
		
		
		relocated();
	}

	@Override
	protected void relocated() {
		hostOnServer.setyPos(ySize-120);
		hostP2P.setyPos(ySize-120);
	}

	@Override
	protected void buttonClicked(int b) {
		
	}
	
	/**
	 * Starts a Pier to Pier Lobby
	 */
	private void startP2P(){
		int port = 0;
		try {
			port = Integer.parseInt(tebP2P.getText());
			tebP2P.setTextColor(Color.black);
		} catch (Exception e) {
			tebP2P.setTextColor(Color.red);
			return;
		}
		lobby.LobbyControle l = new lobby.LobbyControle(true);
		
		SeyprisMain.getCom().clearGameConnection();
		SeyprisMain.getCom().newCon_openServer(port);
		
		SeyprisMain.enterLobby(l);
	}
	
	/**
	 * Starts a Lobby on the Server
	 */
	private void startOnServer(){
		//TODO
	}

}
