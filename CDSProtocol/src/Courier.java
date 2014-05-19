import java.nio.ByteBuffer;


public class Courier extends Delegate {
	private final int LENGTH_SIZE = 4;
	private final int LENGTH_ENC_KEY = 10;
	private final int LENGTH_META = 10;
	private final int LENGTH_MSG  = 10;
	private final int LENGTH_MAC  = 10;
	private final int MAX_STORAGE = 100;
	private String ID = "COURIER";
	private byte[] pka = {0,1,2,3,4,5,6,7,8,9};
	
	public ByteBuffer getInitialMessage() {
		ByteBuffer returnBuffer = ByteBuffer.allocate(LENGTH_ID+LENGTH_SIZE+LENGTH_ENC_KEY);
		returnBuffer.put(ID.getBytes());
		returnBuffer.putInt(MAX_STORAGE);
		returnBuffer.put(pka);
		returnBuffer.flip();
		return returnBuffer;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		String block1 = extractString(src, 0, LENGTH_ID);
		String block2 = extractString(src, 0, LENGTH_ID);
		String block3 = extractString(src, 0, LENGTH_ID);
		String block4 = extractString(src, 0, LENGTH_META);
		String block5 = extractString(src, 0, LENGTH_MSG);
		String block6 = extractString(src, 0, LENGTH_MAC);
		System.out.println(block1);
		System.out.println(block2);
		System.out.println(block3);
		System.out.println(block4);
		System.out.println(block5);
		System.out.println(block6);
		
		dst.clear();
		dst.put(block6.toUpperCase().getBytes());
		dst.flip();
		return block6.length();
	}
}