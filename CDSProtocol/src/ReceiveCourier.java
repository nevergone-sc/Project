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
		ByteBuffer returnBuffer = ByteBuffer.allocate(ID.length()+3*Integer.SIZE/8+encryptedLength);
		putShortBlock(ID.getBytes(), returnBuffer);
		returnBuffer.putInt(dataManager.maxStorage());
		putLongBlock(encryptedKc, returnBuffer);
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
		receivedID = new String(getShortBlock(src));
		senderID = new String(getShortBlock(src));
		receiverID = new String(getShortBlock(src));

		meta = getLongBlock(src);
		int metaLength = meta.length;
		
		msg = getLongBlock(src);
		int msgLength = msg.length;
		
		totalMAC = new byte[Crypto.LENGTH_MAC];
		src.get(totalMAC);

		int receivedLength = receivedID.length() + senderID.length() + receiverID.length() + 3*Byte.SIZE/8 + 2*Integer.SIZE/8 + metaLength + msgLength;
		
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
		ByteBuffer dataToSave = ByteBuffer.allocate(metaLength+msgLength+2*Integer.SIZE/8);
		dataToSave.putInt(metaLength);
		dataToSave.put(meta);
		dataToSave.putInt(msgLength);
		dataToSave.put(msg);
		dataManager.putData(dataToSave.array(), senderID, receiverID);
		
		// Prepare send data ----------------------------------------------------------------------------
		src.reset();
		byte[] totalReceived = new byte[receivedLength + Crypto.LENGTH_MAC];
		src.get(totalReceived);
		dst.clear();
		dst.put(crypto.getMACDigest(totalReceived, kc));
		dst.flip();
					
		terminate();
		ui.nextStep("", ID);
		return Crypto.LENGTH_HASH;
	}
}