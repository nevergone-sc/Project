import java.nio.ByteBuffer;

public class DataCreator extends Delegate {
	private static final boolean debug = true;
	
	private String ID;
	private String dstID;
	private byte[] dstPK;
	private byte[] mySK;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private ByteBuffer sentMessage;
	private UserInterface ui;
	
	private byte[] kc;
	
	public DataCreator(String id, Crypto c, DataManager dm, String dstID) {
		ID = id;
		this.dstID = dstID; 
		crypto = c;
		dataManager = dm;
		mySK = dataManager.getPrivateKey();
		dstPK = dataManager.getPublicKey(dstID);
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
			
		case 1:
			return getMessage2(src, dst);
			
		default:
			return -1;
		}
	}
	
	protected int getMessage1(ByteBuffer src, ByteBuffer dst) {
		// Retrieve received data-----------------------------------------------------------
		String senderID = new String(getShortBlock(src));
		int availableStorage = src.getInt();
		
		byte[] encryptedKc = getLongBlock(src);
		mySK = dataManager.getPrivateKey();
		kc = crypto.decryptAsym(encryptedKc, mySK);
		
		if (debug) {
			//System.out.println("DataCreator-----------------------");
			ui.print(senderID, "SenderID=\t", ID);
			ui.print(String.valueOf(availableStorage), "Max size=\t", ID);
			ui.print(kc, "kC=\t\t", ID);
		}
					
		// Validate received data-------------------------------------------------------------------------
		if (dataManager.maxStorage() < availableStorage) return -1;  
		//if (!senderID.equals("COURIER")) return -1;
					
		// Prepare send data------------------------------------------------------------------------------
		byte[] kab = crypto.generateSymmKey(LENGTH_SYMM_KEY*8);
					
		long timestamp = System.currentTimeMillis();
		ByteBuffer metaBValueBuffer = ByteBuffer.allocate(LENGTH_SYMM_KEY+ID.length()+dstID.length()+2*Byte.SIZE/8+Long.SIZE/8);
		metaBValueBuffer.put(kab);
		putShortBlock(ID.getBytes(), metaBValueBuffer);
		putShortBlock(dstID.getBytes(), metaBValueBuffer);
		metaBValueBuffer.putLong(timestamp);
		metaBValueBuffer.flip();

		byte[] metaBValue = crypto.encryptAsym(metaBValueBuffer.array(), dstPK);
		byte[] metaBSign = crypto.getSIGN(metaBValue, mySK);
		byte[] metaEncryptedSign = crypto.encryptSymm(metaBSign, kab);
					
		byte[] sendData = dataManager.getData(dstID);
		byte[] msgBValue = crypto.encryptSymm(sendData, kab);
		byte[] msgBMAC = crypto.getMACDigest(msgBValue, kab);
				
		int nameBlockLength = ID.length() + dstID.length() + 2*Byte.SIZE/8;
		int metaLength = metaBValue.length + metaEncryptedSign.length;
		int msgLength = msgBValue.length + Crypto.LENGTH_MAC;
		int totalMsgLength = nameBlockLength + metaLength + msgLength + 2*Integer.SIZE/8;
		ByteBuffer totalMsgBuffer = ByteBuffer.allocate(totalMsgLength);
		putShortBlock(ID.getBytes(), totalMsgBuffer);
		putShortBlock(dstID.getBytes(), totalMsgBuffer);
		totalMsgBuffer.putInt(metaLength);
		totalMsgBuffer.put(metaBValue);
		totalMsgBuffer.put(metaEncryptedSign);
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
		ui.nextStep("", ID);
		return totalMsgLength + Crypto.LENGTH_MAC;
	}
	
	protected int getMessage2(ByteBuffer src, ByteBuffer dst) {
		byte[] macDigest = new byte[Crypto.LENGTH_MAC];
		src.get(macDigest);
		boolean isHashValid = crypto.verifyMACDigest(sentMessage.array(), kc, macDigest);
		
		if (debug) {
			ui.print(macDigest, "MAC=\t\t", ID);
			ui.print(String.valueOf(isHashValid), "Verify MAC=\t", ID);
		}
		
		if (!isHashValid) return -1;
		terminate();
		ui.nextStep("", ID);
		return 0;
	}
}
