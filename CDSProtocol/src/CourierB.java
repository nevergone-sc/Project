
public class CourierB {
	protected Delegate makeDelegate() {
		return new SendCourier();
	}
}
