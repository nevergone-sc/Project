
public class Alice extends Accepter {
	private String ID;
	private String dstID;
	private Crypto crypto;
	private DataManager dataManager;
	
	public Alice(String addrs, int port, UserInterface ui, Crypto c, DataManager dm, String id, String dstID) {
		super(addrs, port, ui);
		ID = id;
		this.dstID = dstID;
		crypto = c;
		dataManager = dm;
	}
	
	protected Delegate makeDelegate() {
		ui.setTag("Alice");
		Delegate dataCreator = new DataCreator(ID, ui, crypto, dataManager, dstID);
		return dataCreator;
	}
}
