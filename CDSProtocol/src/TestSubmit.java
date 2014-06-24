import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.Signature;


public class TestSubmit {
	public static void main(String args[]) throws Exception {  
		///*
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				Accepter alice = new Alice();
				alice.setListeningPort(8888);
				UserInterface ui = new ConsoleUserInterface();
				ui.start(alice);
			}
		});
		t1.start();
		
		Initializer courierA = new CourierA();
		courierA.setDstPort(8888);
		UserInterface ui2 = new ConsoleUserInterface();
		ui2.start(courierA);
		//*/
		
		/*
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				Accepter bob = new Bob();
				bob.setListeningPort(9888);
				UserInterface ui = new ConsoleUserInterface();
				ui.start(bob);
				try {
					bob.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t2.start();
		
		Initializer courierB = new CourierB();
		courierB.setDstPort(9888);
		UserInterface ui2 = new ConsoleUserInterface();
		ui2.start(courierB);
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