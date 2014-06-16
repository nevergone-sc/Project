import java.nio.ByteBuffer;


public class ReceiveCourier extends Delegate {
	static final boolean debug = true;
	
	private final int LENGTH_SIZE = 4;
	private final int LENGTH_ENC_KEY = 5;
	private final int LENGTH_META = 5;
	private final int LENGTH_MSG  = 5;
	private final int LENGTH_MAC  = 5;
	private final int MAX_STORAGE = 1000;
	private String ID = "COURIER";
	private int state = 0;
	private Crypto crypto = new Crypto();
	private DataManager dataManager = new DataManager();
	
	byte[] kc = null;
	String senderID = "ALICE";
	byte[] pkA = null;
	String receivedID, receiverID = "";
	byte[] metaBValue, metaBSign, msgB, totalMAC = null;
	
	public ReceiveCourier(Crypto c, DataManager dm) {
		crypto = c;
		dataManager = dm;
		pkA = dataManager.getPublicKey(senderID);
	}
	
	public ByteBuffer getInitialMessage() {
		kc = crypto.generateSymmKey(128);
		byte[] encryptedKc = crypto.encryptAsym(kc, pkA);
		int encryptedLength = encryptedKc.length;
		ByteBuffer returnBuffer = ByteBuffer.allocate(LENGTH_ID+2*Integer.SIZE+encryptedLength);
		returnBuffer.put(wrapID(ID));
		returnBuffer.putInt(MAX_STORAGE);
		returnBuffer.putInt(encryptedLength);
		returnBuffer.put(encryptedKc);
		returnBuffer.flip();
		
		state = 1;
		return returnBuffer;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 1:
			receivedID = extractString(src, 0, LENGTH_ID).trim();
			senderID = extractString(src, 0, LENGTH_ID).trim();
			receiverID = extractString(src, 0, LENGTH_ID).trim();
			
			int metaLength = src.getInt();
			metaBValue = new byte[metaLength-Crypto.LENGTH_SIGN];
			metaBSign = new byte[Crypto.LENGTH_SIGN];
			src.get(metaBValue);
			src.get(metaBSign);
			
			int msgLength = src.getInt();
			msgB = new byte[msgLength];
			src.get(msgB);
			
			totalMAC = new byte[Crypto.LENGTH_MAC];
			src.get(totalMAC);
			
			if (debug) {
				System.out.println("ReceiveCourier---------------------");
				System.out.println("Receiver: " + receivedID);
				System.out.println("Sender: " + senderID);
				System.out.println("Recipient: " + receiverID);
			}
			
			if (!validateMsg()) return -1;
			
			src.rewind();
			dst.clear();
			dst.put(crypto.getHashDigest(src.array()));
			dst.flip();
			
			terminate();
			return dst.remaining();
		
		default:
			return -1;
		}
		
	}
	
	private boolean validateMsg() {
		if (!receivedID.equals(ID)) return false;
		//TODO
		return true;
	}
}