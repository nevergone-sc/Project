import java.nio.ByteBuffer;


public class SendCourier extends Delegate {
	static final boolean debug = true;
	
	private String ID;
	private String receiverID;
	private String senderID;
	private byte[] receiverPK;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private ByteBuffer sentBuffer;
	private UserInterface ui;
	
	byte[] kC;
	
	public SendCourier(String id, Crypto c, DataManager dm, String sedID, String revID) {
		ID = id;
		receiverID = revID;
		senderID = sedID;
		crypto = c;
		dataManager = dm;
		receiverPK = dm.getPublicKey(receiverID);
	}
	
	public void setUserInterface(UserInterface ui) {
		this.ui = ui;
	}
	
	public ByteBuffer getInitialMessage() {
		kC = crypto.generateSymmKey(LENGTH_SYMM_KEY*8);
		int encryptedLength = senderID.length()+receiverID.length()+LENGTH_SYMM_KEY+2*Byte.SIZE/8;
		ByteBuffer encryptedBuffer = ByteBuffer.allocate(encryptedLength);
		putShortBlock(receiverID.getBytes(), encryptedBuffer);
		putShortBlock(senderID.getBytes(), encryptedBuffer);
		encryptedBuffer.put(kC);
		byte[] encryptedBlock = crypto.encryptAsym(encryptedBuffer.array(), receiverPK);
		
		ByteBuffer dataToSendBuffer = ByteBuffer.wrap(dataManager.getData(senderID, receiverID));
		// Get Meta
		byte[] meta = getLongBlock(dataToSendBuffer);
		int metaLength = meta.length;
		// Get Message
		byte[] msg = getLongBlock(dataToSendBuffer);
		int msgLength = msg.length;
		
		int totalMsgLength = ID.length() + Crypto.LENGTH_ASYM_CIPHER + Byte.SIZE/8 + 2*Integer.SIZE/8 + metaLength + msgLength;
		ByteBuffer totalMsgBuffer = ByteBuffer.allocate(totalMsgLength);
		putShortBlock(ID.getBytes(), totalMsgBuffer);
		totalMsgBuffer.put(encryptedBlock);
		putLongBlock(meta, totalMsgBuffer);
		putLongBlock(msg, totalMsgBuffer);
		totalMsgBuffer.flip();
		
		sentBuffer = totalMsgBuffer;
		
		state = 1;
		return totalMsgBuffer;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 1: 
			byte[] receivedMAC = new byte[Crypto.LENGTH_MAC];
			src.get(receivedMAC);
			boolean isMACValid = crypto.verifyMACDigest(sentBuffer.array(), kC, receivedMAC);
			if (debug) {
				ui.print(receivedMAC, "MAC=\t\t", ID);
				ui.print(String.valueOf(isMACValid), "Verify MAC=\t\t", ID);
			}
			
			if (!isMACValid) return -1;
			terminate();
			ui.nextStep("", ID);
			return 0;
			
		default:
			return -1;
		}
	}
}