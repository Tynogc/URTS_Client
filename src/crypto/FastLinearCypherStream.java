package crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * An cross-Implementation of an Enigma-Based-Cypher-Stream and the Fast-Linear-Crypto-Keygeneration Methods
 * @author Sven T. Schneider
 *
 */
public class FastLinearCypherStream {

	private byte[][] rotors;
	private int[] rotorPos;
	
	private byte[] xOr1;
	private int xOrPos;
	private byte[] xOr2;
	
	private final int wordSize;
	
	private static final int ROTOR_NUMBER = 32;
	private static final int XOR_SIZE = 2048;
	
	public FastLinearCypherStream(byte[] pw){
		wordSize = pw.length;
		
		rotorPos = new int[ROTOR_NUMBER];
		rotors = new byte[ROTOR_NUMBER][0];
		rotors[0] = FastLinearCrypto.generateRoundKey(pw);
		for (int i = 1; i < rotors.length; i++) {
			rotors[i] = FastLinearCrypto.generateRoundKey(rotors[i-1]);
		}
		
		MessageDigest md;
		
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			debug.Debug.printExeption(e);
			return;
		}
		
		xOr1 = new byte[XOR_SIZE];
		byte[] curr = rotors[rotors.length-1];
		int c = 0;
		for (int i = 0; i < xOr1.length; i++) {
			curr = FastLinearCrypto.generateRoundKey(curr);
			md.update(rotors[c%rotors.length]);
			md.update(curr);
			byte[] tr = md.digest();
			for (int j = 0; j < tr.length; j++) {
				if(i>=xOr1.length)
					break;
				xOr1[i] = tr[j];
				i++;
			}
			md.reset();
			c++;
		}
		
		xOr2 = new byte[XOR_SIZE];
		curr = rotors[rotors.length-1];
		c = 0;
		for (int i = 0; i < xOr2.length; i++) {
			curr = FastLinearCrypto.generateRoundKey(curr);
			md.update(rotors[c%rotors.length]);
			md.update(curr);
			byte[] tr = md.digest();
			for (int j = 0; j < tr.length; j++) {
				if(i>=xOr2.length)
					break;
				xOr2[i] = tr[j];
				i++;
			}
			md.reset();
			c++;
		}
	}
	
	public void reset(){
		for (int i = 0; i < rotorPos.length; i++) {
			rotorPos[i] = 0;
		}
		xOrPos = 0;
	}
	
	public byte encrypt(byte b){
		b = (byte)(b^xOr1[xOrPos]);
		
		byte u = b;
		b += latSum();
		
		b = (byte)(b^xOr2[xOrPos]);
		
		done(u);
		return b;
	}
	
	public byte decrypt(byte b){
		b = (byte)(b^xOr2[xOrPos]);
		
		b -= latSum();
		byte u = b;
		
		b = (byte)(b^xOr1[xOrPos]);
		
		done(u);
		return b;
	}
	
	private int latSum(){
		int u = 0;
		for (int i = 0; i < rotorPos.length; i++) {
			u += (int)(rotors[i][rotorPos[i]] & 0xff);
		}
		return u;
	}
	
	private void done(byte u){
		xOrPos++;
		if(xOrPos>=XOR_SIZE)
			xOrPos = 0;
		
		rotorPos[(int)(u & 0xff)%ROTOR_NUMBER]++;
		if(rotorPos[(int)(u & 0xff)%ROTOR_NUMBER] >= wordSize)
			rotorPos[(int)(u & 0xff)%ROTOR_NUMBER] = 0;
	}
}
