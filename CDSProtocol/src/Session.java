import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Session extends Thread {
	private static final int MAX_BUFFER_SIZE = 16384;
	private static final int TIMEOUT = 1000;
	
	int sessionID;
	Channel channel;
	Delegate delegate;
	UserInterface ui;
	
	ByteBuffer receiveBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
	ByteBuffer preSendBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE-1);
	
	public Session(Channel c, Delegate d, UserInterface ui, int id) {
		channel = c;
		delegate = d;
		sessionID = id;
		this.ui = ui;
	}
	
	public void run() {
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
			
			// Start channel.read() together with a TimerTask for timeout
			ScheduledExecutorService readExecutor = Executors.newScheduledThreadPool(2); 
			final Future<Integer> readHandler = readExecutor.submit(new CallableReadChannel());
			readExecutor.schedule(new TaskCanceller(readHandler), TIMEOUT, TimeUnit.MILLISECONDS);
			try {
				lengthReceived = readHandler.get();
			} catch (Exception e) {
				ui.printErr("Timeout", "Session " + sessionID);
			}
			if (lengthReceived <= 0) { ui.printErr("Channel Closed", "Session " + sessionID); break; }
			
			if (isErrorMsg(receiveBuffer)) {
				ui.printErr(getErrorMsg(receiveBuffer), "Session " + sessionID);
				break;
			}
			
			lengthSent = delegate.process(receiveBuffer, preSendBuffer);
			// Protocol error
			if (lengthSent < 0) { sendErrorMsg("Protocol Error"); break; }
			// Protocol finished normally
			if (lengthSent == 0) {break;}
			
			int actSent = 0;
			try {
				actSent = channel.write(wrapMsg(preSendBuffer));
				lengthSent++; // The error message mark
			} catch (IOException e) {
				ui.printErr("IOException while writing channel", "Session " + sessionID);
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
			ui.printErr("Fail to close channel", "Session " + sessionID);
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
	
	// Thread for cancelling timeout connections
	class TaskCanceller implements Runnable {
		private Future<Integer> handler;
		
		public TaskCanceller(Future<Integer> h) {
			handler = h;
		}
		
		public void run(){
		    handler.cancel(true);
		}
	}

	class CallableReadChannel implements Callable<Integer>{
		public Integer call() {
			try {
				return channel.read(receiveBuffer);
			} catch (IOException e) {
				ui.printErr("IOException while reading channel", "Session " + sessionID);
			}
			return -1;
		}
	}
}
