package main;

public class EventCounter {

	private static final int TIME_UNTIL_SLOWDOWN = 10000;
	private static final int TIME_UNTIL_SLOWDOWN_SMAL = 500;
	
	private static long lastEvent;
	private static long lastSmalEvent;
	
	public static void event(){
		lastEvent = System.currentTimeMillis();
	}
	
	public static void eventSmal(){
		lastSmalEvent = System.currentTimeMillis();
	}
	
	public static boolean wasEvent(){
		return System.currentTimeMillis()-lastEvent < TIME_UNTIL_SLOWDOWN || 
				System.currentTimeMillis()-lastSmalEvent < TIME_UNTIL_SLOWDOWN_SMAL;
	}
}
