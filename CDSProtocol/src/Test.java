import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.MessageDigest;


public class Test {
	public static void main(String args[]) throws Exception {  
		/*
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
		c.setDstPort(8888);
		c.start();
		
		Initializer c2 = new CourierA();
		c2.setDstPort(8888);
		c2.start();
		*/
		
		Crypto c = new Crypto();
		byte[] plaintext = "Here is your message!".getBytes();
		KeyPair keyPair = c.generateAsymKey(1024);
		
		
		byte[] ciphertext = c.encryptAsym(plaintext, keyPair.getPublic().getEncoded());
		byte[] decrypted = c.decryptAsym(ciphertext, keyPair.getPrivate().getEncoded());
		
		MessageDigest mg = MessageDigest.getInstance("SHA-256");
		mg.update((byte) 2);
		mg.update((byte) 3);
		byte[] hash = mg.digest();
		System.out.println(new String(hash));
		
		byte[] input = {(byte) 2, (byte) 3};
		System.out.println(c.verifyHashDigest(input, hash));
		
		byte[] a = {1};
		byte[] b = {1};
		System.out.println(a.equals(b));
	}
}