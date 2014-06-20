
public class CourierA extends Initializer {
	protected Delegate makeDelegate() {
		Crypto crypto = new Crypto();
		DataManager dataManager = new DataManager(1000);
		dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
		return new ReceiveCourier(crypto, dataManager);
	}
}
