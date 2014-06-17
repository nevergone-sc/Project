import java.nio.ByteBuffer;


public class ReceiveCourier extends Delegate {
	static final boolean debug = true;
	
	private String ID = "COURIER";
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	
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
		ByteBuffer returnBuffer = ByteBuffer.allocate(LENGTH_ID+2*Integer.SIZE/8+encryptedLength);
		returnBuffer.put(wrapID(ID));
		returnBuffer.putInt(dataManager.maxStorage());
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

			int receivedLength = 3*LENGTH_ID + 2*Integer.SIZE/8 + metaLength + msgLength + Crypto.LENGTH_MAC;
			
			if (debug) {
				System.out.println("ReceiveCourier---------------------");
				System.out.println("Receiver: " + receivedID);
				System.out.println("Sender: " + senderID);
				System.out.println("Recipient: " + receiverID);
				System.out.println("META length: " + metaLength);
				System.out.println("Message length: " + msgLength);
			}
			
			if (!validateMsg()) return -1;
			
			src.rewind();
			byte[] totalReceived = new byte[receivedLength];
			src.get(totalReceived);
			dst.clear();
			dst.put(crypto.getHashDigest(totalReceived));
			dst.flip();
			
			terminate();
			return Crypto.LENGTH_HASH;
		
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