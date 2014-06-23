
public class CourierB extends Initializer{
	protected Delegate makeDelegate(UserInterface ui) {
		Crypto c = new Crypto();
		DataManager dm = new DataManager(1000);
		dm.setPathPublicKey("Bob", "PublicKey_Bob");
		dm.setPathData("Alice", "Bob", "Data_Alice_To_Bob");
		
		Delegate sendCourier = new SendCourier(c, dm);
		sendCourier.setUserInterface(ui);
		return sendCourier;
	}
}
