import java.nio.ByteBuffer;

public abstract class Delegate {
	protected final int LENGTH_ID = 16;
	protected final int LENGTH_SYMM_KEY = 16;
	protected final int LENGTH_ASYM_KEY = 128;
	protected boolean isAlive = true;
	
	abstract public ByteBuffer getInitialMessage();
	abstract public int process(ByteBuffer src, ByteBuffer dst);
	abstract public void setUserInterface(UserInterface ui);
	
	public boolean isAlive() {
		return isAlive;
	}
	
	public void terminate() {
		isAlive = false;
	}
	
	public static String extractString(ByteBuffer src, int offset, int length) {
		byte[] bytes = new byte[length];
		src.get(bytes, offset, length);
		return new String(bytes);
	}
	
	public byte[] wrapID(String ID) {
		byte[] returnArray = new byte[LENGTH_ID];
		System.arraycopy(ID.getBytes(), 0, returnArray, 0, ID.length());
		return returnArray;
	}
	
	public String dewrapID(byte[] bytes) {
		String returnString = new String(bytes);
		return returnString.trim();
	}
	
	static public void printByteArray(byte[] src) {
		for (int i = 0; i < src.length; i++ ) {
			System.out.print(src[i] + " ");
		}
	}
}
