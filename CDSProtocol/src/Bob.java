
public class Bob extends Accepter {
	protected Delegate makeDelegate() {
		Crypto c = new Crypto();
		DataManager dm = new DataManager(1000);
		dm.setPathPrivateKey("PrivateKey_Bob");
		dm.setPathPublicKey("Alice", "PublicKey_Alice");
		return new DataReceiver(c, dm);
	}
}
