package cdspcore;
import java.util.Scanner;


public class ConsoleUserInterface implements UserInterface{
	private String tag;
	
	public synchronized void setTag(String tag) {
		this.tag = tag;
	}
	
	public synchronized void print(String str, String tag, String threadID) {
		System.out.println(threadID + ": " + tag + str);
	}
	
	public synchronized void print(byte[] src, String tag, String threadID) {
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
		System.out.print(threadID + ": ");
		System.out.print(tag);
		int length = src.length;
		char[] hexChars = new char[length * 2];
	    for ( int j = 0; j < length; j++ ) {
	        int v = src[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    for ( int i = 0; i < hexChars.length; i++) {
	    	System.out.print(hexChars[i] + " ");
	    }
	    System.out.println();
	}
	
	public synchronized void nextStep(String tag, String threadID) {
		System.out.println("-------------------------------------------------");
	}
	
	public synchronized void printErr(String str, String threadID) {
		System.err.println(threadID + ": " + str);
	}

	public synchronized String getInput(String info, String threadID) {
		Scanner scanner = new Scanner(System.in);
		System.out.println(threadID + ": " + info);
		String returnString = scanner.nextLine();
		scanner.close();
		return returnString;
	}
	
	public void start(ProtocolEntity entity) {
		entity.start();
	}
}
