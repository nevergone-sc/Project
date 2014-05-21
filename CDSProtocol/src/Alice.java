
public class Alice extends Accepter {
	protected Delegate makeDelegate() {
		return new DataCreator();
	}
}
