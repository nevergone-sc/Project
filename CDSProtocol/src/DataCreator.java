import java.nio.ByteBuffer;

public class DataCreator extends Delegate {
	static final boolean debug = true;
	
	private final int LENGTH_SIZE = 4;
	private final int LENGTH_META = 10;
	private final int LENGTH_MSG  = 10;
	private final int DATA_SIZE = 100;
	private String ID = "ALICE";
	private String dstID = "BOB";
	private byte[] pkB;
	private byte[] skA;
	private byte[] pka = {1,2,3,4,5};
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	
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
			byte[] skA = dataManager.getPrivateKey();
			byte[] kc = crypto.decryptAsym(encryptedKc, skA);
			
			if (debug) {
				System.out.println("DataCreator-----------------------");
				System.out.println("SenderID: " + senderID);
				System.out.println("Max size: " + maxSize);
				System.out.print("EncPKA: ");
				printByteArray(kc);
				System.out.println();
			}
			
			if (!senderID.equals("COURIER")) return -1;
			if (DATA_SIZE > maxSize) return -1;
			
			byte[] kab = crypto.generateSymmKey(LENGTH_SYMM_KEY);
			
			long timestamp = System.currentTimeMillis();
			ByteBuffer metaBValueBuffer = ByteBuffer.allocate(LENGTH_SYMM_KEY+2*LENGTH_ID+Long.SIZE+Crypto.LENGTH_SIGN);
			metaBValueBuffer.put(kab);
			metaBValueBuffer.put(wrapID(ID));
			metaBValueBuffer.put(wrapID(dstID));
			metaBValueBuffer.putLong(timestamp);
			metaBValueBuffer.flip();
			byte[] metaBValue = crypto.encryptAsym(metaBValueBuffer.array(), pkB);
			byte[] metaBSign = crypto.getSIGN(metaBValue, skA);
			
			byte[] sendData = dataManager.getData(ID);
			byte[] msgBValue = crypto.encryptSymm(sendData, kab);
			byte[] msgBMAC = crypto.getMACDigest(msgBValue, kab);
			
			int metaLength = metaBValue.length + Crypto.LENGTH_SIGN;
			int msgLength = msgBValue.length + Crypto.LENGTH_MAC;
			int totalSendLength = 3*LENGTH_ID + metaLength + msgLength + 2*Integer.SIZE + Crypto.LENGTH_MAC;
			ByteBuffer sendBuffer = ByteBuffer.allocate(totalSendLength);
			sendBuffer.put(senderID.getBytes());
			sendBuffer.put(ID.getBytes());
			sendBuffer.put(dstID.getBytes());
			sendBuffer.putInt(metaLength);
			sendBuffer.put(metaBValue);
			sendBuffer.put(metaBSign);
			sendBuffer.putInt(msgLength);
			sendBuffer.put(msgBValue);
			sendBuffer.put(msgBMAC);
			
			byte[] totalSendMAC = crypto.getMACDigest(sendBuffer.array(), kc);
			
			sendBuffer.put(totalSendMAC);
			sendBuffer.flip();
			
			dst.put(sendBuffer);
			
			state = 1;
			return dst.remaining();
			
		case 1:
			if (debug) {
				System.out.println("DataCreator-----------------------");
				System.out.println("hash: " + extractString(src, 0, Crypto.LENGTH_HASH));
			}
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
