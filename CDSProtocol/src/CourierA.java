
public class CourierA extends Initializer {
	protected Delegate makeDelegate() {
		Crypto crypto = new Crypto();
		DataManager dataManager = new DataManager();
		dataManager.setPathPublicKey("ALICE", "PublicKey_Alice");
		return new ReceiveCourier(crypto, dataManager);
	}
}
