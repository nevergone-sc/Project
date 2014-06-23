
public class Alice extends Accepter {
	protected Delegate makeDelegate(UserInterface ui) {
		ui.setTag("Alice");
		Crypto crypto = new Crypto();
		
		DataManager dataManager = new DataManager(1000);
		dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
		dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
		dataManager.setPathData("Alice", "Bob", "Data_Alice_To_Bob(Alice)");
		
		Delegate dataCreator = new DataCreator(crypto, dataManager);
		dataCreator.setUserInterface(ui);
		return dataCreator;
	}
}
