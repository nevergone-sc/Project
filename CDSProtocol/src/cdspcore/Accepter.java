package cdspcore;

// An Accepter defines a class of entities who is passive and only accepts connections
public abstract class Accepter implements ProtocolEntity{
	protected String address;
	protected int listeningPort;
	//private Delegate delegate;
	protected UserInterface ui = new ConsoleUserInterface();
	protected boolean isAlive = true;
	
	public Accepter(String a, int p, UserInterface ui) {
		address = a;
		listeningPort = p;
		this.ui = ui;
	}
	
	public void start() {
		Channel mainChannel = null;
		try {
			mainChannel = new UDPChannel(listeningPort);
			ui.print("Ready", "", "Accepter");
			while (isAlive) {
				Delegate delegate = makeDelegate();
				Channel subChannel = mainChannel.accept();
				ui.print("Accepts a Connection", "", "Accepter");
				if (delegate != null) {
					Session session = new Session(subChannel, delegate, ui, 0);
					session.start();
				}
			}
		} catch (Exception e) {
			ui.printErr(e.getMessage(), "");
		} finally {
			try {
				mainChannel.close();
			} catch (Exception e) {
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
	
	protected abstract Delegate makeDelegate();
}