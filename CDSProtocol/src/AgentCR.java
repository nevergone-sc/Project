import cdspcore.*;

public class AgentCR {
	public static void main(String[] args) {	
		String myID = "Courier";
		String senderID = "Alice";
		String dstAddress = "localhost";
		int dstPort = 9888;
		int maxCapacity = 1000000;
		UserInterface ui = new NullUserInterfaceA();
		
		for (int i = 0; i < 500; i++) {
			try {
				DataManager dataManager = new DataManager(myID, maxCapacity);
				dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
				dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
				Initiator courier = 
						new CourierA(dstAddress, dstPort, ui, new Crypto(), dataManager, myID, senderID);
				courier.start();
			} catch (NumberFormatException e) {
			}
		}
	}
}
