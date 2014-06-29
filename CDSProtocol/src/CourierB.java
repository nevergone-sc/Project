
public class CourierB extends Initializer{
	private String ID;
	private String sedID;
	private String revID;
	private Crypto crypto;
	private DataManager dataManager;
	
	public CourierB(String addrs, int port, UserInterface ui, Crypto c, DataManager dm, String id, String sedID, String revID) {
		super(addrs, port, ui);
		ID = id;
		this.sedID = sedID;
		this.revID = revID;
		crypto = c;
		dataManager = dm;
	}
	
	protected Delegate makeDelegate() {
		Delegate sendCourier = new SendCourier(ID, crypto, dataManager, sedID, revID);
		sendCourier.setUserInterface(ui);
		return sendCourier;
	}
}
