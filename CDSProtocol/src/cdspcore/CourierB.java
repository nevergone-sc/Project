package cdspcore;

/* CourierB is an Initiator, who acts like a CourierSender
 */
public class CourierB extends Initiator{
	private String ID;
	private String revID;
	private Crypto crypto;
	private DataManager dataManager;
	
	public CourierB(String addrs, int port, UserInterface ui, Crypto c, DataManager dm, String id, String revID) {
		super(addrs, port, ui);
		ID = id;
		this.revID = revID;
		crypto = c;
		dataManager = dm;
	}
	
	protected Delegate makeDelegate() {
		Delegate sendCourier = new CourierSender(ID, ui, crypto, dataManager, revID);
		return sendCourier;
	}
}
