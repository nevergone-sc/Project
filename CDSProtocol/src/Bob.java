
public class Bob extends Accepter {
	protected Delegate makeDelegate() {
		return new DataReceiver();
	}
}
