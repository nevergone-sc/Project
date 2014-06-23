import java.nio.ByteBuffer;

public class DataReceiver extends Delegate {
	static final boolean debug = true;
	
	private String ID = "Bob";
	private byte[] mySK;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private UserInterface ui;
	
	byte[] kC;
	
	public DataReceiver(Crypto c, DataManager dm) {
		crypto = c;
		dataManager = dm;
		mySK = dm.getPrivateKey();
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
		// Retrieve received data ----------------------------------------------------------------
		// Sender ID
		String senderID = extractString(src, 0, LENGTH_ID).trim();
		
		byte[] encryptedBlock = new byte[Crypto.LENGTH_ASYM_CIPHER];
		src.get(encryptedBlock);
		byte[] plainBlock = crypto.decryptAsym(encryptedBlock, mySK);
		
		byte[] dstIDArray = new byte[LENGTH_ID];
		System.arraycopy(plainBlock, 0, dstIDArray, 0, LENGTH_ID);
		// Receiver ID from Courier
		String dstID = new String(dstIDArray).trim();
		
		byte[] srcIDArray = new byte[LENGTH_ID];
		System.arraycopy(plainBlock, LENGTH_ID, srcIDArray, 0, LENGTH_ID);
		// Sender ID from Courier
		String srcID = new String(srcIDArray).trim();
		
		// kC
		kC = new byte[LENGTH_SYMM_KEY];
		System.arraycopy(plainBlock, 2*LENGTH_ID, kC, 0, LENGTH_SYMM_KEY);
		
		int dataLength = src.getInt();
		
		byte[] encryptedMetaBlock = new byte[Crypto.LENGTH_ASYM_CIPHER];
		src.get(encryptedMetaBlock);
		
		ByteBuffer metaBlock = ByteBuffer.wrap(crypto.decryptAsym(encryptedMetaBlock, mySK));
		
		// kAB
		byte[] msgKey = new byte[LENGTH_SYMM_KEY];
		metaBlock.get(msgKey);
		
		String metaSrcID = extractString(metaBlock, 0, LENGTH_ID).trim();
		String metaDstID = extractString(metaBlock, 0, LENGTH_ID).trim();
		Long timestamp = metaBlock.getLong();
		
		// Signature for META
		byte[] metaSIGN = new byte[Crypto.LENGTH_SIGN];
		src.get(metaSIGN);
		
		byte[] encryptedMsgBlock = new byte[dataLength-Crypto.LENGTH_MAC-Crypto.LENGTH_ASYM_CIPHER-Crypto.LENGTH_SIGN];
		src.get(encryptedMsgBlock);
		
		byte[] msgMAC = new byte[Crypto.LENGTH_MAC];
		src.get(msgMAC);
		
		if (debug) {
			ui.print("SenderID=\t\t" + senderID, ID);
			ui.print("EncPKB=\t\t" + new String(encryptedBlock), ID);
		}
		
		// Validate received data ----------------------------------------------------------------
		if (!dstID.equals(ID)) {
			System.out.println(dstID);
			System.err.println("Destination ID in prefix not match");
			return -1;
		} else if (!metaDstID.equals(ID)) {
			System.err.println("Destination ID in Meta not match");
			return -1;
		} else if (!srcID.equals(metaSrcID)) {
			System.err.println("Source ID not match between prefix and Meta");
			return -1;
		} // TODO: For timestamp validation
		
		byte[] srcPK = dataManager.getPublicKey(srcID);
		if (!crypto.verifySIGN(encryptedMetaBlock, srcPK, metaSIGN)) {
			System.err.println("Meta signature not valid");
			return -1;
		}
		
		if (!crypto.verifyMACDigest(encryptedMsgBlock, msgKey, msgMAC)) {
			System.err.println("Message MAC not valid");
			return -1;
		}
		
		// Operate on received data ----------------------------------------------------------------
		byte[] plainMsg = crypto.decryptSymm(encryptedMsgBlock, msgKey);
		ui.print("Result=\t\t" + new String(plainMsg), ID);
		
		// Prepare for send data -------------------------------------------------------------------
		int totalReceivedLength = LENGTH_ID+Crypto.LENGTH_ASYM_CIPHER+Integer.SIZE/8+dataLength;
		src.rewind();
		byte[] totalReceived = new byte[totalReceivedLength];
		src.get(totalReceived);
		byte[] totalReceivedMAC = crypto.getMACDigest(totalReceived, kC);
		
		dst.clear();
		dst.put(totalReceivedMAC);
		dst.flip();
		
		terminate();
		return totalReceivedMAC.length;
	}
}