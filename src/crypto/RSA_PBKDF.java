package crypto;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Computation-Intensive Function to generate RSA-Subkeys or Linear Keys out of Salt and the Password
 * @author Sven Schneider
 */
public class RSA_PBKDF {
	
	private static boolean printTypeHex = true;
	
	private byte[][] salt;
	private byte[][] key;
	
	private static final byte ROUND_ENLARGE = 1;
	private static final byte ROUND_REDUCE = -1;
	
	private byte[] roundShedule;
	private final int numberOfRounds;
	private final int outputLenght;
	private int curretLenght;
	
	private final SCMHA scmha;
	
	public RSA_PBKDF(int rounds, int outputSize, int SCMHA_SIZE) throws NoSuchAlgorithmException{
		if(rounds<outputSize)
			rounds = outputSize;
		numberOfRounds = rounds;
		outputLenght = outputSize;
		scmha = new SCMHA(SCMHA_SIZE);
	}
	
	public byte[] operate(byte[] key, byte[] salt){
		setKey(key);
		setSalt(salt);
		
		int l = numberOfRounds;
		if(key.length/2>numberOfRounds)
			l = key.length/2;
		
		generateShedule(l);
		
		for (int i = 0; i < roundShedule.length; i++) {
			oneRound(roundShedule[i]);
		}
		
		int s1 = getSum(1, key);
		int s2 = getSum(2, key);
		for (int i = 0; i < curretLenght; i++) {
			if(i%2 == 0){
				xOR(this.key[i], getSalt(s1+i), 0);
			}else{
				byte[] b = exponent(this.key[i], getSalt(s1+i), false);
				xOR(this.key[i], b, 0);
			}
		}
		
		byte[] ret = new byte[outputLenght];
		int p = 0;
		int q = 0;
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (byte)(this.key[p][q] ^ getSalt(p+s2)[q]);
			q++;
			if(q >= 4){
				q = 0;
				p++;
			}
		}
		
		for (int i = 0; i < this.key.length; i++) {
			for (int j = 0; j < this.key[i].length; j++) {
				this.key[i][j] = 0;
			}
		}
		for (int i = 0; i < this.salt.length; i++) {
			for (int j = 0; j < this.salt[i].length; j++) {
				this.salt[i][j] = 0;
			}
		}
		
		return ret;
	}
	
	private void setSalt(byte[] s){
		int l = s.length/4+1;
		salt = new byte[l][4];
		l = 0;
		int u = 0;
		for (int i = 0; i < s.length; i++) {
			salt[u][l] = s[i];
			l++;
			if(l>=4){
				l = 0;
				u++;
			}
		}
	}
	
	private void setKey(byte[] k){
		int l = outputLenght/4+4;
		if(k.length/4+1 > l)
			l = k.length/4+1;
		
		key = new byte[l][0];
		l = 10;
		int u = -1;
		for (int i = 0; i < k.length; i++) {
			if(l >= 4){
				l = 0;
				u++;
				key[u] = new byte[4];
			}
			key[u][l] = k[i];
			l++;
		}
		curretLenght = u+1;
	}
	
	private void generateShedule(int roundLenght){
		roundShedule = new byte[roundLenght];
		
		int cl = curretLenght;
		int p = 1;
		int toAdd = (outputLenght/4+1)-curretLenght;
		for (int i = 0; i < toAdd+2 || i < 2; i++) {
			while(roundShedule[p] != 0){
				p++;
				if(p>=roundShedule.length)
					p = 0;
			}
			
			roundShedule[p] = ROUND_ENLARGE;
			cl++;
			p+=3;
			if(cl<8){
				p-=2;
			}
			if(p>=roundShedule.length)
				p -= roundShedule.length;
		}
		p = roundShedule.length-1;
		for (int i = 0; i > toAdd-2 || i>-2; i--) {
			while(roundShedule[p] != 0){
				p--;
				if(p<0)
					p = roundShedule.length-1;
			}
			
			roundShedule[p] = ROUND_REDUCE;
			cl--;
			p-=3;
			if(p<0)
				p += roundShedule.length;
		}
	}
	
	private void oneRound(byte type){
		/*System.out.println();
		for (int i = 0; i < key.length; i++) {
			printAsArray(key[i]);
			System.out.print("- ");
		}*/
		
		//Process Salt...
		byte[] saltBuffer = new byte[salt.length*salt[0].length];
		int saltBufferPos = 0;
		for (int i = 0; i < salt.length; i++) {
			for (int j = 0; j < salt[i].length; j++) {
				saltBuffer[saltBufferPos] = salt[i][j];
				saltBufferPos++;
			}
		}
		scmha.update(saltBuffer);
		scmha.update(getKey(0));
		scmha.update(getKey(4));
		scmha.update(getKey(8));
		setSalt(scmha.digest());
		scmha.reset();
		
		byte[] w = getKey(0); //Take first Block
		xOR(w, getSalt(getSum(1, w)), 0); //XOr with Salt
		xOR(w, ch(getKey(1), getKey(2), getKey(3)), 0); //Perform First Mix
		xOR(w, getSalt(getSum(1, w)), 0); //XOr with Salt
		xOR(w, shuffleSum(getKey(3)), 0); //XOr with ShuffleSum of 2
		
		xOR(getKey(4), rotate(w, getSum(1, getKey(1))), getSum(1, getSalt(0))); //XOrs bank 3 withe a rotated W
		
		xOR(w, ma(getKey(5), getKey(6), getKey(7)), 0); //Perform Ma-Mix
		xOR(w, getSalt(getSum(1, w)), 0); //XOr with Salt
		xOR(w, shuffleSum(getKey(7)), 0); //XOr with shuffleSm of bank 6;
		
		key[0] = w;
		
		//Shuffle Banks 2,3,6,7
		if(curretLenght>=8){
			key[3] = exponent(exponent(key[2], getSalt(salt.length/2), false), key[3], false);
			key[2] = cellularMutationRound(key[2], 5, getSum(1, key[3]));
			key[6] = cellularMutationRound(key[6], 5, getSum(1, key[2]));
			byte[] q = key[7].clone();
			cellularMutationInArray(q);
			q = exponent(key[7], q, false);
			xOR(key[7], q, 1);
		}
		
		if(type == ROUND_ENLARGE){
			curretLenght++;
			key[curretLenght-1] = exponent(key[0], key[curretLenght-2], false);
			xOR(key[curretLenght-1], getSalt(getSum(1, salt[0])), 0);
			key[curretLenght-1] = rotate(key[curretLenght-1], getSum(1, salt[1]));
		}else if(type == ROUND_REDUCE){
			curretLenght--;
			key[curretLenght-1] = exponent(key[curretLenght], 
					cellularMutationRound(key[0], 5, 1), false);
			key[curretLenght] = new byte[0];
		}
		
		w = key[0];
		for (int i = 0; i < curretLenght-1; i++) {
			key[i] = key[i+1];
		}
		key[curretLenght-1] = w;
	}
	
	private byte[] getKey(int pos){
		return key[pos%curretLenght];
	}
	
	private byte[] getSalt(int pos){
		return salt[pos%salt.length];
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException{
		byte[] a = new byte[20];
		byte[] b = new byte[4];
		
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(a);
		sr.nextBytes(b);
		
		a = "ThisIsThePassword:".getBytes();
		b = "SaltSaltSaltSaltSalt".getBytes();
		
		RSA_PBKDF r = new RSA_PBKDF(10, 30, SCMHA.SCMHA_1024);
		byte[] at = r.operate(a, b);
		System.out.println();
		System.out.println("Done!");
		printAsArray(at);
		b[3]++;
		at = r.operate(a, b);
		System.out.println();
		System.out.println("Done!");
		printAsArray(at);
	}
	
	////////////////////////////////////////////////////////////////////////
	// Process
	////////////////////////////////////////////////////////////////////////
	
	private static byte[] ch(byte[] a, byte[] b, byte[] c){
		byte[] r = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			byte k = (byte)(a[i]^0xff);
			r[i] = (byte)((a[i] & b[i])^(k & c[i]));
		}
		return r;
	}
	
	private static byte[] ma(byte[] a, byte[] b, byte[] c){
		byte[] r = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			r[i] = (byte)((a[i] & b[i])^(a[i] & c[i])^(b[i] & c[i]));
		}
		return r;
	}
	
	private static byte[] shuffleSum(byte[] a){
		byte[] b = rotate(a, 2);
		byte[] c = rotate(a, 18);
		a = rotate(a, 22);
		byte[] r = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			r[i] = (byte)(a[i]^b[i]^c[i]);
		}
		return r;
	}
	
	private static byte[] exponent(byte[] a, byte[] b, boolean bigOutput){
		BigInteger b1 = new BigInteger(a).abs();
		BigInteger b2 = new BigInteger(b).abs();
		
		long l = Long.MAX_VALUE;
		if(!bigOutput)
			l = (long)Integer.MAX_VALUE;
		b1 = b1.modPow(b2, BigInteger.valueOf(l).multiply(BigInteger.valueOf(2l)));
		
		byte[] c = b1.toByteArray();
		if((bigOutput && c.length<8))
			return Arrays.copyOf(c, 8);
		if((!bigOutput && c.length<4))
			return Arrays.copyOf(c, 4);
		if((bigOutput && c.length>8) || (!bigOutput && c.length>4))
			return Arrays.copyOfRange(c, 1, c.length);
		return c;
	}
	
	private static byte[] cellularMutationRound(byte[] b, int ammount, int destructor){
		byte[] r = Arrays.copyOf(b, b.length);
		for (int i = 0; i < ammount; i++) {
			b = cellularMutation(b);
		}
		xOR(b, rotate(r, getSum(1, r)), destructor);
		return b;
	}
	
	private static byte[] cellularMutation(byte[] b){
		byte[] r = new byte[b.length];
		for (int i = 0; i < r.length; i++) {
			r[i] = singleMutation(b, i, 0, false);
		}
		return r;
	}
	
	private static void cellularMutationInArray(byte[] b){
		for (int i = 0; i < b.length; i++) {
			b[i] = singleMutation(b, i, 0, false);
		}
	}
	
	private static byte singleMutation(byte[] b, int x, int y, boolean inv){
		byte r = 0;
		for (int i = 0; i < 8; i++) {
			int u = 0;
			if(extractAdvanced(b, x+1, y+i+1))u++;
			if(extractAdvanced(b, x+1, y+i))u++;
			if(extractAdvanced(b, x+1, y+i-1))u++;
			if(extractAdvanced(b, x, y+i+1))u++;
			if(extractAdvanced(b, x, y+i-1))u++;
			if(extractAdvanced(b, x-1, y+i+1))u++;
			if(extractAdvanced(b, x-1, y+i))u++;
			if(extractAdvanced(b, x-1, y+i-1))u++;
			
			boolean t;
			if(extractAdvanced(b, x, y+i)){
				t = u == 2 || u == 3 || u == 6;
			}else{
				t = u == 3 || u == 6;
			}
			
			r = setBit(r, i, t);
		}
		return r;
	}
	
	private static boolean extractAdvanced(byte[] b, int x, int y){
		x += b.length;
		x = x%b.length;
		y += 8;
		y = y%8;
		return extractBit(b[x], y);
	}
	
	////////////////////////////////////////////////////////////////////////
	// Utility
	////////////////////////////////////////////////////////////////////////

	private static byte[] rotate(byte[] b, int ammount) {
		ammount = ammount%(b.length*8);
		int length = b.length;
		byte[] ret = new byte[length];
		int stack = ammount % 8;
		int add = ammount / 8;
		add = add % length;
		if (stack < 0) {
			stack += 8;
			add++;
		}
		for (int i = 0; i < length; i++) {
			int u = i + length * 2 - add;
			ret[i] = (byte) ((b[u % length] & 0xff) << stack | (b[(u + 1) % length] & 0xff) >> (8 - stack));
		}
		return ret;
	}

	private static void xOR(byte[] b, byte[] tw, int start) {
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (b[i] ^ tw[(i + start) % tw.length]);
		}
	}

	private static int getSum(int it, byte[] b) {
		if (it <= 0)
			it = 1;
		int u = 0;
		for (int i = 0; i < b.length; i += it) {
			u += b[i] & 0xff;
		}
		return u;
	}
	
	private static boolean extractBit(byte b, int pos){
		return ((0x01<<pos)&b) != 0;
	}
	
	private static byte setBit(byte b, int pos, boolean t){
		if(t){
			return (byte)(b | (0x01<<pos));
		}else{
			int i = ~(0x01<<pos);
			return (byte)(b & i);
		}
	}
	
	////////////////////////////////////////////////////////////////////////
	// Print
	////////////////////////////////////////////////////////////////////////
	
	private static void printAsArray(byte[] a){
		for (int i = 0; i < a.length; i++) {
			System.out.print(printBit(a[i]&0xff));
		}
	}
	
	private static String printBit(int a){
		if(printTypeHex){
			if(a<16)
				return "0"+Integer.toHexString(a)+" ";
			return Integer.toHexString(a)+" ";
		}
		String s = Integer.toBinaryString(a);
		while (s.length()<8) {
			s = "0"+s;
		}
		return s+"\n";
	}
}
