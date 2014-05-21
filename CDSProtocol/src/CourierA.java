
public class CourierA extends Initializer {
	protected Delegate makeDelegate() {
		return new ReceiveCourier();
	}
}
