
public class Alice extends Sender {
	protected Delegate makeDelegate() {
		return new DataCreator();
	}
}
