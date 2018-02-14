package crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.security.auth.DestroyFailedException;

import utility.ProgressUpdate;

public class PasswordEncrypter {

	public static byte[] encrypt(byte[] s, byte[] pw, int rounds, int memory, utility.ProgressUpdate pu){
		memory /= 8;
		memory *= 8;
		
		if(memory<32)
			memory = 32;
		
		if(rounds<2)
			rounds = 2;
		
		if(pu == null)
			pu = new ProgressUpdate() {public void update(int current, int max) {}};
		
		SecureRandom sr = new SecureRandom();
		
		int toAdd = memory/4*s.length+sr.nextInt(s.length/2);
		s = new PasswordPadding(toAdd).addPadding(s, pw.clone());
		
		byte[][] roundKeys = new byte[rounds][0];
		byte[][] salt = new byte[rounds][];
		
		for (int i = 0; i < salt.length; i++) {
			salt[i] = new byte[sr.nextInt(memory)];
			sr.nextBytes(salt[i]);
		}
		
		RSA_PBKDF pbkdf = null;
		try {
			pbkdf = new RSA_PBKDF(rounds, memory, SCMHA.SCMHA_512);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		roundKeys[0] = pbkdf.operate(pw, salt[0]);
		for (int i = 1; i < salt.length; i++) {
			roundKeys[i] = pbkdf.operate(roundKeys[i-1], salt[i]);
			pu.update(i, rounds);
		}
		
		//Everything is set: Start encrypting...
		FastLinearCrypto flc;
		for (int i = roundKeys.length-1; i >= 0; i--) {
			flc = new FastLinearCrypto(roundKeys[i]);
			byte[] st = flc.encrypt(s, 0);
			flc.destroy();
			s = Arrays.copyOf(st, st.length+salt[i].length);
			for (int j = 0; j < salt[i].length; j++) {
				s[j+st.length] = salt[i][j];
			}
			pu.update(i, rounds);
		}
		
		byte[] st = s;
		s = new byte[st.length+8];
		System.arraycopy(st, 0, s, 8, st.length);
		s[0] = (byte)(rounds%256);
		s[1] = (byte)(rounds/256);
		s[2] = (byte)(memory%256);
		s[3] = (byte)(memory/256);
		s[4] = (byte)(toAdd%256);
		s[5] = (byte)(toAdd/256);
		s[6] = 0;
		s[7] = 1;
		
		for (int i = 0; i < salt.length; i++) {
			for (int j = 0; j < salt[i].length; j++) {
				salt[i][j] = 0;
			}
			for (int j = 0; j < roundKeys[i].length; j++) {
				roundKeys[i][j] = 0;
			}
		}
		System.out.println("Rounds: "+rounds+" Memory: "+memory+" ToAdd: "+toAdd);
		return s;
	}
	
	/**
	 * Rudimentary checks the Start-Vector, minimizes the chance of Function-Escalation
	 * @param s
	 * @return true if the Start Vector seams OK (Probability ~1:65.000)
	 */
	public static boolean check(byte[] s){
		return s[6] == 0 && s[7] == 1;
	}
	
	/**
	 * Decrypts the given byte-Array <b>s</b> to its original State.<br>
	 * <u>WARNING:</u> it is Recommended to use the check()-function
	 * previously, to minimize the Possibility for function-escalation.<br>
	 * If the given Byte-Array <b>s</b> has faulty Start-Vectors, this function can run a very, very long time!
	 * @param s
	 * @param pw
	 * @param pu
	 * @return
	 */
	public static byte[] decrypt(byte[] s, byte[] pw, utility.ProgressUpdate pu){
		if(pu == null)
			pu = new ProgressUpdate() {public void update(int current, int max) {}};
		
		final int rounds = (s[0] & 0xff)+(s[1] & 0xff)*256;
		final int memory = (s[2] & 0xff)+(s[3] & 0xff)*256;
		final int toAdd = (s[4] & 0xff)+(s[5] & 0xff)*256;
		System.out.println("Rounds: "+rounds+" Memory: "+memory+" ToRmv: "+toAdd);
		s = Arrays.copyOfRange(s, 8, s.length);
		
		RSA_PBKDF pbkdf = null;
		try {
			pbkdf = new RSA_PBKDF(rounds, memory, SCMHA.SCMHA_512);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		byte[] q = pw.clone();
		
		FastLinearCrypto flc;
		for (int i = 0; i < rounds; i++) {
			int l = s.length%memory;
			byte[] salt = Arrays.copyOfRange(s, s.length-l, s.length);
			pw = pbkdf.operate(pw, salt);
			flc = new FastLinearCrypto(pw);
			s = Arrays.copyOf(s, s.length-l);
			s = flc.decrypt(s, 0);
			flc.destroy();
			pu.update(i, rounds);
		}
		
		s = new PasswordPadding(toAdd).removePadding(s, q);
		
		return s;
	}
}
