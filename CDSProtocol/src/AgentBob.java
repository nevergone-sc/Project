import cdspcore.Accepter;
import cdspcore.Bob;
import cdspcore.DataManager;

import cdspcore.*;

public class AgentBob {
	public static void main(String[] args) {
		String myID = "Bob";
		String locAddress = "localhost";
		int lisPort = 8888;
		
		try {
			DataManager dataManager = new DataManager(myID);
			dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
			dataManager.setPathPrivateKey("PrivateKey_Bob");

			Accepter receiver = new Bob(locAddress, lisPort, new NullUserInterface(), new Crypto(), dataManager, myID);
			receiver.start();
		} catch (NumberFormatException e) {
		}
	}
}
