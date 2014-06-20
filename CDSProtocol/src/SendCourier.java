import java.nio.ByteBuffer;


public class SendCourier extends Delegate {
	static final boolean debug = true;
	
	private String ID = "Courier";
	private String receiverID = "Bob";
	private String senderID = "Alice";
	private byte[] receiverPK;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private ByteBuffer sentBuffer;
	
	byte[] kC;
	
	public SendCourier(Crypto c, DataManager dm) {
		crypto = c;
		dataManager = dm;
		receiverPK = dm.getPublicKey(receiverID);
	}
	
	public ByteBuffer getInitialMessage() {
		kC = crypto.generateSymmKey(LENGTH_SYMM_KEY*8);
		int encryptedLength = 2*LENGTH_ID+LENGTH_SYMM_KEY;
		ByteBuffer encryptedBuffer = ByteBuffer.allocate(encryptedLength);
		encryptedBuffer.put(wrapID(receiverID));
		encryptedBuffer.put(wrapID(senderID));
		encryptedBuffer.put(kC);
		byte[] encryptedBlock = crypto.encryptAsym(encryptedBuffer.array(), receiverPK);
		
		byte[] dataToSend = dataManager.getData(senderID, receiverID);
		int totalMsgLength = LENGTH_ID + Crypto.LENGTH_ASYM_CIPHER + Integer.SIZE/8 + dataToSend.length;
		ByteBuffer totalMsgBuffer = ByteBuffer.allocate(totalMsgLength);
		totalMsgBuffer.put(wrapID(ID));
		totalMsgBuffer.put(encryptedBlock);
		totalMsgBuffer.putInt(dataToSend.length);
		totalMsgBuffer.put(dataToSend);
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
				System.out.println("SendCourier--------------");
				System.out.println("MAC: " + new String(receivedMAC));
			}
			
			if (!isMACValid) return -1;
			terminate();
			return 0;
			
		default:
			return -1;
		}
	}
}
