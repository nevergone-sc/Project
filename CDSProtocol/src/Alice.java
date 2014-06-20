
public class Alice extends Accepter {
	protected Delegate makeDelegate() {
		Crypto crypto = new Crypto();
		DataManager dataManager = new DataManager(1000);
		dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
		dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
		dataManager.setPathData("Alice", "Bob", "Data_Alice_To_Bob(Alice)");
		return new DataCreator(crypto, dataManager);
	}
}
