
public class CourierB extends Initializer{
	protected Delegate makeDelegate() {
		Crypto c = new Crypto();
		DataManager dm = new DataManager(1000);
		dm.setPathPublicKey("Bob", "PublicKey_Bob");
		dm.setPathData("Alice", "Bob", "Data_Alice_To_Bob");
		return new SendCourier(c, dm);
	}
}
