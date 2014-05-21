import java.nio.ByteBuffer;


public class SendCourier extends Delegate {
	static final boolean debug = true;
	
	private final int LENGTH_ENC_KEY = 5;
	private final int LENGTH_META = 5;
	private final int LENGTH_MSG  = 5;
	private final int LENGTH_MAC  = 5;
	
	private String ID = "COURIER2";
	private byte[] encPKB = {0,1,2,3,4};
	private int state = 0;
	
	public ByteBuffer getInitialMessage() {
		byte[] metab = {'m','e','t','a','b'};
		byte[] msgb = {'m','s','g','b', ' '};
		
		ByteBuffer returnBuffer = ByteBuffer.allocate(LENGTH_ID + LENGTH_ENC_KEY + LENGTH_META + LENGTH_MSG + LENGTH_MAC);
		returnBuffer.put(wrapID(ID));
		returnBuffer.put(encPKB);
		returnBuffer.put(metab);
		returnBuffer.put(msgb);
		returnBuffer.flip();
		
		return returnBuffer;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		if (!isAlive) return 0;
		switch (state) {
		case 0: 
			if (debug) {
				System.out.println("SendCourier--------------");
				System.out.println("MAC: " + extractString(src, 0, LENGTH_MAC));
			}
			terminate();
			return 0;
			
		default:
			return -1;
		}
	}
}
