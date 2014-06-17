import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.Signature;


public class Test {
	public static void main(String args[]) throws Exception {  

		Thread t1 = new Thread(new Runnable() {
			public void run() {
				Accepter r = new Alice();
				r.setListeningPort(8888);
				try {
					r.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t1.start();
		
		Initializer i = new CourierA();
		i.setDstPort(8888);
		i.start();
		/*
		Initializer i2 = new CourierA();
		i2.setDstPort(8888);
		i2.start();
		*/
		
		/*
		DataManager dm = new DataManager();
		Crypto c = new Crypto();
		KeyPair kp = c.generateAsymKey(1024);
		//dm.setPathPublicKey("ALICE", "PublicKey_Alice");
		//dm.setPathPrivateKey("PrivateKey_Alice");
		dm.setPathPublicKey("BOB", "PublicKey_Bob");
		dm.setPathPrivateKey("PrivateKey_Bob");
		//dm.putPublicKey(kp.getPublic().getEncoded(), "BOB");
		//dm.putPrivateKey(kp.getPrivate().getEncoded());
		


		byte[] plaintext = "Here is youafasfasfsfafasfr mesfaskdjfjhaksdjflassage!!".getBytes();
		byte[] plaintext2 = "Here is not your message!".getBytes();
		byte[] key = c.generateSymmKey(128);
		byte[] key2 = c.generateSymmKey(128);
		KeyPair keyPair = c.generateAsymKey(1024);
		byte[] publicKey = dm.getPublicKey("BOB");
		byte[] privateKey = dm.getPrivateKey();
		
		byte[] ciphertext = c.getMACDigest(plaintext, key);
		System.out.println(ciphertext.length);
		*/
	}
}