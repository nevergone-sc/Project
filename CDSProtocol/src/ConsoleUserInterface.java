import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface{
	private String tag;
	
	public synchronized void setTag(String tag) {
		this.tag = tag;
	}
	
	public synchronized void print(String str, String threadID) {
		System.out.println(threadID + ": " + str);
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
