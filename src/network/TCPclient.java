package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import crypto.FastLinearCrypto;
import crypto.RSAsaveKEY;

import static network.StaticComStrings.*;

/**
 * This class provides the communication-Basics over a TCP-Socket <br>
 * such as encrypted Read+Write, Connection-Names, Connection-Keys etc.
 * @author Sven T. Schneider
 */
public class TCPclient implements Runnable{

	/**
	 * RSA master-Key
	 */
	private final RSAsaveKEY myKey;
	
	/**
	 * The other Side's RSA public key
	 */
	private RSAsaveKEY otherKey;
	
	/**
	 * The TCP-Socket
	 */
	private final Socket socket;
	
	/**
	 * Output-Stream, will write on the socket
	 */
	private final PrintWriter outStream;
	
	/**
	 * Input-Stream will read from the Socket
	 */
	private final Scanner inStream;
	
	/**
	 * Input-Buffer, input will be continuously read and written to that Buffer
	 */
	private List<String> input;
	
	/**
	 * The connection textual representation (other sides User-Name or Server-Name)
	 */
	private String otherName;
	private final String myName;
	
	/**
	 * The connection's alias, will be the  same for both connected clients and unique in the network. <br>
	 * Will be provided by the Central Server or the Host in PierToPier configuration
	 */
	private String connectionName;
	
	private boolean isConnected;
	private boolean isRunning;
	
	public final String connectionAdress;
	
	/**
	 * A Fast-Linear-Encryption frame to handle encryption over this connection
	 */
	private FastLinearCrypto linearCrypto;
	private static final int LINEAR_KEY_SIZE = 32;
	
	/**
	 * This Semaphore is used to handle access on the Input-List and the LinearCipher
	 */
	private final Semaphore sema;
	
	/**
	 * System-Time of Connection in Milliseconds
	 */
	public final long connectionStartTime;
	
	/**
	 * Status line, Disconnection note etc.
	 */
	public String status;
	
	/**
	 * Starts a TCP-Handler for the given Socket
	 * @param s the socket to be handled
	 * @param myKey your (private-) RSA-key
	 * @param myName your Name by which to be referred to
	 * @param insistConName Insist on a connection-Alias IMPORTANT: set null if your not the Server in this instance!
	 */
	public TCPclient(Socket s, RSAsaveKEY myKey, String myName, String insistConName) throws IllegalStateException{
		this.myKey = myKey;
		socket = s;
		this.myName = myName;
		
		connectionName = insistConName;
		
		try {
			inStream = new Scanner(socket.getInputStream());
			outStream = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			debug.Debug.println("*Error initialising Socket", debug.Debug.ERROR);
			debug.Debug.println(e.toString(), debug.Debug.SUBERR);
			e.printStackTrace();
			throw new IllegalStateException("Socket unfit for transmission!");
		}
		
		connectionAdress = socket.getInetAddress().getHostAddress()+":"+socket.getPort();
		
		input = new ArrayList<>();
		sema = new Semaphore(1);
		
		connectionStartTime = System.currentTimeMillis();
		isRunning = true;
		
		new Thread(this, "Connection Handler "+connectionAdress).start();
	}

	@Override
	public void run() {
		try {//Initialise
			init();
		} catch (Exception e) {
			debug.Debug.println("*Error Connecting to "+socket.getInetAddress().toString(), debug.Debug.ERROR);
			debug.Debug.println(e.toString(), debug.Debug.SUBERR);
			e.printStackTrace();
			return;
		}
		
		if(!isConnected)
			setRunning(false);
		
		try {//Loop
			while(isRunning()){
				loop();
			}
		} catch (Exception e) {
			debug.Debug.println("*Error during Receiver-Loop", debug.Debug.ERROR);
			debug.Debug.println(e.toString(), debug.Debug.SUBERR);
			e.printStackTrace();
		}
		
		try {//Close and free resources
			close();
		} catch (Exception e) {
			debug.Debug.println("*Error during Closing-Operations", debug.Debug.ERROR);
			debug.Debug.println(e.toString(), debug.Debug.SUBERR);
			e.printStackTrace();
		}
		debug.Debug.println("*Connection Terminated: "+otherName, debug.Debug.WARN);
	}
	
	/**
	 * Initializes the connection (Setup Names, Keys, etc.)
	 * @throws Exception if an error occurs
	 */
	private void init() throws Exception{
		status = "INIT";
		//***Exchange Greeting: Name + PublicKey
		outStream.println(myName+DIV+myKey.getPublicKeyString());
		while(!inStream.hasNext())
			Thread.sleep(10);
		String s = inStream.nextLine();
		otherName = s.split(DIV)[0];
		otherKey = new RSAsaveKEY(s.split(DIV)[1]); //Create an RSA-Key from the public-key-string
		
		//***Exchange Connection-Name
		s = NULL;
		if(connectionName != null) s = connectionName;//If this side provides a Connection-Name send it
		outStream.println(s);
		while(!inStream.hasNext())
			Thread.sleep(10);
		s = inStream.nextLine();
		if(s.compareTo(NULL) == 0 && connectionName == null)//Both null
			throw new IllegalStateException("Both sides have not declared a Connection-Name!");
		if(connectionName == null)//All OK set conName if not already set.
			connectionName = s;
		
		//***Exchange Linear Key
		byte[] firstKeyHalf = new byte[LINEAR_KEY_SIZE/2];//16-byte (128 bit) entropy for each key-half, resulting in a 256bit-key
		crypto.Random.generateSR().nextBytes(firstKeyHalf);//fill the Key-Half with fresh entropy
		byte[] mem = crypto.RSAcrypto.encryptByte(firstKeyHalf, myKey, false); //Encrypt key with my private key
		s = crypto.RSAcrypto.encrypt(mem, otherKey, true); //Encrypt further with other-sides public-key
		outStream.println(s);//and send
		while(!inStream.hasNext())
			Thread.sleep(10);
		s = inStream.nextLine();//receive other key-half
		mem = crypto.RSAcrypto.decryptByte(s, myKey, false); //Decrypt with my Key
		byte[] secondKeyHalf = crypto.RSAcrypto.decryptByte(mem, otherKey, true); //Finally decrypt the second half
		mem = new byte[LINEAR_KEY_SIZE];
		//The order of these two keys is decided by the "order" of the two RSA modulus
		if(myKey.getModulus().compareTo(otherKey.getModulus())>0){//You first
			for (int i = 0; i < LINEAR_KEY_SIZE/2; i++) {
				mem[i*2] = firstKeyHalf[i];
				mem[i*2+1] = secondKeyHalf[i];
			}
		}else{
			for (int i = 0; i < LINEAR_KEY_SIZE/2; i++) {
				mem[i*2+1] = firstKeyHalf[i];
				mem[i*2] = secondKeyHalf[i];
			}
		}
		linearCrypto = new FastLinearCrypto(mem); //And we can init the linear Cipher
		
		//*** Exchange Hash of important values
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(myName.getBytes());
		md.update(myKey.getPublicKeyString().getBytes());
		mem = linearCrypto.encrypt(md.digest(), 0); //Encrypt digested Hash
		outStream.println(Base64.getEncoder().encodeToString(mem));
		while(!inStream.hasNext())
			Thread.sleep(10);
		s = inStream.nextLine();//receive other side's hash
		md.reset(); // And compute hash to compare...
		md.update(otherName.getBytes());
		md.update(otherKey.getPublicKeyString().getBytes());
		
		//Compare the Hashes
		if(!MessageDigest.isEqual(md.digest(), linearCrypto.decrypt(Base64.getDecoder().decode(s), 0))){
			//Hashes INCORRECT!! cancel connection
			debug.Debug.println("*Connection-Check failed! (Hash wrong)", debug.Debug.ERROR);
			return;
		}
		
		//And we are ready to start the Loop!
		isConnected = true;
		status = "CONNECTED";
		
		debug.Debug.println("*Connection Aquired ("+connectionName+"): ");
		debug.Debug.print(otherName, debug.Debug.MESSAGE);
	}
	
	private void loop(){
		if(inStream.hasNext()){
			String s = inStream.nextLine();
			sema.acquireUninterruptibly();
			//Decipher the Message and put it into the Input-List
			try {
				s = new String(linearCrypto.decrypt(Base64.getDecoder().decode(s), 0));
				//Check for close-command
				if(s.startsWith(CLOSE_CONNECTION)){
					status = "Connection Closed: "+s.substring(CLOSE_CONNECTION.length());
					setRunning(false);
				}else{
					input.add(s);
				}
			} catch (Exception e) {
				debug.Debug.println("*Decryption-Error! "+e.toString(), debug.Debug.ERROR);
				e.printStackTrace();
				//TODO handle Error (requires re-send)
			}
			sema.release();
		}else{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				//Ignore interrupts
			}
		}
	}
	
	private void close() throws Exception{
		isConnected = false;
		
		if(outStream != null)
			outStream.close();
		if(inStream != null)
			inStream.close();
		
		socket.close();
	}	
	///////////////////////////////////READ, WRITE, UTILITY/////////////////////////////////////
	
	/**
	 * @param s sends the String (encrypted) over the socket
	 */
	public void send(String s){
		sema.acquireUninterruptibly();
		if(!isConnected){
			sema.release();
			debug.Debug.println("Message should be sent, but client is not ready!", debug.Debug.WARN);
			return;
		}
		try {
			s = Base64.getEncoder().encodeToString(linearCrypto.encrypt(s.getBytes(), 0));
			outStream.println(s);
			
			//Check errors...
			if(outStream.checkError()){
				isRunning = false; //Stream Error, close connection :(
				debug.Debug.println("*The Output-Stream has encountered a Problem!", debug.Debug.ERROR);
			}
		} catch (Exception e) {
			debug.Debug.printExeption(e);
		}
		sema.release();
	}
	
	/**
	 * Returns the next String the socket has received, will return null if there's nothing new to process
	 * @return Next Message or null
	 */
	public String getNextInput(){
		sema.acquireUninterruptibly();
		if(input.isEmpty()){
			sema.release();
			return null;
		}
		String s = input.remove(0);
		sema.release();
		return s;
	}
	
	/**
	 * Closes this connection
	 */
	public synchronized void closeConnection(String reason){
		if(isConnected)
			send(CLOSE_CONNECTION+reason);
		isRunning = false;
	}
	
	///////////////////////////////////GETTER+SETTER/////////////////////////////////////
	
	public synchronized boolean isConnected() {
		return isConnected && !socket.isClosed();
	}
	
	public synchronized boolean isRunning() {
		return isRunning;
	}
	
	private synchronized void setRunning(boolean r){
		isRunning = r;
	}
	
	public RSAsaveKEY getOtherKey() {
		return otherKey;
	}
	
	/**
	 * @return the user/server-name connected to
	 */
	public String getOtherName() {
		return otherName;
	}
	
	/**
	 * @return a String to identify the connection (will be the same for both sides); 
	 * This will be used for message forwarding, FROM and TO Tags etc.
	 */
	public String getConnectionName() {
		return connectionName;
	}
}
