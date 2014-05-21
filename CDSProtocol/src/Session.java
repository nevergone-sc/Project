import java.nio.ByteBuffer;


public class Session extends Thread {
	int sessionID;
	Channel channel;
	Delegate delegate;
	
	public Session(Channel c, Delegate d, int id) {
		channel = c;
		delegate = d;
		sessionID = id;
	}
	
	public void run() {
		ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
		ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
		int lengthSent = 0;
		int lengthReceived = 0;
		
		while (delegate.isAlive()) {
			receiveBuffer.clear();
			sendBuffer.clear();
			try {
				lengthReceived = channel.read(receiveBuffer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receiveBuffer.flip();
			if (lengthReceived <= 0) break;
			
			lengthSent = delegate.process(receiveBuffer, sendBuffer);
			if (lengthSent < 0) { System.out.println("process not valid"); break; }
			if (lengthSent == 0) {break;}
			
			int actSent = 0;
			try {
				//System.out.println(Delegate.extractString(sendBuffer, 0, 10));
				actSent = channel.write(sendBuffer);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (actSent != lengthSent) {
				System.out.println("Send fail: " + "length sent = " + lengthSent + " actual sent = " + actSent);
				break;
			}
			
		}
		System.out.println("Session " + sessionID + " process finished");
		try {
			channel.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}