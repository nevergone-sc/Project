package cdspcore;


public class Bob extends Accepter {
	private String ID;
	private Crypto crypto;
	private DataManager dataManager;
	
	public Bob(String addrs, int port, UserInterface ui, Crypto c, DataManager dm, String id) {
		super(addrs, port, ui);
		ID = id;
		crypto = c;
		dataManager = dm;
	}
	
	protected Delegate makeDelegate() {
		ui.setTag("Bob");
		Delegate dataReceiver = new DataReceiver(ID, ui, crypto, dataManager);
		return dataReceiver;
	}
}
