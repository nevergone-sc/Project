import java.nio.ByteBuffer;


public class DataReceiver extends Delegate {
	static final boolean debug = true;
	
	private final int LENGTH_SIZE = 4;
	private final int LENGTH_ENC_KEY = 5;
	private final int LENGTH_META = 5;
	private final int LENGTH_MSG  = 5;
	private final int LENGTH_MAC  = 5;
	private final int MAX_STORAGE = 1000;
	private String ID = "BOB";
	private byte[] pkb = {2,3,4,5,6};
	private int state = 0;
	
	public ByteBuffer getInitialMessage() {
		return null;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 0:
			String senderID = extractString(src, 0, LENGTH_ID);
			byte[] encPKB = new byte[LENGTH_ENC_KEY];
			src.get(encPKB);
			String metab = extractString(src, 0, LENGTH_META);
			String msgb = extractString(src, 0, LENGTH_MSG);
			
			if (debug) {
				System.out.println("DataReceiver---------------------");
				System.out.println("SenderID:\t" + senderID);
				System.out.print("EncPKB:\t");
				printByteArray(encPKB);
				System.out.println();
				System.out.println("metab:\t" + metab);
				System.out.println("msgb:\t" + msgb);
			}
			
			byte[] mackc = {'m','a','c','k','c'};
			dst.put(mackc);
			dst.flip();
			
			terminate();
			return mackc.length;
			
		default:
			return -1;
		}
	}

}
