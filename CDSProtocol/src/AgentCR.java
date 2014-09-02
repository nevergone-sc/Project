import cdspcore.*;

public class AgentCR {
	public static void main(String[] args) {	    
		String myID = "Courier";
		String senderID = "Alice";
		String dstAddress = "localhost";
		int dstPort = 9888;
		int maxCapacity = 1000;
		
		try {
			DataManager dataManager = new DataManager(myID, maxCapacity);
			dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
			dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
			Initiator courier = 
					new CourierA(dstAddress, dstPort, new NullUserInterface(), new Crypto(), dataManager, myID, senderID);
			courier.start();
		} catch (NumberFormatException e) {
		}
	}
}
