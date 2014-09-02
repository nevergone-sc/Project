import cdspcore.*;

public class AgentAlice {
	public static void main(String[] args) {
		String locAddress = "localhost";
		String myID = "Alice";
		String revID = "Bob";
		int lisPort = 9888;
		
		try {	
			DataManager dataManager = new DataManager(myID);
			dataManager.setPathPrivateKey("PrivateKey_Alice");
			dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
	
			Accepter alice = 
					new Alice(locAddress, lisPort, new NullUserInterface(), new Crypto(), dataManager, myID, revID);
			alice.start();
		} catch (NumberFormatException e) {
			
		}
	}
}
