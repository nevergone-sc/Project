package cdspcore;

import java.io.IOException;
import java.nio.ByteBuffer;

/* Delegate of the dataReceiver, defines how message is received from a courierSender
 */
public class DataReceiver extends Delegate {
	static final boolean debug = true;
	
	private String ID;
	private byte[] mySK;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private UserInterface ui;
	
	byte[] kC;
	
	public DataReceiver(String id, UserInterface ui, Crypto c, DataManager dm) {
		ID = id;
		crypto = c;
		dataManager = dm;
		this.ui = ui;
		
		try {
			mySK = dm.getPrivateKey();
		} catch (IOException e) {
			ui.printErr(e.getMessage(), ID);
			ui.printErr("Process finished", ID);
			terminate();
		}
	}
	
	public void setUserInterface(UserInterface ui) {
		this.ui = ui;
	}
	
	public ByteBuffer getInitialMessage() {
		return null;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 0:
			return getMessage1(src, dst);
			
		default:
			return -1;
		}
	}

	protected int getMessage1(ByteBuffer src, ByteBuffer dst) {
		src.mark();
		// Retrieve received data ----------------------------------------------------------------
		// Sender ID
		String senderID = new String(getShortBlock(src));
		int lengthSenderID = senderID.length();
		
		byte[] encryptedBlock = new byte[Crypto.LENGTH_ASYM_CIPHER];
		src.get(encryptedBlock);
		kC = crypto.decryptAsym(encryptedBlock, mySK);
		
		// Retrieve and validate all Data blocks from Alices
		int totalMetaLength = 0;
		int totalMsgLength = 0;
		
		while (src.remaining() > 0) {
			byte[] meta = getLongBlock(src);
			int metaLength = meta.length;
			totalMetaLength += metaLength + Integer.SIZE/8;
			
			byte[] ecryptedMetaValue = new byte[Crypto.LENGTH_ASYM_CIPHER];
			System.arraycopy(meta, 0, ecryptedMetaValue, 0, Crypto.LENGTH_ASYM_CIPHER);
			
			ByteBuffer metaBlock = ByteBuffer.wrap(crypto.decryptAsym(ecryptedMetaValue, mySK));
			
			// kAB
			byte[] msgKey = new byte[LENGTH_SYMM_KEY];
			metaBlock.get(msgKey);
			
			String metaSrcID = new String(getShortBlock(metaBlock));
			String metaDstID = new String(getShortBlock(metaBlock));
			Long timestamp = metaBlock.getLong();
			
			// Signature for META
			int metaSignLength = metaLength - Crypto.LENGTH_ASYM_CIPHER;
			byte[] encryptedMetaSign = new byte[metaSignLength];
			System.arraycopy(meta, Crypto.LENGTH_ASYM_CIPHER, encryptedMetaSign, 0, metaSignLength);
			
			byte[] metaSIGN = crypto.decryptSymm(encryptedMetaSign, msgKey);
			
			byte[] msg = getLongBlock(src);
			int msgLength = msg.length;
			totalMsgLength += msgLength + Integer.SIZE/8;
			int msgValueLength = msgLength - Crypto.LENGTH_MAC;
	
			byte[] encryptedMsgBlock = new byte[msgValueLength];
			System.arraycopy(msg, 0, encryptedMsgBlock, 0, msgValueLength);
			byte[] msgMAC = new byte[Crypto.LENGTH_MAC];
			System.arraycopy(msg, msgValueLength, msgMAC, 0, Crypto.LENGTH_MAC);
			
			if (debug) {
				ui.print("-----------------------------------------------------------------", "", "");
				ui.print(senderID, "SenderID=\t\t", ID);
				ui.print(metaSrcID, "SourceID=\t\t", ID);
				ui.print(encryptedBlock, "EncPKB=\t\t", ID);
			}
			
			// Validate received data ----------------------------------------------------------------
			if (!metaDstID.equals(ID)) {
				ui.printErr("Destination ID in Meta not match", ID);
				terminate();
				return -1;
			} // TODO: For timestamp validation
			
			byte[] srcPK;
			try {
				srcPK = dataManager.getPublicKey(metaSrcID);
			} catch (IOException e) {
				ui.printErr(e.getMessage(), ID);
				terminate();
				return -1;
			}
			if (!crypto.verifySIGN(ecryptedMetaValue, srcPK, metaSIGN)) {
				ui.printErr("Meta Signature Verified False", ID);
				terminate();
				return -1;
			}
			
			if (!crypto.verifyMACDigest(encryptedMsgBlock, msgKey, msgMAC)) {
				ui.printErr("Message MAC Verified False", ID);
				terminate();
				return -1;
			}
			
			// Operate on received data ----------------------------------------------------------------
			byte[] plainMsg = crypto.decryptSymm(encryptedMsgBlock, msgKey);
			ui.print(String.valueOf(timestamp), "TimeStamp=\t\t", ID);
			ui.print(new String(plainMsg), "Result=\t\t", ID);
		}
		
		// Prepare for send data -------------------------------------------------------------------
		int totalReceivedLength = lengthSenderID+Byte.SIZE/8+Crypto.LENGTH_ASYM_CIPHER+totalMetaLength+totalMsgLength;
		src.reset();
		byte[] totalReceived = new byte[totalReceivedLength];
		src.get(totalReceived);
		byte[] totalReceivedMAC = crypto.getMACDigest(totalReceived, kC);
		
		dst.clear();
		dst.put(totalReceivedMAC);
		dst.flip();
		
		terminate();
		ui.nextStep("", ID);
		return totalReceivedMAC.length;
	}
}