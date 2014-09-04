import cdspcore.CourierA;
import cdspcore.CourierB;
import cdspcore.Crypto;
import cdspcore.DataManager;
import cdspcore.Initiator;
import cdspcore.ProtocolEntity;


public class NullUserInterfaceB implements cdspcore.UserInterface{
	int n = 0;
	int m = 0;
	long time = System.currentTimeMillis();
	long currentTime;
	boolean stop = false;
	
	public NullUserInterfaceB(){
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
			if (n == 1000) {
				currentTime = System.currentTimeMillis();
				
				stop = true;
				long timeGap = currentTime - time;
				//long timeGap = System.currentTimeMillis() - time;
				time = currentTime;
				System.out.println(timeGap);
			}
		} else if (!str.equals("noisy") && threadID == "" && !stop) {
			m++;
			//if (m < 2000) startNewCR();
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
		String revID = "Bob";
		String dstAddress = "localhost";
		int dstPort = 8888;

		try {
			DataManager dataManager = new DataManager(myID);
			dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
			dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
					
			Initiator courier = 
					new CourierB(dstAddress, dstPort, this, new Crypto(), dataManager, myID, revID);
			courier.start();
		} catch (NumberFormatException e) {
		}
	}
	
	private void startNewNoisy() {
		String myID = "noisy";
		String revID = "Bob";
		String dstAddress = "localhost";
		int dstPort = 8888;

		try {
			DataManager dataManager = new DataManager(myID);
			dataManager.setPathPublicKey("Alice", "PublicKey_Alice");
			dataManager.setPathPublicKey("Bob", "PublicKey_Bob");
					
			Initiator courier = 
					new CourierB(dstAddress, dstPort, this, new Crypto(), dataManager, myID, revID);
			courier.start();
		} catch (NumberFormatException e) {
		}
	}
}
