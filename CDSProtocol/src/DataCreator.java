import java.nio.ByteBuffer;

public class DataCreator extends Delegate {
	static final boolean debug = true;
	
	private final int LENGTH_SIZE = 4;
	private final int LENGTH_ENC_KEY = 10;
	private final int LENGTH_META = 10;
	private final int LENGTH_MSG  = 10;
	private final int LENGTH_MAC  = 10;
	private final int LENGTH_HASH = 5;
	private final int DATA_SIZE = 100;
	private String ID = "ALICE";
	private byte[] pka = {1,2,3,4,5};
	private int state = 0;
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 0:
			String extractedID = extractString(src, 0, LENGTH_ID);
			String senderID = extractedID.substring(0, 7);
			int maxSize = src.getInt();
			byte[] encPKA = new byte[LENGTH_ENC_KEY];
			src.get(encPKA);
			
			if (debug) {
				System.out.println("DataCreator-----------------------");
				System.out.println("SenderID: " + senderID);
				System.out.println("Max size: " + maxSize);
				System.out.print("EncPKA: ");
				printByteArray(encPKA);
				System.out.println();
			}
			
			if (!senderID.equals("COURIER")) return -1;
			if (DATA_SIZE > maxSize) return -1;
			
			byte[] kab = {'k','a','b',' ', ' '};
			byte[] metab = {'m','e','t','a','b'};
			byte[] msgb = {'m','s','g','b', ' '};
			byte[] mackc = {'m','a','c','k','c'};
			
			dst.put("COURIER         ".getBytes());
			dst.put("ALICE           ".getBytes());
			dst.put("BOB             ".getBytes());
			dst.put(metab);
			dst.put(msgb);
			dst.put(mackc);
			dst.flip();
			
			state = 1;
			return dst.remaining();
			
		case 1:
			if (debug) {
				System.out.println("DataCreator-----------------------");
				System.out.println("hash: " + extractString(src, 0, LENGTH_HASH));
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
