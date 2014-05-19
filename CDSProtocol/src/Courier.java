import java.nio.ByteBuffer;


public class Courier extends Delegate {
	public ByteBuffer getInitialMessage() {
		String message = "Hello world";
		ByteBuffer returnBuffer = ByteBuffer.wrap(message.getBytes());
		return returnBuffer;
	}
	
	public int process(ByteBuffer src, ByteBuffer dst) {
		String returnString = extractString(src, 0, LENGTH_NAME);
		System.out.println(returnString);
		
		
		
		
		
		
		dst = ByteBuffer.wrap(returnString.getBytes());
		return dst.limit();
	}
}