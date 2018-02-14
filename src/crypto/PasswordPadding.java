package crypto;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * @author Sven T. Schneider
 * 
 * This System uses a Pseudo-Random Algorithm to Pad a byte-Array with Random Data at Random Positions.
 *
 */
public class PasswordPadding {
	
	/**
	 * Additional Random Data can be Placed here, it will be XOred with the generated Numbers for Padding
	 */
	private byte[] random1;
	private final int ammountToAdd;
	
	private int[] positions;
	
	public PasswordPadding(final int ammount){
		ammountToAdd = ammount;
	}
	
	/**
	 * Additional Random Data can be placed here, it will be XOred with the generated Numbers for Padding
	 */
	public void setAdditionalRandom(byte[] r){
		random1 = r;
	}
	
	/**
	 * Adds the Random Padding
	 * 
	 * @param values The Byte-Array to be Padded
	 * @param pw The Password
	 * @return a Padded Byte-Array with the length: values.lenght+ammountToAdd
	 */
	public byte[] addPadding(byte[] values, byte[] pw){
		getValues(pw, values.length, values.length+ammountToAdd);
		print();
		
		byte[] ret = new byte[values.length+ammountToAdd];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(ret);
		
		if(random1 != null){
			int p = 0;
			for (int i = 0; i < ret.length; i++) {
				if(p >= random1.length)
					p = 0;
				
				ret[i] = (byte)(ret[i] ^ random1[p]);
				
				p++;
			}
		}
		
		int ro = positions.length/2;
		for (int i = 0; i < values.length; i++) {
			ret[positions[i]] = (byte)(values[i] + ret[positions[i+ro]]);
		}
		
		return ret;
	}
	
	/**
	 * Removes the Random Padding
	 * 
	 * @param values The Byte-Array to be Padded
	 * @param pw The Password
	 * @return Un-Padded Byte-Array with the length: values.lenght-ammountToAdd
	 */
	public byte[] removePadding(byte[] values, byte[] pw){
		getValues(pw, values.length-ammountToAdd, values.length);
		print();
		
		byte[] ret = new byte[values.length-ammountToAdd];
		
		int ro = positions.length/2;
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (byte)(values[positions[i]] - values[positions[i+ro]]);
		}
		
		return ret;
	}
	
	private void getValues(byte[] pw, int size, int toSize){
		System.out.println("Size "+ size + " to " + toSize);
		byte[] klpw = getSubArray(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, pw);
		positions = new int[size*2];
		for (int i = 0; i < positions.length; i++) {
			positions[i] = -1;
		}
		
		int pos = 0;
		while(true){
			for (int i = 0; i < klpw.length-1; i+=2) {
				int u = klpw[i] & 0xff;
				u*=255;
				u += klpw[i+1] & 0xff;
				u = u%toSize;
				
				for (int j = 0; j < positions.length; j++) {
					if(positions[j] == u)
						u = -1;
				}
				if(u<0) continue;
				
				positions[pos] = u;
				pos++;
				if(pos >= positions.length)
					return;
			}
			
			klpw = getSubArray(klpw, pw);
		}
	}
	
	private void print(){
		System.out.print("R: ");
		for (int i = 0; i < positions.length; i++) {
			System.out.print(positions[i]+ " ");
		}
		System.out.println();
	}
	
	private byte[] getSubArray(byte[] last, byte[] pw){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(last);
			return md.digest(pw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
