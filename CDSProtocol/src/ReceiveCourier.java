import java.nio.ByteBuffer;

public class ReceiveCourier extends Delegate {
	static final boolean debug = true;
	
	private String ID;
	private String senderID;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private UserInterface ui;
	
	private byte[] kc;
	private byte[] senderSK;
	private String receivedID, receiverID = "";
	private byte[] meta, msg, totalMAC = null;
	
	public ReceiveCourier(String id, Crypto c, DataManager dm, String sedID) {
		ID = id;
		senderID = sedID;
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
		src.mark();
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
			ui.print(receivedID, "Receiver=\t", ID);
			ui.print(senderID, "Sender=\t", ID);
			ui.print(receiverID, "Recipient=\t", ID);
			ui.print(String.valueOf(metaLength), "META length=\t", ID);
			ui.print(String.valueOf(msgLength), "MSG length=\t", ID);
		}
					
		// Validate received data------------------------------------------------------------------------
		if (!receivedID.equals(ID)) return -1;
		
		byte[] messageReceived = new byte[receivedLength];
		src.reset();
		src.get(messageReceived);
		if (!crypto.verifyMACDigest(messageReceived, kc, totalMAC)) {System.err.println("mac error"); return -1;}
		
		// Operate received data ------------------------------------------------------------------------
		ByteBuffer dataToSave = ByteBuffer.allocate(metaLength+msgLength);
		dataToSave.put(meta);
		dataToSave.put(msg);
		dataManager.putData(dataToSave.array(), senderID, receiverID);
		
		// Prepare send data ----------------------------------------------------------------------------
		src.reset();
		byte[] totalReceived = new byte[receivedLength + Crypto.LENGTH_MAC];
		src.get(totalReceived);
		dst.clear();
		dst.put(crypto.getHashDigest(totalReceived));
		dst.flip();
					
		terminate();
		ui.nextStep("", ID);
		return Crypto.LENGTH_HASH;
	}
}