package crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class KeySaveLoad {

	private static final String DEVIDER = " : ";
	private static final String encryptedPrivate = "EncrPrivateExponent";
	private static final String privateExp = "PrivateExponent";
	private static final String publicExp = "PublicExponent";
	private static final String modulus = "Modulus";
	private static final String modLenght = "Lenght";
	
	
	/**
	 * This Method saves the Key in the given file.
	 * This happens unencrypted, so it is NOT recommended for Private-Keys
	 * @param key The key to be saved
	 * @param f The File the Key should be stored in
	 * @param publ Save only the Public part of the Key. Recommended: true
	 */
	public final void saveKey(RSAsaveKEY key, final File f, final boolean publ){
		subSave1(f);
		PrintWriter writer = null;
		try { 
			writer = new PrintWriter(new FileWriter(f, true), true);
			if(publ){
				writer.println("--------------Begin RSA Public Key--------------");
				subSave2(writer, null, key.getPublicExponent(), key.getModulus());
				writer.println("---------------End RSA Public Key---------------");
			}else{
				writer.println("--------------Begin RSA Private Key--------------");
				subSave2(writer, key.getPrivateExponent(), key.getPublicExponent(), key.getModulus());
				writer.println("---------------End RSA Private Key---------------");
			}
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
	}
	
	/**
	 * Stores the whole Key, however the Private-Exponent gets Encrypted.
	 * @param key The key to be saved
	 * @param f The File the Key should be stored in
	 * @param pw The Password to Encrypt
	 */
	public final void saveKeyEncrypted(final RSAsaveKEY key, final File f, byte[] pw,
			int rounds, int memory, utility.ProgressUpdate pu){
		saveKeyEncrypted(key, f, pw, null, rounds, memory, pu);
	}
	
	/**
	 * Stores the whole Key, however the Private-Exponent gets encrypted (first by the Password, then by the RSA-Key).
	 * @param key The key to be saved
	 * @param f The File the Key should be stored in
	 * @param pw The Password to Encrypt
	 * @param encrKey An RSA-Key to additionally encrypt the main Key, null if unused.
	 */
	public final void saveKeyEncrypted(final RSAsaveKEY key, final File f, byte[] pw, final RSAsaveKEY encrKey,
			int rounds, int memory, utility.ProgressUpdate pu){
		subSave1(f);
		byte[] b = PasswordEncrypter.encrypt(key.getPrivateExponent().toByteArray(), pw, rounds, memory, pu);
		if(encrKey != null)
			b = RSAcrypto.encryptByte(b, encrKey, true);
		String privExp = Base64.getEncoder().encodeToString(b);
		//Destroy the password
		for (int i = 0; i < pw.length; i++) {
			pw[i] = 0;
		}
		//Print
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(f, true), true);
			writer.println("--------------Begin RSA encrypted Key--------------");
			writer.println(encryptedPrivate+DEVIDER+privExp); //Save the encrypted Exponent
			subSave2(writer, null, key.getPublicExponent(), key.getModulus());
			writer.println("---------------End RSA encrypted Key---------------");
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
	}
	
	//Helper to save Keys
	private void subSave1(File f){
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(f.getPath()));
			writer.println("-Saved at: ["+
					new java.text.SimpleDateFormat("dd.MM.yy hh:mm").format(new java.util.Date (System.currentTimeMillis()))
					+"]");
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
	}
	
	private final void subSave2(final PrintWriter writer, BigInteger priv, BigInteger pub, BigInteger mod){
		if(priv != null){
			writer.println(privateExp+DEVIDER+priv.toString(16));
		}
		if(pub != null){
			writer.println(publicExp+DEVIDER+pub.toString(16));
		}
		writer.println(modulus+DEVIDER+mod.toString(16));
		writer.println(modLenght+DEVIDER+mod.bitLength()+" bit");
	}
	
	
	public final RSAsaveKEY loadEncrypted(File f, byte[] pw, utility.ProgressUpdate p)
			throws IOException, KeyException, InvalidKeySpecException{
		return loadEncrypted(f, pw, null, p);
	}
	
	public final RSAsaveKEY loadEncrypted(File f, byte[] pw, RSAsaveKEY decrKey, utility.ProgressUpdate p)
			throws IOException, KeyException, InvalidKeySpecException{
		String mod = null;
		String pri = null;
		String pub = null;
		boolean wasEncr = false;
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String s;
		do{
			s = br.readLine();
			if(s==null)break;
			if(s.length()==0)break;
			if(s.startsWith("-"))
				continue;
			String[] st = s.split(DEVIDER);
			//Compare to leading Srings
			if(st[0].compareTo(publicExp)==0){
				pub = st[1];
			}
			if(st[0].compareTo(modulus)==0){
				mod = st[1];
			}
			if(st[0].compareTo(privateExp)==0){
				pri = st[1];
				wasEncr = false;
			}
			if(st[0].compareTo(encryptedPrivate)==0){
				pri = st[1];
				wasEncr = true;
			}
		}while (true); 
		br.close();
		
		//Test if PW was encrypted
		if(wasEncr && pw == null){
			throw new KeyException("This key was Encrypted");
		}
		if(!wasEncr && pw != null){
			throw new KeyException("This key wasn't Encrypted");
		}
		RSAsaveKEY key;
		if(wasEncr){
			byte[] bl = Base64.getDecoder().decode(pri);
			if(decrKey != null){
				bl = RSAcrypto.decryptByte(bl, decrKey, false);
				if(!PasswordEncrypter.check(bl))//Prevent the Following method to run till eternity...
					throw new KeyException("Faulty RSA-Decryption!");
			}
			BigInteger b = new BigInteger(PasswordEncrypter.decrypt(bl, pw, p));
			key = new RSAsaveKEY(b, new BigInteger(pub, 16), new BigInteger(mod, 16));
			pw = null;
			bl = null;
			Runtime.getRuntime().gc();
		}else{
			key = new RSAsaveKEY(pri, pub, mod);
		}
		
		return key;
	}
	
	public final RSAsaveKEY load(File f) throws IOException, KeyException, InvalidKeySpecException{
		return loadEncrypted(f, null, null);
	}
	
	public static boolean isKeyEncrypted(File f) throws IOException{
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String s;
		do{
			s = br.readLine();
			if(s==null)break;
			if(s.length()==0)break;
			if(s.startsWith("-"))
				continue;
			String[] st = s.split(DEVIDER);
			
			if(st[0].compareTo(privateExp)==0){
				br.close();
				return false;
			}
			if(st[0].compareTo(encryptedPrivate)==0){
				br.close();
				return true;
			}
		}while (true); 
		
		br.close();
		return false;
	}
	
}
