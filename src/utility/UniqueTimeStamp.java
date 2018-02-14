package utility;

import java.util.concurrent.Semaphore;

public class UniqueTimeStamp {

	private static Semaphore sema;
	
	static{
		sema = new Semaphore(1);
	}
	
	/**
	 * Will create a unique time-stamp in hexadecimal form. If full is selected, the returned String is
	 * the exact representation of System.currentTimeMillis(); however this Method will hold until the
	 * System-Time has changed.
	 * @param full if this is False, a reduced time-Stamp will be returned in the range of 0 - 0xffff ffff
	 * so the Time-stamp would repeat every ~1000 days
	 */
	public static String getTimeStamp(boolean full){
		sema.acquireUninterruptibly();
		long s = System.currentTimeMillis();
		while(s == System.currentTimeMillis()){//Wait until system-Time has passed
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
		
		sema.release();
		if(full)
			return Long.toHexString(s);
		else
			return Long.toHexString(s%0xffffffffl);
	}
	
	/**
	 * Will create a unique time-stamp in String form with the given radix. If full is selected, the returned String is
	 * the exact representation of System.currentTimeMillis(); however this Method will hold until the
	 * System-Time has changed.
	 * @param full if this is False, a reduced time-Stamp will be returned in the range of 0 - 0xffff ffff
	 * so the Time-stamp would repeat every ~1000 days
	 */
	public static String getTimeStamp(boolean full, int radix){
		sema.acquireUninterruptibly();
		long s = System.currentTimeMillis();
		while(s == System.currentTimeMillis()){//Wait until system-Time has passed
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
		
		sema.release();
		if(full)
			return Long.toString(s, radix);
		else
			return Long.toString(s%0xffffffffl, radix);
	}
}
