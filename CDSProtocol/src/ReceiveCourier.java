import java.nio.ByteBuffer;


public class ReceiveCourier extends Delegate {
	static final boolean debug = true;
	
	private final int LENGTH_SIZE = 4;
	private final int LENGTH_ENC_KEY = 5;
	private final int LENGTH_META = 5;
	private final int LENGTH_MSG  = 5;
	private final int LENGTH_MAC  = 5;
	private final int MAX_STORAGE = 1000;
	private String ID = "COURIER";
	private byte[] encPKA = {0,1,2,3,4};
	private int state = 0;
	
	public ByteBuffer getInitialMessage() {
		ByteBuffer returnBuffer = ByteBuffer.allocate(LENGTH_ID+LENGTH_SIZE+LENGTH_ENC_KEY);
		returnBuffer.put(wrapID(ID));
		returnBuffer.putInt(MAX_STORAGE);
		returnBuffer.put(encPKA);
		returnBuffer.flip();
		return returnBuffer;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 0:
			String block1 = extractString(src, 0, LENGTH_ID);
			String block2 = extractString(src, 0, LENGTH_ID);
			String block3 = extractString(src, 0, LENGTH_ID);
			String block4 = extractString(src, 0, LENGTH_META);
			String block5 = extractString(src, 0, LENGTH_MSG);
			String block6 = extractString(src, 0, LENGTH_MAC);
			
			if (debug) {
				System.out.println("ReceiveCourier---------------------");
				System.out.println("Receiver: " + block1);
				System.out.println("Sender: " + block2);
				System.out.println("Recipient: " + block3);
				System.out.println("MetaB: " + block4);
				System.out.println("MsgB: " + block5);
				System.out.println("Mac: " + block6);
			}
			
			dst.clear();
			dst.put(block6.toUpperCase().getBytes());
			dst.flip();
			
			terminate();
			return block6.length();
		
		default:
			return -1;
		}
		
	}
}