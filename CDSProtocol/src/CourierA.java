
public class CourierA extends Initializer {
	private String ID;
	private String sedID;
	private Crypto crypto;
	private DataManager dataManager;
	
	public CourierA(String addrs, int port, UserInterface ui, Crypto c, DataManager dm, String id, String sedID) {
		super(addrs, port, ui);
		ID = id;
		this.sedID = sedID;
		crypto = c;
		dataManager = dm;
	}
	
	protected Delegate makeDelegate() {
		Delegate receiveCourier = new ReceiveCourier(ID, crypto, dataManager, sedID);
		receiveCourier.setUserInterface(ui);
		return receiveCourier;
	}

}
