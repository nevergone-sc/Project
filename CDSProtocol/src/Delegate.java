import java.nio.ByteBuffer;

public abstract class Delegate {
	protected int LENGTH_ID = 16;
	
	abstract public ByteBuffer getInitialMessage();
	abstract public int process(ByteBuffer src, ByteBuffer dst);
	
	public static String extractString(ByteBuffer src, int offset, int length) {
		byte[] bytes = new byte[length];
		src.get(bytes, offset, length);
		return new String(bytes);
	}
}
