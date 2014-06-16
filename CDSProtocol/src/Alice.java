
public class Alice extends Accepter {
	protected Delegate makeDelegate() {
		Crypto crypto = new Crypto();
		DataManager dataManager = new DataManager();
		dataManager.setPathPublicKey("ALICE", "PublicKey_Alice");
		dataManager.setPathPublicKey("BOB", "PublicKey_Bob");
		dataManager.setPathData("ALICE", "Data_Alice");
		return new DataCreator(crypto, dataManager);
	}
}
