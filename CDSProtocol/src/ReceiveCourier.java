import java.nio.ByteBuffer;

public class ReceiveCourier extends Delegate {
	static final boolean debug = true;
	
	private String ID = "Courier";
	private String senderID = "Alice";
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private UserInterface ui;
	
	private byte[] kc;
	private byte[] senderSK;
	private String receivedID, receiverID = "";
	private byte[] meta, msg, totalMAC = null;
	
	public ReceiveCourier(Crypto c, DataManager dm) {
		crypto = c;
		dataManager = dm;
		senderSK = dataManager.getPublicKey(senderID);
	}
	
	public void setUserInterface(UserInterface ui) {
		this.ui = ui;
	}
	
	public ByteBuffer getInitialMessage() {
		kc = crypto.generateSymmKey(128);
		byte[] encryptedKc = crypto.encryptAsym(kc, senderSK);
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
			return getMessage1(src, dst);
		
		default:
			return -1;
		}
	}
	
	protected int getMessage1(ByteBuffer src, ByteBuffer dst) {
		// Retrieve received data----------------------------------------------------------------------
		receivedID = extractString(src, 0, LENGTH_ID).trim();
		senderID = extractString(src, 0, LENGTH_ID).trim();
		receiverID = extractString(src, 0, LENGTH_ID).trim();
		
		int metaLength = src.getInt();
		meta = new byte[metaLength];
		src.get(meta);
		
		int msgLength = src.getInt();
		msg = new byte[msgLength];
		src.get(msg);
		
		totalMAC = new byte[Crypto.LENGTH_MAC];
		src.get(totalMAC);

		int receivedLength = 3*LENGTH_ID + 2*Integer.SIZE/8 + metaLength + msgLength;
		
		if (debug) {
			ui.print("Receiver=\t" + receivedID, ID);
			ui.print("Sender=\t" + senderID, ID);
			ui.print("Recipient=\t" + receiverID, ID);
			ui.print("META length=\t" + metaLength, ID);
			ui.print("MSG length=\t" + msgLength, ID);
		}
					
		// Validate received data------------------------------------------------------------------------
		if (!receivedID.equals(ID)) return -1;
		
		byte[] messageReceived = new byte[receivedLength];
		src.rewind();
		src.get(messageReceived);
		if (!crypto.verifyMACDigest(messageReceived, kc, totalMAC)) return -1;
		
		// Operate received data ------------------------------------------------------------------------
		ByteBuffer dataToSave = ByteBuffer.allocate(metaLength+msgLength);
		dataToSave.put(meta);
		dataToSave.put(msg);
		dataManager.putData(dataToSave.array(), senderID, receiverID);
		
		// Prepare send data ----------------------------------------------------------------------------
		src.rewind();
		byte[] totalReceived = new byte[receivedLength + Crypto.LENGTH_MAC];
		src.get(totalReceived);
		dst.clear();
		dst.put(crypto.getHashDigest(totalReceived));
		dst.flip();
					
		terminate();
		return Crypto.LENGTH_HASH;
	}
}