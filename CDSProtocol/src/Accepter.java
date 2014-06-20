public abstract class Accepter {
	private int listeningPort;
	private boolean isAlive = true;
	
	public void start() throws Exception {
		Channel mainChannel = new UDPChannel(listeningPort);
		System.out.println("Accepter Ready");
		while (isAlive) {
			Channel subChannel = mainChannel.accept();
			System.out.println("Accepter Accepts a Connection");
			Delegate delegate = makeDelegate();
			Session session = new Session(subChannel, delegate, 0);
			session.start();
		}
		mainChannel.close();
	}
	
	public void setListeningPort(int port) {
		listeningPort = port;
	}
	
	public synchronized void stop() {
		isAlive = false;
	}
	
	protected abstract Delegate makeDelegate();
}