public abstract class Accepter implements ProtocolEntity{
	private int listeningPort = 8888;
	private UserInterface ui = new ConsoleUserInterface();
	private boolean isAlive = true;
	
	public void start() {
		Channel mainChannel = null;
		try {
			mainChannel = new UDPChannel(listeningPort);
			ui.print("Ready", "Accepter");
			while (isAlive) {
				Channel subChannel = mainChannel.accept();
				ui.print("Accepts a Connection", "Accepter");
				Delegate delegate = makeDelegate(ui);
				Session session = new Session(subChannel, delegate, 0);
				session.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mainChannel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setListeningPort(int port) {
		listeningPort = port;
	}
	
	public void setUserInterface(UserInterface ui) {
		this.ui = ui;
	}
	
	public synchronized void stop() {
		isAlive = false;
	}
	
	protected abstract Delegate makeDelegate(UserInterface ui);
}