import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.Key;
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
		byte[] key = c.generateSymmKey(256);
		byte[] key2 = c.generateSymmKey(256);
		KeyPair keyPair = c.generateAsymKey(1024);
		
		
		byte[] ciphertext = c.encryptAsym(plaintext, keyPair.getPublic().getEncoded());
		byte[] decrypted = c.decryptAsym(ciphertext, keyPair.getPrivate().getEncoded());
		
		byte[] digest = c.getMACDigest(plaintext, key2);
		System.out.println(c.verifyMACDigest(plaintext, key2, digest));
	}
}