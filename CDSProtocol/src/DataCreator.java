import java.nio.ByteBuffer;

public class DataCreator extends Delegate {
	private static final boolean debug = true;
	
	private String ID = "Alice";
	private String dstID = "Bob";
	private byte[] dstPK;
	private byte[] mySK;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private ByteBuffer sentMessage;
	
	
	public DataCreator(Crypto c, DataManager dm) {
		crypto = c;
		dataManager = dm;
		mySK = dataManager.getPrivateKey();
		dstPK = dataManager.getPublicKey(dstID);
	}
	
	public ByteBuffer getInitialMessage() {
		return null;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 0:
			return getMessage1(src, dst);
			
		case 1:
			return getMessage2(src, dst);
			
		default:
			return -1;
		}
	}
	
	protected int getMessage1(ByteBuffer src, ByteBuffer dst) {
		// Retrieve received data-----------------------------------------------------------
		String senderID = extractString(src, 0, LENGTH_ID).trim();
		int availableStorage = src.getInt();
		
		int encryptedLength = src.getInt();
		byte[] encryptedKc = new byte[encryptedLength];
		src.get(encryptedKc);
		mySK = dataManager.getPrivateKey();
		byte[] kc = crypto.decryptAsym(encryptedKc, mySK);
		
		if (debug) {
			System.out.println("DataCreator-----------------------");
			System.out.println("SenderID: " + senderID);
			System.out.println("Max size: " + availableStorage);
			System.out.print("kC: ");
			printByteArray(kc);
			System.out.println();
		}
					
		// Validate received data-------------------------------------------------------------------------
		if (dataManager.maxStorage() < availableStorage) return -1;  
		//if (!senderID.equals("COURIER")) return -1;
					
		// Prepare send data------------------------------------------------------------------------------
		byte[] kab = crypto.generateSymmKey(LENGTH_SYMM_KEY*8);
					
		long timestamp = System.currentTimeMillis();
		ByteBuffer metaBValueBuffer = ByteBuffer.allocate(LENGTH_SYMM_KEY+2*LENGTH_ID+Long.SIZE/8);
		metaBValueBuffer.put(kab);
		metaBValueBuffer.put(wrapID(ID));
		metaBValueBuffer.put(wrapID(dstID));
		metaBValueBuffer.putLong(timestamp);
		metaBValueBuffer.flip();

		byte[] metaBValue = crypto.encryptAsym(metaBValueBuffer.array(), dstPK);
		byte[] metaBSign = crypto.getSIGN(metaBValue, mySK);
					
		byte[] sendData = dataManager.getData(ID, dstID);
		byte[] msgBValue = crypto.encryptSymm(sendData, kab);
		byte[] msgBMAC = crypto.getMACDigest(msgBValue, kab);
				
		int metaLength = metaBValue.length + Crypto.LENGTH_SIGN;
		int msgLength = msgBValue.length + Crypto.LENGTH_MAC;
		int totalMsgLength = 3*LENGTH_ID + metaLength + msgLength + 2*Integer.SIZE/8;
		ByteBuffer totalMsgBuffer = ByteBuffer.allocate(totalMsgLength);
		totalMsgBuffer.put(wrapID(senderID));
		totalMsgBuffer.put(wrapID(ID));
		totalMsgBuffer.put(wrapID(dstID));
		totalMsgBuffer.putInt(metaLength);
		totalMsgBuffer.put(metaBValue);
		totalMsgBuffer.put(metaBSign);
		totalMsgBuffer.putInt(msgLength);
		totalMsgBuffer.put(msgBValue);
		totalMsgBuffer.put(msgBMAC);
		totalMsgBuffer.flip();
					
		byte[] totalMsgMAC = crypto.getMACDigest(totalMsgBuffer.array(), kc);
		
		ByteBuffer totalSendBuffer = ByteBuffer.allocate(totalMsgLength + Crypto.LENGTH_MAC);
		totalSendBuffer.put(totalMsgBuffer);
		totalSendBuffer.put(totalMsgMAC);
		totalSendBuffer.flip();
		sentMessage = totalSendBuffer; 
		
		dst.clear();
		dst.put(totalSendBuffer);
		dst.flip();
		
		state = 1;
		return totalMsgLength + Crypto.LENGTH_MAC;
	}
	
	protected int getMessage2(ByteBuffer src, ByteBuffer dst) {
		byte[] hashDigest = new byte[Crypto.LENGTH_HASH];
		src.get(hashDigest);
		boolean isHashValid = crypto.verifyHashDigest(sentMessage.array(), hashDigest);
		
		if (debug) {
			System.out.println("DataCreator-----------------------");
			System.out.println("hash: " + new String(hashDigest));
			System.out.println("Verify Hash Digest: " + isHashValid);
		}
		
		if (!isHashValid) return -1;
		terminate();
		return 0;
	}
}
