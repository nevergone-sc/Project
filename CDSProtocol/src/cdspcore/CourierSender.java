package cdspcore;

import java.io.IOException;
import java.nio.ByteBuffer;

/* Delegate of a courierSender, defines how data is transferred to dataReceiver
 */
public class CourierSender extends Delegate {
	static final boolean debug = true;
	
	private String ID;
	private String dstID;
	private byte[] receiverPK;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private ByteBuffer sentBuffer;
	private UserInterface ui;
	
	byte[] kC;
	
	public CourierSender(String id, UserInterface ui, Crypto c, DataManager dm, String revID) {
		ID = id;
		dstID = revID;
		crypto = c;
		dataManager = dm;
		this.ui = ui;
		
		try {
			receiverPK = dm.getPublicKey(dstID);
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
		kC = crypto.generateSymmKey(LENGTH_SYMM_KEY*8);
		ByteBuffer encryptedBuffer = ByteBuffer.allocate(LENGTH_SYMM_KEY);
		encryptedBuffer.put(kC);
		byte[] encryptedBlock = crypto.encryptAsym(encryptedBuffer.array(), receiverPK);
		
		ByteBuffer dataToSendBuffer;
		try {
			byte[] data = dataManager.getData(dstID);
			dataToSendBuffer = ByteBuffer.wrap(data);
		} catch (IOException e) {
			ui.printErr(e.getMessage(), ID);
			terminate();
			return null;
		}

		int totalMsgLength = ID.length() + Crypto.LENGTH_ASYM_CIPHER + Byte.SIZE/8 + dataToSendBuffer.limit();
		ByteBuffer totalMsgBuffer = ByteBuffer.allocate(totalMsgLength);
		putShortBlock(ID.getBytes(), totalMsgBuffer);
		totalMsgBuffer.put(encryptedBlock);
		totalMsgBuffer.put(dataToSendBuffer);
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
			
			// Erase all data that has been transmitted
			dataManager.deleteData(dstID);

			terminate();
			ui.nextStep("", ID);
			return 0;
			
		default:
			return -1;
		}
	}
}