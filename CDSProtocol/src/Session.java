import java.nio.ByteBuffer;

public class Session extends Thread {
	private static final int MAX_BUFFER_SIZE = 1024;
	
	int sessionID;
	Channel channel;
	Delegate delegate;
	UserInterface ui;
	
	public Session(Channel c, Delegate d, UserInterface ui, int id) {
		channel = c;
		delegate = d;
		sessionID = id;
		this.ui = ui;
	}
	
	public void run() {
		ByteBuffer receiveBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
		ByteBuffer preSendBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE-1);
		int lengthSent = 0;
		int lengthReceived = 0;
		
		ByteBuffer initialMsg = delegate.getInitialMessage();
		if (initialMsg != null) {
			try {
				preSendBuffer.put(initialMsg);
				preSendBuffer.flip();
				channel.write(wrapMsg(preSendBuffer));
				ui.print("Initial Message sent", "", "Initializer");
			} catch (Exception e) {
				ui.printErr("Fail to send Initial Message", "Session " + sessionID);
			}
		}
		
		while (delegate.isAlive()) {
			receiveBuffer.clear();
			preSendBuffer.clear();
			try {
				lengthReceived = channel.read(receiveBuffer);
			} catch (Exception e) {
				e.printStackTrace();
				sendErrorMsg("Channel Read Error");
			}
			if (lengthReceived <= 0) { sendErrorMsg("IO Error"); break; }
			
			if (isErrorMsg(receiveBuffer)) {
				ui.printErr(getErrorMsg(receiveBuffer), "Session " + sessionID);
				break;
			}
			lengthSent = delegate.process(receiveBuffer, preSendBuffer);
			if (lengthSent < 0) { sendErrorMsg("Protocol Error"); break; }
			if (lengthSent == 0) {break;}
			
			int actSent = 0;
			try {
				//System.out.println(Delegate.extractString(sendBuffer, 0, 10));
				actSent = channel.write(wrapMsg(preSendBuffer));
				lengthSent++;
				
			} catch (Exception e) {
				sendErrorMsg("IO Error");
			}
			if (actSent != lengthSent) {
				ui.printErr("Send fail: " + "length sent = " + lengthSent + " actual sent = " + actSent, "Session " + sessionID);
				break;
			}
			
		}
		ui.print("process finished", "", "Session " + sessionID);
		try {
			channel.close();
		} catch (Exception e) {
			ui.print("Fail to close channel", "", "Session " + sessionID);
		}
	}
	
	private ByteBuffer wrapMsg(ByteBuffer src) {
		ByteBuffer returnBuffer = ByteBuffer.allocate(src.capacity()+1);
		returnBuffer.put((byte) 0);
		returnBuffer.put(src);
		returnBuffer.flip();
		return returnBuffer;
	}
	
	private void sendErrorMsg(String error) {
		ByteBuffer errorMsg = ByteBuffer.allocate(error.length()+1);
		errorMsg.put((byte) error.length());
		errorMsg.put(error.getBytes());
		errorMsg.flip();
		try {
			channel.write(errorMsg);
		} catch (Exception e) {
			ui.printErr("Fail to send error message", "Session " + sessionID);
		}
	}
	
	private String getErrorMsg(ByteBuffer src) {
		src.rewind();
		byte stringLength = src.get();
		byte[] errorMsgBytes = new byte[stringLength];
		src.get(errorMsgBytes);
		String errorMsg = new String(errorMsgBytes);
		return errorMsg;
	}
	
	private boolean isErrorMsg(ByteBuffer src) {
		return (!(src.get() == 0));
	}
}