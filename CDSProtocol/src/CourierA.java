
public class CourierA extends Initializer {
	protected Delegate makeDelegate(UserInterface ui) {
		Crypto crypto = new Crypto();
		DataManager dataManager = new DataManager(1000);
		dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
		
		
		Delegate receiveCourier = new ReceiveCourier(crypto, dataManager);
		receiveCourier.setUserInterface(ui);
		return receiveCourier;
	}

}
