
public class TestSubmit {
	public static void main(String args[]) throws Exception {  
		///*
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				Crypto crypto = new Crypto();
				
				DataManager dataManager = new DataManager(1000);
				dataManager.setPathPrivateKey("PrivateKey_Alice");
				dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
				dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
				dataManager.setPathData("Alice", "Bob", "Data_Alice_To_Bob(Alice)");
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
				Crypto crypto = new Crypto();
				
				DataManager dataManager = new DataManager(1000);
				dataManager.setPathPrivateKey("PrivateKey_Bob");
				dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
				
				UserInterface ui = new ConsoleUserInterface();
				
				Delegate dataReceiver = new DataReceiver(crypto, dataManager);
				dataReceiver.setUserInterface(ui);
				
				Accepter bob = new Accepter("localhost", 9888, dataReceiver, ui);
				bob.setUserInterface(ui);
				bob.setListeningPort(9888);
				
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
		
		Crypto c = new Crypto();
		DataManager dm = new DataManager(1000);
		dm.setPathPublicKey("Bob", "PublicKey_Bob");
		dm.setPathData("Alice", "Bob", "Data_Alice_To_Bob");
		
		UserInterface ui = new ConsoleUserInterface();
		
		Delegate sendCourier = new SendCourier(c, dm);
		sendCourier.setUserInterface(ui);
		
		Initializer courierB = new Initializer("", 9888, sendCourier, ui);
		courierB.setDstPort(9888);
		ui.start(courierB);
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