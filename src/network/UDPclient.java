package network;

import java.util.List;
import java.util.concurrent.locks.Lock;

import crypto.FastLinearCrypto;
import crypto.RSAsaveKEY;

/**
 * Will hold a single UDP connection, unique on the Port due to the connectioID.<br>
 * Will automatically split outgoing Messages into suited sizes, will use Single-Stand Error correction.<br>
 * Lost packages will be re-requested. Should behave equal to the TCP-Implementation.<br>
 * This Implementation however will have higher Network-Latency, therefore it should only be used as
 * Fallback, if TCP-Hole-Punching dosn't work on the (NAT-)System.<br><br>
 * Messages will be composed as follows: 1024byte per block; one block consist of following Byte-Order:<br>
 * 0b Con-ID 0-255<br>
 * 1b-3b ACK-Line 1<br>
 * 4b-6b ACK-Line 2<br>
 * 7b-9b ACK-Line 3<br>
 * 10b-12b ACK-Line 4<br>
 * 13b-14b Message-ID, 0-65280<br>
 * 15b Message-Length (N. of Parts)<br>
 * 16b-17b Start Position of this Part<br>
 * 18b-19b Length of valid Bytes in Part<br>
 * 20b-22b Message-Trace<br>
 * 23b-1022b Message-Part<br>
 * 1023b Checksum<br>
 * <br>
 * ACK-Lines will be used as follows:<br>
 * 2bytes of Message-ID,<br>
 * 1 ACK-Bit<br>
 * 7bits of either Message-Checksum (If ACK = True)<br>
 * OR 7bit Part-Number to re-send
 * @author Sven T. Schneider
 */
public class UDPclient implements IpInterface{

	/**
	 * Messages to be sent, not acknowledged by the other side yet
	 */
	private List<SplitMsg> blocksToSend;
	
	/**
	 * Partially received Messages
	 */
	private List<SplitMsg> blocksReceived;
	
	/**
	 * Lock to restrict access to the FastLinearCrypto while in use.
	 */
	private Lock cipherLock;
	/**
	 * Cipher for Message-Processing
	 */
	private FastLinearCrypto flc;
	
	/**
	 * Sends the given String. Important: due to implementation the maximum length of the String is around
	 * 64 000 Characters, the real cutoff should be at 10 000 however, due to resource-management.
	 */
	@Override
	public void send(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getNextInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeConnection(String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RSAsaveKEY getOtherKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOtherName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConnectionName() {
		// TODO Auto-generated method stub
		return null;
	}

}

class SplitMsg{
	
	/**
	 * The Connection-ID, 0-255
	 */
	public final int conID;
	
	/**
	 * A Message-Specific ID, ranging from 1-65025
	 */
	public final int msgID;
	
	/**
	 * <b>Send-Mode:</b> Stores the bytes ready to be send, as complete 1024-byte Message-Blocks.
	 * Will be empty in Receive-Mode
	 */
	private byte[][] messageBytes;
	
	/**
	 * The Message itself, as soon as complete it will be processed.
	 */
	private byte[] message;
	
	/**
	 * Message-Checksum, used for ACK
	 */
	public byte checkSum;
	
	public SplitMsg(int mid, int cid, FastLinearCrypto f){
		msgID = mid;
		conID = cid;
	}
	
	/**
	 * Initializes Send-Mode, the message will be split and processed.<br>
	 * The Sub-Message can then be accessed by the remote sender.
	 * @param s
	 */
	public void initSend(byte[] s){
		
	}
	
	/**Creates the Message-Checksum*/
	private void csMsg(){
		byte b = 0;
		for (int i = 0; i < message.length; i++) {
			b += message[i];
		}
		checkSum = (byte)(b & 0x7f);
	}
}
