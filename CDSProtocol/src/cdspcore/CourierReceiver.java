package cdspcore;

import java.io.IOException;
import java.nio.ByteBuffer;

/* Delegate of a courierReceiver, defines how data from a dataCreator is checked
 */
public class CourierReceiver extends Delegate {
	static final boolean debug = true;
	
	private String ID;
	private String senderID;
	private int state = 0;
	private Crypto crypto;
	private DataManager dataManager;
	private UserInterface ui;
	
	private byte[] kc;
	private byte[] senderSK;
	private String dstID = "";
	private byte[] meta, msg, totalMAC = null;
	
	public CourierReceiver(String id, UserInterface ui, Crypto c, DataManager dm, String sedID) {
		ID = id;
		senderID = sedID;
		crypto = c;
		dataManager = dm;
		this.ui = ui;
		
		try {
			senderSK = dataManager.getPublicKey(senderID);
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
		senderID = new String(getShortBlock(src));
		dstID = new String(getShortBlock(src));

		meta = getLongBlock(src);
		int metaLength = meta.length;
		
		msg = getLongBlock(src);
		int msgLength = msg.length;
		
		totalMAC = new byte[Crypto.LENGTH_MAC];
		src.get(totalMAC);

		int receivedLength = senderID.length() + dstID.length() + 2*Byte.SIZE/8 + metaLength + msgLength + 2*Integer.SIZE/8;
		
		if (debug) {
			ui.print(senderID, "Sender=\t", ID);
			ui.print(dstID, "Recipient=\t", ID);
			ui.print(String.valueOf(metaLength), "META length=\t", ID);
			ui.print(String.valueOf(msgLength), "MSG length=\t", ID);
		}
					
		// Validate received data -----------------------------------------------------------------------		
		byte[] messageReceived = new byte[receivedLength];
		src.reset();
		src.get(messageReceived);
		if (!crypto.verifyMACDigest(messageReceived, kc, totalMAC)) {
			ui.printErr("MAC Verified False", ID);
			terminate();
			return -1;
		}
		
		// Operate received data ------------------------------------------------------------------------
		ByteBuffer dataToSave = ByteBuffer.allocate(metaLength+msgLength+2*Integer.SIZE/8);
		dataToSave.putInt(metaLength);
		dataToSave.put(meta);
		dataToSave.putInt(msgLength);
		dataToSave.put(msg);
				
		// Prepare send data ----------------------------------------------------------------------------
		src.reset();
		byte[] totalReceived = new byte[receivedLength + Crypto.LENGTH_MAC];
		src.get(totalReceived);
		dst.clear();
		dst.put(crypto.getMACDigest(totalReceived, kc));
		dst.flip();
		
		// Save received data to local file
		try {
			dataManager.putData(dataToSave.array(), dstID);
		} catch (IOException e) {
			ui.printErr(e.getMessage(), ID);
			terminate();
			return -1;
		}

		terminate();
		ui.nextStep("", ID);
		return Crypto.LENGTH_HASH;
	}
}