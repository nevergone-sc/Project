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
	
	public byte[] getShortBlock(ByteBuffer src) {
		byte length = src.get();
		byte[] returnBlock = new byte[length];
		src.get(returnBlock);
		return returnBlock;
	}
	
	public int putShortBlock(byte[] src, ByteBuffer dst) {
		byte length = (byte) src.length;
		dst.put(length);
		dst.put(src);
		return length + Byte.SIZE/8;
	}
	
	public byte[] getLongBlock(ByteBuffer src) {
		int length = src.getInt();
		byte[] returnBlock = new byte[length];
		src.get(returnBlock);
		return returnBlock;
	}
	
	public int putLongBlock(byte[] src, ByteBuffer dst) {
		int length = src.length;
		dst.putInt(length);
		dst.put(src);
		return length + Integer.SIZE/8;
	}
	
	static public void printByteArray(byte[] src) {
		for (int i = 0; i < src.length; i++ ) {
			System.out.print(src[i] + " ");
		}
	}
}
