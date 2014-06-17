import java.nio.ByteBuffer;

public class DataCreator extends Delegate {
	static final boolean debug = true;
	
	private final int DATA_SIZE = 100;
	private String ID = "ALICE";
	private String dstID = "BOB";
	private byte[] pkB;
	private byte[] skA;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private ByteBuffer sentMessage = null;
	
	
	public DataCreator(Crypto c, DataManager dm) {
		crypto = c;
		dataManager = dm;
		skA = dataManager.getPrivateKey();
		pkB = dataManager.getPublicKey("BOB");
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 0:
			String senderID = extractString(src, 0, LENGTH_ID).trim();
			int maxSize = src.getInt();
			
			int encryptedLength = src.getInt();
			byte[] encryptedKc = new byte[encryptedLength];
			src.get(encryptedKc);
			skA = dataManager.getPrivateKey();
			byte[] kc = crypto.decryptAsym(encryptedKc, skA);
			
			if (debug) {
				System.out.println("DataCreator-----------------------");
				System.out.println("SenderID: " + senderID);
				System.out.println("Max size: " + maxSize);
				System.out.print("kC: ");
				printByteArray(kc);
				System.out.println();
			}
			
			if (!senderID.equals("COURIER")) return -1;
			if (DATA_SIZE > maxSize) return -1;
			
			byte[] kab = crypto.generateSymmKey(LENGTH_SYMM_KEY*8);
			
			long timestamp = System.currentTimeMillis();
			ByteBuffer metaBValueBuffer = ByteBuffer.allocate(LENGTH_SYMM_KEY+2*LENGTH_ID+Long.SIZE/8);
			metaBValueBuffer.put(kab);
			metaBValueBuffer.put(wrapID(ID));
			metaBValueBuffer.put(wrapID(dstID));
			metaBValueBuffer.putLong(timestamp);
			metaBValueBuffer.flip();

			//System.out.println(metaBValueBuffer.array().length);
			byte[] metaBValue = crypto.encryptAsym(metaBValueBuffer.array(), pkB);
			byte[] metaBSign = crypto.getSIGN(metaBValue, skA);
			
			byte[] sendData = dataManager.getData(ID);
			byte[] msgBValue = crypto.encryptSymm(sendData, kab);
			byte[] msgBMAC = crypto.getMACDigest(msgBValue, kab);
			
			int metaLength = metaBValue.length + Crypto.LENGTH_SIGN;
			int msgLength = msgBValue.length + Crypto.LENGTH_MAC;
			int totalSendLength = 3*LENGTH_ID + metaLength + msgLength + 2*Integer.SIZE/8 + Crypto.LENGTH_MAC;
			ByteBuffer sendBuffer = ByteBuffer.allocate(totalSendLength);
			sendBuffer.put(wrapID(senderID));
			sendBuffer.put(wrapID(ID));
			sendBuffer.put(wrapID(dstID));
			sendBuffer.putInt(metaLength);
			sendBuffer.put(metaBValue);
			sendBuffer.put(metaBSign);
			sendBuffer.putInt(msgLength);
			sendBuffer.put(msgBValue);
			sendBuffer.put(msgBMAC);
			
			byte[] totalSendMAC = crypto.getMACDigest(sendBuffer.array(), kc);
			
			sendBuffer.put(totalSendMAC);
			sendBuffer.flip();
			sentMessage = sendBuffer;
			
			dst.put(sendBuffer);
			dst.flip();
			
			state = 1;
			return sendBuffer.capacity();
			
		case 1:
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
			
		default:
			return -1;
		}
	}

	public ByteBuffer getInitialMessage() {
		return null;
	}
}
