import cdspcore.CourierA;
import cdspcore.Crypto;
import cdspcore.DataManager;
import cdspcore.Initiator;
import cdspcore.ProtocolEntity;


public class NullUserInterfaceA implements cdspcore.UserInterface{
	int n = 0;
	int m = 0;
	long time = System.currentTimeMillis();
	long currentTime;
	boolean stop = false;
	
	public NullUserInterfaceA(){
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewCR();
		startNewNoisy();
		

	}
	
	public void setTag(String tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void print(String str, String tag, String threadID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void print(byte[] bytes, String tag, String threadID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nextStep(String tag, String threadID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printErr(String str, String threadID) {
		// TODO Auto-generated method stub
		if (str.equals("noisy") && threadID == "" && !stop) {
			startNewNoisy();
			n++;
			//System.out.println(n);
			/*
			if (n == 100 || n == 200 || n == 300 || n == 400 || n == 500 || n == 600 || n == 700 ||
				n == 800 || n == 900 || n == 1000) {
			*/
			if (n == 100) {
				currentTime = System.currentTimeMillis();
				
				stop = true;
				long timeGap = currentTime - time;
				time = currentTime;
				System.out.println(timeGap);
			}
		} else if (!str.equals("noisy") && threadID == "" && !stop) {
			m++;
			if (m < 2000) startNewCR();
		} else {
			System.out.println(str);
		}
		
		
	}

	@Override
	public String getInput(String info, String threadID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start(ProtocolEntity entity) {
		// TODO Auto-generated method stub
		
	}

	private void startNewCR() {
		String myID = "Courier";
		String senderID = "Alice";
		String dstAddress = "localhost";
		int dstPort = 9888;
		int maxCapacity = 1000000;
		
			try {
				DataManager dataManager = new DataManager(myID, maxCapacity);
				dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
				dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
				Initiator courier = 
						new CourierA(dstAddress, dstPort, this, new Crypto(), dataManager, myID, senderID);
				courier.start();
			} catch (NumberFormatException e) {
			}
	}
	
	private void startNewNoisy() {
		String myID = "noisy";
		String senderID = "Alice";
		String dstAddress = "localhost";
		int dstPort = 9888;
		int maxCapacity = 1000000;
			try {
				DataManager dataManager = new DataManager(myID, maxCapacity);
				dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
				dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
				Initiator courier = 
						new CourierA(dstAddress, dstPort, this, new Crypto(), dataManager, myID, senderID);
				courier.start();
			} catch (NumberFormatException e) {}
	}
}
