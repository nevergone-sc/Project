import java.nio.ByteBuffer;

public class Session extends Thread {
	private static final int MAX_BUFFER_SIZE = 1024;
	
	int sessionID;
	Channel channel;
	Delegate delegate;
	
	public Session(Channel c, Delegate d, int id) {
		channel = c;
		delegate = d;
		sessionID = id;
	}
	
	public void run() {
		ByteBuffer receiveBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
		ByteBuffer sendBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
		int lengthSent = 0;
		int lengthReceived = 0;
		
		while (delegate.isAlive()) {
			receiveBuffer.clear();
			sendBuffer.clear();
			try {
				lengthReceived = channel.read(receiveBuffer);
			} catch (Exception e) {
				sendErrorMsg("IO Error");
			}
			receiveBuffer.flip();
			if (lengthReceived <= 0) { sendErrorMsg("IO Error"); break; }
			
			lengthSent = delegate.process(receiveBuffer, sendBuffer);
			if (lengthSent < 0) { sendErrorMsg("Protocol Error"); break; }
			if (lengthSent == 0) {break;}
			
			int actSent = 0;
			try {
				//System.out.println(Delegate.extractString(sendBuffer, 0, 10));
				actSent = channel.write(sendBuffer);
				
			} catch (Exception e) {
				sendErrorMsg("IO Error");
			}
			if (actSent != lengthSent) {
				System.err.println("Send fail: " + "length sent = " + lengthSent + " actual sent = " + actSent);
				break;
			}
			
		}
		System.out.println("Session " + sessionID + " process finished");
		try {
			channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendErrorMsg(String error) {

	}
	
	private boolean isErrorMsg(ByteBuffer src) {
		return false;
	}
}