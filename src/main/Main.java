package main;

public class Main {

	/**
	 * Value for Performance-Checks, Milliseconds for a game-Tick
	 */
	public static final int msPerTick = 500;
	
	/**
	 * Test-Initiation of the Client
	 * @param args
	 */
	public static void main(String[] args) {
		new SeyprisMain(new debug.DebugFrame(), true);
	}
}
