import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public class Test {
	public static void main(String args[]) throws Exception {  
		/*
		Delegate delegate = new Courier();
		ByteBuffer bb = ByteBuffer.allocate(20);
		ByteBuffer initMsg = delegate.getInitialMessage();
		System.out.println(Delegate.extractString(initMsg, 0, 7));
		System.out.println(initMsg.getInt());
		byte[] pka = new byte[initMsg.remaining()];
		initMsg.get(pka);
		System.out.println(pka[0]);
		System.out.println(pka[1]);
		System.out.println(pka[2]);
		System.out.println(pka[9]);
		
		*/
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				Accepter r = new Alice();
				try {
					r.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t1.start();
		
		
		Initializer c = new CourierA();
		c.start();
		
		Initializer c2 = new CourierA();
		c2.start();
	}
}