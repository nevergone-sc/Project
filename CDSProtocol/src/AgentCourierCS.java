import cdspcore.*;

public class AgentCourierCS {
	public static void main(String[] args) {	 
		String myID = "Courier";
		String revID = "Bob";
		String dstAddress = "localhost";
		int dstPort = 8888;

		try {
			DataManager dataManager = new DataManager(myID);
			dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
			dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
					
			Initiator courier = 
					new CourierB(dstAddress, dstPort, new NullUserInterface(), new Crypto(), dataManager, myID, revID);
			courier.start();
		} catch (NumberFormatException e) {
		}
	}
}
