import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public class Test {
	public static void main(String args[]) {  

		ByteBuffer bb = ByteBuffer.allocate(20);
		ByteBuffer b = ByteBuffer.wrap("1234567890123456789".getBytes());
		Delegate c = new Courier();
		System.out.println(c.process(b, bb));		
	}
}
