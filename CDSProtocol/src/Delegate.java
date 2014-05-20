import java.nio.ByteBuffer;

public abstract class Delegate {
	protected int LENGTH_ID = 16;
	protected boolean isAlive = true;
	
	abstract public ByteBuffer getInitialMessage();
	abstract public int process(ByteBuffer src, ByteBuffer dst);
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public static String extractString(ByteBuffer src, int offset, int length) {
		byte[] bytes = new byte[length];
		src.get(bytes, offset, length);
		return new String(bytes);
	}
	
	protected byte[] wrapID(String ID) {
		byte[] returnArray = new byte[LENGTH_ID];
		System.arraycopy(ID.getBytes(), 0, returnArray, 0, ID.length());
		return returnArray;
	}
	
	static protected void printByteArray(byte[] src) {
		for (int i = 0; i < src.length; i++ ) {
			System.out.print(src[i] + " ");
		}
	}
	
	protected void terminate() {
		isAlive = false;
	}
}
