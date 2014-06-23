
public class Bob extends Accepter {
	protected Delegate makeDelegate(UserInterface ui) {
		ui.setTag("Bob");
		Crypto c = new Crypto();
		DataManager dm = new DataManager(1000);
		dm.setPathPrivateKey("PrivateKey_Bob");
		dm.setPathPublicKey("Alice", "PublicKey_Alice");
		
		Delegate dataReceiver = new DataReceiver(c, dm);
		dataReceiver.setUserInterface(ui);
		return dataReceiver;
	}
}
